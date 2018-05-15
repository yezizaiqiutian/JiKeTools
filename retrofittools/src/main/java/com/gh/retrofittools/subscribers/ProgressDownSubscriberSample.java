package com.gh.retrofittools.subscribers;

import android.os.Handler;

import com.gh.retrofittools.listener.DownloadProgressListener;
import com.gh.retrofittools.listener.HttpDownOnNextListener;

import java.lang.ref.SoftReference;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author: gh
 * @description:
 * @date: 2018/5/14.
 * @from:
 */
public class ProgressDownSubscriberSample<T> implements Observer<T>,DownloadProgressListener {

    //弱引用结果回调
    private SoftReference<HttpDownOnNextListener> mSubscriberOnNextListener;
    private Handler handler;
    private Disposable disposable;

    public ProgressDownSubscriberSample(HttpDownOnNextListener listener, Handler handler) {
        this.mSubscriberOnNextListener = new SoftReference<>(listener);
        this.handler = handler;
    }

    public void setListener(HttpDownOnNextListener listener) {
        this.mSubscriberOnNextListener = new SoftReference<>(listener);
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;

        if(mSubscriberOnNextListener.get()!=null){
            mSubscriberOnNextListener.get().onStart();
        }
    }

    @Override
    public void onComplete() {
        if(mSubscriberOnNextListener.get()!=null){
            mSubscriberOnNextListener.get().onComplete();
        }
    }

    @Override
    public void onError(Throwable e) {
        if(mSubscriberOnNextListener.get()!=null){
            mSubscriberOnNextListener.get().onError(e);
        }
    }

    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onNext(t);
        }
    }

    @Override
    public void update(final long read, final long count, boolean done) {
        if (mSubscriberOnNextListener.get() == null /*|| !downInfo.isUpdateProgress()*/) return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                mSubscriberOnNextListener.get().updateProgress(read, count);
            }
        });
    }

    public void unsubscribe() {
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }

}
