package com.mul.network.http.response;

/**
 * @ProjectName: MulNetWork
 * @Package: com.mul.network.http.response
 * @ClassName: ApiResponse
 * @Author: zdd
 * @CreateDate: 2020/7/15 9:24
 * @Description: java类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/7/15 9:24
 * @UpdateRemark: 更新说明
 * @Version: 1.0.0
 */
public class ApiResponse<B> {
    public boolean success;
    public int status;
    public String message;
    public String url;
    public B body;
}