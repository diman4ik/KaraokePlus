package ru.karaokeplus.karaokeplus.content.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import ru.karaokeplus.karaokeplus.R;
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
    private String _code;


    private List<CategoryContent.CategoryItem> _categoriesList = new ArrayList<CategoryContent.CategoryItem>();


    public List<CategoryContent.CategoryItem> getCategories() {
        return _categoriesList;
    }

    public Song() {
    }

    Song(Parcel in) {
        _id = in.readInt();
        _songName = in.readString();
        _songAuthor = in.readString();
        _songCategories = in.readString();
        _code = in.readString();
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

    public String getSongCode() { return _code; }

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

        String [] cats = _songCategories.split(",");

        for(String cat : cats) {
            if(cat.contains("русск")) {
                if(!_categoriesList.contains(CategoryContent.ITEM_MAP.get(R.string.category_russian))) {
                    _categoriesList.add(CategoryContent.ITEM_MAP.get(R.string.category_russian));
                }

            }
            else if(cat.contains("иностр")) {
                if (!_categoriesList.contains(CategoryContent.ITEM_MAP.get(R.string.category_foreign))) {
                    _categoriesList.add(CategoryContent.ITEM_MAP.get(R.string.category_foreign));
                }
            }
            else if(cat.contains("рок")) {
                if (!_categoriesList.contains(CategoryContent.ITEM_MAP.get(R.string.category_rock))) {
                    _categoriesList.add(CategoryContent.ITEM_MAP.get(R.string.category_rock));
                }
            }
            else if(cat.contains("поп")) {
                if (!_categoriesList.contains(CategoryContent.ITEM_MAP.get(R.string.category_pop))) {
                    _categoriesList.add(CategoryContent.ITEM_MAP.get(R.string.category_pop));
                }
            }
            else if(cat.contains("шансон")) {
                if (!_categoriesList.contains(CategoryContent.ITEM_MAP.get(R.string.category_shanson))) {
                    _categoriesList.add(CategoryContent.ITEM_MAP.get(R.string.category_shanson));
                }
            }
        }
    }

    public void setSongCode(String code) {
        _code = code;
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
