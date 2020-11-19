package com.mul.mulnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.mul.network.NetWorkListener;
import com.mul.network.NetWorkManager;
import com.mul.network.NetWorkStatus;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NetWorkManager.getInstance().setNetWorkListener(new NetWorkListener() {
            @Override
            public void onNetWorkChange(NetWorkStatus mNetWorkStatus) {
                Log.i("网络状态：", mNetWorkStatus.netWorkType + "==" + mNetWorkStatus.netWorkTypeStr);
            }
        });
    }
}