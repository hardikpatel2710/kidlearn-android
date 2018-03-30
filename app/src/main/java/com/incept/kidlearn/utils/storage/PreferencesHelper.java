package com.incept.kidlearn.utils.storage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.incept.kidlearn.FlavorSpecific;

import java.util.Map;

/**
 * Created by Hardik Patel on 16/02/18.
 */

public class PreferencesHelper {
    public static final int PREFS_TYPE_INTEGER = 0;
    public static final int PREFS_TYPE_LONG = 1;
    public static final int PREFS_TYPE_FLOAT = 2;
    public static final int PREFS_TYPE_BOOLEAN = 3;
    public static final int PREFS_TYPE_STRING = 4;
    private static final String CLASS_NAME = "PreferencesHelper: ";

    /**
     * Write data into Activity's default preferences file.
     *
     * @param activity - Activity's context.
     * @param data     - Map of key value pair.
     */
    public static void writeToPreferences(Activity activity, Map<String, Object> data) {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToPreferences().");

        if (data == null) {
            Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToPreferences() - Data not provided - " + data);
            return;
        }

        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Iterate over map and save each key value in shared preferences.
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();

            if (key == null) {
                continue;
            }

            Object value = entry.getValue();

            // Check type of value and accordingly save in shared preferences.
            if (value instanceof Integer) {
                editor.putInt(key, Integer.parseInt(value.toString()));
            } else if (value instanceof Long) {
                editor.putLong(key, Long.parseLong(value.toString()));
            } else if (value instanceof Float) {
                editor.putFloat(key, Float.parseFloat(value.toString()));
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, Boolean.parseBoolean(value.toString()));
            } else if (value instanceof String) {
                editor.putString(key, value.toString());
            } else {
                Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToPreferences() - Invalid datatype - " + value);
            }
        }

        editor.commit();
    }

    /**
     * Write data into specific share preferences file provided by name.
     *
     * @param context  - Application context.
     * @param fileName - Shared preferences file name.
     * @param data     - Map of key value pair.
     */
    public static void writeToSharedPreferences(Context context, String fileName, Map<String, Object> data) {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToSharedPreferences().");

        if (fileName == null) {
            Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToSharedPreferences() - File name not provided - " + fileName);
            return;
        }

        if (data == null) {
            Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToSharedPreferences() - Data not provided - " + data);
            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Iterate over map and save each key value in shared preferences.
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();

            if (key == null) {
                continue;
            }

            Object value = entry.getValue();

            // Check type of value and accordingly save in shared preferences.
            if (value instanceof Integer) {
                editor.putInt(key, Integer.parseInt(value.toString()));
            } else if (value instanceof Long) {
                editor.putLong(key, Long.parseLong(value.toString()));
            } else if (value instanceof Float) {
                editor.putFloat(key, Float.parseFloat(value.toString()));
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, Boolean.parseBoolean(value.toString()));
            } else if (value instanceof String) {
                editor.putString(key, value.toString());
            } else {
                Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToSharedPreferences() - Invalid datatype - " + value);
            }
        }

        editor.commit();
    }

    public static Object readFromPreferences(Activity activity, String key, int type) {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "readFromPreferences().");

        if (TextUtils.isEmpty(key)) {
            Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "readFromPreferences() - Key is empty - " + key);
            return null;
        }

        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);

        // Check for respective datatype and return value.
        switch (type) {
            case PREFS_TYPE_INTEGER:
                return sharedPreferences.getInt(key, -1);

            case PREFS_TYPE_LONG:
                return sharedPreferences.getLong(key, -1);

            case PREFS_TYPE_FLOAT:
                return sharedPreferences.getFloat(key, -1);

            case PREFS_TYPE_BOOLEAN:
                return sharedPreferences.getBoolean(key, false);

            case PREFS_TYPE_STRING:
                return sharedPreferences.getString(key, null);
        }

        return null;
    }

    public static Object readFromSharedPreferences(Context context, String fileName, String key, int type) {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "readFromSharedPreferences().");

        if (TextUtils.isEmpty(fileName)) {
            Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "readFromSharedPreferences() - Filename not provided - " + fileName);
            return null;
        }

        if (TextUtils.isEmpty(key)) {
            Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "readFromSharedPreferences() - Key not provided - " + key);
            return null;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);

        // Check for respective datatype and return value.
        switch (type) {
            case PREFS_TYPE_INTEGER:
                return sharedPreferences.getInt(key, -1);

            case PREFS_TYPE_LONG:
                return sharedPreferences.getLong(key, -1);

            case PREFS_TYPE_FLOAT:
                return sharedPreferences.getFloat(key, -1);

            case PREFS_TYPE_BOOLEAN:
                return sharedPreferences.getBoolean(key, false);

            case PREFS_TYPE_STRING:
                return sharedPreferences.getString(key, null);
        }

        return null;
    }
}
