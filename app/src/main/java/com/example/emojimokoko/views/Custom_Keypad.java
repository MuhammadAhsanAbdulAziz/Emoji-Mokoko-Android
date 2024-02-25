package com.example.emojimokoko.views;

import android.annotation.SuppressLint;
import android.content.ClipDescription;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emojimokoko.R;
import com.example.emojimokoko.adapters.CategoryStickerAdapter;
import com.example.emojimokoko.adapters.GifAdapter;
import com.example.emojimokoko.adapters.StickerAdapter;
import com.example.emojimokoko.utils.IconGroup2;
import com.example.emojimokoko.utils.SqliteManager;
import com.example.emojimokoko.utils.UtilManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.UUID;

public class Custom_Keypad extends InputMethodService {
    StickerAdapter stickerAdapter;
    CategoryStickerAdapter categoryStickerAdapter;
    GifAdapter gifAdapter;
    MediaPlayer mediaPlayer;
    RecyclerView stickerRecyclerView, previousStickerRecyclerView, gifRecyclerView, categoryRecyclerView;
    LinearLayout settingTab;
    ImageView settingBtn, deleteBtn, gifBtn, emojiBtn;
    CardView emojiCard, gifCard, appColor;
    View stickerPickerView;
    ImageView appBtn;
    LinearLayout keyboard, dialogLayout, keyboardLayout;
    private InputMethodManager inputMethodManager;
    private final SqliteManager sqliteManager = new SqliteManager(this);
    public ArrayList<Integer> allEmojis = new ArrayList<>();
    int buttonColor = Color.parseColor("#0057cc");


    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        SharedPreferences sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences2.registerOnSharedPreferenceChangeListener(listener2);
        stickerPickerView = getLayoutInflater().inflate(R.layout.sticker_picker, null);
        previousStickerRecyclerView = stickerPickerView.findViewById(R.id.previousRecyclerView);


    }

    @SuppressLint({"InflateParams"})
    @Override
    public View onCreateInputView() {
        stickerPickerView = getLayoutInflater().inflate(R.layout.sticker_picker, null);
        settingBtn = stickerPickerView.findViewById(R.id.settingbtn);
        stickerRecyclerView = stickerPickerView.findViewById(R.id.stickerRecyclerView);
        gifRecyclerView = stickerPickerView.findViewById(R.id.gifRecyclerView);
        previousStickerRecyclerView = stickerPickerView.findViewById(R.id.previousRecyclerView);
        settingTab = stickerPickerView.findViewById(R.id.settingtab);
        gifBtn = stickerPickerView.findViewById(R.id.gifbtn);
        emojiBtn = stickerPickerView.findViewById(R.id.emojibtn);
        emojiCard = stickerPickerView.findViewById(R.id.emojicard);
        gifCard = stickerPickerView.findViewById(R.id.gifcard);
        mediaPlayer = MediaPlayer.create(this, R.raw.song); // Replace 'your_sound' with the actual sound file name
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        categoryRecyclerView = stickerPickerView.findViewById(R.id.categoryRecyclerView);
        previous();
        dialogLayout = stickerPickerView.findViewById(R.id.dialoglayout);
        keyboardLayout = stickerPickerView.findViewById(R.id.keyboardlayout);
        deleteBtn = stickerPickerView.findViewById(R.id.deletebtn);
        appBtn = stickerPickerView.findViewById(R.id.appbtn);
        keyboard = stickerPickerView.findViewById(R.id.keyboard);
        appColor = stickerPickerView.findViewById(R.id.appcolor);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
                vibrateOnEmojiPress();
                categoryRecyclerView.setVisibility(View.GONE);
                emojiCard.setCardBackgroundColor(Color.WHITE);
                emojiBtn.setImageDrawable(getResources().getDrawable(R.drawable.blackemoji));
                gifRecyclerView.setVisibility(View.GONE);
                stickerRecyclerView.setVisibility(View.VISIBLE);
                gifCard.setCardBackgroundColor(Color.WHITE);
                allEmojis = getEmoji();
                stickerAdapter = new StickerAdapter(getApplicationContext(), allEmojis, new StickerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String str) {

                        sendStickerToWhatsApp(str);
                        mediaPlayer.start();
                        vibrateOnEmojiPress();
                    }
                });
                stickerRecyclerView.setAdapter(stickerAdapter);
            }
        });

        emojiBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
                vibrateOnEmojiPress();
                stickerRecyclerView.setVisibility(View.VISIBLE);
                gifRecyclerView.setVisibility(View.GONE);

                gifCard.setCardBackgroundColor(Color.WHITE);

                if (categoryRecyclerView.getVisibility() == View.GONE) {
                    categoryRecyclerView.setVisibility(View.VISIBLE);
                    emojiCard.setCardBackgroundColor(buttonColor);
                    emojiBtn.setImageDrawable(getResources().getDrawable(R.drawable.baseline_emoji_emotions_24));
                } else if (categoryRecyclerView.getVisibility() == View.VISIBLE) {
                    categoryRecyclerView.setVisibility(View.GONE);
                    emojiCard.setCardBackgroundColor(Color.WHITE);
                    emojiBtn.setImageDrawable(getResources().getDrawable(R.drawable.blackemoji));
                }
            }
        });
        gifBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
                vibrateOnEmojiPress();
                if (stickerRecyclerView.getVisibility() == View.VISIBLE) {
                    stickerRecyclerView.setVisibility(View.GONE);
                    gifRecyclerView.setVisibility(View.VISIBLE);
                    gifCard.setCardBackgroundColor(buttonColor);
                    emojiCard.setCardBackgroundColor(Color.WHITE);
                    emojiBtn.setImageDrawable(getResources().getDrawable(R.drawable.blackemoji));
                    ArrayList<Integer> arr = new ArrayList<>();
                    arr.add(R.raw.finchat);
                    arr.add(R.raw.heartmokoko);
                    arr.add(R.raw.worship);
                    gifAdapter = new GifAdapter(getApplicationContext(), arr, new GifAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendGifToWhatsApp(Integer.parseInt(str));
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    gifRecyclerView.setAdapter(gifAdapter);
                }
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
                vibrateOnEmojiPress();
                toggleKeyboards();
            }
        });
        allEmojis = getEmoji();
        stickerAdapter = new StickerAdapter(this, allEmojis, new StickerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String str) {

                sendStickerToWhatsApp(str);
                mediaPlayer.start();
                vibrateOnEmojiPress();
            }
        });

        getCategoryEmojis();

        if (UtilManager.getDefaults("Recently Emoji", getApplicationContext()) != null) {
            if (UtilManager.getDefaults("Recently Emoji", getApplicationContext()).equals("YES")) {
                previousStickerRecyclerView.setVisibility(View.VISIBLE);
            } else {
                previousStickerRecyclerView.setVisibility(View.GONE);
            }
        } else {
            previousStickerRecyclerView.setVisibility(View.GONE);
        }

        categoryRecyclerView.setAdapter(categoryStickerAdapter);
        stickerRecyclerView.setAdapter(stickerAdapter);

        return stickerPickerView;
    }

    private void getCategoryEmojis() {
        ArrayList<Integer> categoryArray = new ArrayList<>();
        categoryArray.add(R.raw.dim2_emoji_a_07);
        categoryArray.add(R.raw.dim2_emoji_a_01);
        categoryArray.add(R.raw.dim2_emoji_a_19);
        categoryArray.add(R.raw.dim2_emoji_a_1_58);
        categoryArray.add(R.raw.dim2_emoji_a_27);
        categoryArray.add(R.raw.dim2_emoji_a_33);
        categoryArray.add(R.raw.dim2_emoji_a_41);
        categoryArray.add(R.raw.dim2_emoji_a_49);
        categoryArray.add(R.raw.dim2_emoji_a_59);
        categoryArray.add(R.raw.dim2_emoji_a_1_03);
        categoryArray.add(R.raw.dim2_emoji_a_1_12);
        categoryArray.add(R.raw.dim2_emoji_a_1_25);
        categoryArray.add(R.raw.dim2_emoji_a_1_33);
        categoryStickerAdapter = new CategoryStickerAdapter(getApplicationContext(), categoryArray, new CategoryStickerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String str) {
                mediaPlayer.start();
                vibrateOnEmojiPress();
                int resourceId = Integer.parseInt(str);
                if (resourceId == R.raw.dim2_emoji_a_07) {
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroup2.recommendationGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(str);
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                } else if (resourceId == R.raw.dim2_emoji_a_01) {
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroup2.coolGuyEntersGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(str);
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                } else if (resourceId == R.raw.dim2_emoji_a_19) {
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroup2.iLoveYouGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(str);
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                } else if (resourceId == R.raw.dim2_emoji_a_1_58) {
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroup2.veryLightGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(str);
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                } else if (resourceId == R.raw.dim2_emoji_a_27) {
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroup2.mayhemGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(str);
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                } else if (resourceId == R.raw.dim2_emoji_a_33) {
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroup2.prayingGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(str);
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                } else if (resourceId == R.raw.dim2_emoji_a_41) {
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroup2.phantomGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(str);
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                } else if (resourceId == R.raw.dim2_emoji_a_49) {
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroup2.thumbLostGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(str);
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                } else if (resourceId == R.raw.dim2_emoji_a_59) {
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroup2.mellowGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(str);
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                } else if (resourceId == R.raw.dim2_emoji_a_1_03) {
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroup2.wowGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(str);
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                } else if (resourceId == R.raw.dim2_emoji_a_1_12) {
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroup2.justTwoOfUsGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(str);
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                } else if (resourceId == R.raw.dim2_emoji_a_1_25) {
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroup2.decayingGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(str);
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                } else if (resourceId == R.raw.dim2_emoji_a_1_33) {
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroup2.gwentGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(str);
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                }
            }
        });

    }

    private void sendStickerToWhatsApp(String stickerResource) {

        if (UtilManager.getDefaults("Recently Emoji", this) != null) {
            if (UtilManager.getDefaults("Recently Emoji", this).equals("YES")) {
                int stickerId = getResources().getIdentifier(stickerResource, "raw", getApplicationContext().getPackageName());
                sqliteManager.addEmoji(String.valueOf(stickerId), stickerResource);
                previous();
            }
        }
        InputConnection inputConnection = getCurrentInputConnection();
        if (isWhatsAppSupportsCommitContent(inputConnection)) {
            try {
                ClipDescription description;
                File mWebpFile;
                if (isWhatsAppSupported(inputConnection)) {
                    description = new ClipDescription("Sticker", new String[]{"image/webp.wasticker"});
                    String newStickerResource = stickerResource.replace("dim2", "dim1");
                    int stickerId = getResources().getIdentifier(newStickerResource, "raw", getApplicationContext().getPackageName());
                    mWebpFile = getFileForResource(this, stickerId);
                } else {
                    description = new ClipDescription("Sticker", new String[]{"image/png"});
                    int stickerId = getResources().getIdentifier(stickerResource, "raw", getApplicationContext().getPackageName());
                    mWebpFile = getFileForResource(this, stickerId);
                }
                if (mWebpFile == null) {
                    return;
                }
                Uri contentUri;
                contentUri = FileProvider.getUriForFile(this, "com.example.emojimokoko.provider", mWebpFile);
                final int flag = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
                InputContentInfoCompat inputContentInfo = new InputContentInfoCompat(contentUri, description, null);
                InputConnectionCompat.commitContent(inputConnection, getCurrentInputEditorInfo(), inputContentInfo, flag, null);
            } catch (Exception e) {
                Log.e("StickerSendDebug", "Error sending sticker: " + e.getMessage());
            }
        } else {
            Log.e("StickerSendDebug", "WhatsApp does not support direct sticker sending");
        }
    }

    private void sendGifToWhatsApp(int gifResource) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (isWhatsAppSupportsCommitContent(inputConnection)) {
            try {
                File gifFile = getGifFileForResource(this, gifResource);
                if (gifFile == null) {
                    return;
                }

                Uri contentUri = FileProvider.getUriForFile(this, "com.example.emojimokoko.provider", gifFile);
                final int flag = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;

                ClipDescription description = new ClipDescription("GIF", new String[]{"image/gif"});
                InputContentInfoCompat inputContentInfo = new InputContentInfoCompat(contentUri, description, null);

                InputConnectionCompat.commitContent(inputConnection, getCurrentInputEditorInfo(), inputContentInfo, flag, null);
            } catch (Exception e) {
                Log.e("GifSendDebug", "Error sending GIF: " + e.getMessage());
            }
        } else {
            Log.e("GifSendDebug", "WhatsApp does not support direct GIF sending");
        }
    }

    private File getFileForResource(@NonNull Context context, @RawRes int res) {
        String name = generateShortUUID();
        File outputFile;
        outputFile = new File(getFilesDir(), name + ".png");
        final byte[] buffer = new byte[4096];
        InputStream resourceReader = null;
        try {
            try {
                resourceReader = context.getResources().openRawResource(res);
                OutputStream dataWriter = null;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dataWriter = Files.newOutputStream(outputFile.toPath());
                    }
                    while (true) {
                        final int numRead = resourceReader.read(buffer);
                        if (numRead <= 0) {
                            break;
                        }
                        assert dataWriter != null;
                        dataWriter.write(buffer, 0, numRead);
                    }
                    return outputFile;
                } finally {
                    if (dataWriter != null) {
                        dataWriter.flush();
                        dataWriter.close();
                    }
                }
            } finally {
                if (resourceReader != null) {
                    resourceReader.close();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

    private File getGifFileForResource(@NonNull Context context, @RawRes int res) {
        UUID uuid = UUID.randomUUID();
        String name = uuid.toString();
        File outputFile = new File(getFilesDir(), name + ".gif");
        final byte[] buffer = new byte[4096];
        InputStream resourceReader = null;
        try {
            try {
                resourceReader = context.getResources().openRawResource(res);
                OutputStream dataWriter = null;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dataWriter = Files.newOutputStream(outputFile.toPath());
                    }
                    while (true) {
                        final int numRead = resourceReader.read(buffer);
                        if (numRead <= 0) {
                            break;
                        }
                        assert dataWriter != null;
                        dataWriter.write(buffer, 0, numRead);
                    }
                    return outputFile;
                } finally {
                    if (dataWriter != null) {
                        dataWriter.flush();
                        dataWriter.close();
                    }
                }
            } finally {
                if (resourceReader != null) {
                    resourceReader.close();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

    boolean isWhatsAppSupportsCommitContent(InputConnection inputConnection) {
        if (inputConnection == null) return false;
        EditorInfo editorInfo = getCurrentInputEditorInfo();
        if (editorInfo == null) return false;
        String[] supportedMimeTypes = EditorInfoCompat.getContentMimeTypes(editorInfo);
        for (String mimeType : supportedMimeTypes) {
            if (ClipDescription.compareMimeTypes(mimeType, "image/*")) {
                return true;
            }
        }
        return false;
    }

    boolean isWhatsAppSupported(InputConnection inputConnection) {
        if (inputConnection == null) return false;
        EditorInfo editorInfo = getCurrentInputEditorInfo();
        if (editorInfo == null) return false;
        String[] supportedMimeTypes = EditorInfoCompat.getContentMimeTypes(editorInfo);
        for (String mimeType : supportedMimeTypes) {
            if (ClipDescription.compareMimeTypes(mimeType, "image/webp.wasticker")) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Integer> getEmoji() {

        ArrayList<Integer> arr;
        arr = getSpecificRawResourceIds(this);
        return arr;
    }

    @SuppressLint({"DefaultLocale", "DiscouragedApi"})
    public ArrayList<Integer> getSpecificRawResourceIds(Context context) {
        ArrayList<Integer> specificResourceIds = new ArrayList<>();
        Resources resources = context.getResources();
        String packageName = context.getPackageName();
        String resourceName;
        for (int i = 1; i <= 64; i++) {
            resourceName = "dim2" + "_" + "emoji" + "_" + "a" + "_" + String.format("%02d", i);
            int resourceId = resources.getIdentifier(resourceName, "raw", packageName);
            specificResourceIds.add(resourceId);
        }

        for (int i = 1; i <= 64; i++) {
            resourceName = "dim2" + "_" + "emoji" + "_" + "a" + "_" + "1" + "_" + String.format("%02d", i);
            int resourceId = resources.getIdentifier(resourceName, "raw", packageName);
            specificResourceIds.add(resourceId);
        }

        return specificResourceIds;
    }

    private void vibrateOnEmojiPress() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            long[] pattern = {0, 50}; // Vibrate for 50 milliseconds
            vibrator.vibrate(pattern, -1); // -1 means do not repeat
        }
    }

    private void previous() {
        stickerAdapter = new StickerAdapter(getApplicationContext(), sqliteManager.readEmoji(), new StickerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String str) {
                mediaPlayer.start();
                vibrateOnEmojiPress();
                sendStickerToWhatsApp(str);

            }
        });
        previousStickerRecyclerView.setAdapter(stickerAdapter);
    }

    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("process") || key.equals("app")) {
                allEmojis = getEmoji();
                stickerAdapter.setData(allEmojis);
                stickerAdapter.notifyDataSetChanged();
                changeImages();
            }
        }
    };
    SharedPreferences.OnSharedPreferenceChangeListener listener2 = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("Recently Emoji")) {
                if (UtilManager.getDefaults("Recently Emoji", getApplicationContext()) != null) {
                    if (UtilManager.getDefaults("Recently Emoji", getApplicationContext()).equals("YES")) {
                        previousStickerRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        previousStickerRecyclerView.setVisibility(View.GONE);
                    }
                } else {
                    previousStickerRecyclerView.setVisibility(View.GONE);
                }
            }
        }
    };

    @SuppressLint("UseCompatLoadingForDrawables")
    private void changeImages() {
        if (UtilManager.getDefaults("process", this) != null) {
            if (UtilManager.getDefaults("process", this).equals("Messages")) {
                appBtn.setImageDrawable(getResources().getDrawable(R.drawable.massenger));
                keyboard.setBackgroundColor(Color.parseColor("#f0fef2"));
                appColor.setCardBackgroundColor(Color.parseColor("#dae6f7"));
                buttonColor = Color.parseColor("#0057cc");
            } else if (UtilManager.getDefaults("process", this).equals("WhatsApp")) {
                appBtn.setImageDrawable(getResources().getDrawable(R.drawable.whatsapp));
                keyboard.setBackgroundColor(Color.parseColor("#f9fefb"));
                appColor.setCardBackgroundColor(Color.parseColor("#e9fbf0"));
                buttonColor = Color.parseColor("#25d366");

            } else if (UtilManager.getDefaults("process", this).equals("Messenger")) {
                appBtn.setImageDrawable(getResources().getDrawable(R.drawable.facebook));
                keyboard.setBackgroundColor(Color.parseColor("#f3f8fc"));
                appColor.setCardBackgroundColor(Color.parseColor("#dae7f7"));
                buttonColor = Color.parseColor("#17a9fd");

            } else if (UtilManager.getDefaults("process", this).equals("Instagram")) {
                appBtn.setImageDrawable(getResources().getDrawable(R.drawable.instagram));
                keyboard.setBackgroundColor(Color.parseColor("#fcf8f9"));
                appColor.setCardBackgroundColor(Color.parseColor("#fbe7e9"));
                buttonColor = Color.parseColor("#f34e56");

            } else if (UtilManager.getDefaults("process", this).equals("Telegram")) {
                appBtn.setImageDrawable(getResources().getDrawable(R.drawable.telegram));
                keyboard.setBackgroundColor(Color.parseColor("#f3f8fc"));
                appColor.setCardBackgroundColor(Color.parseColor("#dae7f7"));
                buttonColor = Color.parseColor("#17a9fd");
            } else if (UtilManager.getDefaults("process", this).equals("Discord")) {
                appBtn.setImageDrawable(getResources().getDrawable(R.drawable.discord));
                keyboard.setBackgroundColor(Color.parseColor("#f7f7fd"));
                appColor.setCardBackgroundColor(Color.parseColor("#e6e8fc"));
                buttonColor = Color.parseColor("#5865f2");
                keyboardLayout.setVisibility(View.GONE);
                dialogLayout.setVisibility(View.VISIBLE);
                keyboard.setBackgroundColor(Color.TRANSPARENT);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        keyboardLayout.setVisibility(View.VISIBLE);
                        dialogLayout.setVisibility(View.GONE);
                        keyboard.setBackgroundColor(Color.parseColor("#f0fef2"));
                    }
                }, 5000);

            } else if (UtilManager.getDefaults("process", this).equals("X")) {
                appBtn.setImageDrawable(getResources().getDrawable(R.drawable.twitter));
                keyboard.setBackgroundColor(Color.parseColor("#f4f3f3"));
                appColor.setCardBackgroundColor(Color.parseColor("#dedede"));
                buttonColor = Color.parseColor("#000");
            } else if (UtilManager.getDefaults("process", this).equals("Tiktok")) {
                appBtn.setImageDrawable(getResources().getDrawable(R.drawable.tiktok));
                keyboard.setBackgroundColor(Color.parseColor("#f4f3f3"));
                appColor.setCardBackgroundColor(Color.parseColor("#dedede"));
                buttonColor = Color.parseColor("#000");
            }
        }
    }

    private void toggleKeyboards() {
        // Check if the current input method is your custom keyboard
        if (isCustomKeyboardActive()) {
            // Switch to the default system keyboard
            inputMethodManager.showInputMethodPicker();
        } else {
            // Switch back to your custom keyboard
            inputMethodManager.showInputMethodPicker();
        }
    }

    private boolean isCustomKeyboardActive() {
        String currentInputMethod = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        return currentInputMethod.equals(getPackageName() + "/" + Custom_Keypad.class.getName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            inputConnection.finishComposingText();
            inputConnection.commitText("", 0);
        }
        File filesDir = getFilesDir(); // Get the files directory
        if (filesDir.isDirectory()) {
            File[] files = filesDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    // Delete each file
                    if (file.delete()) {
                        Log.d("Deletion of file", "Deleting");
                    } else {
                        Log.d("Deletion of file", "Not Deleting");
                    }
                }
            }
        }

    }

    @Override
    public void onWindowHidden() {
        super.onWindowHidden();
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            inputConnection.finishComposingText();
            inputConnection.commitText("", 0);
        }
        File filesDir = getFilesDir(); // Get the files directory
        if (filesDir.isDirectory()) {
            File[] files = filesDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    // Delete each file
                    if (file.delete()) {
                        Log.d("Deletion of file", "Deleting");
                    } else {
                        Log.d("Deletion of file", "Not Deleting");
                    }
                }
            }
        }
    }

    public String generateShortUUID() {
        UUID uuid = UUID.randomUUID();
        long mostSignificantBits = uuid.getMostSignificantBits();
        long leastSignificantBits = uuid.getLeastSignificantBits();

        // Concatenate both parts and convert to a base 36 string
        String combinedBits = Long.toString(mostSignificantBits, 36) + Long.toString(leastSignificantBits, 36);

        // Take the first 7 characters, or adjust for 8 characters if needed
        return combinedBits.substring(0, 7);
    }
}
