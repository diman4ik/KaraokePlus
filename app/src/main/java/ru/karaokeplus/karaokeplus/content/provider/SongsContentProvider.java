package ru.karaokeplus.karaokeplus.content.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import ru.karaokeplus.karaokeplus.content.DatabaseUtils;
import ru.karaokeplus.karaokeplus.content.dao.AbstractDAO;
import ru.karaokeplus.karaokeplus.content.dao.SongsDAO;


public class SongsContentProvider extends ContentProvider {

    private static final int MATCH_DIR = 1;
    private static final int MATCH_ID = 2;
    private static final int MATCH_JOIN = 3;

    private static UriMatcher URI_MATCHER;
    private DatabaseHelper mDatabaseHelper;

    public SongsContentProvider()
    {}

    private static void addUri(UriMatcher urimatcher, String table) {
        urimatcher.addURI(DatabaseUtils.AUTHORITY, table, MATCH_DIR);
        urimatcher.addURI(DatabaseUtils.AUTHORITY, (new StringBuilder()).append(table).append("/#").toString(), MATCH_ID);
    }

    private static void addUri(UriMatcher urimatcher, String table1, String table2) {
        urimatcher.addURI(DatabaseUtils.AUTHORITY,
                (new StringBuilder()).append(table1).append(DatabaseUtils.COMPLEX_MATCH_SEPARATOR).append(table2).toString(), MATCH_JOIN);
    }

    private int bulkInsert(String s, ContentValues [] acontentvalues) {
        SQLiteDatabase sqlitedatabase = mDatabaseHelper.getWritableDatabase();
        sqlitedatabase.beginTransaction();

        for(ContentValues contentvalues : acontentvalues) {
            String id = contentvalues.getAsString(AbstractDAO.ID_COLUMN);
            if (TextUtils.isEmpty(id) || updateById(s, id, contentvalues) <= 0) {
                insert(s, contentvalues);
            }
        }

        sqlitedatabase.setTransactionSuccessful();
        sqlitedatabase.endTransaction();
        return acontentvalues.length;
    }

    private int delete(String table, String whereClause, String [] whereArgs) {
        return mDatabaseHelper.getWritableDatabase().delete(table, whereClause, whereArgs);
    }

    private int deleteById(String table, String id) {
        return mDatabaseHelper.getWritableDatabase().delete(table, AbstractDAO.WHERE_BASE_COLUMNS_ID, new String[] { id });
    }

    private String getTableFromUri(Uri uri) {
        return (String)uri.getPathSegments().get(0);
    }

    private String [] getTablesFromUri(Uri uri) {
        return ((String)uri.getPathSegments().get(0)).split(DatabaseUtils.COMPLEX_MATCH_SEPARATOR);
    }

    private long insert(String table, ContentValues contentvalues) {
        long l = mDatabaseHelper.getWritableDatabase().insert(table, null, contentvalues);
        if (l <= 0L)
        {
            throw new SQLiteException((new StringBuilder()).append("Failed to insert row into table '").append(table).append("'").toString());
        } else
        {
            return l;
        }
    }

    private int notifyChange(Uri uri, int i) {
        if (i > 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return i;
    }

    private Uri notifyInsert(Uri uri, long l) {
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, l);
    }

    private Cursor query(String table, String [] columns, String selection, String [] selectionArgs, String orderBy) {
        return mDatabaseHelper.getReadableDatabase().query(table, columns, selection, selectionArgs, null, null, orderBy);
    }

    private Cursor queryWithJoin(String table1, String table2, String join, String [] projections, String selection, String [] selectionArgs, String orderBy) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        StringBuilder sb = new StringBuilder();
        sb.append(table1);
        sb.append(" LEFT JOIN ");
        sb.append(table2);
        sb.append(" ON (");
        sb.append(join);
        sb.append(")");
        String table = sb.toString();

        queryBuilder.setTables(table);
        queryBuilder.setProjectionMap(DatabaseUtils.createProjectionMap(projections));

        Cursor cursor = queryBuilder.query(mDatabaseHelper.getReadableDatabase(), null, selection, selectionArgs, null, null, orderBy);
        return cursor;
    }

    private Cursor queryById(String table, String [] columns, String id) {
        return mDatabaseHelper.getReadableDatabase().query(table, columns, AbstractDAO.WHERE_BASE_COLUMNS_ID, new String[]{id}, null, null, null);
    }

    private int update(String table, ContentValues contentvalues, String whereClause, String [] whereArgs) {
        return mDatabaseHelper.getWritableDatabase().update(table, contentvalues, whereClause, whereArgs);
    }

    private int updateById(String table, String id, ContentValues contentvalues) {
        SQLiteDatabase sqlitedatabase = mDatabaseHelper.getWritableDatabase();
        int j = sqlitedatabase.update(table, contentvalues, AbstractDAO.WHERE_BASE_COLUMNS_ID, new String[] { id });
        if (j < 1)
        {
            if (sqlitedatabase.insert(table, null, contentvalues) > 0L)
            {
                j = 1;
            }
        }
        return j;
    }

    private Uri uriWithoutId(Uri uri) {
        return Uri.parse((new StringBuilder()).append(uri.getScheme()).append("://").append(uri.getAuthority()).append("/").append(getTableFromUri(uri)).toString());
    }

    private Cursor withNotificationUri(Uri uri, Cursor cursor) {
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    public int bulkInsert(Uri uri, ContentValues acontentvalues[]) {
        switch (URI_MATCHER.match(uri))
        {
            default:
                throw new SQLiteException((new StringBuilder()).append("Unknown uri: ").append(uri).toString());

            case MATCH_DIR:
                return notifyChange(uri, bulkInsert(getTableFromUri(uri), acontentvalues));
        }
    }

    @Override
    public int delete(Uri uri, String s, String as[]) {
        switch (URI_MATCHER.match(uri))
        {
            default:
                throw new SQLiteException((new StringBuilder()).append("Unknown uri: ").append(uri).toString());

            case MATCH_DIR:
                return notifyChange(uri, delete(getTableFromUri(uri), s, as));

            case MATCH_ID:
                return notifyChange(uri, deleteById(getTableFromUri(uri), uri.getLastPathSegment()));
        }
    }

    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri))
        {
            default:
                throw new SQLiteException((new StringBuilder()).append("Unknown uri: ").append(uri).toString());

            case MATCH_DIR:
                return (new StringBuilder()).append("vnd.android.cursor.dir/vnd.").append(uri.getAuthority()).append(".").append(getTableFromUri(uri)).toString();

            case MATCH_ID:
                return (new StringBuilder()).append("vnd.android.cursor.dir/item.").append(uri.getAuthority()).append(".").append(getTableFromUri(uri)).toString();
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentvalues) {

        switch (URI_MATCHER.match(uri))
        {
            default:
                throw new SQLiteException((new StringBuilder()).append("Unknown uri: ").append(uri).toString());

            case MATCH_DIR:
                return notifyInsert(uri, insert(getTableFromUri(uri), contentvalues));

            case MATCH_ID:
                notifyChange(uriWithoutId(uri), updateById(getTableFromUri(uri), uri.getLastPathSegment(), contentvalues));
                break;
        }
        return uri;
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String [] projection, String selection, String [] selectionArgs, String sortOrder) {
        switch (URI_MATCHER.match(uri))
        {
            default:
                throw new SQLiteException((new StringBuilder()).append("Unknown uri: ").append(uri).toString());

            case MATCH_DIR:
                return withNotificationUri(uri, query(getTableFromUri(uri), projection, selection, selectionArgs, sortOrder));

            case MATCH_JOIN:
                String [] tables = getTablesFromUri(uri);

                String [] selections = selection.split(",");

                String join = selections[0];
                selection = selections[1];

                return withNotificationUri(uri, queryWithJoin(tables[0], tables[1], join, projection, selection, selectionArgs, sortOrder));

            case MATCH_ID:
                return withNotificationUri(uri, queryById(getTableFromUri(uri), projection, uri.getLastPathSegment()));
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentvalues, String s, String as[]) {
        switch (URI_MATCHER.match(uri))
        {
            default:
                throw new SQLiteException((new StringBuilder()).append("Unknown uri: ").append(uri).toString());

            case MATCH_DIR:
                return notifyChange(uri, update(getTableFromUri(uri), contentvalues, s, as));

            case MATCH_ID:
                return notifyChange(uri, updateById(getTableFromUri(uri), uri.getLastPathSegment(), contentvalues));
        }
    }

    static
    {
        URI_MATCHER = new UriMatcher(-1);
        addUri(URI_MATCHER, SongsDAO.SONGS_TABLE_NAME);
    }
}
