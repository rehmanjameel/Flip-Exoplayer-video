package org.codebase.fliphorizontally.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.Calendar;

public class App extends Application {

    public static Context context;

    private static final String KEY_LOGGED_IN = "logged_in";
    public static final String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";
    public static final String SERVER = "http://192.168.1.9:8000";
    protected static final String API = String.format("%s/api/", SERVER);
    public static final String GET_DATA_API = String.format("%sgetdata/", API);
    public static final String GET_REGISTERED_USERS_API = String.format("%sregistergetdata/", API);

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

//        createNotificationChannnel();
    }

    public static Context getContext() {
        return context;
    }

    public static SharedPreferences getPreferenceManager() {
        return getContext().getSharedPreferences("shared_prefs", MODE_PRIVATE);
    }

    public static void saveString(String key, String value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getString(key, "");
    }

    public static void saveInt(String key, int value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public static int getInt(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getInt(key, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }

    public static void saveLogin(boolean value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean(KEY_LOGGED_IN, value).apply();
    }

    public static boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    public static void logout() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().clear().apply();
    }

    private void createNotificationChannnel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
