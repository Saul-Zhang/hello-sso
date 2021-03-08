package com.zs.hellossoserver.utils;

import org.apache.tomcat.util.codec.binary.Base64;

import java.security.MessageDigest;

/**
 * @author ZhangSong
 */
public class MD5Utils {
    public static String getMD5Str(String strValue) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return Base64.encodeBase64String(md5.digest(strValue.getBytes()));
    }

    public static void main(String[] args) {
        try {
            String md5 = getMD5Str("aaa112");
            System.out.println(md5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
