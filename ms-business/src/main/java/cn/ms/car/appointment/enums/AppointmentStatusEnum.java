package cn.ms.car.appointment.enums;

/**
 * 预约状态枚举类
 */
public enum AppointmentStatusEnum {

    // 状态：预约中0/已到店1/用户取消2/超时取消3/已结算4/已支付5
    // 枚举实例数组 = [APPOINTING, ARRIVAL, CANCEL, TIMEOUT_CANCEL, STATEMENT, PAID]
    // 枚举实例对象.name() == APPOINTING
    // 枚举实例对象.ordinal() == 索引

    APPOINTING("预约中"),
    ARRIVAL("已到店"),
    CANCEL("用户取消"),
    TIMEOUT_CANCEL("超时取消"),
    STATEMENT("已结算"),
    PAID("已支付");

    private String name;

    AppointmentStatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static void main(String[] args) {
        AppointmentStatusEnum instance = AppointmentStatusEnum.PAID;
        System.out.println("instance.name() = " + instance.name());
        System.out.println("instance.getName() = " + instance.getName());
        System.out.println("instance.ordinal() = " + instance.ordinal());
    }
}
