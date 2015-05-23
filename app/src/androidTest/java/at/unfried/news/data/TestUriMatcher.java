/*
 * Copyright (C) 2015 Jürgen Unfried
 *
 * This file is part of the RSS Prefetch Reader app that was developed as final
 * exam for the "Developing Android Apps" course (https://www.udacity.com/course/ud853)
 *
 * This app is not allowed to be published!
 */
package at.unfried.news.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

public class TestUriMatcher extends AndroidTestCase {
    private static final String NEWS_TITLE = "Testtitel";

    // content://at.unfried.news/news"
    private static final Uri TEST_NEWS_DIR = NewsContract.NewsEntry.CONTENT_URI;
    private static final Uri TEST_NEWS_WITH_TITLE = NewsContract.NewsEntry.buildNewsUriWithTitle(NEWS_TITLE);

    public void testUriMatcher() {
        UriMatcher testMatcher = NewsProvider.buildUriMatcher();

        Log.d("TestUriMatcher", TEST_NEWS_DIR.toString());
        Log.d("TestUriMatcher", TEST_NEWS_WITH_TITLE.toString());

        assertEquals("Error: The NEWS URI was matched incorrectly.",
                testMatcher.match(TEST_NEWS_DIR), NewsProvider.NEWS);
        assertEquals("Error: The NEWS URI WITH TITLE was matched incorrectly.",
                testMatcher.match(TEST_NEWS_WITH_TITLE), NewsProvider.NEWS_WITH_TITLE);
    }
}
