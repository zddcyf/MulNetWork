package com.mul.network.http.utils;

import com.mul.network.http.config.NetworkConfig;
import com.mul.utils.log.LogUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @ProjectName: MulNetWork
 * @Package: com.mul.network.http.utils
 * @ClassName: UrlUtils
 * @Author: zdd
 * @CreateDate: 2020/7/15 9:43
 * @Description: java类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/7/15 9:43
 * @UpdateRemark: 更新说明
 * @Version: 1.0.0
 */
public class UrlCreatorUtils {
    public static String createUrlFromParams(String url, Map<String, Object> mParams) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        if (url.indexOf("?") > 0 || url.indexOf("&") > 0) {
            sb.append("&");
        } else {
            sb.append("?");
        }
        for (Map.Entry<String, Object> entry : mParams.entrySet()) {
            try {
                String value = URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8");
                sb.append(entry.getKey()).append("=").append(value).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        sb.deleteCharAt(sb.length() - 1);

        LogUtil.saveI(NetworkConfig.LOG_URL, "get请求拼接完成路径=" + sb.toString());
        return sb.toString();
    }
}