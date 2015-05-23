/*
 * Copyright (C) 2015 Jürgen Unfried
 *
 * This file is part of the RSS Prefetch Reader app that was developed as final
 * exam for the "Developing Android Apps" course (https://www.udacity.com/course/ud853)
 *
 * This app is not allowed to be published!
 */
package at.unfried.news;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * {@link NewsAdapter} exposes a list of news entries
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}
 *
 * Created by juergen on 20.03.2015.
 */
public class NewsAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_TOPNEWS = 0;
    private static final int VIEW_TYPE_NEWS = 1;

    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();
    /**
     * Viewholder for caching.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView titleView;
        public final TextView descriptionView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            descriptionView = (TextView) view.findViewById(R.id.list_item_description);
            titleView = (TextView) view.findViewById(R.id.list_item_title);
        }
    }
    /**
     * Creates a new News Adapter.
     * @param context the context
     * @param c the cursor
     * @param flags flags
     */
    public NewsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());

        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_TOPNEWS:
                layoutId = R.layout.list_item_topnews;
                break;
            case VIEW_TYPE_NEWS:
                layoutId = R.layout.list_item_news;
                break;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String imageUrl = cursor.getString(NewsListFragment.COL_THUMB);
        if (imageUrl.isEmpty()) {
            viewHolder.iconView.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.with(context).load(imageUrl).placeholder(R.mipmap.ic_launcher).into(viewHolder.iconView);
        }

        // Read data from cursor
        String description = cursor.getString(NewsListFragment.COL_DESC);
        viewHolder.descriptionView.setText(description);

        String title = cursor.getString(NewsListFragment.COL_TITLE);
        viewHolder.titleView.setText(title);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TOPNEWS : VIEW_TYPE_NEWS;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}
