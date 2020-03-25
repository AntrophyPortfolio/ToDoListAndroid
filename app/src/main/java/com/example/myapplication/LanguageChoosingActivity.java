package com.example.myapplication;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.Locale;

public class LanguageChoosingActivity extends MainActivity {


    private CustomAdapterLanguages adapter;
    private ListView listViewLanguages;
    private static String defaultLanguage = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_choosing);

        String[] languages = {getString(R.string.language_czech), getString(R.string.language_english)};
        int[] FLAGS = {R.drawable.czech_flag, R.drawable.english_flag};


        listViewLanguages = findViewById(R.id.listViewLang);

        adapter = new CustomAdapterLanguages(this, languages, FLAGS);

        listViewLanguages.setAdapter(adapter);

        listViewLanguages.setOnItemClickListener((parent, view, position, id) -> {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.choose_default_language_dialog_title);
            alert.setMessage(R.string.choose_default_language_dialog_message);

            alert.setPositiveButton(R.string.yes, (dialog, which) -> {
                switch (position) {
                    case 0:
                        changeDefaultLanguage("cs");
                        defaultLanguage = "cs";
                        break;
                    case 1:
                        changeDefaultLanguage("en");
                        defaultLanguage = "en";
                        break;
                    default:
                        changeDefaultLanguage("en");
                        defaultLanguage = "en";
                        break;
                }
                startActivity(new Intent(LanguageChoosingActivity.this, MainActivity.class));
            });
            alert.setNegativeButton(R.string.no, (dialog, which) -> {
                return;
            });
            alert.show();
        });

    }

    private void changeDefaultLanguage(String language){

        Locale locale = new Locale(language);
        locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    public static String getDefaultLanguage() {
        return defaultLanguage;
    }

    public static void setDefaultLanguage(String defaultLanguage) {
        LanguageChoosingActivity.defaultLanguage = defaultLanguage;
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
