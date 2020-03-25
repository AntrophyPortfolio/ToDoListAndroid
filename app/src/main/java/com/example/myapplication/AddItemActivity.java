package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class AddItemActivity extends MainActivity {

    private static double chosenLatitude;
    private static double chosenLongitude;
    public static ArrayList<Record> listItems ;
    private Calendar date = Calendar.getInstance();
    private EditText textWhenRemember;
    private EditText textWhatToRemind;
    private ImageView timeSet;
    private ImageView selectedLocation;
    private ImageView NotSelectedLocation;
    private boolean editingItem = MainActivity.isEditingItem();
    private Record chosenItem = MainActivity.getChosenItem();
    private Geocoder geocoder;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);


        textWhenRemember = findViewById(R.id.textWhen);
        timeSet = findViewById(R.id.timeSet);
        Button buttonRemember = findViewById(R.id.buttonRemember);
        Button buttonEdit = findViewById(R.id.buttonEdit);
        selectedLocation = findViewById(R.id.mapSet);
        NotSelectedLocation = findViewById(R.id.mapNotSet);


        chosenLatitude = MainActivity.getCurrentLatitude();
        chosenLongitude = MainActivity.getCurrentLongitude();

        if (editingItem) {
            buttonRemember.setVisibility(View.GONE);
            buttonEdit.setVisibility(View.VISIBLE);


            textWhenRemember.setText(chosenItem.getDueDate().get(Calendar.DAY_OF_MONTH) + "/"
                    + chosenItem.getDueDate().get(Calendar.MONTH) + "/" + chosenItem.getDueDate().get(Calendar.YEAR) +
                    " " + chosenItem.getDueDate().get(Calendar.HOUR_OF_DAY) + ":" + chosenItem.getDueDate().get(Calendar.MINUTE));

            chosenLatitude = chosenItem.getLatitude();
            chosenLongitude = chosenItem.getLongitude();

            date.set(chosenItem.getDueDate().get(Calendar.YEAR), chosenItem.getDueDate().get(Calendar.MONTH),
                    chosenItem.getDueDate().get(Calendar.DAY_OF_MONTH), chosenItem.getDueDate().get(Calendar.HOUR_OF_DAY),
                    chosenItem.getDueDate().get(Calendar.MINUTE));

            textWhatToRemind = findViewById(R.id.textWhatToRemind);
            textWhatToRemind.setText(chosenItem.getName());
            if (chosenLongitude == 0 && chosenLatitude == 0) {
                selectedLocation.setVisibility(View.GONE);
                NotSelectedLocation.setVisibility(View.VISIBLE);
            } else {
                selectedLocation.setVisibility(View.VISIBLE);
                NotSelectedLocation.setVisibility(View.GONE);
            }

        } else {
            textWhenRemember.setText(getString(R.string.editTextNow));
            buttonRemember.setVisibility(View.VISIBLE);
            buttonEdit.setVisibility(View.GONE);
        }


    }

    @SuppressLint("SetTextI18n")
    public void showDateTimePicker(View view) {
        final Calendar currentDate = Calendar.getInstance();
        new DatePickerDialog(AddItemActivity.this, (view1, year, monthOfYear, dayOfMonth) -> {

            if (view1.isShown()) {
                date.set(year, monthOfYear, dayOfMonth, 8, 00);
                textWhenRemember.setText(date.get(Calendar.DAY_OF_MONTH) + "/"
                        + date.get(Calendar.MONTH) + "/" + date.get(Calendar.YEAR) +
                        " " + date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE));
            }


            new TimePickerDialog(AddItemActivity.this, (view11, hourOfDay, minute) -> {
                if (view11.isShown()) {
                    date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    date.set(Calendar.MINUTE, minute);

                    textWhenRemember.setText(date.get(Calendar.DAY_OF_MONTH) + "/"
                            + date.get(Calendar.MONTH) + "/" + date.get(Calendar.YEAR) +
                            " " + date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE));

                    timeSet.setVisibility(View.VISIBLE);
                }

            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }


    public void handleButtonZapamatovat(View view) {
        geocoder = new Geocoder(this, Locale.getDefault());
        String address;
        String location = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(chosenLatitude, chosenLongitude, 10);
            if (addresses.size() != 0) {
                address = addresses.get(0).getAddressLine(0);
                location = address.substring(0, address.indexOf(","));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (editingItem) {
            chosenItem.setDueDate(date);
            chosenItem.setName(textWhatToRemind.getText().toString());
            chosenItem.setLocation(location);
            MainActivity.setEditingItem(false);


        } else {
            EditText textWhatToRemind = findViewById(R.id.textWhatToRemind);

            listItems.add(new Record(textWhatToRemind.getText().toString(), date, chosenLatitude, chosenLongitude, location));
        }
        editingItem = false;
        MainActivity.getAdapter().notifyDataSetChanged();
        finish();
    }

    public void handleImageMapsAction(View view) {
        NotSelectedLocation.setVisibility(View.INVISIBLE);
        selectedLocation.setVisibility(View.VISIBLE);
        startActivity(new Intent(AddItemActivity.this, MapsActivity.class));
    }

    public static void setChosenLatitude(double chosenLatitude) {
        AddItemActivity.chosenLatitude = chosenLatitude;
    }

    public static void setChosenLongitude(double chosenLongitude) {
        AddItemActivity.chosenLongitude = chosenLongitude;
    }
}

