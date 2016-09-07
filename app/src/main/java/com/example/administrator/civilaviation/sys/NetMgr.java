package com.example.administrator.civilaviation.sys;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 检测手机wifi和数据流量是否开启
 */
public class NetMgr {
    // 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载
    private static NetMgr instance = null;

    // 私有构造方法，防止被实例化
    private NetMgr() {

    }

    // 懒汉式，静态工程方法，创建实例
    public static NetMgr getInstance() {
        if (instance == null) {
            instance = new NetMgr();
        }
        return instance;
    }

    // 判断无线网是否可用
    public boolean isWifiConnected(Context context) {
        ConnectivityManager connMgr
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetInfo != null && wifiNetInfo.isConnected();
    }

}
