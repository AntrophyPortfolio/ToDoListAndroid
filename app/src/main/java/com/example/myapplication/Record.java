package com.example.myapplication;

import java.util.Calendar;

class Record {
    private String name;
    private Calendar dueDate;
    private String location;

    private double latitude;
    private double longitude;
    public Record(String name, Calendar dueDate, double latitude, double longitude, String location) {
        this.name = name;
        this.dueDate = dueDate;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getDueDate() {

        return dueDate;
    }

    public void setDueDate(Calendar dueDate) {
        this.dueDate = dueDate;
    }


    public double getLatitude() {
        return latitude;
    }



    public double getLongitude() {
        return longitude;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return name  + "\n\n" +dueDate.get(Calendar.DAY_OF_MONTH) + "/"
                + dueDate.get(Calendar.MONTH) + "/" + dueDate.get(Calendar.YEAR) +
                " " + dueDate.get(Calendar.HOUR_OF_DAY) + ":" + dueDate.get(Calendar.MINUTE)+ "\n" +location;


    }


}
