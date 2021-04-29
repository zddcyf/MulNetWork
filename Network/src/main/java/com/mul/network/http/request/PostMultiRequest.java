package com.mul.network.http.request;

import android.graphics.Bitmap;

import com.mul.network.http.config.NetworkConfig;
import com.mul.utils.log.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;

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
public class PostMultiRequest<B> extends Request<B, PostMultiRequest<B>> {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public PostMultiRequest(String mUrl) {
        super(mUrl);
    }

    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {
        LogUtil.saveI(NetworkConfig.LOG_HTTP, "url=" + mUrl
                + "\nheader=" + mHeaders.toString()
                + "\nmParams=" + mParams.toString());
        /**
         * from表单传参
         */
        MultipartBody.Builder mMultipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> mObjectEntry : mParams.entrySet()) {
            Object mValue = mObjectEntry.getValue();
            if (mValue instanceof Bitmap) {
                setBitmap(mMultipartBody, mObjectEntry.getKey(), (Bitmap) mValue);
            } else if (mValue instanceof File) {
                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), (File) mValue);
                mMultipartBody.addFormDataPart(mObjectEntry.getKey(), ((File) mValue).getName(), fileBody);
            } else {
                mMultipartBody.addFormDataPart(mObjectEntry.getKey(), String.valueOf(mObjectEntry.getValue()));
            }
        }

        okhttp3.Request request = builder.url(mUrl).post(mMultipartBody.build()).build();
        return request;
    }

    private void setBitmap(MultipartBody.Builder mMultipartBody, String mKey, Bitmap mBitmap) {
        RequestBody requestBody = new RequestBody() {
            @Override
            public void writeTo(@NotNull BufferedSink mBufferedSink) {
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, mBufferedSink.outputStream());
            }

            @Override
            public MediaType contentType() {
                return MediaType.parse("image/jpeg;charset=utf-8");
            }
        };
        mMultipartBody.addFormDataPart(mKey, System.currentTimeMillis() + ".jpeg", requestBody);
    }
}