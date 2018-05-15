package com.gh.retrofittools.listener;

import io.reactivex.Observable;

/**
 * @author: gh
 * @description:
 * @date: 2018/5/14.
 * @from:
 */
public abstract class HttpOnNextListener<T> {

    //无网络情况下在无网缓存时间内走缓存(网络请求失败)
    public static final int NONET_TOCACHE = 1;
    //有网络情况下在有网缓存时间内走缓存(未进行网络请求)
    public static final int HAVENET_TOCACHE = 2;

    /**
     * 成功后回调方法
     *
     * @param t
     */
    public abstract void onNext(T t);

    /**
     * 緩存回調結果
     *
     * @param string
     */
    public void onCacheNext(String string, int cacheType) {

    }

    /**
     * 成功后的ober返回，扩展链接式调用
     *
     * @param observable
     */
    public void onNext(Observable observable) {

    }

    /**
     * 失败或者错误方法
     * 主动调用，更加灵活
     *
     * @param e
     */
    public void onError(Throwable e) {

    }

    /**
     * 取消回調
     */
    public void onCancel() {

    }

}
