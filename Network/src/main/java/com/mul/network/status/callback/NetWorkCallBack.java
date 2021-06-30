package com.mul.network.status.callback;

import com.mul.network.status.config.NetWorkStatus;

/**
 * @ProjectName: MulNetWork
 * @Package: com.mul.network.status
 * @ClassName: NetWorkListener
 * @Author: zdd
 * @CreateDate: 2020/11/19 15:27:56
 * @Description: java类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/19 15:27:56
 * @UpdateRemark: 更新说明
 * @Version: 1.0.0
 */
public interface NetWorkCallBack {
    /**
     * 网络状态发生改变
     *
     * @param mNetWorkStatus 网络状态
     */
    void onNetWorkChange(NetWorkStatus mNetWorkStatus);
}
