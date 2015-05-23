/*
 * Copyright (C) 2015 Jürgen Unfried
 *
 * This file is part of the RSS Prefetch Reader app that was developed as final
 * exam for the "Developing Android Apps" course (https://www.udacity.com/course/ud853)
 *
 * This app is not allowed to be published!
 */
package at.unfried.news;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import at.unfried.news.data.NewsContract;
import at.unfried.news.service.NewsService;
import at.unfried.news.sync.NewsSyncAdapter;

/**
 * Created by juergen on 20.03.2015.
 */
public class NewsListFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>  {
    public static final String LOG_TAG = NewsListFragment.class.getSimpleName();
    private NewsAdapter mNewsAdapter;
    private ListView mListView;
    private static final String SELECTED_KEY = "selected_position";
    private static final int NEWS_LOADER = 0;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String[] NEWS_COLUMNS = {
            NewsContract.NewsEntry._ID,
            NewsContract.NewsEntry.COLUMN_TITLE,
            NewsContract.NewsEntry.COLUMN_DESCRIPTION,
            NewsContract.NewsEntry.COLUMN_LINK,
            NewsContract.NewsEntry.COLUMN_THUMBNAIL,
            NewsContract.NewsEntry.COLUMN_PUBDATE
    };

    static final int COL_ID = 0;
    static final int COL_TITLE = 1;
    static final int COL_DESC = 2;
    static final int COL_LINK = 3;
    static final int COL_THUMB = 4;
    static final int COL_PUBDATE = 5;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public NewsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mNewsAdapter = new NewsAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_news, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listview_news);
        mListView.setAdapter(mNewsAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                TextView title = (TextView) view.findViewById(R.id.list_item_title);
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(NewsContract.NewsEntry.buildNewsUriWithTitle(
                                            title.getText().toString())
                            );
                }
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(NEWS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getActivity(), NewsService.class);

        getActivity().startService(intent);

        getLoaderManager().restartLoader(NEWS_LOADER, null, this);
        NewsSyncAdapter.syncImmediately(getActivity());

    }

    /**
     * LOADER FUNCTIONS
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = NewsContract.NewsEntry.COLUMN_PUBDATE + " DESC";

        return new CursorLoader(getActivity(),
                NewsContract.NewsEntry.buildNewsUri(),
                NEWS_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mNewsAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNewsAdapter.swapCursor(null);
    }
}
