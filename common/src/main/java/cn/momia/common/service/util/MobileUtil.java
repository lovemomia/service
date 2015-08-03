package cn.momia.common.service.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class MobileUtil {
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[0-9]{10}$");

    public static boolean isInvalidMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) return true;
        return !MOBILE_PATTERN.matcher(mobile).find();
    }

    public static String encrypt(String mobile) {
        if (isInvalidMobile(mobile)) return mobile;
        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }
}
