package com.gh.retrofittools.listener;

/**
 * @author: gh
 * @description:
 * @date: 2018/5/14.
 * @from:
 */
public interface DownloadProgressListener {
    /**
     * 下载进度
     * @param read
     * @param count
     * @param done
     */
    void update(long read, long count, boolean done);
}
