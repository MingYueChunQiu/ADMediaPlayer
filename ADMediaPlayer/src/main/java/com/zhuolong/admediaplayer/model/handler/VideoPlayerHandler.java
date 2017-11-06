package com.zhuolong.admediaplayer.model.handler;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;

import com.zhuolong.admediaplayer.constants.StateConstants;
import com.zhuolong.admediaplayer.controller.VideoPlayerManager;
import com.zhuolong.admediaplayer.listener.OnPlayControlListener;
import com.zhuolong.admediaplayer.listener.OnPlayStateChangeListener;

/**
 * Created by 明月春秋 on 2017/10/31.
 * 视频播放处理器
 * 对播放相关的各种状态进行处理
 */

public class VideoPlayerHandler extends Handler {

    private VideoPlayerManager videoPlayerManager = null;//播放管理器
    private MediaPlayer mediaPlayer = null;//需要处理的播放器
    private Handler deliveryHandler = null;//分发handler，用于将子线程的用户处理发送到主线程更新UI
    private OnPlayControlListener onPlayControlListener = null;//播放控制监听器
    private OnPlayStateChangeListener onPlayStateChangeListener = null;//播放状态监听器

    /**
     * 初始化视频播放处理器
     * @param videoPlayerManager
     *          视频播放管理器
     * @param mediaPlayer
     *          多媒体播放器
     * @param deliveryHandler
     *          分发处理器
     * @param onPlayControlListener
     *          播放控制监听器
     * @param onPlayStateChangeListener
     *          播放状态监听器
     */
    public VideoPlayerHandler(VideoPlayerManager videoPlayerManager,
                              MediaPlayer mediaPlayer,
                              Handler deliveryHandler,
                              OnPlayControlListener onPlayControlListener,
                              OnPlayStateChangeListener onPlayStateChangeListener){
        this.videoPlayerManager = videoPlayerManager;
        this.mediaPlayer = mediaPlayer;
        this.deliveryHandler = deliveryHandler;
        this.onPlayControlListener = onPlayControlListener;
        this.onPlayStateChangeListener = onPlayStateChangeListener;
    }

    @Override
    public void handleMessage(final Message msg) {
        switch (msg.what){
            case StateConstants.STATE_PREPARED:
                deliveryHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!judgeOnPlayStateChangeListener()){
                            return;
                        }
                        onPlayStateChangeListener.onPrepared(mediaPlayer);
                    }
                });
                int isStarted = msg.arg1;
                if (isStarted == 1){
                    mediaPlayer.seekTo(0);
                    videoPlayerManager.setStartState();
                    Message message = this.obtainMessage(StateConstants.STATE_STARTED);
                    this.sendMessage(message);
                }
                break;
            case StateConstants.STATE_STARTED:
                mediaPlayer.start();
                if (!judgeOnPlayControlListener()){
                    return;
                }
                deliveryHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPlayControlListener.onStart(mediaPlayer);
                    }
                });
                break;
            case StateConstants.STATE_PAUSE:
                if (!judgeOnPlayControlListener()){
                    return;
                }
                deliveryHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPlayControlListener.onPause(mediaPlayer);
                    }
                });
                break;
            case StateConstants.STATE_STOP:
                if (!judgeOnPlayControlListener()){
                    return;
                }
                deliveryHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPlayControlListener.onStop(mediaPlayer);
                    }
                });
                break;
            case StateConstants.STATE_RESET:
                if (!judgeOnPlayControlListener()){
                    return;
                }
                deliveryHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPlayControlListener.onReset(mediaPlayer);
                    }
                });
                break;
            case StateConstants.STATE_COMPLETION:
                if (!judgeOnPlayStateChangeListener()){
                    return;
                }
                deliveryHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPlayStateChangeListener.onCompletion(mediaPlayer);
                    }
                });
                break;
            case StateConstants.STATE_ERROR:
                //千万不能在此用该方法，否则视频无法播放,因为错误回调在准备开始前必然会被调用一次Error(0,0)
//              mediaPlayer.reset();
                if (!judgeOnPlayStateChangeListener()){
                    return;
                }
                deliveryHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        int what = msg.arg1;
                        int extra = msg.arg2;
                        //错误回调在准备开始前必然会被调用一次Error(0,0)
                        if (what == 0){
                            return;
                        }
//                        mediaPlayer.reset();
                        onPlayStateChangeListener.onError(mediaPlayer, what, extra);
                    }
                });
                break;
            case StateConstants.STATE_RELEASE:
                if (!judgeOnPlayControlListener()){
                    return;
                }
                deliveryHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPlayControlListener.onRelease(mediaPlayer);
                    }
                });
                break;
            case StateConstants.STATE_SIZE_CHANGED:
                if (!judgeOnPlayStateChangeListener()){
                    return;
                }
                deliveryHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        int width = msg.arg1;
                        int height = msg.arg2;
                        onPlayStateChangeListener.onSizeChanged(mediaPlayer, width, height);
                    }
                });
                break;
            case StateConstants.STATE_BUFFER_UPDATE:
                if (!judgeOnPlayStateChangeListener()){
                    return;
                }
                deliveryHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPlayStateChangeListener.onBufferingUpdate(mediaPlayer);
                    }
                });
                break;
            case StateConstants.STATE_RESTART:
                if (!judgeOnPlayControlListener()){
                    return;
                }
                deliveryHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPlayControlListener.onRestart(mediaPlayer);
                    }
                });
                break;
            case StateConstants.STATE_SEEKED:
                if (!judgeOnPlayStateChangeListener()){
                    return;
                }
                deliveryHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPlayStateChangeListener.onSeeked(mediaPlayer);
                    }
                });
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }

    /**
     * 判断播放控制监听器是否为空
     * @return
     * 			返回判断结果
     * 			true：不为空
     * 			false：为空
     */
    private boolean judgeOnPlayControlListener(){
        if (onPlayControlListener == null){
            return false;
        }
        return true;
    }

    /**
     * 判断播放状态监听器是否为空
     * @return
     * 			返回判断结果
     * 			true：不为空
     * 			false：为空
     */
    private boolean judgeOnPlayStateChangeListener(){
        if (onPlayStateChangeListener == null){
            return false;
        }
        return true;
    }

    /**
     * 设置播放控制监听器
     * @param onPlayControlListener
     *          传入的播放控制监听器
     */
    public void setOnPlayControlListener(OnPlayControlListener onPlayControlListener){
        this.onPlayControlListener = onPlayControlListener;
    }

    /**
     * 设置播放状态监听器
     * @param onPlayStateChangeListener
     *          传入的播放状态监听器
     */
    public void setOnPlayStateChangeListener(OnPlayStateChangeListener onPlayStateChangeListener){
        this.onPlayStateChangeListener = onPlayStateChangeListener;
    }
}
