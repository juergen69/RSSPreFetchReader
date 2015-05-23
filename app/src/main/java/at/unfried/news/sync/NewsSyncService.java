/*
 * Copyright (C) 2015 Jürgen Unfried
 *
 * This file is part of the RSS Prefetch Reader app that was developed as final
 * exam for the "Developing Android Apps" course (https://www.udacity.com/course/ud853)
 *
 * This app is not allowed to be published!
 */
package at.unfried.news.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NewsSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static NewsSyncAdapter sNewsSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sNewsSyncAdapter == null) {
                sNewsSyncAdapter = new NewsSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sNewsSyncAdapter.getSyncAdapterBinder();
    }
}