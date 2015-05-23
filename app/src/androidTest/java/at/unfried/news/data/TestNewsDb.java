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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Tests if the database and the tables where correctly setup.
 */
public class TestNewsDb extends AndroidTestCase {
    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(NewsDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(NewsContract.NewsEntry.TABLE_NAME);

        mContext.deleteDatabase(NewsDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new NewsDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain the news table
        assertTrue("Error: Your database was created without the news table",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + NewsContract.NewsEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> newsColumnHashSet = new HashSet<>();
        newsColumnHashSet.add(NewsContract.NewsEntry._ID);
        newsColumnHashSet.add(NewsContract.NewsEntry.COLUMN_TITLE);
        newsColumnHashSet.add(NewsContract.NewsEntry.COLUMN_LINK);
        newsColumnHashSet.add(NewsContract.NewsEntry.COLUMN_DESCRIPTION);
        newsColumnHashSet.add(NewsContract.NewsEntry.COLUMN_CATEGORY);
        newsColumnHashSet.add(NewsContract.NewsEntry.COLUMN_PUBDATE);
        newsColumnHashSet.add(NewsContract.NewsEntry.COLUMN_THUMBNAIL);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            newsColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required news
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required news entry columns",
                newsColumnHashSet.isEmpty());
        c.close();
        db.close();
    }

    public void testNewsTable() {
        insertNews();
    }

    public long insertNews() {
        NewsDbHelper dbHelper= new NewsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createNewsValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long locationRowId;
        locationRowId = db.insert(NewsContract.NewsEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                NewsContract.NewsEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: News Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();

        return locationRowId;
    }
}
