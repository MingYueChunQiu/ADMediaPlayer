package com.zhuolong.admediaplayer.controller;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.SurfaceHolder;

import com.zhuolong.admediaplayer.constants.ExceptionConstants;
import com.zhuolong.admediaplayer.constants.StateConstants;
import com.zhuolong.admediaplayer.exception.MediaPlayerException;
import com.zhuolong.admediaplayer.listener.OnPlayControlListener;
import com.zhuolong.admediaplayer.listener.OnPlayStateChangeListener;
import com.zhuolong.admediaplayer.model.handler.VideoPlayerHandler;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by 明月春秋 on 2017/10/29.
 * 视频播放管理器
 * 控制MediaPlayer、监听器、处理器等之间的相互配合
 */

public class VideoPlayerManager {

    private MediaPlayer mediaPlayer;//播放器
    private OnPlayControlListener onPlayControlListener;//播放控制监听器
    private OnPlayStateChangeListener onPlayStateChangeListener;//播放状态监听器
    private SurfaceHolder surfaceHolder;//SurfaceView控制器
    private String videoPath;//播放视频路径
    private Uri videoUri;//播放视频网址
    private Context context;//播放的上下文
    private FileDescriptor fileDescriptor;//文件描述符
    private long offset, length;//文件偏移量和长度
    private AssetFileDescriptor assetFileDescriptor;//资源文件描述符
    private boolean isLooping = false;//视频播放是否循环
    private int currentState = -1;//当前的播放状态
    //分发handler，用于将子线程的用户处理发送到主线程更新UI
    private Handler deliveryHandler = new Handler(Looper.getMainLooper());
    private VideoPlayerHandler videoPlayerHandler = null;//处理播放相关的处理器
    private boolean isPrepared = false;//标记是否资源已准备好
    private int isStarted = 0;//标记是否开始播放

    /**
     * 传入相关参数，完成播放器初始化
     * @param surfaceHolder
     *          SurfaceView控制器
     * @param videoPath
     *          播放视频路径
     * @param isLooping
     *          视频是否循环播放
     * @param onPlayControlListener
     *          播放控制监听器
     * @param onPlayStateChangeListener
     *          播放相关状态监听器
     */
    public VideoPlayerManager(SurfaceHolder surfaceHolder, String videoPath, boolean isLooping,
                              OnPlayControlListener onPlayControlListener,
                              OnPlayStateChangeListener onPlayStateChangeListener){
        this.surfaceHolder = surfaceHolder;
        this.videoPath = videoPath;
        this.isLooping = isLooping;
        this.onPlayControlListener = onPlayControlListener;
        this.onPlayStateChangeListener = onPlayStateChangeListener;
        mediaPlayer = new MediaPlayer();
        initMediaPlayer();
    }

    /**
     * 传入相关参数，完成播放器初始化
     * @param surfaceHolder
     *          SurfaceView控制器
     * @param videoPath
     *          播放视频路径
     * @param isLooping
     *          视频是否循环播放
     */
    public VideoPlayerManager(SurfaceHolder surfaceHolder, String videoPath, boolean isLooping){
        this(surfaceHolder, videoPath, isLooping, null, null);
    }

    /**
     * 传入相关参数，完成播放器初始化
     * @param surfaceHolder
     *          SurfaceView控制器
     * @param context
     *          上下文
     * @param videoUri
     *          视频网址
     * @param isLooping
     *          视频是否循环播放
     * @param onPlayControlListener
     *          播放控制监听器
     * @param onPlayStateChangeListener
     *          播放相关状态监听器
     */
    public VideoPlayerManager(SurfaceHolder surfaceHolder, Context context, Uri videoUri, boolean isLooping,
                              OnPlayControlListener onPlayControlListener,
                              OnPlayStateChangeListener onPlayStateChangeListener){
        this.surfaceHolder = surfaceHolder;
        this.context = context;
        this.videoUri = videoUri;
        this.isLooping = isLooping;
        this.onPlayControlListener = onPlayControlListener;
        this.onPlayStateChangeListener = onPlayStateChangeListener;
        mediaPlayer = new MediaPlayer();
        initMediaPlayer();
    }

    /**
     * 传入相关参数，完成播放器初始化
     * @param surfaceHolder
     *          SurfaceView控制器
     * @param context
     *          上下文
     * @param videoUri
     *          视频网址
     * @param isLooping
     *          视频是否循环播放
     */
    public VideoPlayerManager(SurfaceHolder surfaceHolder, Context context, Uri videoUri, boolean isLooping){
        this(surfaceHolder, context, videoUri, isLooping, null, null);
    }

    /**
     * 传入相关参数，完成播放器初始化
     * @param surfaceHolder
     *          SurfaceView控制器
     * @param fileDescriptor
     *          文件描述符
     * @param isLooping
     *          视频是否循环播放
     * @param onPlayControlListener
     *          播放控制监听器
     * @param onPlayStateChangeListener
     *          播放相关状态监听器

    public VideoPlayerManager(SurfaceHolder surfaceHolder, FileDescriptor fileDescriptor, boolean isLooping,
                              OnPlayControlListener onPlayControlListener,
                              OnPlayStateChangeListener onPlayStateChangeListener){
        this(surfaceHolder, fileDescriptor, 0, 0,
                isLooping,
                onPlayControlListener,
                onPlayStateChangeListener);
    }*/

    /**
     * 传入相关参数，完成播放器初始化
     * @param surfaceHolder
     *          SurfaceView控制器
     * @param fileDescriptor
     *          文件描述符
     * @param isLooping
     *          视频是否循环播放

    public VideoPlayerManager(SurfaceHolder surfaceHolder, FileDescriptor fileDescriptor, boolean isLooping){
        this(surfaceHolder, fileDescriptor, isLooping, null, null);
    }*/

    /**
     * 传入相关参数，完成播放器初始化
     * 直接跟资源文件描述符相关的MediaPlayer构造函数方法会报错，所以内部调用MediaPlayer的文件描述符构造函数方法
     * @param surfaceHolder
     *          SurfaceView控制器
     * @param assetFileDescriptor
     *          资源文件描述符
     * @param isLooping
     *          视频是否循环播放
     * @param onPlayControlListener
     *          播放控制监听器
     * @param onPlayStateChangeListener
     *          播放相关状态监听器
     */
    public VideoPlayerManager(SurfaceHolder surfaceHolder, AssetFileDescriptor assetFileDescriptor, boolean isLooping,
                              OnPlayControlListener onPlayControlListener,
                              OnPlayStateChangeListener onPlayStateChangeListener) {
        this.surfaceHolder = surfaceHolder;
        this.assetFileDescriptor = assetFileDescriptor;
        this.isLooping = isLooping;
        this.onPlayControlListener = onPlayControlListener;
        this.onPlayStateChangeListener = onPlayStateChangeListener;
        mediaPlayer = new MediaPlayer();
        initMediaPlayer();
    }

    /**
     * 传入相关参数，完成播放器初始化
     * @param surfaceHolder
     *          SurfaceView控制器
     * @param assetFileDescriptor
     *          资源文件描述符
     * @param isLooping
     *          视频是否循环播放
     * @throws MediaPlayerException
     *          如果用户运行Android版本低于24，则使用该构造函数会抛出异常
     */
    public VideoPlayerManager(SurfaceHolder surfaceHolder, AssetFileDescriptor assetFileDescriptor,
                              boolean isLooping) throws MediaPlayerException {
        this(surfaceHolder, assetFileDescriptor, isLooping, null, null);
    }

    /**
     * 传入相关参数，完成播放器初始化
     * @param surfaceHolder
     *          SurfaceView控制器
     * @param fileDescriptor
     *          文件描述符
     * @param offset
     * 			文件偏移量
     * @param length
     * 			文件长度
     * @param isLooping
     *          视频是否循环播放
     * @param onPlayControlListener
     *          播放控制监听器
     * @param onPlayStateChangeListener
     *          播放相关状态监听器
     */
    public VideoPlayerManager(SurfaceHolder surfaceHolder, FileDescriptor fileDescriptor,
                              long offset, long length,
                              boolean isLooping,
                              OnPlayControlListener onPlayControlListener,
                              OnPlayStateChangeListener onPlayStateChangeListener){
        this.surfaceHolder = surfaceHolder;
        this.fileDescriptor = fileDescriptor;
        this.offset = offset;
        this.length = length;
        this.isLooping = isLooping;
        this.onPlayControlListener = onPlayControlListener;
        this.onPlayStateChangeListener = onPlayStateChangeListener;
        mediaPlayer = new MediaPlayer();
        initMediaPlayer();
    }

    /**
     * 传入相关参数，完成播放器初始化
     * @param surfaceHolder
     *          SurfaceView控制器
     * @param fileDescriptor
     *          文件描述符
     * @param offset
     * 			文件偏移量
     * @param length
     * 			文件长度
     * @param isLooping
     *          视频是否循环播放
     */
    public VideoPlayerManager(SurfaceHolder surfaceHolder, FileDescriptor fileDescriptor,
                              long offset, long length,
                              boolean isLooping){
        this(surfaceHolder, fileDescriptor, offset, length, isLooping, null, null);
    }

    /**
     * 获取当前播放相关状态
     * @return
     *          返回播放相关状态
     */
    public int getPlayerState(){
        return currentState;
    }

    /**
     * 判断播放器是否能播放
     * 根据当前播放相关的状态量进行判断
     * @return
     *          返回判断结果
     *          true：能播放
     *          false：不能播放
     */
    public boolean isCanPlay(){
        if (mediaPlayer == null ||
                currentState == StateConstants.STATE_ERROR ||
                currentState == StateConstants.STATE_RELEASE){
            return false;
        }
        return true;
    }

    /**
     * 初始化播放器，设置播放准备、完成、错误等监听事件
     */
    private void initMediaPlayer(){
        if (mediaPlayer == null){
            return;
        }
        mediaPlayer.reset();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //使用MediaPlayer的setDisplay需要在SurfeView创建或准备好后执行
                mediaPlayer.setDisplay(surfaceHolder);
                setCurrentPlayerState(StateConstants.STATE_PREPARED);
                isPrepared = true;
                Message message = videoPlayerHandler.obtainMessage(StateConstants.STATE_PREPARED);
                //通知处理器：0 表示没有开始播放	1 表示开始播放
                message.arg1 = isStarted;
                videoPlayerHandler.sendMessage(message);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setCurrentPlayerState(StateConstants.STATE_COMPLETION);
                Message message = videoPlayerHandler.obtainMessage(StateConstants.STATE_COMPLETION);
                videoPlayerHandler.sendMessage(message);
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                //此方法在准备开始前必然会被调用一次Error(0,0)
                setCurrentPlayerState(StateConstants.STATE_ERROR);
                Message message = videoPlayerHandler.obtainMessage(StateConstants.STATE_ERROR);
                message.arg1 = what;
                message.arg2 = extra;
                videoPlayerHandler.sendMessage(message);
                return true;
            }
        });
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                setCurrentPlayerState(StateConstants.STATE_SIZE_CHANGED);
                Message message = videoPlayerHandler.obtainMessage(StateConstants.STATE_ERROR);
                message.arg1 = width;
                message.arg2 = height;
                videoPlayerHandler.sendMessage(message);
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                setCurrentPlayerState(StateConstants.STATE_BUFFER_UPDATE);
                Message message = videoPlayerHandler.obtainMessage(StateConstants.STATE_BUFFER_UPDATE);
                videoPlayerHandler.sendMessage(message);
            }
        });
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                setCurrentPlayerState(StateConstants.STATE_SEEKED);
                Message message = videoPlayerHandler.obtainMessage(StateConstants.STATE_SEEKED);
                videoPlayerHandler.sendMessage(message);
            }
        });
        try {
            if (videoPath != null) {
                mediaPlayer.setDataSource(videoPath);
            }
            if (context != null && videoUri != null) {
                mediaPlayer.setDataSource(context, videoUri);
            }
            if (fileDescriptor != null) {
                mediaPlayer.setDataSource(fileDescriptor, offset, length);
            }
            if (assetFileDescriptor != null){
                //如果Android运行版本大于等于24，则运行此代码
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    try {
                        mediaPlayer.setDataSource(assetFileDescriptor);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                                assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        initializePlayer();
        videoPlayerHandler = new VideoPlayerHandler(this,
                mediaPlayer, deliveryHandler,
                onPlayControlListener, onPlayStateChangeListener);
        mediaPlayer.prepareAsync();
    }

    /**
     * 提供给用户调用的接口，为直接播放模式
     */
    public void startPlayer(){
        startPlayer(false);
    }

    /**
     * 真正执行播放功能的私有方法，不对用户开放
     * 开始播放视频，并设置当前播放管理器状态为正在播放
     * 如果视频正在播放，停止并重置播放器
     * @param isRestart
     * 			标记是否重新开始播放视频
     */
    private void startPlayer(final boolean isRestart){
        if (!isCanPlay()){
            return;
        }
        if (mediaPlayer.isPlaying() && !isRestart){
            return;
        }
        if (!isPrepared){
            // 不是第一次播放，不能重置，reset()后MediaPlayer会进入idle状态，
            // 在setDataSource()会进入到initial状态，此时才能调用prepare()或prepareAsync()
            // mediaPlayer.reset();
            isStarted = 1;//通知播放管理器需要进行播放
            mediaPlayer.prepareAsync();
        }
        else {
            setCurrentPlayerState(StateConstants.STATE_STARTED);
            Message message = videoPlayerHandler.obtainMessage(StateConstants.STATE_STARTED);
            videoPlayerHandler.sendMessage(message);
        }
    }

    /**
     * 重新开始播放视频
     */
    public void reStartPlayer(){
        if (!isCanPlay()){
            return;
        }
        //如果是播放中按重放键，则直接将视频重置到0位置处
        if (isPrepared){
            setPlayerPosition(0);
        }
        startPlayer(true);
        setCurrentPlayerState(StateConstants.STATE_RESTART);
        Message message = videoPlayerHandler.obtainMessage(StateConstants.STATE_RESTART);
        videoPlayerHandler.sendMessage(message);
    }

    /**
     * 设置当前播放相关状态
     * @param state
     *          设置的播放状态
     */
    private void setCurrentPlayerState(int state){
        currentState = state;
    }

    /**
     * 暂停播放
     */
    public void pausePlayer(){
        if (mediaPlayer == null || !mediaPlayer.isPlaying()){
            return;
        }
        mediaPlayer.pause();
        isStarted = 0;
        setCurrentPlayerState(StateConstants.STATE_PAUSE);
        Message message = videoPlayerHandler.obtainMessage(StateConstants.STATE_PAUSE);
        videoPlayerHandler.sendMessage(message);
    }

    /**
     * 停止播放
     */
    public void stopPlayer(){
        if (mediaPlayer == null){
            return;
        }
        mediaPlayer.stop();
        isPrepared = false;
        isStarted = 0;
        setCurrentPlayerState(StateConstants.STATE_STOP);
        Message message = videoPlayerHandler.obtainMessage(StateConstants.STATE_STOP);
        videoPlayerHandler.sendMessage(message);

    }

    /**
     * 重置播放器
     */
    public void resetPlayer(){
        if (mediaPlayer == null){
            return;
        }
        mediaPlayer.reset();
        isPrepared = false;
        isStarted = 0;
        setCurrentPlayerState(StateConstants.STATE_RESET);
        Message message = videoPlayerHandler.obtainMessage(StateConstants.STATE_RESET);
        videoPlayerHandler.sendMessage(message);
    }

    /**
     * 释放播放器
     */
    public void releasePlayer(){
        stopPlayer();
        if (mediaPlayer == null){
            return;
        }
        mediaPlayer.release();
        setCurrentPlayerState(StateConstants.STATE_RELEASE);
        Message message = videoPlayerHandler.obtainMessage(StateConstants.STATE_RELEASE);
        videoPlayerHandler.sendMessage(message);
        //清理所有资源
        clearResource();
    }

    /**
     * 清理重置所有资源
     */
    private void clearResource() {
        mediaPlayer = null;
        videoPath = null;
        videoUri = null;
        fileDescriptor = null;
        assetFileDescriptor = null;
        surfaceHolder = null;
        videoPlayerHandler = null;
        deliveryHandler = null;
        onPlayControlListener = null;
        onPlayStateChangeListener = null;
        isPrepared = false;
        isStarted = 0;
    }

    /**
     * 获取当前播放位置
     * @return
     *          返回当前播放位置
     */
    public int getPlayerCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 获取播放总时长
     * @return
     *          返回播放视频时长
     */
    public int getPlayerDuration(){
        return mediaPlayer.getDuration();
    }

    /**
     * 初始化MediaPlayer播放属性
     */
    private void initializePlayer(){
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setScreenOnWhilePlaying(true);//设置播放时屏幕常亮
        mediaPlayer.setLooping(isLooping);
    }

    /**
     * 提供设置正在播放状态
     * 避免用户直接调用状态修改方法
     */
    public void setStartState(){
        setCurrentPlayerState(StateConstants.STATE_STARTED);
    }

    /**
     * 定位播放器的播放位置
     * @param position
     * 			定位的播放位置
     */
    public void setPlayerPosition(int position){
        mediaPlayer.seekTo(position);
    }

    /**
     * 设置播放控制监听器
     * 并传给分发处理器
     * @param onPlayControlListener
     *          传入的播放控制监听器
     */
    public void setOnPlayControlListener(OnPlayControlListener onPlayControlListener){
        this.onPlayControlListener = onPlayControlListener;
        videoPlayerHandler.setOnPlayControlListener(onPlayControlListener);
    }

    /**
     * 设置播放状态监听器
     * 并传给分发处理器
     * @param onPlayStateChangeListener
     *          传入的播放状态监听器
     */
    public void setOnPlayStateChangeListener(OnPlayStateChangeListener onPlayStateChangeListener){
        this.onPlayStateChangeListener = onPlayStateChangeListener;
        videoPlayerHandler.setOnPlayStateChangeListener(onPlayStateChangeListener);
    }

    /**
     * 设置视频文件路径
     * 重新设置文件资源时，MediaPlayer的create方法里会自动调用prepare()，
     * prepare()里不能change the audio stream type，要求必须与初次文件的资源相符
     * 所以如果传入其他类型资源会直接报运行时异常
     * @param videoPath
     *          传入的视频文件路径
     * @throws MediaPlayerException
     *          抛出资源设置方法不同造成的异常
     */
    public void setVideoPath(String videoPath) throws MediaPlayerException {
        if (videoUri != null || fileDescriptor != null || assetFileDescriptor != null){
            throw new MediaPlayerException(ExceptionConstants.RESOURCE_SET_METHOD_ERROR_TYPE,
                    ExceptionConstants.RESOURCE_SET_METHOD_ERROR);
        }
        resetInit();
        this.videoPath = videoPath;
        try {
            mediaPlayer.setDataSource(videoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置视频地址
     * 重新设置文件资源时，MediaPlayer的create方法里会自动调用prepare()，
     * prepare()里不能change the audio stream type，要求必须与初次文件的资源相符
     * 所以如果传入其他类型资源会直接报运行时异常
     * @param videoUri
     *          传入的视频地址
     * @throws MediaPlayerException
     *          抛出资源设置方法不同造成的异常
     */
    public void setVideoUri(Uri videoUri) throws MediaPlayerException {
        if (videoPath != null || fileDescriptor != null || assetFileDescriptor != null){
            throw new MediaPlayerException(ExceptionConstants.RESOURCE_SET_METHOD_ERROR_TYPE,
                    ExceptionConstants.RESOURCE_SET_METHOD_ERROR);
        }
        resetInit();
        this.videoUri = videoUri;
        try {
            mediaPlayer.setDataSource(context, videoUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置文件描述符
     * 重新设置文件资源时，MediaPlayer的create方法里会自动调用prepare()，
     * prepare()里不能change the audio stream type，要求必须与初次文件的资源相符
     * 所以如果传入其他类型资源会直接报运行时异常
     * @param fileDescriptor
     *          传入的文件描述符

    public void setFileDescriptor(FileDescriptor fileDescriptor) throws MediaPlayerException {
        FileInputStream fis = new FileInputStream(fileDescriptor);
        FileChannel fileChannel = fis.getChannel();
        try {
            setFileDescriptor(fileDescriptor, 0, fileChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileChannel.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    /**
     * 设置文件描述符
     * 重新设置文件资源时，MediaPlayer的create方法里会自动调用prepare()，
     * prepare()里不能change the audio stream type，要求必须与初次文件的资源相符
     * 所以如果传入其他类型资源会直接报运行时异常
     * @param fileDescriptor
     *          传入的文件描述符
     * @param offset
     *          文件描述符偏移量
     * @param length
     *          文件描述符大小
     * @throws MediaPlayerException
     *          抛出资源设置方法不同造成的异常
     */
    public void setFileDescriptor(FileDescriptor fileDescriptor, long offset, long length) throws MediaPlayerException {
        if (videoPath != null || videoUri != null || assetFileDescriptor != null){
            throw new MediaPlayerException(ExceptionConstants.RESOURCE_SET_METHOD_ERROR_TYPE,
                    ExceptionConstants.RESOURCE_SET_METHOD_ERROR);
        }
        resetInit();
        this.fileDescriptor = fileDescriptor;
        this.offset = offset;
        this.length = length;
        try {
            mediaPlayer.setDataSource(fileDescriptor, offset, length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置资源文件描述符
     * 重新设置文件资源时，MediaPlayer的create方法里会自动调用prepare()，
     * prepare()里不能change the audio stream type，要求必须与初次文件的资源相符
     * 所以如果传入其他类型资源会直接报运行时异常
     * @param assetFileDescriptor
     *          传入的资源文件描述符
     * @throws MediaPlayerException
     *          抛出资源设置方法不同造成的异常
     */
    public void setAssetFileDescriptor(AssetFileDescriptor assetFileDescriptor) throws MediaPlayerException {
        if (videoPath != null || videoUri != null || fileDescriptor != null){
            throw new MediaPlayerException(ExceptionConstants.RESOURCE_SET_METHOD_ERROR_TYPE,
                    ExceptionConstants.RESOURCE_SET_METHOD_ERROR);
        }
        resetInit();
        this.assetFileDescriptor = assetFileDescriptor;
        try {
            //如果Android版本大于等于24，才能使用资源文件描述符
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaPlayer.setDataSource(assetFileDescriptor);
            }
            else {
                mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                        assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重新初始化播放器相关
     */
    private void resetInit(){
        stopPlayer();
        mediaPlayer.seekTo(0);
    }

    /**
     * 获取视频是否正在播放
     * @return
     *          返回判断结果
     *          true：视频正在播放
     *          false：视频未播放
     */
    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
}
