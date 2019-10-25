package com.example.friendstr_game;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String email, name, uid;
    private int highScore;

    // Required empty public constructor
    public User() {
    }

    public User(String email, String name, String uid) {
        this.email = email;
        this.name = name;
        this.uid = uid;
        this.highScore = 0;
    }

    protected User(Parcel in) {
        email = in.readString();
        name = in.readString();
        uid = in.readString();
        highScore = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public int getHighScore() {
        return highScore;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(uid);
        dest.writeInt(highScore);
    }
}
