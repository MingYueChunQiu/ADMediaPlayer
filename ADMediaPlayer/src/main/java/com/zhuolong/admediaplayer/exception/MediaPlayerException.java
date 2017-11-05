package com.zhuolong.admediaplayer.exception;

/**
 * Created by 明月春秋 on 2017/10/29.
 */

public class MediaPlayerException extends Exception {

    private int exceptionType;//异常类型
    private String exceptionMsg;//异常信息

    public int getExceptionType() {
        return exceptionType;
    }

    public String getExceptionMsg() {
        return exceptionMsg;
    }

    /***
     * 根据传入的异常类型与信息构建OkHttp类型的异常
     * @param type
     *              异常类型
     * @param msg
     *              异常信息
     */
    public MediaPlayerException(int type, String msg){
        exceptionType = type;
        exceptionMsg = msg;
    }
}
