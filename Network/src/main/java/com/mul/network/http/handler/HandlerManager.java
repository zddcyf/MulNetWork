package com.mul.network.http.handler;

import android.os.Handler;
import android.os.Looper;

/**
 * @ProjectName: MulNetWork
 * @Package: com.mul.network.http.handler
 * @ClassName: HandlerManager
 * @Author: zdd
 * @CreateDate: 2021/2/7 9:28:06
 * @Description: java类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/2/7 9:28:06
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class HandlerManager {
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private HandlerManager() {
    }

    private static class HandlerManagerSington {
        private static final HandlerManager HANDLER_MANAGER = new HandlerManager();
    }

    public static HandlerManager getInstance() {
        return HandlerManagerSington.HANDLER_MANAGER;
    }

    public Handler getHandler() {
        return mHandler;
    }
}
