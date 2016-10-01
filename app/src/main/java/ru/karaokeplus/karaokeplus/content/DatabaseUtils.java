package ru.karaokeplus.karaokeplus.content;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DatabaseUtils {

    public static class ColumnBuilder
    {

        private boolean mIsPrimary;
        private String mName;
        private boolean mNotNull;
        private String mType;

        public ColumnBuilder blob(String s)
        {
            mName = s;
            mType = COLUMN_TYPE_BLOB;
            return this;
        }

        public String build()
        {
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append(mName).append(" ");
            stringbuilder.append(mType);
            if (mIsPrimary)
            {
                stringbuilder.append(" PRIMARY KEY");
            }
            if (mNotNull)
            {
                stringbuilder.append(" NOT NULL");
            }
            return stringbuilder.toString();
        }

        public ColumnBuilder integer(String s)
        {
            mName = s;
            mType = COLUMN_TYPE_INTEGER;
            return this;
        }

        public ColumnBuilder notNull()
        {
            if (!mIsPrimary)
            {
                mNotNull = true;
            }
            return this;
        }

        public ColumnBuilder primaryKey()
        {
            mIsPrimary = true;
            mNotNull = false;
            return this;
        }

        public ColumnBuilder real(String s)
        {
            mName = s;
            mType = COLUMN_TYPE_REAL;
            return this;
        }

        public ColumnBuilder text(String s)
        {
            mName = s;
            mType = COLUMN_TYPE_TEXT;
            return this;
        }

        public ColumnBuilder()
        {
            mIsPrimary = false;
            mNotNull = false;
        }
    }

    public static class TableBuilder
    {

        private List mColumns;
        private final String mName;

        public TableBuilder addColumn(ColumnBuilder columnbuilder)
        {
            mColumns.add(columnbuilder);
            return this;
        }

        public void create(SQLiteDatabase sqlitedatabase)
        {
            ArrayList arraylist = new ArrayList();
            for (Iterator iterator = mColumns.iterator(); iterator.hasNext();
                 arraylist.add(((ColumnBuilder)iterator.next()).build())) { }
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append("CREATE TABLE ").append(mName);
            stringbuilder.append("(").append(TextUtils.join(", ", arraylist));
            stringbuilder.append(");");
            sqlitedatabase.execSQL(stringbuilder.toString());
        }

        public TableBuilder(String s)
        {
            mName = s;
            mColumns = new ArrayList();
        }
    }


    public static final String AUTHORITY = "com.exchange.investorassistant.contentprovider";
    private static final String COLUMN_TYPE_BLOB = "BLOB";
    private static final String COLUMN_TYPE_INTEGER = "INTEGER";
    private static final String COLUMN_TYPE_REAL = "REAL";
    private static final String COLUMN_TYPE_TEXT = "TEXT";
    private static final String INDEX_SFX = "_idx";
    private static final String LOG_TAG = "DatabaseUtils";
    public static final String SCHEME = "content";

    public static final String COMPLEX_MATCH_SEPARATOR = "&";

    private DatabaseUtils()
    {
    }

    public static void createIndex(SQLiteDatabase sqlitedatabase, String table, String index, String [] columns)
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("CREATE INDEX IF NOT EXISTS ").append(index);
        stringbuilder.append(INDEX_SFX).append(" ON ").append(table);
        stringbuilder.append("(").append(TextUtils.join(", ", columns));
        stringbuilder.append(");");
        sqlitedatabase.execSQL(stringbuilder.toString());
    }

    public static void dropTable(SQLiteDatabase sqlitedatabase, String s)
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("DROP TABLE IF EXISTS ").append(s).append(";");
        sqlitedatabase.execSQL(stringbuilder.toString());
    }

    public static Uri getUri(String s)
    {
        return Uri.parse((new StringBuilder()).append("content://com.exchange.investorassistant.contentprovider/").append(s).toString());
    }

    public static Uri getComplexUri(String table1, String table2) {
        return Uri.parse((new StringBuilder()).append("content://com.exchange.investorassistant.contentprovider/").append(table1).append(COMPLEX_MATCH_SEPARATOR).append(table2).toString());
    }

    public static String in(String s, int i, boolean flag)
    {
        StringBuilder stringbuilder = new StringBuilder();
        if (i > 0)
        {
            StringBuilder stringbuilder1 = stringbuilder.append(s);
            if (flag)
            {
                s = " not ";
            }
            else
            {
                s = " ";
            }
            stringbuilder1.append(s).append("in (");
            for (int j = 0; j < i; j++)
            {
                if (j > 0)
                {
                    stringbuilder.append(", ");
                }
                stringbuilder.append("?");
            }

            stringbuilder.append(")");
        }
        return stringbuilder.toString();
    }

    public static String addTablePrefix(String tableName, String column) {
        return tableName + "." + column;
    }

    public static Map<String,String> createProjectionMap(String [] projections) {
        HashMap<String, String> map = new HashMap<String, String>();

        for(String projection: projections) {
            map.put(projection, projection);
        }

        return map;
    }

    public static String [] createProjections(String table1, String [] projections1, String table2, String [] projections2) {
        String [] ret = new String[projections1.length + projections2.length];

        int counter = 0;

        for(String projection: projections1) {
            ret[counter] = table1 + '.' + projection;
            counter++;
        }

        for(String projection: projections2) {
            ret[counter] = table2 + '.' + projection;
            counter++;
        }

        return ret;
    }
}
