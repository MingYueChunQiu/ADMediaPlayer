package com.zhuolong.admediaplayer.constants;

/**
 * Created by 明月春秋 on 2017/10/29.
 * 播放相关的常量类
 */

public class StateConstants {

    //播放器准备完毕
    public static final int STATE_PREPARED = 0x00;

    //正在播放
    public static final int STATE_STARTED = 0x01;

    //播放暂停
    public static final int STATE_PAUSE = 0x02;

    //播放重置
    public static final int STATE_RESET = 0x03;

    //播放停止
    public static final int STATE_STOP = 0x04;

    //释放播放器
    public static final int STATE_RELEASE = 0x05;

    //播放完成
    public static final int STATE_COMPLETION = 0x06;

    //播放发生错误
    public static final int STATE_ERROR = 0x07;

    //播放窗口大小发生改变
    public static final int STATE_SIZE_CHANGED = 0x08;

    //播放时资源缓冲更新
    public static final int STATE_BUFFER_UPDATE = 0x09;

    //重新开始播放
    public static final int STATE_RESTART = 0x0A;

    //调整播放位置
    public static final int STATE_SEEKED = 0x0B;

}
