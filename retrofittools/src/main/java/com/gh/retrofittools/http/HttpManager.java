package com.gh.retrofittools.http;

import android.util.Log;

import com.gh.retrofittools.RxRetrofitApp;
import com.gh.retrofittools.api.BaseApi;
import com.gh.retrofittools.cookies.CookieManger;
import com.gh.retrofittools.exception.RetryWhenNetworkException;
import com.gh.retrofittools.http.cookie.CookieInterceptor;
import com.gh.retrofittools.https.TrustAllCerts;
import com.gh.retrofittools.listener.HttpOnNextListener;
import com.gh.retrofittools.subscribers.ProgressSubscriber;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.lang.ref.SoftReference;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author: gh
 * @description:http交互处理类
 * @date: 2018/5/14.
 * @from:
 */
public class HttpManager {

    private volatile static HttpManager INSTANCE;

    //使用cookie而不适用token的情况
    private CookieManger cookieManger;

    //构造方法私有
    private HttpManager() {
    }

    //获取单例
    public static HttpManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 处理http请求
     *
     * @param baseApi 封装的请求数据
     */
    public ProgressSubscriber doHttpDeal(BaseApi baseApi) {
        //手动创建一个OkHttpClient并设置超时时间缓存等设置
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        setHttps(builder);
        setCookieManager(builder);

        builder.connectTimeout(baseApi.getConnectionTime(), TimeUnit.SECONDS);
//        builder.addInterceptor(new NetInterceptor());
        builder.addInterceptor(new CookieInterceptor(baseApi.isCache(), baseApi.getUrl()));
        if (RxRetrofitApp.isDebug()) {
            builder.addInterceptor(getHttpLoggingInterceptor());
        }

        //创建retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseApi.getBaseUrl())
                .build();

        //rx处理
        ProgressSubscriber subscriber = new ProgressSubscriber(baseApi);
        Observable observable = baseApi.getObservable(retrofit)
                //失败后的retry配置
                .retryWhen(new RetryWhenNetworkException(baseApi.getRetryCount(),
                        baseApi.getRetryDelay(), baseApi.getRetryIncreaseDelay()))
                //生命周期管理
                .compose(baseApi.getRxAppCompatActivity().bindUntilEvent(ActivityEvent.PAUSE))
                //http请求线程
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(baseApi);

        /*链接式对象返回*/
        SoftReference<HttpOnNextListener> httpOnNextListener = baseApi.getListener();
        if (httpOnNextListener != null && httpOnNextListener.get() != null) {
            httpOnNextListener.get().onNext(observable);
        }

        /*数据回调*/
        observable.subscribe(subscriber);

        return subscriber;

    }

    /**
     * 日志输出
     * 自行判定是否添加
     * @return
     */
    private HttpLoggingInterceptor getHttpLoggingInterceptor(){
        //日志显示级别
        HttpLoggingInterceptor.Level level= HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor=new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("RxRetrofit","Retrofit====Message:"+message);
            }
        });
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
    }

    /**
     * 信任证书1
     * 信任所有证书
     * @param builder
     */
    private void setHttps(OkHttpClient.Builder builder) {
        //忽略证书
        //https证书_1信任所有
        builder.sslSocketFactory(TrustAllCerts.createSSLSocketFactory());
        builder.hostnameVerifier(new TrustAllCerts.TrustAllHostnameVerifier());
    }

    /**
     * 使用cookie而不适用token的情况
     * @param builder
     */
    private void setCookieManager(OkHttpClient.Builder builder) {
        cookieManger = new CookieManger(RxRetrofitApp.getApplication());
        builder.cookieJar(cookieManger);
    }

    /**
     * 信任证书2
     * 信任针对证书
     * @param builder
     */
    private void setHttps2(OkHttpClient.Builder builder) {
        //https证书_2针对证书
        builder.sslSocketFactory(RxRetrofitApp.getSslParams().sSLSocketFactory,RxRetrofitApp.getSslParams().trustManager);

//        ClearableCookieJar cookieJar =
//                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(RxRetrofitApp.getApplication()));
//        ClearableCookieJar cookieJar=
//                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(RxRetrofitApp.getApplication()));
//        builder.cookieJar(cookieJar);

//        builder.cookieJar(new CookieJar() {
//            private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
//
//            @Override
//            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//                cookieStore.put(url, cookies);
//            }
//
//            @Override
//            public List<Cookie> loadForRequest(HttpUrl url) {
//                List<Cookie> cookies = cookieStore.get(url);
//                return cookies != null ? cookies : new ArrayList<Cookie>();
//            }
//        });
//
//        builder
//                .cookieJar(new CookieJar() {
//                    private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
//
//                    @Override
//                    public void saveFromResponse(HttpUrl url, List cookies) {
//                        cookieStore.put(url, cookies);
//                    }
//
//                    @Override
//                    public List loadForRequest(HttpUrl url) {
//                        List cookies = cookieStore.get(url);
//                        return cookies != null ? cookies : new ArrayList();
//                    }
//                });
//
//        builder.cookieJar(new JavaNetCookieJar(RxRetrofitApp.getApplication()));
//
//        builder.addInterceptor(new AddCookiesInterceptor());
//        builder.addInterceptor(new ReceivedCookiesInterceptor());
    }

    /**
     * 清空Cookie
     */
    public void clearAllCookies() {
        cookieManger.clearAll();
    }

}
