package com.gh.retrofittools;

import android.app.Application;

import com.gh.retrofittools.https.HttpsUtils;

import java.io.InputStream;

/**
 * @author: gh
 * @description:
 * @date: 2018/5/14.
 * @from:
 */
public class RxRetrofitApp {
    private static Application application;
    private static boolean debug;
    private static HttpsUtils.SSLParams sslParams;

    public static void init(Application app){
        setApplication(app);
        setDebug(true);
    }

    public static void init(Application app, boolean debug,int ca){
        setApplication(app);
        setDebug(debug);
        setSslParams(HttpsUtils.getSslSocketFactory(new InputStream[]{application.getResources().openRawResource(ca)}, null, null));
    }

    public static Application getApplication() {
        return application;
    }

    private static void setApplication(Application application) {
        RxRetrofitApp.application = application;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        RxRetrofitApp.debug = debug;
    }

    public static HttpsUtils.SSLParams getSslParams() {
        return sslParams;
    }

    public static void setSslParams(HttpsUtils.SSLParams sslParams) {
        RxRetrofitApp.sslParams = sslParams;
    }
}
