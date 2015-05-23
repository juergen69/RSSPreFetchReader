/*
 * Copyright (C) 2015 Jürgen Unfried
 *
 * This file is part of the RSS Prefetch Reader app that was developed as final
 * exam for the "Developing Android Apps" course (https://www.udacity.com/course/ud853)
 *
 * This app is not allowed to be published!
 */
package at.unfried.news;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by juergen on 25.03.2015.
 */
public class NewsDetailActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_detail);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(NewsDetailFragment.DETAIL_URI, getIntent().getData());

            NewsDetailFragment fragment = new NewsDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.news_detail_container, fragment)
                    .commit();
        }
    }
}
