package cn.ms.car.appointment.domain.vo;

import cn.ms.car.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 服务项对象
 *
 * @author ms
 * @date 2023-03-30
 */
public class BusServiceItemVo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    public static final Integer PACKAGE_YES = 1;
    public static final Integer PACKAGE_NO = 0;

    public static final Integer SALE_STATUS_NO = 0;
    public static final Integer SALE_STATUS_YES = 1;

    /**
     * 编号
     */
    private Long id;

    /**
     * 服务项名称
     */
    @NotNull(message = "服务项名称不能为空")
    private String name;

    /**
     * 服务项原价
     */
    @NotNull(message = "服务项原价不能为空")
    @Min(value = 0, message = "服务原价不能小于 0")
    private BigDecimal originalPrice;

    /**
     * 服务项折扣价
     */
    @NotNull(message = "服务项折扣价不能为空")
    @Min(value = 0, message = "服务折扣价不能小于 0")
    private BigDecimal discountPrice;

    /**
     * 是否套餐
     */
    @NotNull(message = "是否套餐不能为空")
    private Integer carPackage;

    /**
     * 备注信息
     */
    @NotNull(message = "备注信息不能为空")
    private String info;

    /**
     * 服务分类
     */
    @NotNull(message = "服务分类不能为空")
    private Integer serviceCatalog;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setCarPackage(Integer carPackage) {
        this.carPackage = carPackage;
    }

    public Integer getCarPackage() {
        return carPackage;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setServiceCatalog(Integer serviceCatalog) {
        this.serviceCatalog = serviceCatalog;
    }

    public Integer getServiceCatalog() {
        return serviceCatalog;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("originalPrice", getOriginalPrice())
                .append("discountPrice", getDiscountPrice())
                .append("carPackage", getCarPackage())
                .append("info", getInfo())
                .append("createTime", getCreateTime())
                .append("serviceCatalog", getServiceCatalog())
                .toString();
    }
}
