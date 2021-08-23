package com.mul.mulnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mul.network.http.ApiService;
import com.mul.network.http.callback.JsonCallBack;
import com.mul.network.http.response.ApiResponse;
import com.mul.network.status.NetWorkManager;
import com.mul.utils.date.DateUtils;
import com.mul.utils.params.ParamsSplice;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test = findViewById(R.id.test);
        NetWorkManager.getInstance().setNetWorkListener(mNetWorkStatus -> {
            String mS = mNetWorkStatus.netWorkType + "==" + mNetWorkStatus.netWorkTypeStr
                    + "==" + mNetWorkStatus.ip;
            Log.i("网络状态：", mS);
            runOnUiThread(()-> {
                test.setText(mS);
            });
        });

        ApiService.obtain().get("")
                .addParam("", "")
                .addHeader("", "")
                .execute(new JsonCallBack<Object>() {
                    @Override
                    public void onSuccess(ApiResponse<Object> mResponse) {
                        super.onSuccess(mResponse);
                    }
                });
        Map<String, Object> mParams = new HashMap<>();
        mParams.put("appKey", "160819069911758");
        mParams.put("createTime", DateUtils.INSTANCE.getDate("yyyy-MM-dd HH:mm:ss"));
        mParams.put("requestId", "18a5a949845b43029ed4b02fc04241bc"); // uuid 去掉:号、并且字母转换成大写
        mParams.put("deviceMac", "c766d1945");
        mParams.put("deviceType", "GQC");
        mParams.put("sign", ParamsSplice.paramSplice("key=mima", mParams));
        ApiService.obtain().<String>post("http://192.168.11.123:32300/SUPPORT/device/v1/code/generate")
                .addParams(mParams)
                .execute(new JsonCallBack<String>() {
                    @Override
                    public void onSuccess(ApiResponse<String> mResponse) {
                        super.onSuccess(mResponse);
                    }

                    @Override
                    public void onError(final ApiResponse<String> mResponse) {
                    }
                });
    }
}