package ru.karaokeplus.karaokeplus;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.karaokeplus.karaokeplus.search.ExampleAdapter;
import ru.karaokeplus.karaokeplus.search.SongSuggestion;
import ru.karaokeplus.karaokeplus.content.dao.SongsDAO;
import ru.karaokeplus.karaokeplus.content.data.CategoryContent;
import ru.karaokeplus.karaokeplus.content.data.Song;


/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link CategoryListActivity}.
 */
public class CategoryDetailActivity extends AppCompatActivity {

    private DrawerLayout _drawerLayout;
    private ListView _mainMenuList;

    private String[] _mainMenuItems;
    private CategoryDetailFragment _fragment;
    private ViewGroup _leftDrawer;
    private ActionBarDrawerToggle _drawerToggle;

    private SearchView _searchView;
    private MenuItem _searchItem;

    private int _clickCounter;
    private long _clickTime;

    private SongsDAO _sdao;
    private static List _songs;

    CategoryContent.CategoryItem _selectedCategory  = CategoryContent.ITEM_MAP.get(R.string.category_all);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_item_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationIcon(R.drawable.menu_black);

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

                    Toast.makeText(CategoryDetailActivity.this, R.string.reload_message, Toast.LENGTH_LONG).show();
                    reloadSongs();
                    _fragment.setCategory(_selectedCategory);
                }
            }
        });


        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        _mainMenuList = (ListView) findViewById(R.id.leftmenu_items);

        _mainMenuItems = new String[]{
                getString(R.string.category_all),
                getString(R.string.category_russian),
                getString(R.string.category_foreign),
                getString(R.string.category_rock),
                getString(R.string.category_pop),
                getString(R.string.category_shanson)
        };

        _mainMenuList.setAdapter(new ArrayAdapter<String>(this, R.layout.item_list_content, R.id.content, _mainMenuItems));
        _mainMenuList.setOnItemClickListener(new DrawerItemClickListener());

        _leftDrawer = (ViewGroup) findViewById(R.id.left_drawer);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        _drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                _drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        _drawerLayout.setDrawerListener(_drawerToggle);

        _sdao = new SongsDAO(this);

        _songs = _sdao.getAll();

        if (_songs.size() == 0) {
            reloadSongs();
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putInt(CategoryDetailFragment.ARG_ITEM_ID,
                    getIntent().getIntExtra(CategoryDetailFragment.ARG_ITEM_ID, 0));
            _fragment = new CategoryDetailFragment();
            _fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, _fragment, "FRAGMENT_TAG")
                    .commit();
        }
        else {
            _fragment = (CategoryDetailFragment) getSupportFragmentManager().findFragmentByTag("FRAGMENT_TAG");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (_drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            _mainMenuList.setItemChecked(-1, true);

            String mainMenuItem = _mainMenuItems[position];

            for (CategoryContent.CategoryItem item : CategoryContent.ITEMS) {

                if (item.categoryName.equals(mainMenuItem)) {
                    _selectedCategory = item;
                    _fragment.setCategory(_selectedCategory);
                    break;
                }
            }

            _mainMenuList.setItemChecked(position, true);
            _drawerLayout.closeDrawer(_leftDrawer);
        }
    }
}
