package limitless.android.unfollowtita.Other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SharePref {

    public static final String screenName = "screenName";
    public static final String userId = "userId";
    public static final String settingLoadBlocked = "sLoadBlocked";
    public static final String settingLoadMuted = "sLoadMuted";
    public static final String settingLoadHeader = "settingLoadHeader";
    public static String changeApiKey = "changeApiKey";
    public static String unfollowCount = "unfollowCount";
    public static String PRO_VERSION = "pro_version";

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public SharePref(Context context) {
        sp = context.getSharedPreferences(Constant.SharePrefName, Context.MODE_PRIVATE);
    }

    @SuppressLint("CommitPrefEdits")
    public void getEditor(){
        editor = sp.edit();
    }


    public String getString(String key, String def){
        return sp.getString(key, def);
    }

    public void put(String key, boolean b){
        getEditor();
        editor.putBoolean(key, b).apply();
    }

    public boolean get(String key, boolean def){
        return sp.getBoolean(key, def);
    }

    public long getLong(String key, long def) {
        return sp.getLong(key, def);
    }

    public void put(String key, int count) {
        getEditor();
        editor.putInt(key, count).apply();
    }

    public int getInt(String key, int def) {
        return sp.getInt(key, def);
    }

    public void clearAll() {
        getEditor();
        editor.clear().apply();
    }

    public String get(String key, String def) {
        return sp.getString(key, def);
    }

    public void put(String key, String value){
        getEditor();
        editor.putString(key, value).apply();
    }

    public long get(String key, long def){
        return sp.getLong(key, def);
    }

    public void put(String key, long value){
        getEditor();
        editor.putLong(key, value).apply();
    }

}
