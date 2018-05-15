package com.example.gh.jiketools.net;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.gh.jiketools.R;
import com.example.gh.jiketools.base.BaseActivity;
import com.gh.retrofittools.http.HttpManager;
import com.gh.retrofittools.listener.HttpOnNextListener;
import com.gh.retrofittools.listener.UploadProgressListener;
import com.gh.retrofittools.upload.ProgressRequestBody;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @author: gh
 * @description:
 * @date: 2018/5/15.
 * @from:
 */
public class UpLoadActivity extends BaseActivity {

    @BindView(R.id.id_numberprogressbar)
    NumberProgressBar id_numberprogressbar;
    @BindView(R.id.id_tv_msgdown)
    TextView id_tv_msgdown;
    @BindView(R.id.id_iv_img)
    ImageView id_iv_img;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, UpLoadActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.id_btn_upload)
    public void onViewClicked() {
        doUpload();
    }

    private void doUpload() {

        File file = new File("/storage/emulated/0/Download/11.jpg");
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file_name", file.getName(), new ProgressRequestBody
                (requestBody,
                        new UploadProgressListener() {
                            @Override
                            public void onProgress(final long currentBytesCount, final long totalBytesCount) {

                                /*回到主线程中，可通过timer等延迟或者循环避免快速刷新数据*/
                                Observable.just(currentBytesCount).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {

                                    @Override
                                    public void accept(Long aLong) {
                                        id_tv_msgdown.setText("提示:上传中");
                                        id_numberprogressbar.setMax((int) totalBytesCount);
                                        id_numberprogressbar.setProgress((int) currentBytesCount);
                                    }
                                });
                            }
                        }));
        UploadApi uplaodApi = new UploadApi(httpOnNextListener, this);
        uplaodApi.setPart(part);
        HttpManager manager = HttpManager.getInstance();
        manager.doHttpDeal(uplaodApi);

    }

    /**
     * 上传回调
     */
    private HttpOnNextListener httpOnNextListener = new HttpOnNextListener<String>() {
        @Override
        public void onNext(String o) {
            id_tv_msgdown.setText("成功");
//            Glide.with(mActivity).load(o.getHeadImgUrl()).skipMemoryCache(true).into(id_iv_img);
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            id_tv_msgdown.setText("失败：" + e.toString());
        }

    };
}
