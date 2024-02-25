package com.example.emojimokoko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.VideoView;

import com.example.emojimokoko.utils.UtilManager;

public class settings extends AppCompatActivity {
    ImageView Backbtn;
    SwitchCompat notificationSwitch;
    SwitchCompat EmojiSwitch;
    SwitchCompat RecentlyEmojiSwitch;
    SwitchCompat themeSwitch;
    VideoView videoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_settings);
        Backbtn = findViewById(R.id.backBtn);
        notificationSwitch = findViewById(R.id.switch1);
        EmojiSwitch = findViewById(R.id.switch2);
        RecentlyEmojiSwitch = findViewById(R.id.switch3);
        themeSwitch = findViewById(R.id.switch4);
        videoView = findViewById(R.id.idVideoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.test);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(settings.this, FullscreenVideoActivity.class);
                intent.putExtra("videoResourceId", R.raw.test);
                intent.putExtra("activity","setting");
                startActivity(intent);
                finish();
            }
        });

        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showInputMethodPicker();
//        if(UtilManager.getDefaults("setkeyboard", getApplicationContext()) == null) {
//            InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//            inputMethodManager.showInputMethodPicker();
//            UtilManager.setDefaults("setkeyboard", "YES", getApplicationContext());
//        }

        if(UtilManager.getDefaults("Emoji",this)!=null)
        {
            if(UtilManager.getDefaults("Emoji",this).equals("YES"))
            {
                EmojiSwitch.setChecked(true);
            }
        }
        if(UtilManager.getDefaults("Recently Emoji",this)!=null)
        {
            if(UtilManager.getDefaults("Recently Emoji",this).equals("YES"))
            {
                RecentlyEmojiSwitch.setChecked(true);
            }
        }
        if(UtilManager.getDefaults("Notification",this)!=null)
        {
            if(UtilManager.getDefaults("Notification",this).equals("YES"))
            {
                notificationSwitch.setChecked(true);
            }
        }

        if(UtilManager.getDefaults("Theme",this)!=null)
        {
            if(UtilManager.getDefaults("Theme",this).equals("YES"))
            {
                themeSwitch.setChecked(true);
            }
        }

        EmojiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked())
                {
                    UtilManager.setDefaults("Emoji","YES",getApplicationContext());
                }
                else{
                    UtilManager.setDefaults("Emoji","NO",getApplicationContext());
                }
            }
        });

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked())
                {
                    UtilManager.setDefaults("Theme","YES",getApplicationContext());
                }
                else{
                    UtilManager.setDefaults("Theme","NO",getApplicationContext());
                }
            }
        });

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked())
                {
                    UtilManager.setDefaults("Notification","YES",getApplicationContext());
                }
                else{
                    UtilManager.setDefaults("Notification","NO",getApplicationContext());
                }
            }
        });
        RecentlyEmojiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked())
                {
                    UtilManager.setDefaults("Recently Emoji","YES",getApplicationContext());
                }
                else{
                    UtilManager.setDefaults("Recently Emoji","NO",getApplicationContext());
                }
            }
        });

        RecentlyEmojiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked())
                {
                    UtilManager.setDefaults("Recently Emoji","YES",getApplicationContext());
                }
                else{
                    UtilManager.setDefaults("Recently Emoji","NO",getApplicationContext());
                }
            }
        });


        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // or perform any action you want
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent nextScreen = new Intent(this, MainActivity.class);
        startActivity(nextScreen);
        finish();
    }
}