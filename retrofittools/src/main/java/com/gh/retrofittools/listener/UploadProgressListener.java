package com.gh.retrofittools.listener;

/**
 * @author: gh
 * @description:
 * @date: 2018/5/15.
 * @from:
 */
public interface UploadProgressListener {
    /**
     * 上传进度
     * @param currentBytesCount
     * @param totalBytesCount
     */
    void onProgress(long currentBytesCount, long totalBytesCount);
}
