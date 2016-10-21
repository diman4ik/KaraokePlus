package ru.karaokeplus.karaokeplus;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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

import java.util.List;

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

        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        _mainMenuList = (ListView) findViewById(R.id.leftmenu_items);

        _mainMenuItems = new String[]{
                getString(R.string.category_all),
                getString(R.string.category_russian),
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
                    .add(R.id.item_detail_container, _fragment)
                    .commit();
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
        MenuItem searchItem = menu.findItem(R.id.action_search);
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


    // History
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void loadSearchResult(String query) {

        List<Song> items = CategoryListActivity.getSongs(CategoryContent.ITEM_MAP.get(R.string.category_all));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            String [] columns =  new String [] { "_id", "author", "song"};
            Object[] temp = new Object[] { 0, "none", "none" };

            MatrixCursor cursor = new MatrixCursor(columns);

            for(int i = 0; i < items.size(); i++) {
                temp[0] = i;
                temp[1] = items.get(i).getSongAuthor();
                temp[2] = items.get(i).getSongName();
                cursor.addRow(temp);
            }

            // SearchView
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            _searchView.setSuggestionsAdapter(new ExampleAdapter(this, cursor, items));
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            _mainMenuList.setItemChecked(-1, true);

            String mainMenuItem = _mainMenuItems[position];

            for (CategoryContent.CategoryItem item : CategoryContent.ITEMS) {

                if (item.categoryName.equals(mainMenuItem)) {
                    _fragment.setCategory(item);
                    break;
                }
            }

            _mainMenuList.setItemChecked(position, true);
            _drawerLayout.closeDrawer(_leftDrawer);
        }
    }

    public class ExampleAdapter extends CursorAdapter {

        private List<Song> items;

        private TextView textAuthor;
        private TextView textSong;


        public ExampleAdapter(Context context, Cursor cursor, List<Song> items) {

            super(context, cursor, false);

            this.items = items;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            textAuthor.setText(items.get(cursor.getPosition()).getSongAuthor());
            textSong.setText(items.get(cursor.getPosition()).getSongName());
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
}
