package com.example.gh.jiketools.net;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.gh.jiketools.R;
import com.example.gh.jiketools.base.BaseActivity;
import com.gh.retrofittools.bean.DownInfo;
import com.gh.retrofittools.bean.DownLoadApkInfo;
import com.gh.retrofittools.http.HttpDownManager;
import com.gh.retrofittools.http.HttpDownManagerSimple;
import com.gh.retrofittools.listener.HttpDownOnNextListener;
import com.gh.retrofittools.utils.DbDownUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author: gh
 * @description:
 * @date: 2018/5/15.
 * @from:
 */
public class DownActivity extends BaseActivity {

    @BindView(R.id.id_tv_msgdown)
    TextView id_tv_msgdown;
    @BindView(R.id.id_numberprogressbar)
    NumberProgressBar id_numberprogressbar;

    private List<DownInfo> listData;
    private DbDownUtil dbUtil;
    private DownInfo apkApi;
    private HttpDownManager manager;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, DownActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_down);
        ButterKnife.bind(this);

        initDownload();

    }

    @OnClick({R.id.id_btn_down, R.id.id_btn_pause,R.id.id_btn_downsample})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_btn_down:
//                if (apkApi.getState() != DownState.FINISH) {
                    manager.startDown(apkApi);
//                }
                break;
            case R.id.id_btn_pause:
                manager.pause(apkApi);
                break;
            case R.id.id_btn_downsample:
                downApk();
                break;
        }
    }

    private void initDownload() {
        int id = 1;
        manager = HttpDownManager.getInstance();
        dbUtil = DbDownUtil.getInstance();
        listData = dbUtil.queryDownAll();
        /*第一次模拟服务器返回数据掺入到数据库中*/
        if (listData.isEmpty()) {
            File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "test" + id + ".mp4");
            DownInfo apkApi = new DownInfo("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
            apkApi.setId(id);
            apkApi.setUpdateProgress(true);
            apkApi.setSavePath(outputFile.getAbsolutePath());
            dbUtil.save(apkApi);
            listData = dbUtil.queryDownAll();
        }
        apkApi = listData.get(0);
        apkApi.setListener(httpProgressOnNextListener);
        id_numberprogressbar.setMax((int) apkApi.getCountLength());
        id_numberprogressbar.setProgress((int) apkApi.getReadLength());
        /*第一次恢复 */
        switch (apkApi.getState()) {
            case START:
                /*起始状态*/
                break;
            case PAUSE:
                id_tv_msgdown.setText("暂停中");
                break;
            case DOWN:
                manager.startDown(apkApi);
                break;
            case STOP:
                id_tv_msgdown.setText("下载停止");
                break;
            case ERROR:
                id_tv_msgdown.setText("下載錯誤");
                break;
            case FINISH:
                id_tv_msgdown.setText("下载完成");
                break;
        }
    }

    /*下载回调*/
    HttpDownOnNextListener<DownInfo> httpProgressOnNextListener = new HttpDownOnNextListener<DownInfo>() {
        @Override
        public void onNext(DownInfo baseDownEntity) {
            id_tv_msgdown.setText("提示：下载完成/文件地址->" + baseDownEntity.getSavePath());
        }

        @Override
        public void onStart() {
            id_tv_msgdown.setText("提示:开始下载");
        }

        @Override
        public void onComplete() {
            Toast.makeText(mContext, "提示：下载结束", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            id_tv_msgdown.setText("失败:" + e.toString());
        }


        @Override
        public void onPuase() {
            super.onPuase();
            id_tv_msgdown.setText("提示:暂停");
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void updateProgress(long readLength, long countLength) {
            id_tv_msgdown.setText("提示:下载中");
            id_numberprogressbar.setMax((int) countLength);
            id_numberprogressbar.setProgress((int) readLength);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*记录退出时下载任务的状态-复原用*/
        for (DownInfo downInfo : listData) {
            dbUtil.update(downInfo);
        }
    }

    private void downApk() {
        DownLoadApkInfo info = new DownLoadApkInfo();
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "test" + "a" + ".apk");
        info.setSavePath(outputFile.getAbsolutePath());
        info.setListener(httpProgressOnNextListener2);
        info.setUrl("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");

        HttpDownManagerSimple.getInstance().startDown(info);

    }

    HttpDownOnNextListener<DownLoadApkInfo> httpProgressOnNextListener2 = new HttpDownOnNextListener<DownLoadApkInfo>() {
        @Override
        public void onNext(DownLoadApkInfo baseDownEntity) {
            id_tv_msgdown.setText("提示：下载完成/文件地址->" + baseDownEntity.getSavePath());
        }

        @Override
        public void onStart() {
            id_tv_msgdown.setText("提示:开始下载");
        }

        @Override
        public void onComplete() {
            Toast.makeText(mContext, "提示：下载结束", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            id_tv_msgdown.setText("失败:" + e.toString());
        }


        @Override
        public void onPuase() {
            super.onPuase();
            id_tv_msgdown.setText("提示:暂停");
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void updateProgress(long readLength, long countLength) {
            id_tv_msgdown.setText("提示:下载中");
            id_numberprogressbar.setMax((int) countLength);
            id_numberprogressbar.setProgress((int) readLength);
        }
    };

}
