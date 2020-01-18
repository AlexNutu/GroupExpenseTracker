package com.example.expensetracker.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setCookie(String cookie) {
        prefs.edit().putString("cookie", cookie).commit();
    }

    public String getCookie() {
        String cookie = prefs.getString("cookie","");
        return cookie;
    }
}