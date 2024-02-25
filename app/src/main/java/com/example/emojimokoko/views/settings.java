package com.example.emojimokoko.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;

import com.example.emojimokoko.R;
import com.example.emojimokoko.utils.UtilManager;

public class settings extends AppCompatActivity {
    ImageView backBtn;
    SwitchCompat notificationSwitch;
    SwitchCompat RecentlyEmojiSwitch;
    SwitchCompat themeSwitch;
    ImageButton closeBtn;
    VideoView videoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_settings);
        backBtn = findViewById(R.id.backBtn);
        notificationSwitch = findViewById(R.id.switch1);
        RecentlyEmojiSwitch = findViewById(R.id.switch3);
        themeSwitch = findViewById(R.id.switch4);
        videoView = findViewById(R.id.idVideoView22);
        closeBtn = findViewById(R.id.closebtnn);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.showInputMethodPicker();
//            }
//        },1000);

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
                ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
                layoutParams.height = 1000;
                videoView.setLayoutParams(layoutParams);
                closeBtn.setVisibility(View.VISIBLE);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
                layoutParams.height = 350;
                videoView.setLayoutParams(layoutParams);
                closeBtn.setVisibility(View.GONE);
            }
        });

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

        if(UtilManager.getDefaults("theme",this)!=null)
        {
            if(UtilManager.getDefaults("theme",this).equals("open")) {
                themeSwitch.setChecked(true);
            }
            else {
                themeSwitch.setChecked(false);
            }
        }
        else {
            themeSwitch.setChecked(false);
        }

        themeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UtilManager.getDefaults("theme",getApplicationContext())!=null) {
                    if(UtilManager.getDefaults("theme",getApplicationContext()).equals("close")) {
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                        UtilManager.setDefaults("theme", "open", getApplicationContext());
                    }
                    else {
                        UtilManager.setDefaults("theme", "close", getApplicationContext());
                    }
                }
                else {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                    UtilManager.setDefaults("theme", "open", getApplicationContext());
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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // or perform any action you want
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}