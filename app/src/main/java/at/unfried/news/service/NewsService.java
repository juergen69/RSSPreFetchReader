/*
 * Copyright (C) 2015 Jürgen Unfried
 *
 * This file is part of the RSS Prefetch Reader app that was developed as final
 * exam for the "Developing Android Apps" course (https://www.udacity.com/course/ud853)
 *
 * This app is not allowed to be published!
 */
package at.unfried.news.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

import java.util.Vector;

import at.unfried.news.NewsUtility;
import at.unfried.news.data.NewsContract;

/**
 * Service to collect the News from the server and persist them into the database.
 *
 * Created by juergen on 17.03.2015.
 */
public class NewsService extends IntentService {
    private static final String LOG_TAG = NewsService.class.getSimpleName();
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NewsService() {
        super("NewsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Vector<ContentValues> cVVector = NewsUtility.downloadRssFeed();

        // add rss feed to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            this.getContentResolver().bulkInsert(NewsContract.NewsEntry.CONTENT_URI, cvArray);
        }

        // Download missing articles (database entries which have no fulltext)
        Cursor missingArticles = this.getContentResolver().query(NewsContract.NewsEntry.CONTENT_URI, new String[]{NewsContract.NewsEntry.COLUMN_LINK}, NewsContract.NewsEntry.COLUMN_FULLTEXT + " is null", null, null);
        int indexLink = missingArticles.getColumnIndex(NewsContract.NewsEntry.COLUMN_LINK);

        if (missingArticles.moveToFirst()) {
            do {
                String fullArticle = NewsUtility.downloadFullArticle(missingArticles.getString(indexLink));

                if (fullArticle.isEmpty()) {
                    continue;
                }

                ContentValues cv = new ContentValues();
                cv.put(NewsContract.NewsEntry.COLUMN_FULLTEXT, fullArticle);

                getContentResolver().update(NewsContract.NewsEntry.CONTENT_URI, cv, NewsContract.NewsEntry.COLUMN_LINK + "='" + missingArticles.getString(indexLink) + "'", null);
            } while(missingArticles.moveToNext());
       }

       missingArticles.close();
    }
}
