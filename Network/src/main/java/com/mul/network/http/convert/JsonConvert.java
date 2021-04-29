package com.mul.network.http.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mul.utils.DataUtils;

import java.lang.reflect.Type;

/**
 * @ProjectName: MulNetWork
 * @Package: com.mul.network.http.convert
 * @ClassName: JsonConvert
 * @Author: zdd
 * @CreateDate: 2020/7/15 11:12
 * @Description: json转换器
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/7/15 11:12
 * @UpdateRemark: 更新说明
 * @Version: 1.0.0
 */
public class JsonConvert implements Convert<Object> {
    @Override
    public Object convert(String response, Type type, String url) {
        JSONObject jsonObject = JSON.parseObject(response);
        Object data = jsonObject.get("data");
        if (!DataUtils.isEmpty(data)) {
            if (data instanceof String) {
                return data;
            }
            return JSON.parseObject(data.toString(), type);
        }
        return null;
    }

    @Override
    public Object convert(String response, Class clz, String url) {
        JSONObject jsonObject = JSON.parseObject(response);
        Object data = jsonObject.get("data");
        if (!DataUtils.isEmpty(data)) {
            if (data instanceof String) {
                return data;
            }
            return JSON.parseObject(data.toString(), clz);
        }
        return null;
    }
}