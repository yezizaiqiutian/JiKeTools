package com.gh.retrofittools.interceptor;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.gh.retrofittools.RxRetrofitApp;
import com.gh.retrofittools.common.RxConstant;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author: gh
 * @description: TODO: 2018/5/14  可添加公共参数,需要将公共参数换成与后台相同的
 * @date: 2018/5/14.
 * @from:
 */
public class NetInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (request.method().equals("GET")) {

            //添加公共参数
            HttpUrl httpUrl = request.url()
                    .newBuilder()
                    .addQueryParameter(RxConstant.timesTampKey, String.valueOf(System.currentTimeMillis() / 1000))
                    .addQueryParameter(RxConstant.verKye, getAppVersionCode())
                    .addQueryParameter(RxConstant.deviceKye, RxConstant.deviceValue)
                    .addQueryParameter(RxConstant.signKye, "")
                    .build();
            request = request.newBuilder().url(httpUrl).build();

        } else if (request.method().equals("POST")) {

            if (request.body() instanceof FormBody) {
                FormBody.Builder bodyBuilder = new FormBody.Builder();
                FormBody formBody = (FormBody) request.body();

                //把原来的参数添加到新的构造器，（因为没找到直接添加，所以就new新的）
                for (int i = 0; i < formBody.size(); i++) {
                    bodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                }

                formBody = bodyBuilder
                        .addEncoded(RxConstant.timesTampKey, String.valueOf(System.currentTimeMillis() / 1000))
                        .addEncoded(RxConstant.verKye, getAppVersionCode())
                        .addEncoded(RxConstant.deviceKye, RxConstant.deviceValue)
                        .addEncoded(RxConstant.signKye, "")
                        .build();

                request = request.newBuilder().post(formBody).build();
            } else if (request.body() instanceof MultipartBody) {

                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
                multipartBuilder.setType(MultipartBody.FORM);

                MultipartBody formBody = (MultipartBody) request.body();

                //把原来的参数添加到新的构造器，（因为没找到直接添加，所以就new新的）
                for (int i = 0; i < formBody.size(); i++) {
                    multipartBuilder.addPart(formBody.part(i));
                }

                formBody = multipartBuilder
                        .addFormDataPart(RxConstant.timesTampKey, String.valueOf(System.currentTimeMillis() / 1000))
                        .addFormDataPart(RxConstant.verKye, getAppVersionCode())
                        .addFormDataPart(RxConstant.deviceKye, RxConstant.deviceValue)
                        .addFormDataPart(RxConstant.signKye, "")
                        .build();

                request = request.newBuilder().post(formBody).build();
            }
        }
        return chain.proceed(request);
    }

    /**
     * 获取版本号
     * @return
     */
    public String getAppVersionCode() {
        String versionName = "1";
        Context context = RxRetrofitApp.getApplication().getApplicationContext();
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            versionName = String.valueOf(packageInfo.versionCode);
        }
        return versionName;
    }

}
