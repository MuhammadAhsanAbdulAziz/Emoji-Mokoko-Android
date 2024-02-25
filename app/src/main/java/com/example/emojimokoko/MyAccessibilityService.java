package com.example.emojimokoko;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.example.emojimokoko.utils.UtilManager;

public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = "MyAccessibilityService";
    
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.e(TAG, "onAccessibilityEvent: " );

        if(UtilManager.getDefaults("theme",getApplicationContext())!=null)
        {
            if(UtilManager.getDefaults("theme",getApplicationContext()).equals("close"))
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    disableSelf();
                }
            }
        }


        String packageName = event.getPackageName().toString();
        PackageManager packageManager = this.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName,0);
            CharSequence applicationLabel =  packageManager.getApplicationLabel(applicationInfo);
            if (applicationLabel != null) {
                if(applicationLabel.equals("Messages")) {
                    UtilManager.setDefaults("process", "Messages", getApplicationContext());
                }
                else if(applicationLabel.equals("WhatsApp")){
                    UtilManager.setDefaults("process", "WhatsApp", getApplicationContext());
                }
                else if(applicationLabel.equals("Messenger")){
                    UtilManager.setDefaults("process", "Messenger", getApplicationContext());
                }
                else if(applicationLabel.equals("Instagram")){
                    UtilManager.setDefaults("process", "Instagram", getApplicationContext());
                }
                else if(applicationLabel.equals("Telegram")){
                    UtilManager.setDefaults("process", "Telegram", getApplicationContext());
                }
                else if(applicationLabel.equals("Discord") || applicationLabel.equals("com.discord.MainApplication")){
                    UtilManager.setDefaults("process", "Discord", getApplicationContext());
                }
                else if(applicationLabel.equals("X")){
                    UtilManager.setDefaults("process", "X", getApplicationContext());
                }
                else if(applicationLabel.equals("Tiktok")){
                    UtilManager.setDefaults("process", "Tiktok", getApplicationContext());
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt: " );
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        AccessibilityServiceInfo info =  new AccessibilityServiceInfo();

        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
                AccessibilityEvent.TYPE_VIEW_FOCUSED;

        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;


        this.setServiceInfo(info);
        Log.e(TAG, "onServiceConnected: ");
    }
}
