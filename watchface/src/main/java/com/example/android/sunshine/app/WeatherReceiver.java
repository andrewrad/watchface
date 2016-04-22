package com.example.android.sunshine.app;

import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Andrew on 4/20/2016.
 */
public class WeatherReceiver extends WearableListenerService {
    private String TAG="WeatherReceiver";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"onCreate");
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.e(TAG, "onDataChanged");
        for(DataEvent dataEvent:dataEvents){
            if(dataEvent.getType()==DataEvent.TYPE_CHANGED){
                DataMap dataMap= DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                String path=dataEvent.getDataItem().getUri().getPath();
                if(path.equals("/data")){
                    int highTemp = dataMap.getInt("high-temp");
                    Log.e(TAG,"received: "+highTemp);
                }
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.e(TAG,"onMessageReceived");
        super.onMessageReceived(messageEvent);
    }
}
