package com.mul.network.status;

/**
 * @ProjectName: MulNetWork
 * @Package: com.mul.network.status
 * @ClassName: NetWorkStatus
 * @Author: zdd
 * @CreateDate: 2020/11/19 14:57:55
 * @Description: java类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/19 14:57:55
 * @UpdateRemark: 更新说明
 * @Version: 1.0.0
 */
public class NetWorkStatus {
    /**
     * 2 = 2g
     * 3 = 3g
     * 4 = 4g
     * 5 = 5g
     * 6 = wifi
     * 7 = 以太网
     * 8 = 无网络
     */
    public @NetWorkType
    int netWorkType = NetWorkType.NW_UNKNOWN; // 网络类型

    public String netWorkTypeStr = "无网络";
    public String wifiName = "";
    public String ip = "";
}
