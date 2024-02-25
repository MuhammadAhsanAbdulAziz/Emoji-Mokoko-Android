package com.example.emojimokoko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.VideoView;

import com.example.emojimokoko.utils.SqliteManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final SqliteManager sqliteManager = new SqliteManager(this);
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String ADD_EMOJI_FLAG = "AddEmojiFlag";
    Button  mobilesetting,setting;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mobilesetting = findViewById(R.id.btnGoMobileSetting);
        setting = findViewById(R.id.setting);
        videoView = findViewById(R.id.idVideoView2);
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
                Intent intent = new Intent(MainActivity.this, FullscreenVideoActivity.class);
                intent.putExtra("videoResourceId", R.raw.test);
                intent.putExtra("activity", "main");
                startActivity(intent);
                finish();
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextScreen = new Intent(MainActivity.this, settings.class);
                startActivity(nextScreen);
                finish();
            }
        });

        mobilesetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
                    startActivity(intent);

            }
        });
        boolean addEmojiFlag = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(ADD_EMOJI_FLAG, false);

        if (!addEmojiFlag) {
            List<Integer> rawResourceIds = getAllRawResourceIds(this);
            for (Integer resourceId : rawResourceIds) {
                String entryName = getResources().getResourceEntryName(resourceId);
                sqliteManager.addAllEmoji(String.valueOf(resourceId), entryName);
            }
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putBoolean(ADD_EMOJI_FLAG, true).apply();
        }


    }

    public List<Integer> getAllRawResourceIds(Context context) {
        List<Integer> rawResourceIds = new ArrayList<>();
        Resources resources = context.getResources();
        String packageName = context.getPackageName();

        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int resourceId = resources.getIdentifier(fieldName, "raw", packageName);
            rawResourceIds.add(resourceId);
        }

        return rawResourceIds;
    }
}