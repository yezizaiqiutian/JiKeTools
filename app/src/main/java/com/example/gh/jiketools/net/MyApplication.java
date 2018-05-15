package com.example.gh.jiketools.net;

import android.app.Application;

import com.example.gh.jiketools.BuildConfig;
import com.example.gh.jiketools.R;
import com.gh.retrofittools.RxRetrofitApp;

/**
 * @author: gh
 * @description:
 * @date: 2018/5/14.
 * @from:
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RxRetrofitApp.init(this, BuildConfig.DEBUG, R.raw.ca);
    }

}
