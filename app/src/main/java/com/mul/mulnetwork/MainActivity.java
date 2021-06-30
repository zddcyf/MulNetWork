package com.mul.mulnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mul.network.http.ApiService;
import com.mul.network.http.callback.JsonCallBack;
import com.mul.network.http.response.ApiResponse;
import com.mul.network.status.NetWorkManager;

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

        ApiService.obtain().get("/www/ddd/")
                .addParam("", "")
                .addHeader("", "")
                .execute(new JsonCallBack<Object>() {
                    @Override
                    public void onSuccess(ApiResponse<Object> mResponse) {
                        super.onSuccess(mResponse);
                    }
                });
    }
}