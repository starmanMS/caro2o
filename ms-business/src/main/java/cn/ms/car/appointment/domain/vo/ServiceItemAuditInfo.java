package cn.ms.car.appointment.domain.vo;

import cn.ms.car.appointment.domain.BusServiceItem;
import cn.ms.car.common.core.domain.entity.SysUser;

import java.math.BigDecimal;
import java.util.List;

/**
 * 服务项审核信息对象
 */
public class ServiceItemAuditInfo {

    /**
     * 服务项信息
     */
    private BusServiceItem serviceItem;
    /**
     * 限制折扣价
     */
    private BigDecimal limitDiscountPrice;
    /**
     * 店长列表
     */
    private List<SysUser> shopOwners;
    /**
     * 财务列表
     */
    private List<SysUser> finances;
    /**
     * 备注信息
     */
    private String info;

    public BusServiceItem getServiceItem() {
        return serviceItem;
    }

    public void setServiceItem(BusServiceItem serviceItem) {
        this.serviceItem = serviceItem;
    }

    public BigDecimal getLimitDiscountPrice() {
        return limitDiscountPrice;
    }

    public void setLimitDiscountPrice(BigDecimal limitDiscountPrice) {
        this.limitDiscountPrice = limitDiscountPrice;
    }

    public List<SysUser> getShopOwners() {
        return shopOwners;
    }

    public void setShopOwners(List<SysUser> shopOwners) {
        this.shopOwners = shopOwners;
    }

    public List<SysUser> getFinances() {
        return finances;
    }

    public void setFinances(List<SysUser> finances) {
        this.finances = finances;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
