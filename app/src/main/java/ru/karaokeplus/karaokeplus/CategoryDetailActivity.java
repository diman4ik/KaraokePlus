package ru.karaokeplus.karaokeplus;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import ru.karaokeplus.karaokeplus.content.data.CategoryContent;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link CategoryListActivity}.
 */
public class CategoryDetailActivity extends AppCompatActivity {

    private DrawerLayout _drawerLayout;
    private ListView _mainMenuList;

    private String [] _mainMenuItems;
    private CategoryDetailFragment _fragment;
    private ViewGroup _leftDrawer;
    private ActionBarDrawerToggle _drawerToggle;


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

        getSupportActionBar().setTitle(getString(R.string.app_name));

        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        _mainMenuList = (ListView) findViewById(R.id.leftmenu_items);

        _mainMenuItems = new String [] {
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

        _leftDrawer = (ViewGroup)findViewById(R.id.left_drawer);

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
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);

        /*if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange", newText);

                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }*/
        return super.onCreateOptionsMenu(menu);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            _mainMenuList.setItemChecked( -1, true);

            String mainMenuItem = _mainMenuItems[position];

            for(CategoryContent.CategoryItem item : CategoryContent.ITEMS) {

                if(item.categoryName.equals(mainMenuItem)) {
                    _fragment.setCategory(item);
                    break;
                }
            }

            _mainMenuList.setItemChecked(position, true);
            _drawerLayout.closeDrawer(_leftDrawer);
        }
    }
}
