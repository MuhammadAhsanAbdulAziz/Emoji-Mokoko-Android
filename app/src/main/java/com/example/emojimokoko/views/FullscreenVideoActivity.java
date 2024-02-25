package com.example.emojimokoko.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import com.example.emojimokoko.R;

public class FullscreenVideoActivity extends AppCompatActivity {

    VideoView fullscreenVideoView;
    String activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_video);
        fullscreenVideoView = findViewById(R.id.fullscreenVideoView);
        int videoResourceId = getIntent().getIntExtra("videoResourceId", 0);
        activity = getIntent().getStringExtra("activity");

        if (videoResourceId != 0) {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResourceId);
            fullscreenVideoView.setVideoURI(uri);
            fullscreenVideoView.start();
            fullscreenVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent;
        if(activity.equals("main")) {
            intent = new Intent(this, MainActivity.class);
        }
        else {
            intent = new Intent(this, settings.class);
        }
        startActivity(intent);
        finish();
    }
}