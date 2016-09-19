package com.cc.cachedemo.utils;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by cc on 2016/9/17.
 * 网络权限相关类
 */
public class NetworkUtils {

    /**
     * 判断系统版本，是否需要申请6.0权限
     *
     * @return
     */
    public static boolean isNeedRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 权限检查
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.checkPermission(permission,
                context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 启动网络连接
     *
     * @param context
     */
    @SuppressLint("NewApi")
    public static void openNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifi = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE).getState();
        Intent intent = null;
        // 判断手机系统的版本 即API大于10 就是3.0或以上版本
        if (Build.VERSION.SDK_INT > 10) {
            // 启动3G配置界面
            intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            if (wifi != NetworkInfo.State.CONNECTED || wifi != NetworkInfo.State.CONNECTING) {
                // 启动WIFI配置界面
                intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            }
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings",
                    "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否处于联网状态
     *
     * @param context
     * @return
     */
    public synchronized static boolean isNetwork(Context context) {
        boolean bool = false;
        try {
            if (checkPermission(context, "android.permission.INTERNET")) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager
                        .getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected())
                    bool = networkInfo.isAvailable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }

    /**
     * 网络类型
     *
     * @param context
     * @return "WIFI,2G,3G,4G" or "OFFLINE"
     */
    public static String getNetworkType(Context context) {
        String strNetworkType = "";

        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo == null) {
                strNetworkType = "OFFLINE";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strNetworkType;
    }

    /**
     * 获取手机内网IP
     *
     * @return
     */
    public static String getIpAddress(Context context) {
        String ip = "";
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifi = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState();
        NetworkInfo.State mobile = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE).getState();
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipInt = wifiInfo.getIpAddress();
            ip = (ipInt & 0xFF) + "." + (ipInt >> 8 & 0xFF) + "."
                    + (ipInt >> 16 & 0xFF) + "." + (ipInt >> 24 & 0xFF);
        }
        if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface
                        .getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf
                            .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            ip = inetAddress.getHostAddress().toString();
                        }
                    }
                }
            } catch (SocketException ex) {
            }
        }
        return ip;
    }

    /**
     * 获取APP上行流量
     *
     * @param context
     * @param packageName
     * @return
     */
    public static long getUidTxBytes(Context context, String packageName) {
        long sendByte = 0L;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(packageName,
                    ApplicationInfo.FLAG_SYSTEM);
            sendByte = TrafficStats.getUidTxBytes(info.uid);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return sendByte;
    }

    /**
     * 获取APP下行流量
     *
     * @param context
     * @param packageName
     * @return
     */
    public static long getUidRxBytes(Context context, String packageName) {
        long receiveByte = 0L;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(packageName,
                    ApplicationInfo.FLAG_SYSTEM);
            receiveByte = TrafficStats.getUidRxBytes(info.uid);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return receiveByte;
    }
}
