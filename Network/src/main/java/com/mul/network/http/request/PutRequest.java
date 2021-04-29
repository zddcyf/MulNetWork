package com.mul.network.http.request;

import com.mul.network.http.config.NetworkConfig;
import com.mul.utils.log.LogUtil;

import java.util.Map;

import okhttp3.FormBody;

/**
 * @ProjectName: MulNetWork
 * @Package: com.mul.network.http.request
 * @ClassName: PostRequest
 * @Author: zdd
 * @CreateDate: 2020/7/15 10:01
 * @Description: put请求
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/7/15 10:01
 * @UpdateRemark: 更新说明
 * @Version: 1.0.0
 */
public class PutRequest<B> extends Request<B, PutRequest<B>> {
    public PutRequest(String mUrl) {
        super(mUrl);
    }

    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {
        LogUtil.saveI(NetworkConfig.LOG_HTTP, "url=" + mUrl
                + "\nheader=" + mHeaders.toString()
                + "\nmParams=" + mParams.toString());

        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : mParams.entrySet()) {
            bodyBuilder.add(entry.getKey(), String.valueOf(entry.getValue()));
        }
        okhttp3.Request request = builder.url(mUrl).put(bodyBuilder.build()).build();
        return request;
    }
}