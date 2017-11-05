package com.zhuolong.admediaplayer.listener;

import android.media.MediaPlayer;

/**
 * Created by 明月春秋 on 2017/10/29.
 * 播放状态改变监听器
 * 继承自Listener接口
 * 所有回调方法都被包装成异步方法，并在主线程执行
 */

public interface OnPlayStateChangeListener extends Listener {

    //播放器准备好
    public void onPrepared(MediaPlayer mediaPlayer);

    //播放完成
    public void onCompletion(MediaPlayer mediaPlayer);

    //播放发生错误
    public void onError(MediaPlayer mediaPlayer, int what, int extra);

    //播放界面大小发生变化
    public void onSizeChanged(MediaPlayer mediaPlayer, int width, int height);

    //资源开始异步准备
    public void onBufferingUpdate(MediaPlayer mediaPlayer);

    //播放位置调整完毕
    public void onSeeked(MediaPlayer mediaPlayer);

}
