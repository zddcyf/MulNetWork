package com.mul.network.http.callback;

import com.mul.network.http.response.ApiResponse;

/**
 * @ProjectName: MulNetWork
 * @Package: com.mul.network.http.callback
 * @ClassName: JsonCallBack
 * @Author: zdd
 * @CreateDate: 2020/7/15 9:24
 * @Description: java类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/7/15 9:24
 * @UpdateRemark: 更新说明
 * @Version: 1.0.0
 */
public abstract class JsonCallBack<B> {
    /**
     * 接口成功
     *
     * @param mResponse
     */
    public void onSuccess(ApiResponse<B> mResponse) {

    }

    /**
     * 接口失败
     *
     * @param mResponse
     */
    public void onError(ApiResponse<B> mResponse) {

    }

    /**
     * 数据缓存
     *
     * @param mResponse
     */
    public void onCacheSuccess(ApiResponse<B> mResponse) {
    }
}