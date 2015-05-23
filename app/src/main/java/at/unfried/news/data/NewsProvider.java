/*
 * Copyright (C) 2015 Jürgen Unfried
 *
 * This file is part of the RSS Prefetch Reader app that was developed as final
 * exam for the "Developing Android Apps" course (https://www.udacity.com/course/ud853)
 *
 * This app is not allowed to be published!
 */
package at.unfried.news.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * News Content Provider.
 *
 * Created by juergen on 17.03.2015.
 */
public class NewsProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private NewsDbHelper mDbHelper;

    static final int NEWS = 100;
    static final int NEWS_WITH_TITLE = 101;
    static final int NEWS_WITH_ID = 103;

    private static final SQLiteQueryBuilder sNewsQueryBuilder;

    static {
        sNewsQueryBuilder = new SQLiteQueryBuilder();
        sNewsQueryBuilder.setTables(NewsContract.NewsEntry.TABLE_NAME);
    }

    private static final String sIdSelection =
            NewsContract.NewsEntry.TABLE_NAME+
                    "." + NewsContract.NewsEntry._ID + " = ? ";

    private static final String sTitleSelection =
            NewsContract.NewsEntry.TABLE_NAME+
                    "." + NewsContract.NewsEntry.COLUMN_TITLE + " = ? ";

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = NewsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, NewsContract.PATH_NEWS, NEWS);
        matcher.addURI(authority, NewsContract.PATH_NEWS + "/#", NEWS_WITH_ID);
        matcher.addURI(authority, NewsContract.PATH_NEWS + "/*", NEWS_WITH_TITLE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new NewsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case NEWS: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        NewsContract.NewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case NEWS_WITH_ID: {
                retCursor = sNewsQueryBuilder.query(mDbHelper.getReadableDatabase(),
                        projection,
                        sIdSelection,
                        new String[]{NewsContract.NewsEntry.getIdFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case NEWS_WITH_TITLE: {
                retCursor = sNewsQueryBuilder.query(mDbHelper.getReadableDatabase(),
                        projection,
                        sTitleSelection,
                        new String[]{NewsContract.NewsEntry.getTitleFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case NEWS:
                return NewsContract.NewsEntry.CONTENT_TYPE;
            case NEWS_WITH_TITLE:
            case NEWS_WITH_ID:
                return NewsContract.NewsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case NEWS: {
                long _id = db.insert(NewsContract.NewsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = NewsContract.NewsEntry.buildNewsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case NEWS:
                rowsDeleted = db.delete(
                        NewsContract.NewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case NEWS:
                rowsUpdated = db.update(NewsContract.NewsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(NewsContract.NewsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mDbHelper.close();
        super.shutdown();
    }
}
