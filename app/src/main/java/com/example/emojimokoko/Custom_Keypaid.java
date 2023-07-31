package com.example.emojimokoko;

import android.accessibilityservice.AccessibilityService;
import android.graphics.BitmapFactory;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import androidx.annotation.Keep;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarException;

public class Custom_Keypaid extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {
    private KeyboardView keyboardView;
    private Keyboard keyboard;
    private boolean emojiSectionVisible = false;
    private boolean caps = false;

   @Override
    public View onCreateInputView() {
        keyboardView= (KeyboardView) getLayoutInflater().inflate(R.layout.custom_keyboard_layout,null);
        keyboard=new Keyboard(this,R.xml.custom_keypaid);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);

//        fetchEmojiDataAndAddKeys();
        return keyboardView;
    }

//    private void fetchEmojiDataAndAddKeys() {
//        String apiUrl = "https://2359-119-152-232-150.ngrok-free.app/get_emoji";
//
//        AndroidNetworking.initialize(this);
//        AndroidNetworking.get(apiUrl)
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            JSONArray resArray = response.getJSONArray("res");
//                            if (resArray.length() > 0) {
//
//
//                            } else {
//                                // Handle the case when the "res" array is empty
//                                Log.d("Description", "No emoji data found.");
//                            }
//
//                            // Notify the keyboard view that the keyboard has changed and needs to be redrawn
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//
//                    @Override
//                    public void onError(ANError anError) {
//                        anError.printStackTrace();
//                        Log.e(String.valueOf(anError), "onError: eee...................." );
//                    }
//                });
//    }


    @Override
    public void onPress(int primaryCode) { }

    @Override
    public void onRelease(int primaryCode) { }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        if (primaryCode == -10) { // Handle -10 code key for emoji section
            emojiSectionVisible = !emojiSectionVisible;
            if (emojiSectionVisible) {
                keyboard = new Keyboard(this, R.xml.custom_emoji_keypaid);


                keyboardView.setKeyboard(keyboard);

                keyboardView.setOnKeyboardActionListener(this);
//                keyboardView.setVisibility(View.VISIBLE);
            } else {
                // Hide the custom emoji keyboard
//                keyboardView.setVisibility(View.GONE);
                keyboard = new Keyboard(this, R.xml.custom_keypaid);
                keyboardView.setKeyboard(keyboard);
                keyboardView.setOnKeyboardActionListener(this);
            }
        }  else {
            InputConnection inputConnection = getCurrentInputConnection();
            if (inputConnection != null) {
                switch (primaryCode) {
                    case Keyboard.KEYCODE_DELETE:
                        CharSequence selectedText = inputConnection.getSelectedText(0);

                        if (TextUtils.isEmpty(selectedText)) {
                            inputConnection.deleteSurroundingText(1, 0);
                        } else {
                            inputConnection.commitText("", 1);
                        }
                        break;
                    case Keyboard.KEYCODE_SHIFT:
                        caps = !caps;
                        keyboard.setShifted(caps);
                        keyboardView.invalidateAllKeys();
                        break;
                    case Keyboard.KEYCODE_DONE:
                        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                        break;
                    default:
                        char code = (char) primaryCode;
                        if (Character.isLetter(code) && caps) {
                            code = Character.toUpperCase(code);
                        }
                        inputConnection.commitText(String.valueOf(code), 1);

                }
            }
        }
    }

    private boolean isEmojiCode(int primaryCode) {
        // Replace these codes with the actual emoji codes defined in your XML layout
        return primaryCode == 1001 || primaryCode == 1002 || primaryCode == 1003;
    }

    // Get the corresponding emoji text based on the primaryCode
    private String getEmojiText(int primaryCode) {
        // Replace these mappings with the actual emoji mappings for each code
        // For example, return "\uD83D\uDE00" for primaryCode 1001, "\uD83D\uDE01" for primaryCode 1002, etc.
        switch (primaryCode) {
            case 1001:
                return "😀"; // Emoji for code 1001
            case 1002:
                return "😁"; // Emoji for code 1002
            case 1003:
                return "😂"; // Emoji for code 1003
            // Add more cases for other emoji codes as needed
            default:
                return ""; // Return empty string if no emoji found for the code
        }
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
