/*
 * Copyright (C) 2015 Jürgen Unfried
 *
 * This file is part of the RSS Prefetch Reader app that was developed as final
 * exam for the "Developing Android Apps" course (https://www.udacity.com/course/ud853)
 *
 * This app is not allowed to be published!
 */
package at.unfried.news.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the News database.
 *
 * Created by juergen on 17.03.2015.
 */
public class NewsContract {
    public static final String CONTENT_AUTHORITY = "at.unfried.news";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    public static final String PATH_NEWS = "news";
    public static final String PATH_CATEGORY = "category";

    /* Inner class that defines the table contents of the News entries table */
    public static final class NewsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;

        // Table name
        public static final String TABLE_NAME = "news";

        // Table columns
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_PUBDATE = "pubDate";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_FULLTEXT = "fulltext";

        public static Uri buildNewsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildNewsUri() {
            return CONTENT_URI.buildUpon().build();
        }
        public static Uri buildNewsUriWithTitle(String title) {
            return CONTENT_URI.buildUpon().appendPath(title).build();
        }

        public static Uri buildNewsUriWithCategory(String category) {
            return CONTENT_URI.buildUpon().appendQueryParameter(PATH_CATEGORY, category).build();
        }

        public static String getTitleFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
