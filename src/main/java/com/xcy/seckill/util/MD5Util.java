package com.xcy.seckill.util;


import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

public class MD5Util {
    private static final String salt = "1a2b3c4d";


    /**
     *第一次MD5加密，用户密码加密后进行传输，固定盐
     * @param inputPass
     * @return
     */
    public static String inputPassToFormPass(String inputPass){
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * 第二次MD5加密，服务器得到表单传来的密码后进行加密后，用于存储数据库，随机盐
     * @param formPass
     * @param salt
     * @return
     */
    public static String formPassToDbPass(String formPass,String salt){
        String str = ""+salt.charAt(0)+salt.charAt(2) + formPass +salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * 合并两次加密
     * @param inputPass
     * @param salt
     * @return
     */
    public static String inputPassToDbPass(String inputPass,String salt){
        String formPass = inputPassToFormPass(inputPass);
        return formPassToDbPass(formPass,salt);
    }



    @Test
    public void test(){
        String fromPass = inputPassToFormPass("82357366qwe");
        System.out.println(fromPass);
        System.out.println(formPassToDbPass(fromPass,"gt2h53kd4k"));

    }


    private static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }
}
