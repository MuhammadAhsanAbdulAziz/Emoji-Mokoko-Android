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
    private List<DataItem> emojies;
    private boolean caps = false;

    private static class DataItem {
        private int id;
        private String name;
        private String Image;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        // Getter and Setter methods for name
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        public void setImage(String Image) {
            this.Image = Image;
        }
        public String getImage() {
            return Image;
        }
        public String toString() {
            return "DataItem{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", Image='" + Image + '\'' +
                    // Add other properties here if needed
                    '}';
        }
    }
    @Override
    public View onCreateInputView() {
        keyboardView= (KeyboardView) getLayoutInflater().inflate(R.layout.custom_keyboard_layout,null);
        keyboard=new Keyboard(this,R.xml.custom_keypaid);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);

        fetchEmojiDataAndAddKeys();
        return keyboardView;
    }

    private void fetchEmojiDataAndAddKeys() {
        String apiUrl = "https://911d-119-157-77-76.ngrok-free.app/get_emoji";

        AndroidNetworking.initialize(this);
        AndroidNetworking.get(apiUrl)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray resArray = response.getJSONArray("res");
                            if (resArray.length() > 0) {
                                List<DataItem> dataList = parseJsonData(resArray);
                                emojies= dataList;

                            } else {
                                // Handle the case when the "res" array is empty
                                Log.d("Description", "No emoji data found.");
                            }

                            // Notify the keyboard view that the keyboard has changed and needs to be redrawn
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Log.e(String.valueOf(anError), "onError: eee...................." );
                    }
                });
    }

    private List<DataItem> parseJsonData(JSONArray response) {
        List<DataItem> dataList = new ArrayList<>();

        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject dataObject = response.getJSONObject(i);
                DataItem item = new DataItem();
                item.setId(dataObject.getInt("id"));
                item.setName(dataObject.getString("title"));
                item.setImage(dataObject.getString("image"));
                dataList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataList;
    }
//@Override
//public View onCreateInputView() {
//    keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.custom_keyboard_layout, null);
//    keyboard = new Keyboard(this, R.xml.custom_emoji_keypaid);
//    keyboardView.setKeyboard(keyboard);
//    keyboardView.setOnKeyboardActionListener(this);
//    return keyboardView;
//}

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }



    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        if (primaryCode == -10) { // Handle -10 code key for emoji section
            emojiSectionVisible = !emojiSectionVisible;
            if (emojiSectionVisible) {
                keyboard = new Keyboard(this, R.xml.custom_emoji_keypaid);
                List<Keyboard.Key> keys = keyboard.getKeys();

                // Clear existing keys
//                keys.clear();
                for (DataItem dataItem : emojies) {
                    int id = dataItem.getId();
                    String name = dataItem.getName();
                    String img = dataItem.getImage();
                    Keyboard.Key key = new Keyboard.Key(new Keyboard.Row(keyboard)); // Use the Row constructor

                    key.codes = new int[]{id}; // Set the key code for the emoji
                    key.label="test";

                    keys.add(key);

                }
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
