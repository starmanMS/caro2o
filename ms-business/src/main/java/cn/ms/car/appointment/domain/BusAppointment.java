package cn.ms.car.appointment.domain;

import cn.ms.car.common.annotation.Excel;
import cn.ms.car.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 养修信息预约对象 bus_appointment
 *
 * @author ms
 * @date 2023-03-27
 */
public class BusAppointment extends BaseEntity {
    private static final long serialVersionUID = 1L;

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
    @Excel(name = "客户联系方式")
    private String customerPhone;

    /**
     * 预约时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "预约时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date appointmentTime;

    /**
     * 实际到店时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "实际到店时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date actualArrivalTime;

    /**
     * 车牌号码
     */
    @Excel(name = "车牌号码")
    private String licensePlate;

    /**
     * 汽车类型
     */
    @Excel(name = "汽车类型")
    private String carSeries;

    /**
     * 服务类型【维修0/保养1】
     */
    @Excel(name = "服务类型", readConverterExp = "0=维修,1=保养")
    private Integer serviceType;

    /**
     * 备注信息
     */
    @Excel(name = "备注信息")
    private String info;

    /**
     * 状态【预约中0/已到店1/用户取消2/超时取消3/已结算4/已支付5】
     */
    @Excel(name = "状态", readConverterExp = "0=预约中,1=已到店,2=用户取消,3=超时取消,4=已结算,5=已支付")
    private Integer status;

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

    public void setAppointmentTime(Date appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public Date getAppointmentTime() {
        return appointmentTime;
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

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("customerName", getCustomerName())
                .append("customerPhone", getCustomerPhone())
                .append("appointmentTime", getAppointmentTime())
                .append("actualArrivalTime", getActualArrivalTime())
                .append("licensePlate", getLicensePlate())
                .append("carSeries", getCarSeries())
                .append("serviceType", getServiceType())
                .append("createTime", getCreateTime())
                .append("info", getInfo())
                .append("status", getStatus())
                .toString();
    }
}
