package com.cefy.cefy;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Anurag
 */
public class Utils {
    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constants.SharedPrefs.MY_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }
    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(Constants.SharedPrefs.MY_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }
}
