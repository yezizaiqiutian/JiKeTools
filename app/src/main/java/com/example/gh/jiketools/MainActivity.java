package com.example.gh.jiketools;

import android.os.Bundle;
import android.view.View;

import com.example.gh.jiketools.base.BaseActivity;
import com.example.gh.jiketools.camera.CameraActivity;
import com.example.gh.jiketools.net.DownActivity;
import com.example.gh.jiketools.net.NetActivity;
import com.example.gh.jiketools.net.UpLoadActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.id_btn_tonet, R.id.id_btn_todown, R.id.id_btn_toupload, R.id.id_btn_tocamera})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_btn_tonet:
                NetActivity.actionStart(mContext);
                break;
            case R.id.id_btn_todown:
                DownActivity.actionStart(mContext);
                break;
            case R.id.id_btn_toupload:
                UpLoadActivity.actionStart(mContext);
                break;
            case R.id.id_btn_tocamera:
                CameraActivity.actionStart(mContext);
                break;
        }
    }
}
