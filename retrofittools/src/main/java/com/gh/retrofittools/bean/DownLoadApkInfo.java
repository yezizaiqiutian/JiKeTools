package com.gh.retrofittools.bean;

import com.gh.retrofittools.listener.HttpDownOnNextListener;

/**
 * @author: gh
 * @description:
 * @date: 2018/5/15.
 * @from:
 */
public class DownLoadApkInfo {

    /*存储位置*/
    private String savePath;
    /*文件总长度*/
    private long countLength;
    /*下载长度*/
    private long readLength;
    /*回调监听*/
    private HttpDownOnNextListener listener;
    /*超时设置*/
    private  int connectonTime=6;
    /*url*/
    private String url;
    /*是否需要实时更新下载进度,避免线程的多次切换*/
    private boolean updateProgress;

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public long getCountLength() {
        return countLength;
    }

    public void setCountLength(long countLength) {
        this.countLength = countLength;
    }

    public long getReadLength() {
        return readLength;
    }

    public void setReadLength(long readLength) {
        this.readLength = readLength;
    }

    public HttpDownOnNextListener getListener() {
        return listener;
    }

    public void setListener(HttpDownOnNextListener listener) {
        this.listener = listener;
    }

    public int getConnectonTime() {
        return connectonTime;
    }

    public void setConnectonTime(int connectonTime) {
        this.connectonTime = connectonTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isUpdateProgress() {
        return updateProgress;
    }

    public void setUpdateProgress(boolean updateProgress) {
        this.updateProgress = updateProgress;
    }
}
