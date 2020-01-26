package com.example.expensetracker;

import android.app.Application;

import com.example.expensetracker.helper.ConnectivityReceiver;

public class GroupExpenseTracker extends Application {

    private static GroupExpenseTracker mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized GroupExpenseTracker getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
