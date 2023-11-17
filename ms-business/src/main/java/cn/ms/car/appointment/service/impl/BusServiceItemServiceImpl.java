package cn.ms.car.appointment.service.impl;

import cn.ms.car.appointment.domain.BusServiceItem;
import cn.ms.car.appointment.domain.vo.BusServiceItemVo;
import cn.ms.car.appointment.domain.vo.ServiceItemAuditInfo;
import cn.ms.car.appointment.enums.ServiceItemAuditStatusEnum;
import cn.ms.car.appointment.mapper.BusServiceItemMapper;
import cn.ms.car.flow.service.IBusBpmnInfoService;
import cn.ms.car.appointment.service.IBusServiceItemService;
import cn.ms.car.common.core.domain.entity.SysUser;
import cn.ms.car.common.utils.DateUtils;
import cn.ms.car.common.utils.bean.BeanUtils;
import cn.ms.car.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;

/**
 * 静态导入：import static =》 允许将指定类中的指定公共成员导入到当前类，在当前类中使用这些成员时，就如同本地成员的使用方式
 * 服务项Service业务层处理
 *
 * @author ms
 * @date 2023-03-30
 */
@Service
public class BusServiceItemServiceImpl implements IBusServiceItemService {
    @Autowired
    private BusServiceItemMapper busServiceItemMapper;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IBusBpmnInfoService bpmnInfoService;

    /**
     * 查询服务项
     *
     * @param id 服务项主键
     * @return 服务项
     */
    @Override
    public BusServiceItem selectBusServiceItemById(Long id) {
        return busServiceItemMapper.selectBusServiceItemById(id);
    }

    /**
     * 查询服务项列表
     *
     * @param busServiceItem 服务项
     * @return 服务项
     */
    @Override
    public List<BusServiceItem> selectBusServiceItemList(BusServiceItem busServiceItem) {
        return busServiceItemMapper.selectBusServiceItemList(busServiceItem);
    }

    /**
     * 新增服务项
     *
     * @param vo 服务项
     * @return 结果
     */
    @Override
    public int insertBusServiceItem(BusServiceItemVo vo) {
        // 判断服务项折扣价不能大于原价
        Assert.state(vo.getDiscountPrice().compareTo(vo.getOriginalPrice()) <= 0, "折扣价不能大于原价");

        BusServiceItem serviceItem = new BusServiceItem();
        BeanUtils.copyBeanProp(serviceItem, vo);

        serviceItem.setCreateTime(DateUtils.getNowDate());
        if (BusServiceItemVo.PACKAGE_NO.equals(vo.getCarPackage())) {
            serviceItem.setAuditStatus(ServiceItemAuditStatusEnum.NONE.ordinal());
        } else {
            serviceItem.setAuditStatus(ServiceItemAuditStatusEnum.INITIALING.ordinal()); // 设置默认值
        }
        serviceItem.setSaleStatus(BusServiceItemVo.SALE_STATUS_NO); // 设置默认值

        return busServiceItemMapper.insertBusServiceItem(serviceItem);
    }

    /**
     * 修改服务项
     *
     * @param vo 服务项
     * @return 结果
     */
    @Override
    public int updateBusServiceItem(BusServiceItemVo vo) {
        // 0. 参数校验
        Assert.notNull(vo.getId(), "请选择要修改的服务项");
        // 1. 已上架 或 审核中状态不允许编辑
        BusServiceItem busServiceItem = this.selectBusServiceItemById(vo.getId());
        Integer auditStatus = busServiceItem.getAuditStatus();
        Integer saleStatus = busServiceItem.getSaleStatus();
        Assert.state(ServiceItemAuditStatusEnum.AUDITING.ordinal() != auditStatus && // 审核状态不为审核中
                        !BusServiceItemVo.SALE_STATUS_YES.equals(saleStatus), // 并且上架状态不为已上架
                "当前服务项不允许修改"
        );

        // 2. 判断当前状态是否为审核通过，如果是审核通过
        /*if (PASSED.ordinal() == auditStatus ||
                NONE.ordinal() == auditStatus || // 兼容从不是套餐转换为是套餐
                INITIALING.ordinal() == auditStatus) { // 兼容是套餐转换为不是套餐*/
        // 2.1. 判断如果是套餐：重置为初始化
        if (BusServiceItemVo.PACKAGE_YES.equals(vo.getCarPackage())) {
            busServiceItem.setAuditStatus(ServiceItemAuditStatusEnum.INITIALING.ordinal());
        } else {
            // 2.2. 否则重置为无需审核
            busServiceItem.setAuditStatus(ServiceItemAuditStatusEnum.NONE.ordinal());
        }
        /*}*/

        // 3. 拷贝属性到模型对象（DO）
        BeanUtils.copyBeanProp(busServiceItem, vo);

        // 4. 更新数据
        return busServiceItemMapper.updateBusServiceItem(busServiceItem);
    }

    /**
     * 批量删除服务项
     *
     * @param ids 需要删除的服务项主键
     * @return 结果
     */
    @Override
    public int deleteBusServiceItemByIds(Long[] ids) {
        return busServiceItemMapper.deleteBusServiceItemByIds(ids);
    }

    /**
     * 删除服务项信息
     *
     * @param id 服务项主键
     * @return 结果
     */
    @Override
    public int deleteBusServiceItemById(Long id) {
        return busServiceItemMapper.deleteBusServiceItemById(id);
    }

    @Override
    public int updateSaleStatus(Long id) {
        // 2. 基于 id 查询服务项信息
        BusServiceItem busServiceItem = this.selectBusServiceItemById(id);
        Assert.notNull(busServiceItem, "参数错误");

        // 3. 判断如果是套餐，再判断是否为审核通过，如果不是，直接抛出异常
        if (BusServiceItemVo.PACKAGE_YES.equals(busServiceItem.getCarPackage())) {
            Assert.state(ServiceItemAuditStatusEnum.PASSED.ordinal() == busServiceItem.getAuditStatus(), "只有审核通过才允许上架");
        }

        // 4. 判断当前对象是上架还是下架
        // 4.1 如果是上架，更新为下架，如果是下架，更新为上架
        int status = BusServiceItemVo.SALE_STATUS_YES.equals(busServiceItem.getSaleStatus()) ? BusServiceItemVo.SALE_STATUS_NO : BusServiceItemVo.SALE_STATUS_YES;

        // 5. 更新对象状态
        return busServiceItemMapper.updateStatus(id, status);
    }

    @Override
    public ServiceItemAuditInfo selectAuditInfoById(Long id) {
        ServiceItemAuditInfo info = new ServiceItemAuditInfo();
        // 1. 查询服务项信息
        BusServiceItem serviceItem = this.selectBusServiceItemById(id);
        info.setServiceItem(serviceItem);

        // 2. 查询流程定义中的折扣价
        BigDecimal discountPrice = bpmnInfoService.getCarPackageAuditDiscountPrice();
        info.setLimitDiscountPrice(discountPrice);

        // 3. 查询店长列表
        // sys_user u join sys_user_role er on u.user_id = er.user_id join sys_role r on er.role_id = r.role_id where r.role_key = shopOwner
        // sys_user <==> sys_user_role <==> sys_role ==> role_key == shopOwner
        List<SysUser> showOwners = sysUserService.selectByRoleKey("shopOwner");
        Assert.state(showOwners.size() > 0, "请先添加店长");
        info.setShopOwners(showOwners);

        // 4. 根据流程定义的限制折扣价，判断是否需要查询财务列表
        if (serviceItem.getDiscountPrice().compareTo(discountPrice) >= 0) {
            // 5. 如果需要，查询财务列表
            List<SysUser> financialList = sysUserService.selectByRoleKey("financial");
            Assert.state(financialList.size() > 0, "请先添加财务");
            info.setFinances(financialList);
        }

        // 6. 封装完整对象，返回
        return info;
    }

    @Override
    public void updateAuditStatus(Long id, int auditStatus) {
        busServiceItemMapper.updateAuditStatus(id, auditStatus);
    }
}
