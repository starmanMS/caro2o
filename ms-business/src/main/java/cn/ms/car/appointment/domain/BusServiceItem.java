package cn.ms.car.appointment.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import cn.ms.car.common.annotation.Excel;
import cn.ms.car.common.core.domain.BaseEntity;

/**
 * 服务项对象 bus_service_item
 * 
 * @author ms
 * @date 2023-03-30
 */
public class BusServiceItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private Long id;

    /** 服务项名称 */
    @Excel(name = "服务项名称")
    private String name;

    /** 服务项原价 */
    @Excel(name = "服务项原价")
    private BigDecimal originalPrice;

    /** 服务项折扣价 */
    @Excel(name = "服务项折扣价")
    private BigDecimal discountPrice;

    /** 是否套餐 */
    @Excel(name = "是否套餐")
    private Integer carPackage;

    /** 备注信息 */
    @Excel(name = "备注信息")
    private String info;

    /** 服务分类 */
    @Excel(name = "服务分类")
    private Integer serviceCatalog;

    /** 审核状态 */
    @Excel(name = "审核状态")
    private Integer auditStatus;

    /** 上架状态 */
    @Excel(name = "上架状态")
    private Integer saleStatus;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setOriginalPrice(BigDecimal originalPrice) 
    {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getOriginalPrice() 
    {
        return originalPrice;
    }
    public void setDiscountPrice(BigDecimal discountPrice) 
    {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getDiscountPrice() 
    {
        return discountPrice;
    }
    public void setCarPackage(Integer carPackage) 
    {
        this.carPackage = carPackage;
    }

    public Integer getCarPackage() 
    {
        return carPackage;
    }
    public void setInfo(String info) 
    {
        this.info = info;
    }

    public String getInfo() 
    {
        return info;
    }
    public void setServiceCatalog(Integer serviceCatalog) 
    {
        this.serviceCatalog = serviceCatalog;
    }

    public Integer getServiceCatalog() 
    {
        return serviceCatalog;
    }
    public void setAuditStatus(Integer auditStatus) 
    {
        this.auditStatus = auditStatus;
    }

    public Integer getAuditStatus() 
    {
        return auditStatus;
    }
    public void setSaleStatus(Integer saleStatus) 
    {
        this.saleStatus = saleStatus;
    }

    public Integer getSaleStatus() 
    {
        return saleStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("originalPrice", getOriginalPrice())
            .append("discountPrice", getDiscountPrice())
            .append("carPackage", getCarPackage())
            .append("info", getInfo())
            .append("createTime", getCreateTime())
            .append("serviceCatalog", getServiceCatalog())
            .append("auditStatus", getAuditStatus())
            .append("saleStatus", getSaleStatus())
            .toString();
    }
}
