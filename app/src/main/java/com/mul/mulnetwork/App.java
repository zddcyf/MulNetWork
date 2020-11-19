package com.mul.mulnetwork;

import android.app.Application;

import com.mul.network.NetWorkManager;

/**
 * @ProjectName: NetworkUtils
 * @Package: com.mul.network
 * @ClassName: App
 * @Author: zdd
 * @CreateDate: 2020/11/19 15:30:59
 * @Description: java类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/19 15:30:59
 * @UpdateRemark: 更新说明
 * @Version: 1.0.0
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NetWorkManager.getInstance().init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        NetWorkManager.getInstance().unregisterReceiver();
    }
}
