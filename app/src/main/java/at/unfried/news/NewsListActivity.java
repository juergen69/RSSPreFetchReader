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
import android.support.v7.app.ActionBarActivity;

import at.unfried.news.data.NewsContract;
import at.unfried.news.sync.NewsSyncAdapter;

/**
 * Created by juergen on 21.03.2015.
 */
public class NewsListActivity extends ActionBarActivity implements NewsListFragment.Callback {
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news);
        if (findViewById(R.id.news_detail_container) != null) {
            mTwoPane = true;


                Cursor articles = this.getContentResolver().query(
                        NewsContract.NewsEntry.CONTENT_URI,
                        new String[]{NewsContract.NewsEntry.COLUMN_TITLE},
                        null,
                        null,
                        NewsContract.NewsEntry.COLUMN_PUBDATE + " DESC"
                );

                NewsDetailFragment fragment = new NewsDetailFragment();
                if (articles != null && articles.moveToFirst()) {
                    Bundle args = new Bundle();
                    Uri contentUri = NewsContract.NewsEntry.buildNewsUriWithTitle(articles.getString(0));
                    args.putParcelable(NewsDetailFragment.DETAIL_URI, contentUri);
                    fragment.setArguments(args);
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.news_detail_container, fragment, DETAILFRAGMENT_TAG)
                        .commit();

        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        NewsSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(NewsDetailFragment.DETAIL_URI, contentUri);

            NewsDetailFragment fragment = new NewsDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.news_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, NewsDetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NewsSyncAdapter.syncImmediately(this);
    }
}
