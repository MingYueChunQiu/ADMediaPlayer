Android Dexterous MediaPlayerr：对Android多媒体播放器进行封装的简单易用框架。


通过播放器控制类持有MediaPlayer、监听器、handler等，进行协调工作，实现播放、暂停、停止等，并对播放动作和播放状态进行监听和回调。

使用示例：

1.创建视频播放控制器，可以使用传入监听器的构造方法，也可以稍后创建设置。
VideoPlayerManager videoPlayerManager = new VideoPlayerManager(svDisplay.getHolder(), this, uri, true);

2.设置播放动作控制监听器
videoPlayerManager.setOnPlayControlListener(new OnPlayControlListener() {
            @Override
            public void onStart(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "开始", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPause(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "暂停", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStop(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "停止", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReset(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "重置", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRelease(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "释放", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRestart(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "重放", Toast.LENGTH_SHORT).show();
            }
        });

3.设置播放状态监听器
videoPlayerManager.setOnPlayStateChangeListener(new OnPlayStateChangeListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "准备好了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "已完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(MediaPlayer mediaPlayer, int what, int extra) {
                Toast.makeText(MainActivity.this, "发生错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
                Toast.makeText(MainActivity.this, "大小发生改变", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "缓冲更新", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSeeked(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "定位完成", Toast.LENGTH_SHORT).show();
            }
        });