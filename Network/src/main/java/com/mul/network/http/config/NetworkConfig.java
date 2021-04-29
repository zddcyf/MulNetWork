package com.mul.network.http.config;

/**
 * @ProjectName: MulNetWork
 * @Package: com.mul.network.http.config
 * @ClassName: ErrorConfig
 * @Author: zdd
 * @CreateDate: 2021/3/8 20:00:01
 * @Description: java类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/3/8 20:00:01
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public interface NetworkConfig {
    String LOG_HTTP = "HttpData";
    String LOG_URL = "HttpDataURL";
    /**
     * 同步请求
     */
    String EXECUTE = "execute";
    /**
     * 异步请求
     */
    String ENQUEUE = "enqueue";

    /**
     * 接口请求出错
     */
    String ERROR_MESSAGE_101 = "接口请求出错";
    int ERROR_STATUS_101 = 101;
    /**
     * 请先配置域名以及ip地址
     */
    String ERROR_MESSAGE_102 = "请先配置域名或者ip地址";
    int ERROR_STATUS_102 = 102;
    /**
     * 缓存失败
     */
    String ERROR_MESSAGE_103 = "缓存失败";
    int ERROR_STATUS_103 = 103;
    /**
     * 同步接口请求出错
     */
    String ERROR_MESSAGE_104 = "同步接口请求出错";
    int ERROR_STATUS_104 = 104;
}
