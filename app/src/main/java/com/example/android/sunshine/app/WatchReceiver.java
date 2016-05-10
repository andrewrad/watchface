package com.example.android.sunshine.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.android.sunshine.app.sync.SunshineSyncAdapter;

/**
 * Created by Andrew on 5/9/2016.
 */
public class WatchReceiver extends BroadcastReceiver {
    private String TAG="WatchReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG,"on Receive");
        context.startService(new Intent(context, WatchUpdater.class));
    }
}
