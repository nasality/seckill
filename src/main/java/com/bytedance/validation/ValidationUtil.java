package com.bytedance.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern mobile_pattern = Pattern.compile("/^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$/");
    public static boolean isMobile(String mobile) {
        if (mobile.length() != 11) {
            return false;
        }
        Matcher matcher = mobile_pattern.matcher(mobile);
        return matcher.matches();
    }
}
