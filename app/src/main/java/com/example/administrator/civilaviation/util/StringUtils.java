package com.example.administrator.civilaviation.util;


/**
 *
 */
public class StringUtils {

    public static boolean isNetUrl(String url) {
        return url.contains("http");
//        return url.substring(0, 3).equals("");
    }
}
