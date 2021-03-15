package com.mul.mulnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mul.network.NetWorkListener;
import com.mul.network.NetWorkManager;
import com.mul.network.NetWorkStatus;

public class MainActivity extends AppCompatActivity {

    private TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test = findViewById(R.id.test);
        NetWorkManager.getInstance().setNetWorkListener(new NetWorkListener() {
            @Override
            public void onNetWorkChange(NetWorkStatus mNetWorkStatus) {
                String mS = mNetWorkStatus.netWorkType + "==" + mNetWorkStatus.netWorkTypeStr;
                Log.i("网络状态：", mS);
                test.setText(mS);
            }
        });
    }
}