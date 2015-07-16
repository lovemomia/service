package cn.momia.common.misc;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class ValidateUtil {
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[0-9]{10}$");

    public static boolean isInvalidMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) return true;
        return !MOBILE_PATTERN.matcher(mobile).find();
    }

    public static boolean notIn(String value, String ... args) {
        if (StringUtils.isBlank(value)) return true;
        for (String arg : args) {
            if (value.equals(arg)) return false;
        }

        return true;
    }
}
