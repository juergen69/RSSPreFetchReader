/*
 * Copyright (C) 2015 Jürgen Unfried
 *
 * This file is part of the RSS Prefetch Reader app that was developed as final
 * exam for the "Developing Android Apps" course (https://www.udacity.com/course/ud853)
 *
 * This app is not allowed to be published!
 */
package at.unfried.news.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

/*
    Note: This is not a complete set of tests of the News ContentProvider, but it does test
    that at least the basic functionality has been implemented correctly.
 */
public class TestNewsProvider extends AndroidTestCase {

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                NewsContract.NewsEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                NewsContract.NewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from News table during delete", 0, cursor.getCount());

        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /**
     *   This test checks to make sure that the content provider is registered correctly.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // NewsProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                NewsProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: NewsProvider registered with authority: " + providerInfo.authority +
                    " instead of authority: " + NewsContract.CONTENT_AUTHORITY,
                    providerInfo.authority, NewsContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: NewsProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        // content://at.unfried.news/news/
        String type = mContext.getContentResolver().getType(NewsContract.NewsEntry.CONTENT_URI);
        // vnd.android.cursor.dir/at.unfried.news/news
        assertEquals("Error: the NewsEntry CONTENT_URI should return NewsEntry.CONTENT_TYPE",
                NewsContract.NewsEntry.CONTENT_TYPE, type);

        String testTitle = "Testtitle";
        // content://at.unfried.news/news/Testtitle
        type = mContext.getContentResolver().getType(
                NewsContract.NewsEntry.buildNewsUriWithTitle(testTitle));
        // vnd.android.cursor.dir/at.unfried.news/news/Testtitle
        assertEquals("Error: the NewsEntry CONTENT_URI with news should return NewsEntry.CONTENT_TYPE",
                NewsContract.NewsEntry.CONTENT_ITEM_TYPE, type);

        long id = 5;
        type = mContext.getContentResolver().getType(NewsContract.NewsEntry.buildNewsUri(id));
        assertEquals("Error: the NewsEntry CONTENT_URI with news should return NewsEntry.CONTENT_TYPE", NewsContract.NewsEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testBasicQuery() {
        // insert our test records into the database
        NewsDbHelper dbHelper = new NewsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createNewsValues();
        long newRowId = TestUtilities.insertNewsValues(mContext);

        ContentValues newsValues = TestUtilities.createNewsValues();
        long newsRowId = db.insert(NewsContract.NewsEntry.TABLE_NAME, null, newsValues);
        assertTrue("Unable to Insert NewsEntry into the Database", newsRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor newsCursor = mContext.getContentResolver().query(
                NewsContract.NewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicNewsQuery", newsCursor, newsValues);
    }

    public void testUpdateNews() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createNewsValues();

        Uri newsUri = mContext.getContentResolver().
                insert(NewsContract.NewsEntry.CONTENT_URI, values);
        long newsRowId = ContentUris.parseId(newsUri);

        // Verify we got a row back.
        assertTrue(newsRowId != -1);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(NewsContract.NewsEntry._ID, newsRowId);
        updatedValues.put(NewsContract.NewsEntry.COLUMN_TITLE, "Coulthard warnt!");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor newsCursor = mContext.getContentResolver().query(NewsContract.NewsEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        newsCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                NewsContract.NewsEntry.CONTENT_URI, updatedValues, NewsContract.NewsEntry._ID + "= ?",
                new String[] { Long.toString(newsRowId)});
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        newsCursor.unregisterContentObserver(tco);
        newsCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                NewsContract.NewsEntry.CONTENT_URI,
                null,   // projection
                NewsContract.NewsEntry._ID + " = " + newsRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateNews.  Error validating news entry update.",
                cursor, updatedValues);

        cursor.close();
    }


    // Make sure we can still delete after adding/updating stuff
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createNewsValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(NewsContract.NewsEntry.CONTENT_URI, true, tco);
        Uri newsUri = mContext.getContentResolver().insert(NewsContract.NewsEntry.CONTENT_URI, testValues);

        // Did our content observer get called?
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long newsRowId = ContentUris.parseId(newsUri);

        // Verify we got a row back.
        assertTrue(newsRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                NewsContract.NewsEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating NewsEntry.",
                cursor, testValues);

    }

    // Make sure we can still delete after adding/updating stuff
    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our news delete.
        TestUtilities.TestContentObserver newsObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(NewsContract.NewsEntry.CONTENT_URI, true, newsObserver);
        deleteAllRecordsFromProvider();

        newsObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(newsObserver);
    }
}
