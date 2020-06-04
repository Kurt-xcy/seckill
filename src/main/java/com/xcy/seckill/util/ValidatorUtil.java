package com.xcy.seckill.util;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
    public static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String src){
        if(src.isEmpty()||src==null){
            return false;
        }
        Matcher m = mobile_pattern.matcher(src);
        return m.matches();
    }


}
