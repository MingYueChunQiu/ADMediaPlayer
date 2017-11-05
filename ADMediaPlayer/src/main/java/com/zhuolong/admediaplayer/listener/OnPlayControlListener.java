package com.zhuolong.admediaplayer.listener;

import android.media.MediaPlayer;

/**
 * Created by 明月春秋 on 2017/10/29.
 * 播放控制监听器
 * 继承自Listener接口
 * 所有回调方法都被包装成异步方法，并在主线程执行
 */

public interface OnPlayControlListener extends Listener{

    //开始播放
    public void onStart(MediaPlayer mediaPlayer);

    //播放暂停
    public void onPause(MediaPlayer mediaPlayer);

    //播放停止
    public void onStop(MediaPlayer mediaPlayer);

    //重置播放器
    public void onReset(MediaPlayer mediaPlayer);

    //释放播放器
    public void onRelease(MediaPlayer mediaPlayer);

    //重新开始播放
    public void onRestart(MediaPlayer mediaPlayer);
}
