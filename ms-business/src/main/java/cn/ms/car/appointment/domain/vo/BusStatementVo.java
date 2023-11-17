package cn.ms.car.appointment.domain.vo;

import cn.ms.car.appointment.domain.BusStatementItem;
import cn.ms.car.common.core.domain.BaseEntity;
import cn.ms.car.common.utils.ValidateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author ms
 * @date 2023-03-27
 */
public class BusStatementVo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /* 结算单 id */
    private Long id;

    /**
     * 客户姓名
     */
    @NotNull(message = "客户姓名不能为空")
    private String customerName;

    /**
     * 客户联系方式
     */
    @NotNull(message = "客户联系方式不能为空")
    @Pattern(regexp = ValidateUtils.PHONE_REGEX, message = "手机格式错误")
    private String customerPhone;

    /**
     * 到店时间
     */
    @NotNull(message = "到店时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actualArrivalTime;

    /**
     * 车牌号码
     */
    @NotNull(message = "车牌号码不能为空")
    @Pattern(regexp = ValidateUtils.LICENSE_PLATE_REGEX, message = "车牌号格式错误")
    private String licensePlate;

    /**
     * 汽车类型
     */
    @NotNull(message = "汽车类型不能为空")
    private String carSeries;

    /**
     * 服务类型【维修0/保养1】
     */
    @NotNull(message = "服务类型不能为空")
    private Integer serviceType;

    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;

    /**
     * 结算单明细
     */
    private List<BusStatementItem> items; // 结算单明细

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public List<BusStatementItem> getItems() {
        return items;
    }

    public void setItems(List<BusStatementItem> items) {
        this.items = items;
    }

    /**
     * 备注信息
     */
    private String info;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getActualArrivalTime() {
        return actualArrivalTime;
    }

    public void setActualArrivalTime(Date actualArrivalTime) {
        this.actualArrivalTime = actualArrivalTime;
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

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("customerName", getCustomerName())
                .append("customerPhone", getCustomerPhone())
                .append("actualArrivalTime", getActualArrivalTime())
                .append("licensePlate", getLicensePlate())
                .append("carSeries", getCarSeries())
                .append("serviceType", getServiceType())
                .append("createTime", getCreateTime())
                .append("info", getInfo())
                .toString();
    }
}
