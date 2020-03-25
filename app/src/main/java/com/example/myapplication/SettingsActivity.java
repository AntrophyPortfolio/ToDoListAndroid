package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class SettingsActivity extends MainActivity {


    private static int maxProximity = 1000;
    private EditText textProximity;
    private static boolean notificationEnabled = true;
    private static boolean darkThemeEnabled = false;
    private Switch switchNotifications;
    private Switch switchDarkTheme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (darkThemeEnabled){
            setTheme(R.style.DarkTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        textProximity = findViewById(R.id.editTextProximity);
        textProximity.setText(String.valueOf(getMaxProximity()));

        switchNotifications = findViewById(R.id.switchSendNotifications);
        switchNotifications.setChecked(isNotificationEnabled());

        switchDarkTheme = findViewById(R.id.switchDarkTheme);
        switchDarkTheme.setChecked(darkThemeEnabled);
        switchDarkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                darkThemeEnabled = true;
            } else {
                darkThemeEnabled = false;
            }
        });


    }

    public static int getMaxProximity() {
        return maxProximity;
    }

    public void handleButtonChooseAction(View view) {
        startActivity(new Intent(SettingsActivity.this, LanguageChoosingActivity.class));

    }

    public static boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public static boolean isDarkThemeEnabled() {
        return darkThemeEnabled;
    }


    public static void setMaxProximity(int maxProximity) {
        SettingsActivity.maxProximity = maxProximity;
    }

    public static void setNotificationEnabled(boolean notificationEnabled) {
        SettingsActivity.notificationEnabled = notificationEnabled;
    }

    public static void setDarkThemeEnabled(boolean darkThemeEnabled) {
        SettingsActivity.darkThemeEnabled = darkThemeEnabled;
    }

    public void handleButtonSaveAction(View view) {

        if (Integer.valueOf(textProximity.getText().toString()) >= 0) {
            maxProximity = Integer.valueOf(textProximity.getText().toString());
        }
        notificationEnabled = switchNotifications.isChecked();
        if (isDarkThemeEnabled()){
            setTheme(R.style.DarkTheme);
        } else{
            setTheme(R.style.AppTheme);
        }
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
