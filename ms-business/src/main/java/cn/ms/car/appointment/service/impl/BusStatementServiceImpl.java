package cn.ms.car.appointment.service.impl;

import cn.ms.car.appointment.domain.BusServiceItem;
import cn.ms.car.appointment.domain.BusStatement;
import cn.ms.car.appointment.domain.BusStatementItem;
import cn.ms.car.appointment.domain.vo.BusStatementVo;
import cn.ms.car.appointment.mapper.BusStatementItemMapper;
import cn.ms.car.appointment.mapper.BusStatementMapper;
import cn.ms.car.appointment.service.IBusAppointmentService;
import cn.ms.car.appointment.service.IBusServiceItemService;
import cn.ms.car.appointment.service.IBusStatementService;
import cn.ms.car.common.constant.Constants;
import cn.ms.car.common.utils.DateUtils;
import cn.ms.car.common.utils.SecurityUtils;
import cn.ms.car.common.utils.bean.BeanUtils;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 结算单Service业务层处理
 *
 * Spring 循环依赖：只有在单例且使用属性注入的方式下才可以解决
 * bean 的作用域：singleton / prototype
 *
 * SpringBoot 2.6.x 以后的版本默认关闭了循环依赖，可以通过修改如下配置启用循环依赖支持
 * spring:
 *   main:
 *     allow-circular-references: true
 *
 * 解决方案：懒加载 => @Lazy，这个 Bean 在 Spring 容器启动时，不会直接加载，直到该 bean 对象在某个方法中被调用了以后，才会创建
 * 解决方案：再创建一个第三方类，将双方需要互相引用的方法抽取到第三方，双方只需要依赖到第三方即可
 *
 *
 * @author ms
 * @date 2023-04-01
 */
@Service
public class BusStatementServiceImpl implements IBusStatementService {
    private final BusStatementMapper busStatementMapper;
    private final IBusServiceItemService serviceItemService;
    private final BusStatementItemMapper statementItemMapper;
    private final IBusAppointmentService appointmentService;

    public BusStatementServiceImpl(BusStatementMapper busStatementMapper,
                                   IBusServiceItemService serviceItemService,
                                   BusStatementItemMapper statementItemMapper,
                                   IBusAppointmentService appointmentService) {
        this.busStatementMapper = busStatementMapper;
        this.serviceItemService = serviceItemService;
        this.statementItemMapper = statementItemMapper;
        this.appointmentService = appointmentService;
    }

    /**
     * 查询结算单
     *
     * @param id 结算单主键
     * @return 结算单
     */
    @Override
    public BusStatement selectBusStatementById(Long id) {
        return busStatementMapper.selectBusStatementById(id);
    }

    /**
     * 查询结算单列表
     *
     * @param busStatement 结算单
     * @return 结算单
     */
    @Override
    public List<BusStatement> selectBusStatementList(BusStatement busStatement) {
        return busStatementMapper.selectBusStatementList(busStatement);
    }

    @Override
    public int insertBusStatement(BusStatement statement, Long appointmentId) {
        statement.setAppointmentId(appointmentId); // 设置预约 id

        // 补充默认数据
        statement.setIsDelete(Constants.DEL_FLAG_NORMAL); // 默认删除状态为正常
        statement.setStatus(BusStatement.CONSUMER); // 状态为消费中
        statement.setCreateTime(DateUtils.getNowDate());
        return busStatementMapper.insertBusStatement(statement);
    }

    /**
     * 新增结算单
     *
     * @param vo 结算单
     * @return 结果
     */
    @Override
    public int insertBusStatement(BusStatementVo vo) {
        BusStatement statement = new BusStatement();
        BeanUtils.copyBeanProp(statement, vo);
        return this.insertBusStatement(statement, null);
    }

    /**
     * 修改结算单
     *
     * @param vo 结算单
     * @return 结果
     */
    @Override
    public int updateBusStatement(BusStatementVo vo) {
        // 6.2 先基于 id 查询结算单对象
        BusStatement busStatement = this.selectBusStatementById(vo.getId());
        Assert.notNull(busStatement, "参数错误");

        // 6.3 判断状态是否为消费中，如果不是抛出异常
        Assert.state(BusStatement.CONSUMER.equals(busStatement.getStatus()), "已支付的订单不允许修改");

        // 6.4 将允许编辑的值拷贝到模型对象
        BeanUtils.copyBeanProp(busStatement, vo);

        // 6.5 更新对象
        return busStatementMapper.updateBusStatement(busStatement);
    }

    /**
     * 批量删除结算单
     *
     * @param ids 需要删除的结算单主键
     * @return 结果
     */
    @Override
    public int deleteBusStatementByIds(Long[] ids) {
        int rows = busStatementMapper.deleteBusStatementByIds(ids);
        Assert.state(rows > 0, "已支付不能删除");
        return rows;
    }

    /**
     * 删除结算单信息
     *
     * @param id 结算单主键
     * @return 结果
     */
    @Override
    public int deleteBusStatementById(Long id) {
        int rows = busStatementMapper.deleteBusStatementById(id);
        Assert.state(rows > 0, "已支付不能删除");
        return rows;
    }

    /* 此方法涉及到多表操作，需要事务，且设置事务回滚异常为 Exception.class */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertItems(BusStatementVo vo) {
        // 必须由明细
        List<BusStatementItem> items = vo.getItems();
        Assert.state(!CollectionUtils.isEmpty(items), "请选择本次消费的服务项");

        // 结算单状态必须为消费中
        BusStatement busStatement = this.selectBusStatementById(vo.getId());
        Assert.state(BusStatement.CONSUMER.equals(busStatement.getStatus()), "只能修改消费中的结算单");

        // 1. 计算总金额、总数量
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalQuantity = BigDecimal.ZERO;
        for (BusStatementItem item : items) {
            // 基于服务项 id 查询服务项对象
            BusServiceItem serviceItem = serviceItemService.selectBusServiceItemById(item.getItemId());
            Assert.notNull(serviceItem, "服务项不存在：" + item.getItemName());
            // 计算金额小计
            item.setItemName(serviceItem.getName());
            item.setItemPrice(serviceItem.getDiscountPrice()); // 重置前端传入的价格

            BigDecimal amount = item.getItemPrice().multiply(new BigDecimal(item.getItemQuantity() + ""))
                    .setScale(2, BigDecimal.ROUND_HALF_UP); // 金额小计，保留两位小数四舍五入

            totalAmount = totalAmount.add(amount); // 累加总金额
            totalQuantity = totalQuantity.add(new BigDecimal(item.getItemQuantity() + "")); // 累加总数量
        }
        busStatement.setId(vo.getId());
        busStatement.setDiscountAmount(vo.getDiscountAmount());
        busStatement.setTotalAmount(totalAmount);
        busStatement.setTotalQuantity(totalQuantity);

        // 2. 更新结算单总金额、总数量、折扣金额
        int rows = busStatementMapper.updateBusStatement(busStatement);
        Assert.state(rows > 0, "更新结算单失败");

        // 3. 删除当前结算单的所有明细
        statementItemMapper.deleteBusStatementItemByStatementId(vo.getId());

        // 4. 新增结算单明细
        rows = statementItemMapper.batchInsertBusStatementItem(vo.getItems());
        Assert.state(rows > 0, "新增结算单明细失败");
        return rows;
    }

    @Override
    public List<BusStatementItem> selectItemsById(Long statementId) {
        return statementItemMapper.selectBusStatementItemList(statementId);
    }

    @Override
    public int doPrepay(Long id) {
        // 基于 id 查询结算单对象
        BusStatement statement = this.selectBusStatementById(id);
        Assert.state(BusStatement.CONSUMER.equals(statement.getStatus()), "该订单已经支付");

        // 获取到当前登录的用户
        Long userId = SecurityUtils.getUserId(); // 收银员 id
        int rows = busStatementMapper.paySuccess(id, userId);
        Assert.state(rows > 0, "支付失败！");

        // 判断当前订单是否有预约信息，如果有，将其状态更新为已支付
        if (statement.getAppointmentId() != null) {
            rows = appointmentService.updatePaySuccess(statement.getAppointmentId());
            Assert.state(rows > 0, "更新预约信息状态失败");
        }
        return rows;
    }

    @Override
    public BusStatement selectStatementByAppointmentId(Long appointmentId) {
        return busStatementMapper.selectStatementByAppointmentId(appointmentId);
    }
}
