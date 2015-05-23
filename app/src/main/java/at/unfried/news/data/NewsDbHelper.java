/*
 * Copyright (C) 2015 Jürgen Unfried
 *
 * This file is part of the RSS Prefetch Reader app that was developed as final
 * exam for the "Developing Android Apps" course (https://www.udacity.com/course/ud853)
 *
 * This app is not allowed to be published!
 */
package at.unfried.news.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for News data.
 */
public class NewsDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    static final String DATABASE_NAME = "news.db";

    // these indices must match the projection
    public static final int COLINDEX_ID = 0;
    public static final int COLINDEX_TITLE = 1;
    public static final int COLINDEX_LINK = 2;
    public static final int COLINDEX_DESCRIPTION = 3;
    public static final int COLINDEX_CATEGORY = 4;
    public static final int COLINDEX_PUBDATE = 5;
    public static final int COLINDEX_THUMBNAIL = 6;
    public static final int COLINDEX_FULLTEXT = 7;

    /**
     * Creates a new News DB Helper
     *
     * @param context the app context.
     */
    public NewsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold news items.
        final String SQL_CREATE_NEWS_TABLE = "CREATE TABLE " + NewsContract.NewsEntry.TABLE_NAME + " (" +
                NewsContract.NewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                NewsContract.NewsEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                NewsContract.NewsEntry.COLUMN_LINK + " TEXT NOT NULL, " +
                NewsContract.NewsEntry.COLUMN_DESCRIPTION + " TEXT, " +
                NewsContract.NewsEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                NewsContract.NewsEntry.COLUMN_PUBDATE + " LONG NOT NULL, " +
                NewsContract.NewsEntry.COLUMN_THUMBNAIL + " TEXT NOT NULL, " +
                NewsContract.NewsEntry.COLUMN_FULLTEXT + " TEXT, " +
                " UNIQUE (" + NewsContract.NewsEntry.COLUMN_TITLE + ") ON CONFLICT IGNORE);";

        sqLiteDatabase.execSQL(SQL_CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NewsContract.NewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
