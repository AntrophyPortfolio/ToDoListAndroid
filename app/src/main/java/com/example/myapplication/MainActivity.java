package com.example.myapplication;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private ListView mainListView;
    private static Record chosenItem = null;
    private static boolean editingItem = false;
    private static double currentLatitude;
    private static double currentLongitude;

    private LocationManager locationManager;
    private static final long MIN_TIME = 100;
    private static final long MIN_DISTANCE = 0;
    private static final String TAG_CODE_PERMISSION_LOCATION = "1";
    private static int maxProximity;
    private static boolean notificationsEnabled;
    private Calendar currentDate;
    private boolean notificationSent = false;
    private int notificationSentInMinutes = 0;
    private static ArrayAdapter<Record> adapter;
    private static boolean initialStart = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (SettingsActivity.isDarkThemeEnabled()) {
            setTheme(R.style.DarkTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationsEnabled = SettingsActivity.isNotificationEnabled();

        maxProximity = SettingsActivity.getMaxProximity();


        loadData();


        mainListView = findViewById(R.id.mainListView);
        mainListView.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {


            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    TAG_CODE_PERMISSION_LOCATION}, 200);

        }

        mainListView.setOnItemClickListener((adapter, view, position, id) -> {
            editingItem = true;
            chosenItem = AddItemActivity.listItems.get(position);

            startActivity(new Intent(MainActivity.this, AddItemActivity.class));

        });

        mainListView.setOnItemLongClickListener((parent, view, position, id) -> {

            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle(R.string.remove_selected_item_from_list_title);
            alert.setMessage(R.string.remove_selected_item_from_list_message);

            alert.setPositiveButton(R.string.yes, (dialog, which) -> {

                AddItemActivity.listItems.remove(adapter.getItem(position));
                adapter.notifyDataSetChanged();

            });


            alert.setNegativeButton(R.string.no, (dialog, which) -> {
                return;
            });

            alert.show();
            return true;
        });


    }


    public void handleButtonAddNew(View view) {
        editingItem = false;
        startActivity(new Intent(MainActivity.this, AddItemActivity.class));
    }

    @Override
    public void onLocationChanged(Location location) {
        Location deviceLocation = new Location("current location");
        Location recordLocation = new Location("location of record");

        currentDate = Calendar.getInstance();


        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        deviceLocation.setLatitude(currentLatitude);
        deviceLocation.setLongitude(currentLongitude);

        for (int i = 0; i < adapter.getCount(); i++) {
            recordLocation.setLatitude(Objects.requireNonNull(adapter.getItem(i)).getLatitude());
            recordLocation.setLongitude(Objects.requireNonNull(adapter.getItem(i)).getLongitude());
            Record prvek = adapter.getItem(i);

            if (prvek != null && deviceLocation.distanceTo(recordLocation) < maxProximity
                    && currentDate.get(Calendar.YEAR) == prvek.getDueDate().get(Calendar.YEAR)
                    && currentDate.get(Calendar.MONTH) == prvek.getDueDate().get(Calendar.MONTH)
                    && currentDate.get(Calendar.DAY_OF_MONTH) == prvek.getDueDate().get(Calendar.DAY_OF_MONTH)
                    && currentDate.get(Calendar.HOUR_OF_DAY) == prvek.getDueDate().get(Calendar.HOUR_OF_DAY)
                    && currentDate.get(Calendar.MINUTE) == prvek.getDueDate().get(Calendar.MINUTE)) {

                if (notificationSent && currentDate.get(Calendar.MINUTE) > notificationSentInMinutes) {
                    notificationSent = false;
                    return;
                }
                if (notificationSent || !notificationsEnabled) {
                    return;
                } else {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notify = new Notification.Builder(getApplicationContext()).
                            setContentTitle(getString(R.string.notification_dont_forget) + " " +
                                    prvek.getName()).setOnlyAlertOnce(true).setContentText(getString(R.string.distance_from_target_start) +
                            " " + deviceLocation.distanceTo(recordLocation) + " " + " " +
                            getString(R.string.distance_from_target_end)).setSmallIcon(R.drawable.ic_launcher_background).build();
                    notify.flags |= Notification.FLAG_AUTO_CANCEL;
                    notificationManager.notify(0, notify);
                    notificationSent = true;
                    notificationSentInMinutes = currentDate.get(Calendar.MINUTE);
                }


            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }


    public static ArrayAdapter<Record> getAdapter() {
        return adapter;
    }

    public static Record getChosenItem() {
        return chosenItem;
    }

    public static boolean isEditingItem() {
        return editingItem;
    }

    public static void setEditingItem(boolean editingItem) {
        MainActivity.editingItem = editingItem;
    }

    public static double getCurrentLatitude() {
        return currentLatitude;
    }

    public static double getCurrentLongitude() {
        return currentLongitude;
    }

    private void loadData() {

        // LOAD LISTVIEW
        if (initialStart) {
            SharedPreferences sharedPreferences = getSharedPreferences("listView", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences.getString("todo list", null);
            Type type = new TypeToken<ArrayList<Record>>() {
            }.getType();

            AddItemActivity.listItems = gson.fromJson(json, type);

        }
        if (AddItemActivity.listItems == null) {
            AddItemActivity.listItems = new ArrayList<>();
        }

        if (adapter == null) {
            adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, AddItemActivity.listItems);
        }
        // LOAD SETTINGS
        SharedPreferences loadSettings = getSharedPreferences("settings", MODE_PRIVATE);
        SettingsActivity.setDarkThemeEnabled(loadSettings.getBoolean("darkThemeEnabled", false));
        SettingsActivity.setNotificationEnabled(loadSettings.getBoolean("sendNotificationsEnabled", true));
        SettingsActivity.setMaxProximity(loadSettings.getInt("maxProximity", 1000));
        if (initialStart) {
            changeDefaultLanguage(loadSettings.getString("defaultLanguage", "en"));

        }

        adapter.notifyDataSetChanged();
    }

    public void saveData() {
        // SAVE LISTVIEW
        SharedPreferences sharedPreferences = getSharedPreferences("listView", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(AddItemActivity.listItems);
        editor.putString("todo list", json);
        editor.apply();

        // SAVE SETTINGS
        SharedPreferences saveSettings = getSharedPreferences("settings",MODE_PRIVATE);
        editor = saveSettings.edit();
        editor.putInt("maxProximity", SettingsActivity.getMaxProximity());
        editor.putBoolean("darkThemeEnabled", SettingsActivity.isDarkThemeEnabled());
        editor.putBoolean("sendNotificationsEnabled", SettingsActivity.isNotificationEnabled());
        editor.putString("defaultLanguage", LanguageChoosingActivity.getDefaultLanguage());
        editor.commit();
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void handleButtonSettingsAction(MenuItem item) {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    @Override
    protected void onPause() {
        saveData();
        super.onPause();
    }
    private void changeDefaultLanguage(String language){

        Locale locale = new Locale(language);
        locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        initialStart = false;
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
