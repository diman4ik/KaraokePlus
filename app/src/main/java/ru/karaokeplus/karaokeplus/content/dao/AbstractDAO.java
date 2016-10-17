package ru.karaokeplus.karaokeplus.content.dao;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import ru.karaokeplus.karaokeplus.content.DatabaseUtils;

public abstract class AbstractDAO {

    protected static final String COUNT_1_PROJECTION[] = {
            "count(1) as _count"
    };

    public static final String ID_COLUMN = "_id";
    public static final String WHERE_BASE_COLUMNS_ID = "_id=?";

    protected final ContentResolver mContentResolver;
    protected final Context mContext;

    protected AbstractDAO(Context context)
    {
        mContext = context;
        mContentResolver = context.getContentResolver();
    }

    public static double getDouble(Cursor cursor, String s)
    {
        return cursor.getDouble(cursor.getColumnIndex(s));
    }

    public static float getFloat(Cursor cursor, String s)
    {
        return cursor.getFloat(cursor.getColumnIndex(s));
    }

    public static int getId(Cursor cursor)
    {
        Integer id = getNullableInteger(cursor, ID_COLUMN);
        if (cursor == null)
        {
            return Identify.INVALID_ID;
        } else
        {
            return id.intValue();
        }
    }

    public static int getId(Uri uri)
    {
        if (uri == null)
        {
            return Identify.INVALID_ID;
        } else
        {
            return (int) ContentUris.parseId(uri);
        }
    }

    public static int getInt(Cursor cursor, String s)
    {
        return cursor.getInt(cursor.getColumnIndex(s));
    }

    private List getItemsFromCursor(Cursor cursor)
    {
        ArrayList arraylist = new ArrayList();
        if (isCursorValid(cursor))
        {
            if (cursor.moveToFirst())
            {
                do
                {
                    arraylist.add(getItemFromCursor(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return arraylist;
    }

    public static long getLong(Cursor cursor, String s)
    {
        return cursor.getLong(cursor.getColumnIndex(s));
    }

    public static Double getNullableDouble(Cursor cursor, String s)
    {
        int i = cursor.getColumnIndex(s);

        Double ret = null;

        if (!cursor.isNull(i))
        {
            ret = Double.valueOf(cursor.getDouble(i));
        }

        return ret;
    }

    public static Float getNullableFloat(Cursor cursor, String s)
    {
        int i = cursor.getColumnIndex(s);

        Float ret = null;

        if (!cursor.isNull(i))
        {
            ret = Float.valueOf(cursor.getFloat(i));
        }

        return ret;
    }

    public static Integer getNullableInteger(Cursor cursor, String column)
    {
        int i = cursor.getColumnIndex(column);

        Integer ret = null;

        if (!cursor.isNull(i))
        {
            long val = cursor.getLong(i);
            ret = Integer.valueOf(cursor.getInt(i));
        }

        return ret;
    }

    public static Long getNullableLong(Cursor cursor, String s)
    {
        int i = cursor.getColumnIndex(s);

        Long ret = null;

        if (!cursor.isNull(i))
        {
            ret = Long.valueOf(cursor.getLong(i));
        }

        return ret;
    }

    public static String getString(Cursor cursor, String s)
    {
        return cursor.getString(cursor.getColumnIndex(s));
    }

    public static boolean isCursorValid(Cursor cursor)
    {
        return cursor != null;
    }

    public static String[] prepareArguments(Object aobj[])
    {
        String as[] = new String[0];
        if (aobj != null)
        {
            String as1[] = new String[aobj.length];
            int i = 0;
            do
            {
                as = as1;
                if (i >= aobj.length)
                {
                    break;
                }
                as1[i] = String.valueOf(aobj[i]);
                i++;
            } while (true);
        }
        return as;
    }

    private ContentValues[] toContentValues(List list)
    {
        int j = list.size();
        ContentValues acontentvalues[] = new ContentValues[j];
        for (int i = 0; i < j; i++)
        {
            acontentvalues[i] = toContentValues((Identify)list.get(i));
        }

        return acontentvalues;
    }

    protected int bulkInsert(Uri uri, List list)
    {
        ContentValues[] values = toContentValues(list);
        return mContentResolver.bulkInsert(uri, values);
    }

    public int bulkInsert(List list)
    {
        return bulkInsert(getTableUri(), list);
    }

    public int delete(int i)
    {
        int j = 0;
        if (i != Identify.INVALID_ID)
        {
            j = delete(getTableUri(), WHERE_BASE_COLUMNS_ID, new String[] {
                    Integer.toString(i)
            });
        }
        return j;
    }

    protected int delete(Uri uri, String where, String [] selection_args)
    {
        return mContentResolver.delete(uri, where, selection_args);
    }

    public int delete(Identify identify)
    {
        return delete(identify.getId());
    }

    public int deleteAll() {
        return delete(getTableUri(), "", null);
    }

    public int deleteAll(List list)
    {
        int ai[] = new int[list.size()];
        int i = 0;
        if (!list.isEmpty())
        {
            for (i = 0; i < list.size(); i++)
            {
                ai[i] = ((Identify)list.get(i)).getId();
            }

            i = deleteAll(ai);
        }
        return i;
    }

    public int deleteAll(int ai[])
    {
        boolean flag = false;
        ArrayList arraylist = new ArrayList();
        int k = ai.length;
        for (int i = 0; i < k; i++)
        {
            int l = ai[i];
            if (l != Identify.INVALID_ID)
            {
                arraylist.add(Integer.toString(l));
            }
        }

        int j = ((flag) ? 1 : 0);
        if (!arraylist.isEmpty())
        {
            String in = DatabaseUtils.in(ID_COLUMN, ai.length, false);
            String as[] = (String[])arraylist.toArray(new String[arraylist.size()]);
            j = delete(getTableUri(), in, as);
        }
        return j;
    }

    public boolean existsByCustomField(Object obj, String s) {
        obj = mContentResolver.query(getTableUri(), COUNT_1_PROJECTION, s, new String[] {
                obj.toString()
        }, null);
        boolean flag = false;
        boolean flag1 = false;
        if (isCursorValid(((Cursor) (obj))))
        {
            flag = flag1;
            if (((Cursor) (obj)).moveToFirst())
            {
                if (getInt(((Cursor) (obj)), "_count") > 0)
                {
                    flag = true;
                } else
                {
                    flag = false;
                }
            }
            ((Cursor) (obj)).close();
        }
        return flag;
    }

    public boolean existsById(int i)
    {
        return existsByCustomField(Integer.valueOf(i), WHERE_BASE_COLUMNS_ID);
    }

    protected List get(Uri uri, String [] projection, String selection, String [] selectionArgs, String setOrder)
    {
        return getItemsFromCursor(mContentResolver.query(uri, projection, selection, selectionArgs, setOrder));
    }

    protected List get(String selection, String [] selectionArgs, String setOrder)
    {
        return get(getTableUri(), getProjection(), selection, selectionArgs, setOrder);
    }

    public Identify get(int id)
    {
        List list = get(WHERE_BASE_COLUMNS_ID, new String[] {  Integer.toString(id) }, null);

        if (list.isEmpty())
        {
            return null;
        } else {
            return (Identify)list.get(0);
        }
    }

    public List getAll()
    {
        return get(null, null, null);
    }

    public ContentResolver getContentResolver()
    {
        return mContentResolver;
    }

    public Context getContext()
    {
        return mContext;
    }

    protected abstract Identify getItemFromCursor(Cursor cursor);

    protected abstract String[] getProjection();

    protected abstract Uri getTableUri();

    protected Uri insert(Uri uri, Identify identify)
    {
        return mContentResolver.insert(uri, toContentValues(identify));
    }

    public Uri insert(Identify identify)
    {
        return insert(getTableUri(), identify);
    }

    public boolean insertOrUpdate(Identify identify)
    {
        return update(identify) > 0 || insert(identify) != null;
    }

    public boolean isEmptyTable()
    {
        return get(null, null, "null limit 1").isEmpty();
    }

    protected abstract ContentValues toContentValues(Identify identify);

    protected int update(Uri uri, Identify identify, String where, String [] where_args)
    {
        return mContentResolver.update(uri, toContentValues(identify), where, where_args);
    }

    public int update(Identify identify)
    {
        int i = identify.getId();
        return update(getTableUri(), identify, WHERE_BASE_COLUMNS_ID, new String[] {
                Integer.toString(i)
        });
    }
}
