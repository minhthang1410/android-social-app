package com.example.onlyfans;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable, Parcelable {

    private String _token;
    private String _username;
    private String _bio;
    private String _avatar;
    private String imgUrl = "http://10.0.2.2/public/images/";

    public User(String _token, String _username, String _bio, String _avatar) {
        this._token = "Bearer " + _token;
        this._username = _username;
        this._bio = _bio;
        this._avatar = imgUrl + _avatar;
    }

    protected User(Parcel in) {
        _token = in.readString();
        _username = in.readString();
        _bio = in.readString();
        _avatar = in.readString();
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

    public String get_token() {
        return _token;
    }

    public String get_username() {
        return _username;
    }

    public String get_bio() {
        return _bio;
    }

    public String get_avatar() {
        return _avatar;
    }

    public void set_username(String _username) {
        this._username = _username;
    }

    public void set_bio(String _bio) {
        this._bio = _bio;
    }

    public void set_avatar(String _avatar) {
        this._avatar = imgUrl + _avatar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(_token);
        parcel.writeString(_username);
        parcel.writeString(_bio);
        parcel.writeString(_avatar);
    }
}
