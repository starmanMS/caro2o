package cn.ms.car.appointment.enums;

/**
 * 服务项审核状态
 */
public enum ServiceItemAuditStatusEnum {

    // 状态：初始化/审核中/审核通过/审核拒绝/无需审核

    INITIALING("初始化中"),
    AUDITING("审核中"),
    PASSED("审核通过"),
    REJECTED("审核拒绝"),
    NONE("无需审核");

    private String name;

    ServiceItemAuditStatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
