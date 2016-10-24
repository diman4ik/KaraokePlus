package ru.karaokeplus.karaokeplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ru.karaokeplus.karaokeplus.content.dao.SongsDAO;
import ru.karaokeplus.karaokeplus.content.data.CategoryContent;
import ru.karaokeplus.karaokeplus.content.data.Song;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CategoryDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CategoryListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean _twoPane;
    private SongsDAO _sdao;
    private static List _songs;

    private int _clickCounter;
    private long _clickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long clickTime = System.currentTimeMillis();
                if( clickTime - _clickTime > 1000 ) {
                    _clickCounter = 0;
                }

                _clickTime = clickTime;

                _clickCounter += 1;

                if(_clickCounter >= 5) {
                    _clickCounter = 0;

                    Toast.makeText(CategoryListActivity.this, R.string.reload_message, Toast.LENGTH_LONG).show();
                    reloadSongs();
                }
            }
        });

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            _twoPane = true;
        }

        CategoryContent.addItem( new CategoryContent.CategoryItem(R.string.category_all, getString(R.string.category_all), getString(R.string.category_all_desc)));
        CategoryContent.addItem( new CategoryContent.CategoryItem(R.string.category_russian, getString(R.string.category_russian), getString(R.string.category_russian_desc)));
        CategoryContent.addItem( new CategoryContent.CategoryItem(R.string.category_foreign, getString(R.string.category_foreign), getString(R.string.category_foreign_desc)));
        CategoryContent.addItem( new CategoryContent.CategoryItem(R.string.category_rock, getString(R.string.category_rock), getString(R.string.category_rock_desc)));
        CategoryContent.addItem( new CategoryContent.CategoryItem(R.string.category_pop, getString(R.string.category_pop), getString(R.string.category_pop_desc)));
        CategoryContent.addItem( new CategoryContent.CategoryItem(R.string.category_shanson, getString(R.string.category_shanson), getString(R.string.category_shanson_desc)));

        if(_twoPane) {
            _sdao = new SongsDAO(this);

            _songs = _sdao.getAll();

            if (_songs.size() == 0) {
                reloadSongs();
            }
        }

        selectItem(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            default:
                break;
        }

        return true;
    }

    private void reloadSongs() {
        // Прочитать файл с песнями с диска
        try {
            _sdao.deleteAll();
            List<Song> songs = Utils.readFileFromStorage();

            for(Song sng : songs) {
                _sdao.insert(sng);
            }

           _songs = _sdao.getAll();
        } catch (Exception ex) {
            Log.d("FILE", ex.getMessage());
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(CategoryContent.ITEMS));
    }

    public  void selectItem(int position) {
        CategoryContent.CategoryItem item = CategoryContent.ITEMS.get(position);
        if (_twoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(CategoryDetailFragment.ARG_ITEM_ID, item.id);
            CategoryDetailFragment fragment = new CategoryDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, CategoryDetailActivity.class);
            intent.putExtra(CategoryDetailFragment.ARG_ITEM_ID, item.id);
            startActivity(intent);
        }
    }

    private List<Song> getSongs(CategoryContent.CategoryItem category) {
        List<Song> ret = new ArrayList<>();

        for(Song song : ((List<Song>)_songs)) {

            if(song.getCategories().contains(category)) {
                ret.add(song);
            }
        }

        return ret;
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<CategoryContent.CategoryItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<CategoryContent.CategoryItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            //holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).categoryName);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_twoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putInt(CategoryDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        CategoryDetailFragment fragment = new CategoryDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, CategoryDetailActivity.class);
                        intent.putExtra(CategoryDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public CategoryContent.CategoryItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
