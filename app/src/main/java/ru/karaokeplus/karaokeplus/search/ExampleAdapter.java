package ru.karaokeplus.karaokeplus.search;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.karaokeplus.karaokeplus.R;

public class ExampleAdapter extends CursorAdapter {

    private List<SongSuggestion> items;

    private TextView textAuthor;
    private TextView textSong;


    public ExampleAdapter(Context context, Cursor cursor, List<SongSuggestion> items) {
        super(context, cursor, false);
        this.items = items;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int index = cursor.getInt(0);
        textAuthor.setText(items.get(index).author);
        textSong.setText(items.get(index).songname);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.search_item, parent, false);

        textAuthor = (TextView) view.findViewById(R.id.item_author);
        textSong = (TextView) view.findViewById(R.id.item_song);

        return view;
    }
}
