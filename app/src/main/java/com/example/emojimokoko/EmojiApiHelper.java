package com.example.emojimokoko;

import com.androidnetworking.AndroidNetworking;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EmojiApiHelper {
    public static String loadEmojiDataFromApi() {
        // Replace "YOUR_API_ENDPOINT" with the actual API endpoint URL that provides emoji data
        String apiUrl = "http://127.0.0.1:8000/get_emoji";


        try {
            // Simulate making an HTTP request to the API and getting the JSON response
            InputStream inputStream = new URL(apiUrl).openStream();
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

//    public static HashMap<Integer, String> parseEmojiData(String jsonData) {
//        HashMap<Integer, String> emojiMap = new HashMap<>();
//        try {
//            JSONArray emojiArray = new JSONArray(jsonData);
//            for (int i = 0; i < emojiArray.length(); i++) {
//                JSONObject emojiObject = emojiArray.getJSONObject(i);
//                int emojiCode = emojiObject.getInt("code");
//                String emojiText = emojiObject.getString("emoji");
//                emojiMap.put(emojiCode, emojiText);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return emojiMap;
//    }
}
