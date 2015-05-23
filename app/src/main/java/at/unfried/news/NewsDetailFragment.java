/*
 * Copyright (C) 2015 Jürgen Unfried
 *
 * This file is part of the RSS Prefetch Reader app that was developed as final
 * exam for the "Developing Android Apps" course (https://www.udacity.com/course/ud853)
 *
 * This app is not allowed to be published!
 */
package at.unfried.news;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import at.unfried.news.data.NewsContract;
import at.unfried.news.data.NewsDbHelper;

/**
 * Created by juergen on 25.03.2015.
 */
public class NewsDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = NewsDetailFragment.class.getSimpleName();

    private Uri mUri;
    static final String DETAIL_URI = "URI";
    private static final int DETAIL_LOADER = 0;

    private TextView mDateView;
    private TextView mTitleView;
    private ImageView mIconView;
    private TextView mDescriptionView;
    private TextView mFulltextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(NewsDetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_news_detail, container, false);
        mDateView = (TextView) rootView.findViewById(R.id.news_detail_date);
        mTitleView = (TextView) rootView.findViewById(R.id.news_detail_title);
        mIconView = (ImageView) rootView.findViewById(R.id.news_detail_image);
        mDescriptionView = (TextView) rootView.findViewById(R.id.news_detail_description);
        mFulltextView = (TextView) rootView.findViewById(R.id.news_detail_fulltext);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            CursorLoader cursorLoader = new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    null,
                    null,
                    NewsContract.NewsEntry.COLUMN_PUBDATE
            );
            return cursorLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            long date = data.getLong(NewsDbHelper.COLINDEX_PUBDATE);
            mDateView.setText(NewsUtility.convertDateToNiceDate(date));
            mTitleView.setText(data.getString(NewsDbHelper.COLINDEX_TITLE));
            mDescriptionView.setText(data.getString(NewsDbHelper.COLINDEX_DESCRIPTION));
            mFulltextView.setText(data.getString(NewsDbHelper.COLINDEX_FULLTEXT));
            mIconView.setImageResource(R.mipmap.ic_launcher);
            String imageUrl = data.getString(NewsDbHelper.COLINDEX_THUMBNAIL);
            if (imageUrl.isEmpty()) {
                mIconView.setImageResource(R.mipmap.ic_launcher);
            } else {
                Picasso.with(getActivity()).load(imageUrl).placeholder(R.mipmap.ic_launcher).into(mIconView);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Nothing to to
    }

}
