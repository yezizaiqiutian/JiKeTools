package com.example.gh.jiketools.net;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.example.gh.jiketools.R;
import com.example.gh.jiketools.base.BaseActivity;
import com.gh.retrofittools.api.BaseResultEntity;
import com.gh.retrofittools.http.HttpManager;
import com.gh.retrofittools.listener.HttpOnNextListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
public class NetActivity extends BaseActivity {

    @BindView(R.id.id_tv_msg)
    TextView id_tv_msg;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, NetActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_net);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.id_btn_net)
    public void onViewClicked() {
        simpleDo();
    }

    //    完美封装简化版
    private void simpleDo() {
        SubjectPostApi postEntity = new SubjectPostApi(simpleOnNextListener, this);
        postEntity.setAll(true);
        HttpManager manager = HttpManager.getInstance();
        manager.doHttpDeal(postEntity);
    }

    //   回调一一对应
    HttpOnNextListener simpleOnNextListener = new HttpOnNextListener<List<SubjectResulte>>() {
        @Override
        public void onNext(List<SubjectResulte> subjects) {
            id_tv_msg.setText("网络返回：\n" + subjects.toString());
        }

        @Override
        public void onCacheNext(String cache, int cacheType) {
            /*缓存回调*/
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<BaseResultEntity<List<SubjectResulte>>>() {
            }.getType();
            BaseResultEntity resultEntity = gson.fromJson(cache, type);
            id_tv_msg.setText("缓存返回：\n" + resultEntity.getData().toString());
        }

        /*用户主动调用，默认是不需要覆写该方法*/
        @Override
        public void onError(Throwable e) {
            super.onError(e);
            id_tv_msg.setText("失败：\n" + e.toString());
        }

        /*用户主动调用，默认是不需要覆写该方法*/
        @Override
        public void onCancel() {
            super.onCancel();
            id_tv_msg.setText("取消請求");
        }
    };

}
