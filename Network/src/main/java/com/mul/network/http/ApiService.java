package com.mul.network.http;

//import com.facebook.stetho.okhttp3.StethoInterceptor;

import android.annotation.SuppressLint;

import com.mul.network.http.config.NetworkConfig;
import com.mul.network.http.convert.Convert;
import com.mul.network.http.convert.JsonConvert;
import com.mul.network.http.request.DeleteRequest;
import com.mul.network.http.request.GetRequest;
import com.mul.network.http.request.PostMultiRequest;
import com.mul.network.http.request.PostRequest;
import com.mul.network.http.request.PutRequest;
import com.mul.utils.log.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @ProjectName: MulNetWork
 * @Package: com.mul.network.http
 * @ClassName: ApiService
 * @Author: zdd
 * @CreateDate: 2020/7/7 11:40
 * @Description: 接口类
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/7/7 11:40
 * @UpdateRemark: 更新说明
 * @Version: 1.0.0
 */
public class ApiService {
    private static final ApiService API_SERVICE = new ApiService();
    private final ExecutorService mExecutors = Executors.newSingleThreadExecutor();
    private int readTimeOut = 5; // 读取时长
    private int writeTimeOut = 5; // 写入时长
    private int connectTimeOut = 5; // 链接时长
    private OkHttpClient okHttpClient;
    private Convert mConvert;
    private String mBaseUrl;
    private String mCode = "code";
    private int mCodeSuccess = 200;

    private ApiService() {
    }

    public static ApiService obtain() {
        return API_SERVICE;
    }

    /**
     * 读取时长
     *
     * @param readTimeOut 读取时长
     */
    public ApiService setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
        return this;
    }

    /**
     * 写入时长
     *
     * @param writeTimeOut 写入时长
     */
    public ApiService setWriteTimeOut(int writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return this;
    }

    /**
     * 链接时长
     *
     * @param connectTimeOut 链接时长
     */
    public ApiService setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
        return this;
    }

    /**
     * @param mCode 错误或者正确是的code码的名称，一般为code
     */
    public ApiService setCode(String mCode) {
        this.mCode = mCode;
        return this;
    }

    /**
     * @param mCodeSuccess code码为多少时为正确
     */
    public ApiService setCode(int mCodeSuccess) {
        this.mCodeSuccess = mCodeSuccess;
        return this;
    }

    /**
     * @param mCode        错误或者正确是的code码的名称，一般为code
     * @param mCodeSuccess code码为多少时为正确
     */
    public ApiService setCode(String mCode, int mCodeSuccess) {
        this.mCode = mCode;
        this.mCodeSuccess = mCodeSuccess;
        return this;
    }

    /**
     * 初始化数据转换器
     *
     * @param mConvert 数据转换器JsonConvert为默认转换器，可通过集成convert自定义
     */
    public <B> ApiService setConvert(Convert<B> mConvert) {
        this.mConvert = mConvert;
        return this;
    }

    /**
     * 初始化baseUrl以及数据转换器
     *
     * @param mBaseUrl url
     */
    public ApiService setBaseUrl(String mBaseUrl) {
        this.mBaseUrl = mBaseUrl;
        return this;
    }

    /**
     * 初始化okHttpClient
     */
    public void create() {
        SSLContext ssl = null;
        // 适配9.0以后无法使用http请求的问题
        TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {
            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

            }

            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};

        try {
            ssl = SSLContext.getInstance("SSL");
            ssl.init(null, trustManagers, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(ssl.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> {
                // 域名校验的问题。true为信任所有的域名证书
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 添加log日志
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 网络链接器
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(readTimeOut, TimeUnit.SECONDS) // 读取时长
                .writeTimeout(writeTimeOut, TimeUnit.SECONDS) // 写入时长
                .connectTimeout(connectTimeOut, TimeUnit.SECONDS) // 链接时长
                .addInterceptor(httpLoggingInterceptor)
//                .addNetworkInterceptor(new StethoInterceptor())
                .hostnameVerifier((hostname, session) -> {
                    // Auto-generated method stub
                    return true;
                })
                .sslSocketFactory(ssl.getSocketFactory())
                .build();
    }

    /**
     * get请求
     *
     * @param url 接口地址
     */
    public <B> GetRequest<B> get(String url) {
        return new GetRequest<>(setUrl(url));
    }

    /**
     * post请求
     *
     * @param url 接口地址
     */
    public <B> PostRequest<B> post(String url) {
        return new PostRequest<>(setUrl(url));
    }

    /**
     * postMulti请求
     *
     * @param url 接口地址
     */
    public <B> PostMultiRequest<B> postMulti(String url) {
        return new PostMultiRequest<>(setUrl(url));
    }

    /**
     * put请求
     *
     * @param url 接口地址
     */
    public <B> PutRequest<B> put(String url) {
        return new PutRequest<>(setUrl(url));
    }

    /**
     * delete请求
     *
     * @param url 接口地址
     */
    public <B> DeleteRequest<B> delete(String url) {
        return new DeleteRequest<>(setUrl(url));
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public <B> Convert<B> getConvert() {
        if (null == mConvert) {
            mConvert = new JsonConvert();
        }
        return mConvert;
    }

    public String getCode() {
        return mCode;
    }

    public int getCodeSuccess() {
        return mCodeSuccess;
    }

    public ExecutorService getExecutors() {
        return mExecutors;
    }

    @NotNull
    private String setUrl(String url) {
        LogUtil.saveI(NetworkConfig.LOG_URL, "BaseUrl:url=" + mBaseUrl);
        return String.format("%s%s", url.contains("http://") || url.contains("https://") ? "" : mBaseUrl, url);
    }
}
