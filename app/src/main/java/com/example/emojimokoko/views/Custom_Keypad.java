package com.example.emojimokoko;


import android.annotation.SuppressLint;
import android.content.ClipDescription;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emojimokoko.adapters.CategoryStickerAdapter;
import com.example.emojimokoko.adapters.GifAdapter;
import com.example.emojimokoko.adapters.StickerAdapter;
import com.example.emojimokoko.utils.IconGroups;
import com.example.emojimokoko.utils.SqliteManager;
import com.example.emojimokoko.utils.UtilManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Custom_Keypad extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private KeyboardView stickerKeyboard;
    StickerAdapter stickerAdapter;
    CategoryStickerAdapter categoryStickerAdapter;
    GifAdapter gifAdapter;
    MediaPlayer mediaPlayer;
    RecyclerView stickerRecyclerView, previousstickerRecyclerView,gifRecyclerView,categoryRecyclerView;
    LinearLayout settingtab;
    ImageView settingbtn,deletebtn,gifbtn,emojibtn;
    Keyboard keyboard;
    CardView emojicard,gifcard;
    EditText searchbar;
    private boolean emojiSectionVisible = false;
    View stickerPickerView;
    private InputMethodManager inputMethodManager;
    private final SqliteManager sqliteManager = new SqliteManager(this);
    ArrayList<Integer> allEmojis = new ArrayList<>();

//    private void hideKeyboard() {
//        stickerKeyboard.setVisibility(View.GONE);
//    }

    private void sendStickerToWhatsApp(int stickerResource) {
        if (UtilManager.getDefaults("Recently Emoji", this) != null ) {
            if (UtilManager.getDefaults("Recently Emoji", this).equals("YES")) {
                sqliteManager.addEmoji(String.valueOf(stickerResource), getResources().getResourceEntryName(stickerResource));
                loadprevius();
            }
        }
        InputConnection inputConnection = getCurrentInputConnection();
        if (isWhatsAppSupportsCommitContent(inputConnection)) {
            try {
                File mWebpFile = getFileForResource(this, findRawResourceByDrawable(getApplicationContext(), stickerResource));

                if (mWebpFile == null) {
                    return;
                }
                Uri contentUri = FileProvider.getUriForFile(this, "com.example.emojimokoko.provider", mWebpFile);
                final int flag = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
                ClipDescription description;
                if (UtilManager.getDefaults("Emoji", this) != null) {
                    if (UtilManager.getDefaults("Emoji", this).equals("YES")) {

                        description = new ClipDescription("Sticker", new String[]{"image/webp.wasticker"});
                    }
                    else {
                        description = new ClipDescription("Sticker", new String[]{"image/png"});
                    }
                } else {
                    description = new ClipDescription("Sticker", new String[]{"image/png"});
                }

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
                File gifFile = getFileForResource(this, findRawResourceByDrawable(getApplicationContext(), gifResource));
                if (gifFile == null) {
                    return;
                }

                Uri contentUri = FileProvider.getUriForFile(this, "com.example.emojimokoko.provider", gifFile);
                final int flag = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;

                ClipDescription description = new ClipDescription("GIF", new String[]{"image/png"});
                InputContentInfoCompat inputContentInfo = new InputContentInfoCompat(contentUri, description, null);

                InputConnectionCompat.commitContent(inputConnection, getCurrentInputEditorInfo(), inputContentInfo, flag, null);
            } catch (Exception e) {
                Log.e("GifSendDebug", "Error sending GIF: " + e.getMessage());
            }
        } else {
            Log.e("GifSendDebug", "WhatsApp does not support direct GIF sending");
        }
    }


    public int findRawResourceByDrawable(Context context, @DrawableRes int drawableResId) {
        try {
            // Get the name of the drawable resource
            String drawableName = context.getResources().getResourceName(drawableResId);

            // Construct the name of the corresponding raw resource
            String rawResourceName = drawableName.replace("drawable", "raw");

            // Use getIdentifier to find the raw resource ID
            return context.getResources().getIdentifier(
                    rawResourceName, "raw", context.getPackageName());
        } catch (Resources.NotFoundException e) {
            return 0;
        }
    }
    private File getFileForResource(@NonNull Context context, @RawRes int res) {
        File outputFile = new File(getFilesDir(), "image.png");
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

    private ArrayList<Integer> getEmoji() {

        return new ArrayList<>(getAllRawResourceIds(this));
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

    @SuppressLint({"InflateParams"})
    @Override
    public View onCreateInputView() {

        stickerPickerView = getLayoutInflater().inflate(R.layout.sticker_picker, null);
//        keyboard = new Keyboard(getApplicationContext(), R.xml.custom_keypaid);
        settingbtn = stickerPickerView.findViewById(R.id.settingbtn);
//        stickerKeyboard = stickerPickerView.findViewById(R.id.stickerkeyboard);
        stickerRecyclerView = stickerPickerView.findViewById(R.id.stickerRecyclerView);
        gifRecyclerView = stickerPickerView.findViewById(R.id.gifRecyclerView);
        previousstickerRecyclerView = stickerPickerView.findViewById(R.id.previousRecyclerView);
//        searchbar = stickerPickerView.findViewById(R.id.searchbar);
        settingtab = stickerPickerView.findViewById(R.id.settingtab);
        gifbtn = stickerPickerView.findViewById(R.id.gifbtn);
        emojibtn = stickerPickerView.findViewById(R.id.emojibtn);
        emojicard = stickerPickerView.findViewById(R.id.emojicard);
        gifcard = stickerPickerView.findViewById(R.id.gifcard);
        mediaPlayer = MediaPlayer.create(this, R.raw.song); // Replace 'your_sound' with the actual sound file name
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        categoryRecyclerView = stickerPickerView.findViewById(R.id.categoryRecyclerView);
        loadprevius();
//        stickerKeyboard.setKeyboard(keyboard);
//        stickerKeyboard.setOnKeyboardActionListener(this);
        deletebtn = stickerPickerView.findViewById(R.id.deletebtn);
        allEmojis = getEmoji();

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(categoryRecyclerView.getVisibility() == View.VISIBLE)
                {
                    categoryRecyclerView.setVisibility(View.GONE);
                    emojicard.setCardBackgroundColor(Color.WHITE);
                    emojibtn.setImageDrawable(getResources().getDrawable(R.drawable.blackemoji));
                }
                stickerAdapter = new StickerAdapter(getApplicationContext(), allEmojis, new StickerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String str) {

                        sendStickerToWhatsApp(Integer.parseInt(str));
                        mediaPlayer.start();
                        vibrateOnEmojiPress();
                    }
                });
                stickerRecyclerView.setAdapter(stickerAdapter);
            }
        });

        emojibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stickerRecyclerView.setVisibility(View.VISIBLE);
                gifRecyclerView.setVisibility(View.GONE);

                gifcard.setCardBackgroundColor(Color.WHITE);

                if(categoryRecyclerView.getVisibility() == View.GONE)
                {
                    categoryRecyclerView.setVisibility(View.VISIBLE);
                    emojicard.setCardBackgroundColor(Color.parseColor("#42e45d"));
                    emojibtn.setImageDrawable(getResources().getDrawable(R.drawable.baseline_emoji_emotions_24));
                }
                else if(categoryRecyclerView.getVisibility() == View.VISIBLE)
                {
                    categoryRecyclerView.setVisibility(View.GONE);
                    emojicard.setCardBackgroundColor(Color.WHITE);
                    emojibtn.setImageDrawable(getResources().getDrawable(R.drawable.blackemoji));
                }
            }
        });

        gifbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stickerRecyclerView.getVisibility() == View.VISIBLE){
                    stickerRecyclerView.setVisibility(View.GONE);
                    gifRecyclerView.setVisibility(View.VISIBLE);
                    gifcard.setCardBackgroundColor(Color.parseColor("#42e45d"));
                    emojicard.setCardBackgroundColor(Color.WHITE);
                    emojibtn.setImageDrawable(getResources().getDrawable(R.drawable.blackemoji));
                    ArrayList<Integer> arr = new ArrayList<>();
                    arr.add(R.raw.finchat);arr.add(R.raw.heartmokoko);arr.add(R.raw.worship);
                    gifAdapter = new GifAdapter(getApplicationContext(),arr , new GifAdapter.OnItemClickListener() {
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

        settingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleKeyboards();
            }
        });

        stickerAdapter = new StickerAdapter(this, allEmojis, new StickerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String str) {

                sendStickerToWhatsApp(Integer.parseInt(str));
                mediaPlayer.start();
                vibrateOnEmojiPress();
            }
        });

        ArrayList<Integer> categoryArray = new ArrayList<>();
        categoryArray.add(R.raw.recommendation);categoryArray.add(R.raw.coolguyenters);
        categoryArray.add(R.raw.iloveyou);categoryArray.add(R.raw.verylight);
        categoryArray.add(R.raw.mayhemsurprise);categoryArray.add(R.raw.praying);
        categoryArray.add(R.raw.phantomboundy);categoryArray.add(R.raw.thumblostarkemoticon);
        categoryArray.add(R.raw.mellowgreeting);categoryArray.add(R.raw.wow);
        categoryArray.add(R.raw.justthetwoofus);categoryArray.add(R.raw.decayingwatch);
        categoryArray.add(R.raw.gwent);
        categoryStickerAdapter = new CategoryStickerAdapter(getApplicationContext(), categoryArray, new CategoryStickerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String str) {

                int resourceId = Integer.parseInt(str);
                if(resourceId == R.raw.recommendation){
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroups.recommendationGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(Integer.parseInt(str));
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                }
                else if(resourceId == R.raw.coolguyenters){
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroups.coolGuyEntersGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(Integer.parseInt(str));
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                }
                else if(resourceId == R.raw.iloveyou){
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroups.iLoveYouGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(Integer.parseInt(str));
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                }
                else if(resourceId == R.raw.mayhemsurprise){
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroups.mahyemGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(Integer.parseInt(str));
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                }
                else if(resourceId == R.raw.praying){
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroups.prayingGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(Integer.parseInt(str));
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                }
                else if(resourceId == R.raw.phantomboundy){
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroups.phantomGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(Integer.parseInt(str));
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                }
                else if(resourceId == R.raw.thumblostarkemoticon){
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroups.thumbLostGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(Integer.parseInt(str));
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                }
                else if(resourceId == R.raw.mellowgreeting){
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroups.mellowGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(Integer.parseInt(str));
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                }
                else if(resourceId == R.raw.wow){
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroups.wowGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(Integer.parseInt(str));
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                }
                else if(resourceId == R.raw.justthetwoofus){
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroups.justTwoOfUsGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(Integer.parseInt(str));
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                }
                else if(resourceId == R.raw.decayingwatch){
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroups.decayingGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(Integer.parseInt(str));
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                }
                else if(resourceId == R.raw.gwent){
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroups.gwentGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(Integer.parseInt(str));
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                }
                else if(resourceId == R.raw.verylight){
                    stickerAdapter = new StickerAdapter(getApplicationContext(), IconGroups.veryLightGroup(), new StickerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String str) {

                            sendStickerToWhatsApp(Integer.parseInt(str));
                            mediaPlayer.start();
                            vibrateOnEmojiPress();
                        }
                    });
                    stickerRecyclerView.setAdapter(stickerAdapter);
                }
//                sendStickerToWhatsApp(Integer.parseInt(str));
//                mediaPlayer.start();
//                vibrateOnEmojiPress();
            }
        });

        categoryRecyclerView.setAdapter(categoryStickerAdapter);
        stickerRecyclerView.setAdapter(stickerAdapter);
//
//        searchbar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                emojiSectionVisible = !emojiSectionVisible;
//                stickerRecyclerView.setVisibility(View.GONE);
//                stickerKeyboard.setVisibility(View.VISIBLE);
//                settingtab.setVisibility(View.GONE);
//            }
//        });

//        searchbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                emojiSectionVisible = !emojiSectionVisible;
//                stickerKeyboard.setVisibility(View.VISIBLE);
//                stickerRecyclerView.setVisibility(View.GONE);
//                settingtab.setVisibility(View.GONE);
//            }
//        });

//        searchbar.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                stickerAdapter = new StickerAdapter(getApplicationContext(), sqliteManager.searchEmojiByCharacter(charSequence.toString()), new StickerAdapter.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(String str) {
//
//                        sendStickerToWhatsApp(Integer.parseInt(str));
//                        mediaPlayer.start();
//                        vibrateOnEmojiPress();
//                    }
//                });
//                stickerRecyclerView.setAdapter(stickerAdapter);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });


        return stickerPickerView;
    }



    private void vibrateOnEmojiPress() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            // Define the vibration pattern (you can customize this)
            long[] pattern = {0, 50}; // Vibrate for 50 milliseconds
            vibrator.vibrate(pattern, -1); // -1 means do not repeat
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Check if the keyboard is visible
            if (stickerKeyboard != null && stickerKeyboard.getVisibility() == View.VISIBLE) {
                // Hide the keyboard
//                hideKeyboard();
                stickerRecyclerView.setVisibility(View.VISIBLE);
                settingtab.setVisibility(View.VISIBLE);
                return true; // Consumes the back button press
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void loadprevius() {
        stickerAdapter = new StickerAdapter(getApplicationContext(), sqliteManager.readEmoji(), new StickerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String str) {

                sendStickerToWhatsApp(Integer.parseInt(str));

            }
        });
        previousstickerRecyclerView.setAdapter(stickerAdapter);
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
        // Get the currently active input method and compare it with your custom keyboard's package name
        String currentInputMethod = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        return currentInputMethod.equals(getPackageName() + "/" + Custom_Keypad.class.getName());
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
//        EditText searchbar = stickerPickerView.findViewById(R.id.searchbar);
//        Log.d("...........TAG", String.valueOf(primaryCode));
//        if (primaryCode == -4) { // Handle -10 code key for emoji section
//            hideKeyboard();
//            stickerRecyclerView.setVisibility(View.VISIBLE);
//            settingtab.setVisibility(View.VISIBLE);
//        } else {
//            // Check if the targetEditText is initialized
//            if (searchbar != null) {
//                switch (primaryCode) {
//                    case Keyboard.KEYCODE_DELETE:
//                        // Handle backspace key
//                        String currentText = searchbar.getText().toString();
//                        if (!TextUtils.isEmpty(currentText)) {
//                            int selectionStart = searchbar.getSelectionStart();
//                            int selectionEnd = searchbar.getSelectionEnd();
//                            if (selectionStart == selectionEnd) {
//                                // Delete the character before the cursor
//                                if (selectionStart > 0) {
//                                    searchbar.getText().delete(selectionStart - 1, selectionStart);
//                                }
//                            } else {
//                                // Delete the selected text
//                                searchbar.getText().delete(selectionStart, selectionEnd);
//                            }
//                        }
//                        break;
//                    case Keyboard.KEYCODE_DONE:
//                        // Handle enter key
//                        // You can perform some action here if needed
//                        break;
//                    default:
//                        char code = (char) primaryCode;
//                        // Append the character to the targetEditText
//                        searchbar.append(String.valueOf(code));
//                        break;
//                }
//            }
//        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

}
