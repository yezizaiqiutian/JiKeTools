package com.gh.retrofittools.exception;

/**
 * @author: gh
 * @description:
 * @date: 2018/5/14.
 * @from:
 */
public class HttpTimeException extends RuntimeException {

    public static final int NO_DATA = 0x2;

    //没有网络
    public static final String MSG_NO_NET = "网络中断，请检查您的网络状态";

    public HttpTimeException(int resultCode) {
        this(getApiExceptionMessage(resultCode));
    }

    public HttpTimeException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * 转换错误数据
     *
     * @param code
     * @return
     */
    private static String getApiExceptionMessage(int code) {
        String message = "";
        switch (code) {
            case NO_DATA:
                message = "无数据";
                break;
            default:
                message = "error";
                break;

        }
        return message;
    }
}
