package cn.ms.car.appointment.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import cn.ms.car.common.annotation.Excel;
import cn.ms.car.common.core.domain.BaseEntity;

/**
 * 结算单明细对象 bus_statement_item
 * 
 * @author ms
 * @date 2023-04-02
 */
public class BusStatementItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 结算单ID */
    @Excel(name = "结算单ID")
    private Long statementId;

    /** 服务项明细ID */
    @Excel(name = "服务项明细ID")
    private Long itemId;

    /** 服务项明细名称 */
    @Excel(name = "服务项明细名称")
    private String itemName;

    /** 服务项价格 */
    @Excel(name = "服务项价格")
    private BigDecimal itemPrice;

    /** 购买数量 */
    @Excel(name = "购买数量")
    private Long itemQuantity;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setStatementId(Long statementId) 
    {
        this.statementId = statementId;
    }

    public Long getStatementId() 
    {
        return statementId;
    }
    public void setItemId(Long itemId) 
    {
        this.itemId = itemId;
    }

    public Long getItemId() 
    {
        return itemId;
    }
    public void setItemName(String itemName) 
    {
        this.itemName = itemName;
    }

    public String getItemName() 
    {
        return itemName;
    }
    public void setItemPrice(BigDecimal itemPrice) 
    {
        this.itemPrice = itemPrice;
    }

    public BigDecimal getItemPrice() 
    {
        return itemPrice;
    }
    public void setItemQuantity(Long itemQuantity) 
    {
        this.itemQuantity = itemQuantity;
    }

    public Long getItemQuantity() 
    {
        return itemQuantity;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("statementId", getStatementId())
            .append("itemId", getItemId())
            .append("itemName", getItemName())
            .append("itemPrice", getItemPrice())
            .append("itemQuantity", getItemQuantity())
            .toString();
    }
}
