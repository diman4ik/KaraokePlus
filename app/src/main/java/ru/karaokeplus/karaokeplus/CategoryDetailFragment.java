package ru.karaokeplus.karaokeplus;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.karaokeplus.karaokeplus.content.data.CategoryContent;
import ru.karaokeplus.karaokeplus.content.data.Song;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link CategoryListActivity}
 * in two-pane mode (on tablets) or a {@link CategoryDetailActivity}
 * on handsets.
 */
public class CategoryDetailFragment extends ListFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private CategoryContent.CategoryItem mItem;

    private List<Song> _songs;
    private SongsAdapter _adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CategoryDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            Integer categoryKey = getArguments().getInt(ARG_ITEM_ID);

            mItem = CategoryContent.ITEM_MAP.get(categoryKey);

            /*Activity activity = this.getActivity();
            /*CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
             (appBarLayout != null) {
                appBarLayout.setTitle(mItem.categoryName);
            }*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        if (mItem != null) {
            //((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.categoryName);
            _songs = CategoryListActivity.getSongs(mItem);

            _adapter = new SongsAdapter(getActivity(), _songs);
            setListAdapter(_adapter);
        }

        return rootView;
    }

    class SongsAdapter extends ArrayAdapter<Song> {
        Context _context;

        class ViewHolder {
            TextView _songAuthorText;
            TextView _songNameText;
            TextView _songCodeText;
        }

        public SongsAdapter(Context context, List<Song> values) {
            super(context, R.layout.songs_row, values);
            _context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Song song = getItem(position);

            View rowView = convertView;

            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                rowView = inflater.inflate(R.layout.songs_row, parent, false);

                ViewHolder holder = new ViewHolder();

                holder._songAuthorText = (TextView) rowView.findViewById(R.id.song_author_text);
                holder._songNameText = (TextView) rowView.findViewById(R.id.song_name_text);
                holder._songCodeText = (TextView) rowView.findViewById(R.id.song_code_text);

                rowView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) rowView.getTag();

            holder._songAuthorText.setText(song.getSongAuthor());
            holder._songNameText.setText(song.getSongName());
            holder._songCodeText.setText(song.getSongCode());

            return rowView;
        }
    }
}
