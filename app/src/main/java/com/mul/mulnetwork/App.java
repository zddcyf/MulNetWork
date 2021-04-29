package com.mul.mulnetwork;

import android.app.Application;

import com.mul.network.http.ApiService;
import com.mul.network.http.convert.JsonConvert;
import com.mul.network.status.NetWorkManager;

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
        ApiService.obtain()
                .setCode("code")
                .setCode(200)
                .setReadTimeOut(5)
                .setWriteTimeOut(5)
                .setConnectTimeOut(5)
                .setConvert(new JsonConvert())
                .setBaseUrl("http://www.baidu.com/")
                .create();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        NetWorkManager.getInstance().unregisterReceiver();
    }
}
