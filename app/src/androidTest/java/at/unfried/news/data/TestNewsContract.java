/*
 * Copyright (C) 2015 Jürgen Unfried
 *
 * This file is part of the RSS Prefetch Reader app that was developed as final
 * exam for the "Developing Android Apps" course (https://www.udacity.com/course/ud853)
 *
 * This app is not allowed to be published!
 */
package at.unfried.news.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by juergen on 17.03.2015.
 */
public class TestNewsContract extends AndroidTestCase {
    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_NEWS_TITLE = "Coulthard warnt: \"Ein Rennen darf kein Maßstab sein\"";
    private static final String TEST_NEWS_CATEGORY = "News";

    public void testBuildNewsWithTitle() {
        Uri newsUri = NewsContract.NewsEntry.buildNewsUriWithTitle(TEST_NEWS_TITLE);
        assertEquals("Error: News Uri doesn't match the expected result",
                newsUri.toString(),
                "content://at.unfried.news/news/Coulthard%20warnt%3A%20%22Ein%20Rennen%20darf%20kein%20Ma%C3%9Fstab%20sein%22");
    }

    public void testBuildNewsWithCategory() {
        Uri newsUri = NewsContract.NewsEntry.buildNewsUriWithCategory(TEST_NEWS_CATEGORY);
        assertEquals("Error: News Uri doesn't match the expected result",
                newsUri.toString(),
                "content://at.unfried.news/news?category=News");
    }
}
