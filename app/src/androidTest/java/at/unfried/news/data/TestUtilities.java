/*
 * Copyright (C) 2015 Jürgen Unfried
 *
 * This file is part of the RSS Prefetch Reader app that was developed as final
 * exam for the "Developing Android Apps" course (https://www.udacity.com/course/ud853)
 *
 * This app is not allowed to be published!
 */
package at.unfried.news.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import at.unfried.news.NewsUtility;
import at.unfried.news.utils.PollingCheck;

public class TestUtilities extends AndroidTestCase {
    private static final String LOG_TAG = TestUtilities.class.getSimpleName();

    public static final String PUB_DATE = "Tue, 17 Mar 2015 18:33:36 GMT";
    public static final String NICE_DATE = "17. März 2015 19:33";
    public static final long DATE_AS_LONG = 1426617216000L;

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createNewsValues() {
        ContentValues newsValues = new ContentValues();
        newsValues.put(NewsContract.NewsEntry.COLUMN_TITLE, "Coulthard warnt: \"Ein Rennen darf kein Maßstab sein\"");
        newsValues.put(NewsContract.NewsEntry.COLUMN_LINK, "http://www.your-rss-resource-here.com/news/2015/03/coulthard-warnt-ein-rennen-darf-kein-massstab-sein-15031717.html");
        newsValues.put(NewsContract.NewsEntry.COLUMN_DESCRIPTION, "Ex-Pilot David Coulthard warnt davor, die neue Saison auf Basis eines Grand Prix schlechtzureden, auch für die Zukunft der Formel 1 sieht er positive Signale");
        newsValues.put(NewsContract.NewsEntry.COLUMN_CATEGORY, "Formel 1");
        newsValues.put(NewsContract.NewsEntry.COLUMN_PUBDATE, PUB_DATE);
        newsValues.put(NewsContract.NewsEntry.COLUMN_THUMBNAIL, "http://www.www.your-rss-resource-here.com/news/images/136044.jpg\" height=\"200\" width=\"149\"");

        return newsValues;
    }

    public static long insertNewsValues(Context mContext) {
        // insert our test records into the database
        NewsDbHelper dbHelper = new NewsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createNewsValues();

        long newsRowId;
        newsRowId = db.insert(NewsContract.NewsEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert News Values", newsRowId != -1);

        return newsRowId;
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    public static void testPubDateToDBDateConverter() {
        assertEquals("Pubdate conversion failed.", DATE_AS_LONG, NewsUtility.convertPubDateToDBDate(PUB_DATE));
    }

    public static void testDateToNiceDateConverter() {
        assertEquals("Date to nice date failed.", NICE_DATE, NewsUtility.convertDateToNiceDate(DATE_AS_LONG));
    }

    public static void testFullArticleDownload() {
        NewsUtility.downloadFullArticle("http://www.your-rss-resource-here.com/news/2015/03/lewis-hamilton-gegen-mercedes-einbremsung-unfair-15032414.html");
    }
}
