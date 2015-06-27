package net.jeremycasey.hamiltonheatalert.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.jeremycasey.hamiltonheatalert.gcm.GcmPreferences;

public class PreferenceUtil {
    public static boolean getBoolean(Context context, String key, boolean defaultVal) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, defaultVal);
    }

    public static void put(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }
}
