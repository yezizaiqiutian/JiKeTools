package com.gh.retrofittools.http;

import android.os.Handler;
import android.os.Looper;

import com.gh.retrofittools.bean.DownLoadApkInfo;
import com.gh.retrofittools.exception.HttpTimeException;
import com.gh.retrofittools.exception.RetryWhenNetworkException;
import com.gh.retrofittools.interceptor.DownloadInterceptor;
import com.gh.retrofittools.service.HttpDownService;
import com.gh.retrofittools.subscribers.ProgressDownSubscriberSample;
import com.gh.retrofittools.utils.AppUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author: gh
 * @description:
 * @date: 2018/5/15.
 * @from:简单的下载管理可以用于下载Apk等
 */
public class HttpDownManagerSimple {
    /*单利对象*/
    private volatile static HttpDownManagerSimple INSTANCE;
    /*下载进度回掉主线程*/
    private Handler handler;

    private HttpDownManagerSimple() {
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static HttpDownManagerSimple getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpDownManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpDownManagerSimple();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 开始下载
     */
    public void startDown(final DownLoadApkInfo info) {
        /*添加回调处理类*/
        ProgressDownSubscriberSample subscriber = new ProgressDownSubscriberSample(info.getListener(), handler);
        DownloadInterceptor interceptor = new DownloadInterceptor(subscriber);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //手动创建一个OkHttpClient并设置超时时间
        builder.connectTimeout(6, TimeUnit.SECONDS);
        builder.addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(AppUtils.getBasUrl(info.getUrl()))
                .build();
        HttpDownService httpService = retrofit.create(HttpDownService.class);
        /*得到rx对象-上一次下載的位置開始下載*/
        httpService.download("bytes=" + 0 + "-", info.getUrl())
                /*指定线程*/
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                /*失败后的retry配置*/
                .retryWhen(new RetryWhenNetworkException())
                /*读取下载写入文件*/
                .map(new Function<ResponseBody, DownLoadApkInfo>() {
                    @Override
                    public DownLoadApkInfo apply(ResponseBody responseBody) {
                        writeCaches(responseBody, new File(info.getSavePath()), info);
                        return info;
                    }
                })
                /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread())
                /*数据回调*/
                .subscribe(subscriber);

    }

    /**
     * 写入文件
     *
     * @param file
     * @param info
     * @throws IOException
     */
    public void writeCaches(ResponseBody responseBody, File file, DownLoadApkInfo info) {
        try {
            RandomAccessFile randomAccessFile = null;
            FileChannel channelOut = null;
            InputStream inputStream = null;
            try {
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                long allLength = 0 == info.getCountLength() ? responseBody.contentLength() : info.getReadLength() + responseBody
                        .contentLength();

                inputStream = responseBody.byteStream();
                randomAccessFile = new RandomAccessFile(file, "rwd");
                channelOut = randomAccessFile.getChannel();
                MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE,
                        info.getReadLength(), allLength - info.getReadLength());
                byte[] buffer = new byte[1024 * 4];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    mappedBuffer.put(buffer, 0, len);
                }
            } catch (IOException e) {
                throw new HttpTimeException(e.getMessage());
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (channelOut != null) {
                    channelOut.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            }
        } catch (IOException e) {
            throw new HttpTimeException(e.getMessage());
        }
    }

}
