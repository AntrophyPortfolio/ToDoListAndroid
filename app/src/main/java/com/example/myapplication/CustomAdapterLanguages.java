package com.example.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


class CustomAdapterLanguages extends ArrayAdapter {
    private Activity context;
    private int[] flags;
    private String[] languages;

    public CustomAdapterLanguages(Activity context, String[] languages, int[] flags) {
        super(context, R.layout.listview_row, languages);
        this.context = context;
        this.flags = flags;
        this.languages = languages;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_row, null, true);

        //this code gets references to objects in the listview_row.xml file
        TextView nameTextField = rowView.findViewById(R.id.nameTextViewID);
        ImageView imageView = rowView.findViewById(R.id.imageView1ID);

        //this code sets the values of the objects to values from the arrays
        nameTextField.setText(languages[position]);
        imageView.setImageResource(flags[position]);

        return rowView;

    }

    ;


}
