package cn.ms.car.common.utils;

import java.util.regex.Pattern;

/**
 * 规则校验工具类
 */
public class ValidateUtils {

    public static final String PHONE_REGEX = "1[3-9][0-9]\\d{8}";
    public static final String LICENSE_PLATE_REGEX = "[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}";

    /**
     * 手机号正则校验
     *
     * @param phone 手机号
     * @return 规则是否匹配
     */
    public static boolean validatePhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return false;
        }

        return Pattern.matches(PHONE_REGEX, phone);
    }

    /**
     * 车牌号码正则校验
     *
     * @param licensePlate 车牌号码
     * @return 规则是否匹配
     */
    public static boolean validateLicensePlate(String licensePlate) {
        if (StringUtils.isEmpty(licensePlate)) {
            return false;
        }

        return Pattern.matches(LICENSE_PLATE_REGEX, licensePlate);
    }

    public static void main(String[] args) {
        System.out.println(validatePhone("13512344564"));
        System.out.println(validatePhone("11512344564"));
    }
}
