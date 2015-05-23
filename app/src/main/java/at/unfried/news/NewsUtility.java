/*
 * Copyright (C) 2015 JÃ¼rgen Unfried
 *
 * This file is part of the  News app that was developed as final
 * exam for the "Developing Android Apps" course (https://www.udacity.com/course/ud853)
 *
 * This app is not allowed to be published!
 */
package at.unfried.news;

import android.content.ContentValues;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import at.unfried.news.data.NewsContract;

/**
 * Created by juergen on 17.03.2015.
 */
public class NewsUtility {
    public static final String RSS_DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String NICE_DATE_PATTERN = "d. MMMM yyyy HH:mm:ss";

    /**
     * Simulates downloading an RSS Feed
     *
     * @return vector of content values
     */
    public static Vector<ContentValues> downloadRssFeed() {

        Vector<ContentValues> cVVector = new Vector<>(1);

        ContentValues Values = new ContentValues();

        Date date = new Date();

        Values.put(NewsContract.NewsEntry.COLUMN_TITLE, "Random title " + convertDateToNiceDate(date.getTime()));
        Values.put(NewsContract.NewsEntry.COLUMN_LINK, date.getTime());
        Values.put(NewsContract.NewsEntry.COLUMN_DESCRIPTION, "Lorem Ipsum description");
        Values.put(NewsContract.NewsEntry.COLUMN_CATEGORY, "News");
        Values.put(NewsContract.NewsEntry.COLUMN_PUBDATE, date.getTime());
        Values.put(NewsContract.NewsEntry.COLUMN_THUMBNAIL, "");

        cVVector.add(Values);

        return cVVector;
    }

    /**
     * Converts the RSS PubDate to a long value
     *
     * @param pubDate the publication date in the Format ("EEE, dd MMM yyyy HH:mm:ss zzz")
     * @return the time in ms
     */
    public static long convertPubDateToDBDate(String pubDate) {
        try {
            return new SimpleDateFormat(RSS_DATE_PATTERN, Locale.ENGLISH).parse(pubDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Converts a date in long format to a nice, readable date
     *
     * @param dateAsLong the date as long value
     * @return the date as nice readable date using NICE_DATE_PATTERN
     */
    public static String convertDateToNiceDate(long dateAsLong) {
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat(NICE_DATE_PATTERN);

        return shortenedDateFormat.format(dateAsLong);
    }

    /**
     * Simulates downloading an article and extracts the content.
     *
     * @param link the link to the article
     * @return the main article extracted from the website
     */
    public static String downloadFullArticle(String link) {
        StringBuffer sbArticle = new StringBuffer();

        sbArticle.append("Fulltext for Title ").append(link);
        sbArticle.append("\nLorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum efficitur nisi nec velit fermentum, at placerat dui consequat. Cras accumsan vel lectus pharetra consequat. Vivamus eu scelerisque dui. Aenean dignissim vitae diam eget egestas. Aenean a risus porttitor, fringilla lacus id, pretium sapien. Nulla eleifend eros ac enim vulputate finibus. Donec egestas lacinia magna, sit amet suscipit orci consectetur vel. Proin vehicula, sapien sed vestibulum mollis, mauris purus fermentum mi, convallis scelerisque sem felis vitae libero. Fusce elementum nunc non diam auctor, ac commodo justo commodo. Vestibulum nec bibendum lorem. Quisque laoreet malesuada tortor, sit amet ornare lorem eleifend sed. Praesent auctor ac purus a mattis. Donec consequat elit eu felis pellentesque, ac porttitor libero pharetra. Integer fermentum, urna et ullamcorper venenatis, elit lectus maximus erat, quis interdum sapien risus sit amet lectus. Sed leo odio, aliquam non enim ac, volutpat luctus nibh. Maecenas auctor urna ut ullamcorper hendrerit.");

        return sbArticle.toString();
    }

}