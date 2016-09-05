package com.example.administrator.civilaviation.util;

/**
 * 用来存放后台地址信息
 */
public class GetServerUrl {
//    static String url = "http://192.168.1.100:8080/PersonalHomePage";

    // apk的下载地址
    static String apk_url = "https://github.com/mxzs1314/.github.io/raw/master/app/app-release.apk";

    static String url = "https://github.com/mxzs1314/.github.io/raw/master";

    public static String getUrl() {
        return url;
    }

    public static String getApk_url() {
        return apk_url;
    }
}
