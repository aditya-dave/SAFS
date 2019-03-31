package com.test.safs.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Activity{

    private String profilephoto;
    private String name;
    private String sport_name;
    private String date;
    private String time;
    private String location;
    private String key;


    public Activity(String profilephoto, String name, String sport_name, String date, String time, String location,String key) {
        this.profilephoto = profilephoto;
        this.name = name;
        this.sport_name = sport_name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.key = key;
    }

    public Activity() {
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "profilephoto='" + profilephoto + '\'' +
                ", name='" + name + '\'' +
                ", sport_name='" + sport_name + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSport_name() {
        return sport_name;
    }

    public void setSport_name(String sport_name) {
        this.sport_name = sport_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /*public static Creator<Activity> getCREATOR() {
        return CREATOR;
    }

    protected Activity(Parcel in) {
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel in) {
            return new Activity(in);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }*/

}
