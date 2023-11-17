package cn.ms.car.appointment.service.impl;

import cn.ms.car.appointment.domain.BusAppointment;
import cn.ms.car.appointment.domain.BusStatement;
import cn.ms.car.appointment.domain.vo.BusAppointmentVo;
import cn.ms.car.appointment.enums.AppointmentStatusEnum;
import cn.ms.car.appointment.mapper.BusAppointmentMapper;
import cn.ms.car.appointment.service.IBusAppointmentService;
import cn.ms.car.appointment.service.IBusStatementService;
import cn.ms.car.common.core.redis.RedisCache;
import cn.ms.car.common.utils.DateUtils;
import cn.ms.car.common.utils.StringUtils;
import cn.ms.car.common.utils.bean.BeanUtils;
import cn.ms.car.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 养修信息预约Service业务层处理
 *
 * @author ms
 * @date 2023-03-27
 */
@Service
public class BusAppointmentServiceImpl implements IBusAppointmentService {

    @Autowired
    private BusAppointmentMapper busAppointmentMapper;
    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private RedisCache redisCache;

    @Lazy
    @Autowired
    private IBusStatementService statementService;

    /**
     * 查询养修信息预约
     *
     * @param id 养修信息预约主键
     * @return 养修信息预约
     */
    @Override
    public BusAppointment selectBusAppointmentById(Long id) {
        return busAppointmentMapper.selectBusAppointmentById(id);
    }

    /**
     * 查询养修信息预约列表
     *
     * @param busAppointment 养修信息预约
     * @return 养修信息预约
     */
    @Override
    public List<BusAppointment> selectBusAppointmentList(BusAppointment busAppointment) {
        return busAppointmentMapper.selectBusAppointmentList(busAppointment);
    }

    /**
     * 新增养修信息预约
     *
     * @param vo 养修信息预约
     * @return 结果
     */
    @Override
    public int insertBusAppointment(BusAppointmentVo vo) {
        // 预约时间必须大于当前时间
        Assert.state(vo.getAppointmentTime().getTime() > DateUtils.getNowDate().getTime(), "预约时间必须大于当前时间");

        // 同一手机号，一天只能下单 5 次
        // 利用系统参数表，配置当天最大预约次数参数，实现参数灵活配置
        String maxNumVal = sysConfigService.selectConfigByKey("bus.appointment.max.num");
        if (StringUtils.isEmpty(maxNumVal)) {
            maxNumVal = "5"; // 如果当前没有配置系统参数，默认设置为 5
        }
        Integer maxNum = Integer.valueOf(maxNumVal);
        // 利用 Redis 的有效时间 + 计数器可以实现当天次数累加操作
        String key = "bus_appointment_num:" + vo.getCustomerPhone();

        // 过期时间 = 今天最后一秒时间 - 当前时间
        long seconds = DateUtils.getNowToLastSeconds();
        Long increment = redisCache.increment(key, 1, seconds, TimeUnit.SECONDS);
        Assert.state(increment <= maxNum, "当天预约次数已经达到上限！");

        BusAppointment busAppointment = new BusAppointment();
        BeanUtils.copyBeanProp(busAppointment, vo); // 将 vo 对象中同名的属性拷贝到 busAppointment 对象中
        busAppointment.setCreateTime(DateUtils.getNowDate()); // 设置创建时间

        return busAppointmentMapper.insertBusAppointment(busAppointment);
    }

    /**
     * 修改养修信息预约
     *
     * @param vo 养修信息预约
     * @return 结果
     */
    @Override
    public int updateBusAppointment(BusAppointmentVo vo) {

        // 1. 判断当前状态只能是预约中才允许修改
        BusAppointment appointmentInDB = this.selectBusAppointmentById(vo.getId());
        // 非空判断：健壮性考虑
        Assert.notNull(appointmentInDB, "参数错误！");

        // 状态判断
        // 状态：预约中0/已到店1/用户取消2/超时取消3/已结算4/已支付5
        Assert.state(AppointmentStatusEnum.APPOINTING.ordinal() == appointmentInDB.getStatus(), "只有预约中才允许修改！");

        BusAppointment busAppointment = new BusAppointment();
        BeanUtils.copyBeanProp(busAppointment, vo); // 将 vo 对象中同名的属性拷贝到 busAppointment 对象中

        return busAppointmentMapper.updateBusAppointment(busAppointment);
    }

    /**
     * 批量删除养修信息预约
     *
     * @param ids 需要删除的养修信息预约主键
     * @return 结果
     */
    @Override
    public int deleteBusAppointmentByIds(Long[] ids) {
        return busAppointmentMapper.deleteBusAppointmentByIds(ids);
    }

    /**
     * 删除养修信息预约信息
     *
     * @param id 养修信息预约主键
     * @return 结果
     */
    @Override
    public int deleteBusAppointmentById(Long id) {
        return busAppointmentMapper.deleteBusAppointmentById(id);
    }

    @Override
    public int updateArrival(Long id) {
        // 检查状态
        this.checkStatus(id, AppointmentStatusEnum.APPOINTING);
        // 4. 更新状态为已到店 & 更新到店时间
        return busAppointmentMapper.updateArrival(id);
    }

    private void checkStatus(Long id, AppointmentStatusEnum statusEnum) {
        // 1. 基于 id 查询预约信息
        BusAppointment appointment = this.selectBusAppointmentById(id);
        // 2. 判断预约信息是否为空
        Assert.notNull(appointment, "参数错误！");
        // 3. 判断状态是否为预约中
        Assert.state(statusEnum.ordinal() == appointment.getStatus(), "非法状态！");
    }

    @Override
    public int updateCancel(Long id) {
        // 检查状态
        this.checkStatus(id, AppointmentStatusEnum.APPOINTING);
        // 4. 更新状态为已取消
        return busAppointmentMapper.updateStatus(id, AppointmentStatusEnum.CANCEL.ordinal());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BusStatement doCreateStatement(Long id) {
        // 1. 根据 id 查询预约信息
        BusAppointment appointment = this.selectBusAppointmentById(id);

        // 2. 判断状态是否可以生成结算单
        List<Integer> status = Arrays.asList(AppointmentStatusEnum.ARRIVAL.ordinal(), AppointmentStatusEnum.PAID.ordinal(), AppointmentStatusEnum.STATEMENT.ordinal());
        Assert.state(status.contains(appointment.getStatus()), "该状态无法结算");

        // 3. 根据预约 id 查询结算单信息，如果为空，生成结算单
        BusStatement statement = statementService.selectStatementByAppointmentId(id);
        if (statement == null) {
            // 3.1 根据预约信息创建结算单
            statement = this.buildStatement(appointment);
            // 3.2 保存结算单
            statementService.insertBusStatement(statement, id);
            // 3.3 更新预约状态为结算单生成
            busAppointmentMapper.updateStatus(id, AppointmentStatusEnum.STATEMENT.ordinal());
        }
        // 4. 返回结算单
        return statement;
    }

    @Override
    public int updatePaySuccess(Long appointmentId) {
        return busAppointmentMapper.updateStatus(appointmentId, AppointmentStatusEnum.PAID.ordinal());
    }

    private BusStatement buildStatement(BusAppointment appointment) {
        BusStatement statement = new BusStatement();
        statement.setTotalQuantity(BigDecimal.ZERO);
        statement.setTotalAmount(BigDecimal.ZERO);
        statement.setDiscountAmount(BigDecimal.ZERO);
        statement.setAppointmentId(appointment.getId());
        statement.setIsDelete(0);
        statement.setStatus(BusStatement.CONSUMER);
        statement.setActualArrivalTime(appointment.getActualArrivalTime());
        statement.setCarSeries(appointment.getCarSeries());
        statement.setCustomerName(appointment.getCustomerName());
        statement.setCustomerPhone(appointment.getCustomerPhone());
        statement.setInfo(appointment.getInfo());
        statement.setLicensePlate(appointment.getLicensePlate());
        statement.setServiceType(appointment.getServiceType());
        return statement;
    }
}
