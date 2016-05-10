package com.example.android.sunshine.app;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.sunshine.app.data.WeatherContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;

/**
 * Created by Andrew on 5/9/2016.
 */
public class WatchUpdater extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private GoogleApiClient mGoogleApiClient;
    private String TAG="WatchUpdater IntentService";

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
    };

    public WatchUpdater() {
        super("WatchUpdater");
        Log.e(TAG,"constructor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"onHandleIntent");
        mGoogleApiClient=new GoogleApiClient.Builder(this)
//                .enableAutoManage(null,this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        sendWeatherToWear();
    }

    public void sendWeatherToWear(){

        String location = Utility.getPreferredLocation(this);
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                location, System.currentTimeMillis());
        Cursor data = getContentResolver().query(weatherForLocationUri, FORECAST_COLUMNS, null,
                null, WeatherContract.WeatherEntry.COLUMN_DATE + " ASC");

        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        // Extract the weather data from the Cursor
        int weatherId = data.getInt(0);
        int weatherArtResourceId = Utility.getArtResourceForWeatherCondition(weatherId);
        String description = data.getString(1);
        double maxTemp = data.getDouble(2);
        double minTemp = data.getDouble(3);
        String formattedMaxTemperature = Utility.formatTemperature(this, maxTemp);
        String formattedMinTemperature = Utility.formatTemperature(this, minTemp);
        data.close();

        Log.e(TAG,"cursor: "+weatherArtResourceId+", "+maxTemp+", "+minTemp+", "+description+", "+formattedMaxTemperature+", "+formattedMinTemperature);

        PutDataMapRequest putDataMapRequest=PutDataMapRequest.create("/data");

        putDataMapRequest.getDataMap().putString("high_temp",formattedMaxTemperature);
        putDataMapRequest.getDataMap().putString("low_temp", formattedMinTemperature);

        Bitmap bitmap;
        bitmap = BitmapFactory.decodeResource(getResources(), weatherArtResourceId);

        Asset asset = createAssetFromBitmap(bitmap);
        putDataMapRequest.getDataMap().putAsset("image", asset);

        PendingResult<DataApi.DataItemResult> pendingResult= Wearable.DataApi.putDataItem(mGoogleApiClient,putDataMapRequest.asPutDataRequest());
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG,"onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG,"onConnection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG,"onConnection Failed");
    }
}
