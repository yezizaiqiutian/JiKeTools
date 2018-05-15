package com.gh.retrofittools.subscribers;

import android.os.Handler;

import com.gh.retrofittools.bean.DownInfo;
import com.gh.retrofittools.bean.DownState;
import com.gh.retrofittools.http.HttpDownManager;
import com.gh.retrofittools.listener.DownloadProgressListener;
import com.gh.retrofittools.listener.HttpDownOnNextListener;
import com.gh.retrofittools.utils.DbDownUtil;

import java.lang.ref.SoftReference;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author: gh
 * @description:
 * @date: 2018/5/14.
 * @from:
 */
public class ProgressDownSubscriber<T> implements Observer<T>,DownloadProgressListener {

    //弱引用结果回调
    private SoftReference<HttpDownOnNextListener> mSubscriberOnNextListener;
    /*下载数据*/
    private DownInfo downInfo;
    private Handler handler;

    private Disposable disposable;

    public ProgressDownSubscriber(DownInfo downInfo, Handler handler) {
        this.mSubscriberOnNextListener = new SoftReference<>(downInfo.getListener());
        this.downInfo = downInfo;
        this.handler = handler;
    }

    public void setDownInfo(DownInfo downInfo) {
        this.mSubscriberOnNextListener = new SoftReference<>(downInfo.getListener());
        this.downInfo=downInfo;
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;

        if(mSubscriberOnNextListener.get()!=null){
            mSubscriberOnNextListener.get().onStart();
        }
        downInfo.setState(DownState.START);
    }

    @Override
    public void onComplete() {
        if(mSubscriberOnNextListener.get()!=null){
            mSubscriberOnNextListener.get().onComplete();
        }
        HttpDownManager.getInstance().remove(downInfo);
        downInfo.setState(DownState.FINISH);
        DbDownUtil.getInstance().update(downInfo);
    }

    @Override
    public void onError(Throwable e) {
        if(mSubscriberOnNextListener.get()!=null){
            mSubscriberOnNextListener.get().onError(e);
        }
        HttpDownManager.getInstance().remove(downInfo);
        downInfo.setState(DownState.ERROR);
        DbDownUtil.getInstance().update(downInfo);
    }

    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onNext(t);
        }
    }

    @Override
    public void update(long read, long count, boolean done) {
        if (downInfo.getCountLength() > count) {
            read = downInfo.getCountLength() - count + read;
        } else {
            downInfo.setCountLength(count);
        }
        downInfo.setReadLength(read);

        if (mSubscriberOnNextListener.get() == null || !downInfo.isUpdateProgress()) return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                /*如果暂停或者停止状态延迟，不需要继续发送回调，影响显示*/
                if (downInfo.getState() == DownState.PAUSE || downInfo.getState() == DownState.STOP) return;
                downInfo.setState(DownState.DOWN);
                mSubscriberOnNextListener.get().updateProgress(downInfo.getReadLength(), downInfo.getCountLength());
            }
        });
    }

    public void unsubscribe() {
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }

}
