package com.mul.network.status;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * @ProjectName: MulNetWork
 * @Package: com.mul.network.status
 * @ClassName: NetStateUtils
 * @Author: zdd
 * @CreateDate: 2020/11/19 15:05:36
 * @Description: java类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/19 15:05:36
 * @UpdateRemark: 更新说明
 * @Version: 1.0.0
 */
class NetStateUtils {
    private static final NetWorkStatus mNetWorkStatus = new NetWorkStatus();

    public static NetWorkStatus getNetState(Context context) {
        //结果返回值
        //获取手机所有连接管理对象
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        //获取NetworkInfo对象
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        //NetworkInfo对象为空 则代表没有网络
        if (networkInfo == null) {
            mNetWorkStatus.netWorkType = NetWorkType.NW_UNKNOWN;
            mNetWorkStatus.netWorkTypeStr = "无网络";
            return mNetWorkStatus;
        }
        //否则 NetworkInfo对象不为空 则获取该networkInfo的类型
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            //WIFI
            mNetWorkStatus.netWorkType = NetWorkType.NW_WIFI;
            mNetWorkStatus.netWorkTypeStr = "wifi";
            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiMgr.getConnectionInfo();
            mNetWorkStatus.wifiName = info != null ? info.getSSID() : null;
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService
                    (Context.TELEPHONY_SERVICE);
            //3G   联通的3G为UMTS或HSDPA 电信的3G为EVDO
            if (nSubType == TelephonyManager.NETWORK_TYPE_LTE
                    && !telephonyManager.isNetworkRoaming()) {
                mNetWorkStatus.netWorkType = NetWorkType.NW_4G;
                mNetWorkStatus.netWorkTypeStr = "4g";
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                    || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    && !telephonyManager.isNetworkRoaming()) {
                mNetWorkStatus.netWorkType = NetWorkType.NW_3G;
                mNetWorkStatus.netWorkTypeStr = "3g";
                //2G 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS
                    || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
                    || nSubType == TelephonyManager.NETWORK_TYPE_CDMA
                    && !telephonyManager.isNetworkRoaming()) {
                mNetWorkStatus.netWorkType = NetWorkType.NW_2G;
                mNetWorkStatus.netWorkTypeStr = "2g";
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_NR) {
                mNetWorkStatus.netWorkType = NetWorkType.NW_5G;
                mNetWorkStatus.netWorkTypeStr = "5g";
            } else {
                mNetWorkStatus.netWorkType = NetWorkType.NW_2G;
                mNetWorkStatus.netWorkTypeStr = "2g";
            }
        } else if (nType == ConnectivityManager.TYPE_ETHERNET) {
            mNetWorkStatus.netWorkType = NetWorkType.NW_ETHERNET;
            mNetWorkStatus.netWorkTypeStr = "以太网";
        }
        return mNetWorkStatus;
    }

    public static NetWorkStatus getNetWorkStatus() {
        return mNetWorkStatus;
    }
}
