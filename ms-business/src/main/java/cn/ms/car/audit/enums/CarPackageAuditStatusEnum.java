package cn.ms.car.audit.enums;

/**
 * 套餐信息审核状态
 */
public enum CarPackageAuditStatusEnum {

    // 状态：审核中/审核拒绝/审核通过/撤销审核

    AUDITING("审核中"),
    REJECTED("审核拒绝"),
    PASSED("审核通过"),
    CANCELED("撤销审核");

    private String name;

    CarPackageAuditStatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
