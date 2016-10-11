package ru.karaokeplus.karaokeplus.content.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.karaokeplus.karaokeplus.content.dao.SongsDAO;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "karaokeplus.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, "karaokeplus.db", null, 1);
    }

    public void onCreate(SQLiteDatabase sqlitedatabase)
    {
        SongsDAO.onCreate(sqlitedatabase);
    }

    public void onUpgrade(SQLiteDatabase sqlitedatabase, int ver1, int ver2)
    {
        SongsDAO.onUpgrade(sqlitedatabase, ver1, ver2);
    }
}
