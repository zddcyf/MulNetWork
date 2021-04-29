package com.mul.network.http.request;

import com.mul.network.http.config.NetworkConfig;
import com.mul.utils.log.LogUtil;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @ProjectName: MulNetWork
 * @Package: com.mul.network.http.request
 * @ClassName: PostRequest
 * @Author: zdd
 * @CreateDate: 2020/7/15 10:01
 * @Description: post请求
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/7/15 10:01
 * @UpdateRemark: 更新说明
 * @Version: 1.0.0
 */
public class PostRequest<B> extends Request<B, PostRequest<B>> {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public PostRequest(String mUrl) {
        super(mUrl);
    }

    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {
        LogUtil.saveI(NetworkConfig.LOG_HTTP, "url=" + mUrl + "\nheader=" + mHeaders.toString() + "\nmParams=" + mParams.toString());

        /**
         * from表单传参
         */
//        FormBody.Builder bodyBuilder = new FormBody.Builder();
//        for (Map.Entry<String, Object> entry : mParams.entrySet()) {
//            bodyBuilder.add(entry.getKey(), String.valueOf(entry.getValue()));
//        }
//        okhttp3.Request request = builder.url(mUrl).post(bodyBuilder.build()).build();
        /**
         * json串传参
         */
        RequestBody body = RequestBody.create(JSON, com.alibaba.fastjson.JSON.toJSONString(mParams));
        okhttp3.Request request = builder.url(mUrl).post(body).build();
        return request;
    }
}