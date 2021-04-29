package com.mul.network.http.convert;

import java.lang.reflect.Type;

/**
 * @ProjectName: MulNetWork
 * @Package: com.mul.network.http.convert
 * @ClassName: Convert
 * @Author: zdd
 * @CreateDate: 2020/7/15 11:12
 * @Description: java类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/7/15 11:12
 * @UpdateRemark: 更新说明
 * @Version: 1.0.0
 */
public interface Convert<B> {
    B convert(String response, Type type, String url);

    B convert(String response, Class clz, String url);
}