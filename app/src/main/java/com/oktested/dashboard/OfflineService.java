package com.oktested.dashboard;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OfflineService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        onTaskRemoved(intent);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Log.d("offlineService", "offlineService");
        if (intent != null && intent.getStringExtra("hit") != null) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://oktapi.scoopwhoop.com/user-manage/offline/yes")
                    .method("POST", body)
                    .addHeader("Authorization", "Bearer " + AppPreferences.getInstance(getApplicationContext()).getPreferencesString(AppConstants.ACCESS_TOKEN)).addHeader("Cookie", "__cfduid=dcb4bc5af3a5fcdf09943a7851dbb7c4f1605611985")
                    .build();
            try {
                okhttp3.Response response = client.newCall(request).execute();

                Log.d("offlineService", "offlineService" + response.message());
            } catch (IOException e) {
                Log.d("offlineService", "offlineService" + e.getMessage());
                e.printStackTrace();
            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("offlineService", "onTaskRemoved");
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        restartServiceIntent.putExtra("hit", "true");
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }
}