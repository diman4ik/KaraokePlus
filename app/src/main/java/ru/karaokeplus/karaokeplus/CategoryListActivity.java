package ru.karaokeplus.karaokeplus;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import ru.karaokeplus.karaokeplus.search.ExampleAdapter;
import ru.karaokeplus.karaokeplus.search.SongSuggestion;


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

    private SearchView _searchView;
    private MenuItem _searchItem;

    CategoryDetailFragment _fragment;

    CategoryContent.CategoryItem _selectedCategory  = CategoryContent.ITEM_MAP.get(R.string.category_all);


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

        if (savedInstanceState != null) {
            _fragment = (CategoryDetailFragment) getSupportFragmentManager().findFragmentByTag("FRAGMENT_TAG");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        _searchItem = menu.findItem(R.id.action_search);

        MenuItemCompat.setOnActionExpandListener(_searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                _fragment.setCategory(_selectedCategory);
                return true;
            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        _searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        _searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        _searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadSearchResult(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void loadSearchResult(String query) {

        query = query.toLowerCase();

        if(query.length() == 0)
            return;

        List<Song> items = Utils.filterByCategory(_songs, _selectedCategory);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final List<SongSuggestion> suggestions = new ArrayList<SongSuggestion>();
            String [] columns =  new String [] { "_id"};
            Object[] temp = new Object[] { 0 };

            for(Song item : items) {
                suggestions.add(new SongSuggestion(item));
            }

            MatrixCursor cursor = new MatrixCursor(columns);

            int counter = items.size();
            String author = "";

            // Добавить сооответствующих исполнителей
            for(int i = 0; i < items.size(); i++) {
                if(author.equals(items.get(i).getSongAuthor().toLowerCase()))
                    continue;

                author = items.get(i).getSongAuthor().toLowerCase();

                if(query.length() > 2) {
                    if (author.contains(query)) {
                        temp[0] = counter++;
                        cursor.addRow(temp);
                        suggestions.add(new SongSuggestion(items.get(i).getSongAuthor(), ""));
                    }
                }
                else {
                    if (author.startsWith(query)) {
                        temp[0] = counter++;
                        cursor.addRow(temp);
                        suggestions.add(new SongSuggestion(items.get(i).getSongAuthor(), ""));
                    }
                }
            }


            // Добавить песни исполнителей
            for(int i = 0; i < items.size(); i++) {

                author = items.get(i).getSongAuthor().toLowerCase();
                String songname = items.get(i).getSongName().toLowerCase();

                if(author.startsWith(query) && songname.length() > 0) {
                    temp[0] = i;
                    cursor.addRow(temp);
                }
                else if(query.length() > 2 && author.contains(query) && songname.length() > 0) {
                    temp[0] = i;
                    cursor.addRow(temp);
                }
                else {
                    if( query.length() > 2 && songname.contains(query)) {
                        temp[0] = i;
                        cursor.addRow(temp);
                    }
                }
            }

            // SearchView
            _searchView.setSuggestionsAdapter(new ExampleAdapter(this, cursor, suggestions));
            _searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int position) {
                    return true;
                }

                @Override
                public boolean onSuggestionClick(int position) {

                    Cursor searchCursor = _searchView.getSuggestionsAdapter().getCursor();
                    if(searchCursor.moveToPosition(position)) {
                        int index = searchCursor.getInt(0);
                        setListBasedOnSuggestion(suggestions, suggestions.get(index));
                        _searchView.setIconified(true);
                    }

                    //MenuItemCompat.collapseActionView(_searchItem);
                    return true;
                }
            });
        }
    }

    private void setListBasedOnSuggestion(List<SongSuggestion> suggestions, SongSuggestion suggestion) {
        if(suggestion.author.length() > 0 && suggestion.songname.length() > 0) {
            List<Song> songs = new ArrayList<Song>();
            songs.add(suggestion.song);
            _fragment.setSongs(songs);
        }
        else if(suggestion.author.length() > 0) {
            List<Song> songs = new ArrayList<Song>();
            for(SongSuggestion song : suggestions) {
                if(song.author.equals(suggestion.author) && song.song != null)
                    songs.add(song.song);
            }
            _fragment.setSongs(songs);
        }
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

    public void selectItem(int position) {
        CategoryContent.CategoryItem item = CategoryContent.ITEMS.get(position);
        _selectedCategory = item;
        if (_twoPane) {

            if(_fragment == null) {
                Bundle arguments = new Bundle();
                arguments.putInt(CategoryDetailFragment.ARG_ITEM_ID, item.id);
                _fragment = new CategoryDetailFragment();
                _fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, _fragment, "FRAGMENT_TAG")
                        .commit();
            }
            else {
                _fragment.setCategory(item);
            }
        } else {
            Intent intent = new Intent(this, CategoryDetailActivity.class);
            intent.putExtra(CategoryDetailFragment.ARG_ITEM_ID, item.id);
            startActivity(intent);
            finish();
        }
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
            holder.mContentView.setText(_selectedCategory.categoryName);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _selectedCategory = holder.mItem;
                    if (_twoPane) {

                        if(_fragment == null) {
                            Bundle arguments = new Bundle();
                            arguments.putInt(CategoryDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                            _fragment = new CategoryDetailFragment();
                            _fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.item_detail_container, _fragment, "FRAGMENT_TAG")
                                    .commit();
                        }
                        else {
                            _fragment.setCategory(_selectedCategory);
                        }
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, CategoryDetailActivity.class);
                        intent.putExtra(CategoryDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        context.startActivity(intent);
                        finish();
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
