package ru.karaokeplus.karaokeplus.content.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import ru.karaokeplus.karaokeplus.content.dao.Identify;


public class Song implements Identify, Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Song createFromParcel(Parcel parcel)
        {
            return new Song(parcel);
        }

        public Song[] newArray(int i)
        {
            return new Song[i];
        }
    };

    private int _id = Identify.INVALID_ID;
    private String _songName;
    private String _songAuthor;
    private String _songCategories;

    public Song() {

    }

    Song(Parcel in) {
        _id = in.readInt();
        _songName = in.readString();
        _songAuthor = in.readString();
        _songCategories = in.readString();
    }

    @Override
    public int getId() {
        return _id;
    }

    public String getSongName() {
        return _songName;
    }

    public String getSongAuthor() {
        return _songAuthor;
    }

    public String getSongCategories() {
        return _songCategories;
    }

    public void setId(int id) {
        _id = id;
    }

    public void setSongName(String songName) {
        _songName = songName;
    }

    public void setSongAuthor(String songAuthor) {
        _songAuthor = songAuthor;
    }

    public void setSongCategories(String songCategories) {
        _songCategories = songCategories;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
