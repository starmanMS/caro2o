package cn.ms.car.audit.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import cn.ms.car.common.annotation.Excel;
import cn.ms.car.common.core.domain.BaseEntity;

/**
 * 审核信息对象 bus_car_package_audit
 * 
 * @author ms
 * @date 2023-04-08
 */
public class BusCarPackageAudit extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    public static final String DONE_LIST = "DONE";
    public static final String TODO_LIST = "TODO";

    /** 主键 */
    private Long id;

    /** 套餐 id */
    private Long serviceItemId;

    /** 套餐名称 */
    @Excel(name = "套餐名称")
    private String serviceItemName;

    /** 套餐备注 */
    @Excel(name = "套餐备注")
    private String serviceItemInfo;

    /** 套餐价格 */
    @Excel(name = "套餐价格")
    private BigDecimal serviceItemPrice;

    /** 流程实例id */
    private String instanceId;

    /** 创建者 */
    private String creatorId;

    /** 审核备注 */
    @Excel(name = "审核备注")
    private String info;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setServiceItemId(Long serviceItemId) 
    {
        this.serviceItemId = serviceItemId;
    }

    public Long getServiceItemId() 
    {
        return serviceItemId;
    }
    public void setServiceItemName(String serviceItemName) 
    {
        this.serviceItemName = serviceItemName;
    }

    public String getServiceItemName() 
    {
        return serviceItemName;
    }
    public void setServiceItemInfo(String serviceItemInfo) 
    {
        this.serviceItemInfo = serviceItemInfo;
    }

    public String getServiceItemInfo() 
    {
        return serviceItemInfo;
    }
    public void setServiceItemPrice(BigDecimal serviceItemPrice) 
    {
        this.serviceItemPrice = serviceItemPrice;
    }

    public BigDecimal getServiceItemPrice() 
    {
        return serviceItemPrice;
    }
    public void setInstanceId(String instanceId) 
    {
        this.instanceId = instanceId;
    }

    public String getInstanceId() 
    {
        return instanceId;
    }
    public void setCreatorId(String creatorId) 
    {
        this.creatorId = creatorId;
    }

    public String getCreatorId() 
    {
        return creatorId;
    }
    public void setInfo(String info) 
    {
        this.info = info;
    }

    public String getInfo() 
    {
        return info;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("serviceItemId", getServiceItemId())
            .append("serviceItemName", getServiceItemName())
            .append("serviceItemInfo", getServiceItemInfo())
            .append("serviceItemPrice", getServiceItemPrice())
            .append("instanceId", getInstanceId())
            .append("creatorId", getCreatorId())
            .append("info", getInfo())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .toString();
    }
}
