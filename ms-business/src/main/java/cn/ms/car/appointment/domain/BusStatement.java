package cn.ms.car.appointment.domain;

import cn.ms.car.common.annotation.Excel;
import cn.ms.car.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 结算单对象 bus_statement
 *
 * @author ms
 * @date 2023-04-01
 */
public class BusStatement extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 消费中
     */
    public static final Integer CONSUMER = 0;
    /**
     * 已支付
     */
    public static final Integer PAID = 1;

    /**
     * $column.columnComment
     */
    private Long id;

    /**
     * 客户姓名
     */
    @Excel(name = "客户姓名")
    private String customerName;

    /**
     * 客户联系方式
     */
    private String customerPhone;

    /**
     * 实际到店时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "实际到店时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date actualArrivalTime;

    /**
     * 车牌号码
     */
    private String licensePlate;

    /**
     * 汽车类型
     */
    private String carSeries;

    /**
     * 服务类型
     */
    @Excel(name = "服务类型")
    private Integer serviceType;

    /**
     * 预约用户
     */
    @Excel(name = "预约用户")
    private Long appointmentId;

    /**
     * 结算状态
     */
    @Excel(name = "结算状态")
    private Integer status;

    /**
     * 收款时间
     */
    private Date payTime;

    /**
     * 收款人id
     */
    private Long payeeId;

    /**
     * 总消费金额
     */
    @Excel(name = "总消费金额")
    private BigDecimal totalAmount;

    /**
     * 服务项数量
     */
    @Excel(name = "服务项数量")
    private BigDecimal totalQuantity;

    /**
     * 折扣金额
     */
    @Excel(name = "折扣金额")
    private BigDecimal discountAmount;

    /**
     * 备注信息
     */
    @Excel(name = "备注信息")
    private String info;

    /**
     * $column.columnComment
     */
    private Integer isDelete;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setActualArrivalTime(Date actualArrivalTime) {
        this.actualArrivalTime = actualArrivalTime;
    }

    public Date getActualArrivalTime() {
        return actualArrivalTime;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setCarSeries(String carSeries) {
        this.carSeries = carSeries;
    }

    public String getCarSeries() {
        return carSeries;
    }

    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }

    public Integer getServiceType() {
        return serviceType;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayeeId(Long payeeId) {
        this.payeeId = payeeId;
    }

    public Long getPayeeId() {
        return payeeId;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalQuantity(BigDecimal totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("customerName", getCustomerName())
                .append("customerPhone", getCustomerPhone())
                .append("actualArrivalTime", getActualArrivalTime())
                .append("licensePlate", getLicensePlate())
                .append("carSeries", getCarSeries())
                .append("serviceType", getServiceType())
                .append("appointmentId", getAppointmentId())
                .append("status", getStatus())
                .append("payTime", getPayTime())
                .append("payeeId", getPayeeId())
                .append("totalAmount", getTotalAmount())
                .append("totalQuantity", getTotalQuantity())
                .append("discountAmount", getDiscountAmount())
                .append("createTime", getCreateTime())
                .append("info", getInfo())
                .append("isDelete", getIsDelete())
                .toString();
    }
}
