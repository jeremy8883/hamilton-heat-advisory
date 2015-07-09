package net.jeremycasey.hamiltonheatalert.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatusIsImportantChecker;

import java.util.UUID;

public class PreferenceUtil {
    public static boolean getBoolean(Context context, String key, boolean defaultVal) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, defaultVal);
    }

    public static <T> T getObject(Context context, String key, T defaultVal, Class<T> classOfT) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String unlikelyString = UUID.randomUUID().toString();
        String str = sharedPreferences.getString(key, unlikelyString);
        if (unlikelyString.equals(str)) {
            return defaultVal;
        }
        Gson gson = new Gson();
        return gson.fromJson(str, classOfT);
    }

    public static void put(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static void put(Context context, String key, Object value) {
        Gson gson = new Gson();
        String json = gson.toJson(value);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(key, json).apply();
    }
}
