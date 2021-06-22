package com.mul.network.status;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import com.mul.utils.log.LogExceptionResult;
import com.mul.utils.log.LogUtil;
import com.mul.utils.manager.GlobalManager;

/**
 * @ProjectName: MulNetWork
 * @Package: com.mul.network.status
 * @ClassName: NetWorkManager
 * @Author: zdd
 * @CreateDate: 2020/11/19 14:43:00
 * @Description: 网络状态管理器
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/19 14:43:00
 * @UpdateRemark: 更新说明
 * @Version: 1.0.0
 */
public class NetWorkManager {
    public static final String TAG = "NetWorkManager";
    private Application mApplication;
    private static final String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private NetWorkListener mNetWorkListener;
    private ConnectivityManager mConnectivityManager;

    private NetWorkManager() {

    }

    private static class NetWorkManagerSingleton {
        private static final NetWorkManager NET_WORK_MANAGER = new NetWorkManager();
    }

    public static NetWorkManager getInstance() {
        return NetWorkManagerSingleton.NET_WORK_MANAGER;
    }

    public void init() {
        init(GlobalManager.INSTANCE.app);
    }

    public void init(Application mApplication) {
        if (null == mApplication) {
            throw new NullPointerException("mApplication can not be null");
        }
        this.mApplication = mApplication;
        initMonitor();
    }

    /**
     * 初始化监听方式
     */
    private void initMonitor() {
        mConnectivityManager = (ConnectivityManager) mApplication.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//API 大于26时
            mConnectivityManager.registerDefaultNetworkCallback(mNetworkCallback);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//API 大于21时
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            NetworkRequest request = builder.build();
            mConnectivityManager.registerNetworkCallback(request, mNetworkCallback);
        } else {//低版本
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ANDROID_NET_CHANGE_ACTION);
            mApplication.registerReceiver(receiver, intentFilter);
        }
    }

    /**
     * 反注册广播
     */
    public void unregisterReceiver() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//API 大于26时
                mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//API 大于21时
                mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
            } else {//低版本
                mApplication.unregisterReceiver(receiver);
            }
        } catch (Exception mE) {
            LogUtil.saveE(TAG, LogExceptionResult.getException(mE));
        }
    }

    /**
     * 21以及26以上网络状态监听
     */
    private final ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() {
        /**
         * 网络可用的回调连接成功
         */
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            postNetState(NetStateUtils.getNetState(mApplication));
        }

        /**
         * 在网络连接正常的情况下，丢失数据会有回调 即将断开时
         */
        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
        }

        /**
         * 网络不可用时调用和onAvailable成对出现
         */
        @Override
        public void onLost(Network network) {
            super.onLost(network);
            NetWorkStatus mNetWorkStatus = new NetWorkStatus();
            mNetWorkStatus.netWorkType = NetWorkType.NW_UNKNOWN;
            mNetWorkStatus.netWorkTypeStr = "无网络";
            postNetState(mNetWorkStatus);
        }

        @Override
        public void onUnavailable() {
            super.onUnavailable();
        }

        /**
         * 网络功能更改 满足需求时调用
         */
        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
        }

        /**
         * 网络连接属性修改时调用
         */
        @Override
        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
        }

        /**
         * 当对指定网络的访问被阻止或取消阻止时调用。
         */
        @Override
        public void onBlockedStatusChanged(Network network, boolean blocked) {
            super.onBlockedStatusChanged(network, blocked);
        }
    };

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION)) {
                postNetState(NetStateUtils.getNetState(context));
            }
        }
    };

    private void postNetState(NetWorkStatus mNetWorkStatus) {
        if (null != mNetWorkListener) {
            mNetWorkListener.onNetWorkChange(mNetWorkStatus);
        }
    }

    public void setNetWorkListener(NetWorkListener mNetWorkListener) {
        this.mNetWorkListener = mNetWorkListener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && null != mApplication) {//API 大于26时
            postNetState(NetStateUtils.getNetState(mApplication));
        }
    }
}
