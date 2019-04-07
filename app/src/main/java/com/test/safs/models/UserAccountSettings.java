package com.test.safs.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserAccountSettings implements Parcelable {

    private long activities;
    private String name;
    private long friends;
    private String profile_photo;

    public UserAccountSettings(long activities, String name, long friends, String profile_photo) {
        this.activities = activities;
        this.name = name;
        this.friends = friends;
        this.profile_photo = profile_photo;
    }

    public UserAccountSettings(){
    }

    protected UserAccountSettings(Parcel in) {
        activities = in.readLong();
        name = in.readString();
        friends = in.readLong();
        profile_photo = in.readString();
    }

    public static final Creator<UserAccountSettings> CREATOR = new Creator<UserAccountSettings>() {
        @Override
        public UserAccountSettings createFromParcel(Parcel in) {
            return new UserAccountSettings(in);
        }

        @Override
        public UserAccountSettings[] newArray(int size) {
            return new UserAccountSettings[size];
        }
    };

    public long getactivities() {
        return activities;
    }

    public void setactivities(long activities) {
        this.activities = activities;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public long getFriends() {
        return friends;
    }

    public void setFriends(long friends) {
        this.friends = friends;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    @Override
    public String toString() {
        return "user_account_settings{" +
                "activities=" + activities +
                ", name='" + name + '\'' +
                ", friends=" + friends +
                ", profile_photo='" + profile_photo + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(activities);
        parcel.writeString(name);
        parcel.writeLong(friends);
        parcel.writeString(profile_photo);
    }
}
