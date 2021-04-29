package com.mul.network.http.request;

import com.mul.network.http.config.NetworkConfig;
import com.mul.network.http.utils.UrlCreatorUtils;
import com.mul.utils.log.LogUtil;

/**
 * @ProjectName: MulNetWork
 * @Package: com.mul.network.http.request
 * @ClassName: GetRequest
 * @Author: zdd
 * @CreateDate: 2020/7/15 9:42
 * @Description: get请求
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/7/15 9:42
 * @UpdateRemark: 更新说明
 * @Version: 1.0.0
 */
public class GetRequest<B> extends Request<B, GetRequest<B>> {
    public GetRequest(String mUrl) {
        super(mUrl);
    }

    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {
        LogUtil.saveI(NetworkConfig.LOG_HTTP, "url=" + mUrl
                + "\nheader=" + mHeaders.toString()
                + "\nmParams=" + mParams.toString());

        okhttp3.Request request = builder.get().url(UrlCreatorUtils.createUrlFromParams(mUrl, mParams)).build();
        return request;
    }
}