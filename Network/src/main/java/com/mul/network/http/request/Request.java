package com.mul.network.http.request;

import android.annotation.SuppressLint;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;

import com.mul.network.http.ApiService;
import com.mul.network.http.callback.JsonCallBack;
import com.mul.network.http.config.NetworkConfig;
import com.mul.network.http.convert.Convert;
import com.mul.network.http.handler.HandlerManager;
import com.mul.network.http.response.ApiResponse;
import com.mul.network.http.utils.UrlCreatorUtils;
import com.mul.network.status.NetStateUtils;
import com.mul.network.status.NetWorkStatus;
import com.mul.network.status.NetWorkType;
import com.mul.utils.DataUtils;
import com.mul.utils.GenericUtils;
import com.mul.utils.StringUtils;
import com.mul.utils.db.manager.ApiCacheManager;
import com.mul.utils.log.LogExceptionResult;
import com.mul.utils.log.LogUtil;
import com.mul.utils.manager.GlobalManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @ProjectName: MulNetWork
 * @Package: com.mul.network.http.request
 * @ClassName: Request
 * @Author: zdd
 * @CreateDate: 2020/7/14 10:17
 * @Description: 请求基类
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/7/14 10:17
 * @UpdateRemark: 更新说明
 * @Version: 1.0.0
 */
public abstract class Request<B, R extends Request<B, R>> implements Cloneable {
    /**
     * 请求的url   user/list   这种形式
     */
    protected String mUrl;
    /**
     * 数据转换器
     */
    protected Convert<B> mConvert;
    /**
     * 头参数
     */
    protected Map<String, String> mHeaders = new HashMap<>();
    /**
     * 参数集合
     */
    protected Map<String, Object> mParams = new HashMap<>();
    /**
     * 缓存数据时的key
     */
    private String mCacheKey;
    /**
     * 同步请求时的数据类型
     */
    private Type mType;
    private Class mClz;
    /**
     * 请求类型 同步or异步
     */
    private String requestType;
    /**
     * 数据信息
     */
    private ApiResponse<B> apiResponse;
    /**
     * 访问网络类型
     */
    private @CacheStrategy
    int mStrategy;

//    private Handler mHandler = new Handler(Looper.getMainLooper());

    @IntDef({CacheStrategy.CACHE_ONLY, CacheStrategy.CACHE_FIRST, CacheStrategy.NET_ONLY, CacheStrategy.NET_CACHE})
    public @interface CacheStrategy {
        /**
         * 仅仅只访问本地缓存，即使本地缓存不存在，也不会去发起网络请求
         */
        int CACHE_ONLY = 1;
        /**
         * 先访问缓存，同时发起网络请求，成功后缓存到本地
         */
        int CACHE_FIRST = 2;
        /**
         * 仅仅只访问服务器，不存任何存储
         */
        int NET_ONLY = 3;
        /**
         * 先访问网络，成功后缓存到本地
         */
        int NET_CACHE = 4;
    }

    public Request(String mUrl) {
        this.mUrl = mUrl;
        mStrategy = CacheStrategy.NET_ONLY;
    }

    public R addHeader(String key, String value) {
        mHeaders.put(key, value);
        return GenericUtils.cancelUnchecked(this);
    }

    public R addParam(String key, Object value) {
        mParams.put(key, value);
        return GenericUtils.cancelUnchecked(this);
    }

    public R addParams(Map<String, Object> mParams) {
        this.mParams = mParams;
        return GenericUtils.cancelUnchecked(this);
    }

    public R cacheStrategy(@CacheStrategy int mStrategy) {
        this.mStrategy = mStrategy;
        return GenericUtils.cancelUnchecked(this);
    }

    public R cacheKey(String mCacheKey) {
        this.mCacheKey = mCacheKey;
        return GenericUtils.cancelUnchecked(this);
    }

    /**
     * 同步请求时的数据类型
     *
     * @param mType 数据类型
     */
    public R responseType(Type mType) {
        this.mType = mType;
        return GenericUtils.cancelUnchecked(this);
    }

    /**
     * 同步请求时的数据类型
     *
     * @param mClz class
     */
    public R responseType(Class mClz) {
        this.mClz = mClz;
        return GenericUtils.cancelUnchecked(this);
    }

    /**
     * 初始化数据转换器
     *
     * @param mConvert 数据转换器JsonConvert为默认转换器，可通过集成convert自定义
     */
    public R setConvert(Convert<B> mConvert) {
        this.mConvert = mConvert;
        return GenericUtils.cancelUnchecked(this);
    }

    /**
     * 校验是否是正确接口
     *
     * @param mCallBack 回调
     * @return true为有异常，false为没异常
     */
    private boolean checkUrl(JsonCallBack<B> mCallBack) {
        if (!mUrl.contains("http://") && !mUrl.contains("https://")) {
            isNetWork(mCallBack, NetworkConfig.ERROR_MESSAGE_102, NetworkConfig.ERROR_STATUS_102);
            return true;
        }
        return false;
    }

    /**
     * 同步请求
     */
    public void execute(JsonCallBack<B> callBack) {
        requestType = NetworkConfig.EXECUTE;
        if (checkUrl(callBack)) {
            return;
        }

        if (mStrategy == CacheStrategy.CACHE_ONLY) {
            cacheRequest((JsonCallBack<B>) callBack);
        }

        if (mStrategy != CacheStrategy.CACHE_ONLY) {
            ApiService.obtain().getExecutors().execute(() -> {
                try {
                    Response response = getCall().execute();
                    parseResponse(response, callBack);
                } catch (Exception e) {
                    isNetWork((JsonCallBack<B>) callBack, NetworkConfig.ERROR_MESSAGE_104 + "," + LogExceptionResult.getException(e), NetworkConfig.ERROR_STATUS_104);
                }
            });
        }
    }

    /**
     * 异步请求
     *
     * @param callBack 回调
     */
    @SuppressLint("RestrictedApi")
    public void enqueue(JsonCallBack<B> callBack) {
        requestType = NetworkConfig.ENQUEUE;
        if (checkUrl(callBack)) {
            return;
        }

        if (mStrategy != CacheStrategy.NET_ONLY) {
            ArchTaskExecutor.getIOThreadExecutor().execute(() -> cacheRequest(callBack));
        }

        if (mStrategy != CacheStrategy.CACHE_ONLY) {
            getCall().enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    isNetWork(callBack, NetworkConfig.ERROR_MESSAGE_101 + "," + e.getMessage(), NetworkConfig.ERROR_STATUS_101);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    parseResponse(response, callBack);
                }
            });
        }
    }

    /**
     * 缓存数据处理
     *
     * @param callBack 回调
     */
    private void cacheRequest(JsonCallBack<B> callBack) {
        readCache();
        if (null != callBack && null != apiResponse.body) {
            apiResponse.url = getErrorUrl();
            HandlerManager.getInstance().getHandler().post(() -> onCacheSuccess(callBack));
        } else {
            isNetWork(callBack, NetworkConfig.ERROR_MESSAGE_103, NetworkConfig.ERROR_STATUS_103);
        }
    }

    /**
     * 请求异常
     *
     * @param callBack      回调
     * @param mErrorMessage 异常信息
     * @param mErrorStatus  异常状态
     */
    private void isNetWork(JsonCallBack<B> callBack, String mErrorMessage, int mErrorStatus) {
        if (!DataUtils.isEmpty(GlobalManager.INSTANCE.context)) {
            NetWorkStatus mNetState = NetStateUtils.getNetState(GlobalManager.INSTANCE.context);
            if (mNetState.netWorkType == NetWorkType.NW_UNKNOWN) {
                requestError(callBack, NetworkConfig.ERROR_MESSAGE_105, NetworkConfig.ERROR_STATUS_105);
            } else {
                requestError(callBack, mErrorMessage, mErrorStatus);
            }
        } else {
            requestError(callBack, mErrorMessage, mErrorStatus);
        }
    }

    /**
     * 请求异常
     *
     * @param callBack      回调
     * @param mErrorMessage 异常信息
     * @param mErrorStatus  异常状态
     */
    private void requestError(JsonCallBack<B> callBack, String mErrorMessage, int mErrorStatus) {
        apiResponse = new ApiResponse<>();
        apiResponse.message = mErrorMessage;
        apiResponse.status = mErrorStatus;
        apiResponse.url = getErrorUrl();
        HandlerManager.getInstance().getHandler().post(() -> onError(callBack));
    }

    /**
     * 接口请求成功
     *
     * @param callBack 回调
     */
    private void onSuccess(JsonCallBack<B> callBack) {
        StringBuilder submit = new StringBuilder();
        buildLog(submit, "onSuccess：url=");
        try {
            callBack.onSuccess(apiResponse);
            LogUtil.saveI(NetworkConfig.LOG_HTTP, submit.toString());
        } catch (Exception mE) {
            submit.append("\nmE=");
            submit.append(LogExceptionResult.getException(mE));
            LogUtil.saveE(NetworkConfig.LOG_HTTP, submit.toString());
        }

    }

    /**
     * 缓存读取成功
     *
     * @param callBack 回调
     */
    private void onCacheSuccess(JsonCallBack<B> callBack) {
        StringBuilder submit = new StringBuilder();
        buildLog(submit, "onCacheSuccess：url=");
        try {
            callBack.onCacheSuccess(apiResponse);
            LogUtil.saveI(NetworkConfig.LOG_HTTP, submit.toString());
        } catch (Exception mE) {
            submit.append("\nmE=");
            submit.append(LogExceptionResult.getException(mE));
            LogUtil.saveE(NetworkConfig.LOG_HTTP, submit.toString());
        }
    }

    /**
     * 缓存读取失败
     *
     * @param callBack 回调
     */
    private void onError(JsonCallBack<B> callBack) {
        StringBuilder submit = new StringBuilder();
        buildLog(submit, "onError：url=");
        try {
            callBack.onError(apiResponse);
            LogUtil.saveI(NetworkConfig.LOG_HTTP, submit.toString());
        } catch (Exception mE) {
            submit.append("\nmE=");
            submit.append(LogExceptionResult.getException(mE));
            LogUtil.saveE(NetworkConfig.LOG_HTTP, submit.toString());
        }
    }

    /**
     * log组装
     *
     * @param mSubmit sb
     * @param mS      是异常还是成功
     */
    private void buildLog(StringBuilder mSubmit, String mS) {
        mSubmit.append(mS).append(apiResponse.url)
                .append(",requestType=").append(requestType)
                .append("\nstatus=").append(apiResponse.status)
                .append(",message=").append(apiResponse.message)
                .append(",body=").append(DataUtils.isEmpty(apiResponse.body) ? apiResponse.body : apiResponse.body.toString());
    }

    /**
     * 读取缓存
     */
    private void readCache() {
        String key = DataUtils.isEmpty(mCacheKey) ? generateCacheKey() : mCacheKey;
        Object cache = ApiCacheManager.getCache(key);
        apiResponse = new ApiResponse<>();
        apiResponse.success = true;
        apiResponse.status = ApiService.obtain().getCodeSuccess();
        apiResponse.message = "读取缓存成功";
        apiResponse.url = getErrorUrl();
        apiResponse.body = GenericUtils.cancelUnchecked(cache);
    }

    private Call getCall() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeaders(builder);
        okhttp3.Request request = generateRequest(builder);
        return ApiService.obtain().getOkHttpClient().newCall(request);
    }

    /**
     * 添加请求头
     */
    private void addHeaders(okhttp3.Request.Builder builder) {
        for (Map.Entry<String, String> entry : mHeaders.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 处理不同请求的参数（get/post/put/delete等等）
     */
    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);

    /**
     * 数据解析
     *
     * @param response 请求体
     * @param callBack 回调
     */
    protected void parseResponse(Response response, JsonCallBack<B> callBack) {
        String message = null;
        int status = response.code();
        boolean success = response.isSuccessful();
        apiResponse = new ApiResponse<>();
        if (null == mConvert) {
            mConvert = ApiService.obtain().getConvert();
        }
        apiResponse.url = getErrorUrl();
        try {
            String content = Objects.requireNonNull(response.body()).string();
            LogUtil.saveI(NetworkConfig.LOG_HTTP, "url=" + mUrl
                    + ",requestType=" + requestType + "解析数据:\n" + content);
            if (success) {
                if (content.contains(ApiService.obtain().getCode()) && !content.contains(ApiService.obtain().getCodeSuccess() + "")) {
                    JSONObject mJSONObject = new JSONObject(content);
                    status = mJSONObject.optInt(ApiService.obtain().getCode());
                    message = mJSONObject.optString("msg");
                    success = false;
                    apiResponse.body = null;
                } else {
                    if (null != callBack) {
                        ParameterizedType type = (ParameterizedType) callBack.getClass().getGenericSuperclass();
                        assert type != null;
                        Type argument = type.getActualTypeArguments()[0];
                        apiResponse.body = mConvert.convert(content, argument, getErrorUrl());
                    } else if (null != mType) {
                        apiResponse.body = mConvert.convert(content, mType, getErrorUrl());
                    } else if (null != mClz) {
                        apiResponse.body = mConvert.convert(content, mClz, getErrorUrl());
                    } else {
                        message = content;
                        LogUtil.saveI(NetworkConfig.LOG_HTTP, "parseResponse:url=" + apiResponse.url
                                + ",requestType=" + requestType
                                + "\nstatus=" + status + ",message=" + message + ",errorInfo=" + "parseResponse: 无法进行数据解析");
                    }
                }
            } else {
                message = content;
                LogUtil.saveI(NetworkConfig.LOG_HTTP, "parseResponse:url=" + apiResponse.url
                        + ",requestType=" + requestType
                        + "\nstatus=" + status + ",message=" + message + ",errorInfo=" + "访问错误");
            }
        } catch (Exception e) {
            message = e.getMessage();
            success = false;
            LogUtil.saveI(NetworkConfig.LOG_HTTP, "parseResponse:url=" + apiResponse.url
                    + ",requestType=" + requestType
                    + "\nstatus=" + status + ",message=" + message + ",errorInfo=" + "数据解析异常");
        }
        apiResponse.success = success;
        apiResponse.status = status;
        apiResponse.message = message;

        if (mStrategy != CacheStrategy.NET_ONLY && apiResponse.success && apiResponse.body instanceof Serializable) {
            saveCache(apiResponse.body);
        }
        if (!apiResponse.success) {
            HandlerManager.getInstance().getHandler().post(() -> onError(callBack));
        } else {
            HandlerManager.getInstance().getHandler().post(() -> onSuccess(callBack));
        }
    }

    /**
     * 数据保存
     *
     * @param body 数据体
     */
    private void saveCache(B body) {
        String key = DataUtils.isEmpty(mCacheKey) ? generateCacheKey() : mCacheKey;
        ApiCacheManager.save(key, body);
    }

    /**
     * 生成缓存key
     *
     * @return 返回一个key
     */
    private String generateCacheKey() {
        mCacheKey = UrlCreatorUtils.createUrlFromParams(mUrl, mParams);
        return mCacheKey;
    }

    /**
     * @return 返回一个处理后的url
     */
    private String getErrorUrl() {
        return StringUtils.parseString(mUrl.substring(mUrl.indexOf("/", 10)));
    }

    @NonNull
    @Override
    public Request<B, R> clone() throws CloneNotSupportedException {
        return Objects.requireNonNull(GenericUtils.cancelUnchecked(super.clone()));
    }
}
