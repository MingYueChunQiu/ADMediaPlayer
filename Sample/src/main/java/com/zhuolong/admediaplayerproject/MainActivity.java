package com.zhuolong.admediaplayerproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zhuolong.admediaplayer.controller.VideoPlayerManager;

public class MainActivity extends AppCompatActivity {

    private VideoPlayerManager videoPlayerManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
