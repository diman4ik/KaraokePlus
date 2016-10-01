package ru.karaokeplus.karaokeplus.content.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.List;

import ru.karaokeplus.karaokeplus.content.DatabaseUtils;
import ru.karaokeplus.karaokeplus.content.data.Song;

public class SongsDAO  extends AbstractDAO {
    public static interface Columns
            extends BaseColumns
    {
        public static final String SONGNAME = "name";
        public static final String SONGAUTHOR = "author";
        public static final String SONGCATEGORIES = "categories";
    }

    public static final String PROJECTION[] = {
            ID_COLUMN, Columns.SONGNAME, Columns.SONGAUTHOR, Columns.SONGCATEGORIES
    };

    public static final String SONGS_TABLE_NAME = "songs";
    public static final Uri CONTENT_URI = DatabaseUtils.getUri(SONGS_TABLE_NAME);

    public SongsDAO(Context context)
    {
        super(context);
    }

    public static void onCreate(SQLiteDatabase sqlitedatabase)
    {
        DatabaseUtils.TableBuilder tablebuilder = new DatabaseUtils.TableBuilder(SONGS_TABLE_NAME);
        tablebuilder.addColumn((new DatabaseUtils.ColumnBuilder().primaryKey()).integer(ID_COLUMN));
        tablebuilder.addColumn((new DatabaseUtils.ColumnBuilder()).text(Columns.SONGNAME));
        tablebuilder.addColumn((new DatabaseUtils.ColumnBuilder()).text(Columns.SONGAUTHOR));
        tablebuilder.addColumn((new DatabaseUtils.ColumnBuilder()).text(Columns.SONGCATEGORIES));
        tablebuilder.create(sqlitedatabase);
        DatabaseUtils.createIndex(sqlitedatabase, SONGS_TABLE_NAME, ID_COLUMN + "_index", new String[]{
                ID_COLUMN
        });
    }

    public static void onUpgrade(SQLiteDatabase sqlitedatabase, int ver1, int ver2)
    {
        DatabaseUtils.dropTable(sqlitedatabase, SONGS_TABLE_NAME);
        onCreate(sqlitedatabase);
    }

    protected Song getItemFromCursor(Cursor cursor)
    {
        Song ret = new Song();
        ret.setId(getId(cursor));
        ret.setSongName(getString(cursor, Columns.SONGNAME));
        ret.setSongAuthor(getString(cursor, Columns.SONGAUTHOR));
        ret.setSongCategories(getString(cursor, Columns.SONGCATEGORIES));
        return ret;
    }

    protected String[] getProjection()
    {
        return PROJECTION;
    }

    protected Uri getTableUri()
    {
        return CONTENT_URI;
    }

    protected ContentValues toContentValues(Identify identify)
    {
        return toContentValues((Song) identify);
    }

    protected ContentValues toContentValues(Song song)
    {
        ContentValues contentvalues = new ContentValues();
        int id = song.getId();
        if (id != Identify.INVALID_ID) {
            contentvalues.put(ID_COLUMN, Integer.valueOf(id));
        }
        contentvalues.put(Columns.SONGNAME, song.getSongName());
        contentvalues.put(Columns.SONGAUTHOR, song.getSongAuthor());
        contentvalues.put(Columns.SONGCATEGORIES, song.getSongCategories());
        return contentvalues;
    }
}
