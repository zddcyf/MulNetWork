package com.mul.network;

import androidx.annotation.IntDef;

/**
 * @ProjectName: NetworkUtils
 * @Package: com.mul.network
 * @ClassName: NetWorkType
 * @Author: zdd
 * @CreateDate: 2020/11/19 14:58:28
 * @Description: java类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/19 14:58:28
 * @UpdateRemark: 更新说明
 * @Version: 1.0.0
 */
@IntDef({NetWorkType.NW_2G
        , NetWorkType.NW_3G
        , NetWorkType.NW_4G
        , NetWorkType.NW_5G
        , NetWorkType.NW_WIFI
        , NetWorkType.NW_ETHERNET
        , NetWorkType.NW_UNKNOWN})
@interface NetWorkType {
    int NW_2G = 2; // 2G网
    int NW_3G = 3; // 3G网
    int NW_4G = 4; // 4G网
    int NW_5G = 5; // 5G网
    int NW_WIFI = 6; // wifi网
    int NW_ETHERNET = 7; // 以太网
    int NW_UNKNOWN = 8; // 无网络
}
