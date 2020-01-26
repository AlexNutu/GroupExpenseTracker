package com.example.expensetracker.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkStateChecker extends BroadcastReceiver {

    private final ConnectivityListener mConnectivityListener;

    public NetworkStateChecker(ConnectivityListener connectivityListener){
        mConnectivityListener = connectivityListener;
    }

    //context and database helper object
    private MySQLSynchronizer mySQLSynchronizer;
    private DatabaseHelper db;
    private Context context;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        this.mySQLSynchronizer = new MySQLSynchronizer(context, db);

        db =  DatabaseHelper.getInstance(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                if(db.getLastSyncDB(2) == null)
                    db.addOrUpdateSyncDB(2);
                mySQLSynchronizer.synchronizeUsers();
                mySQLSynchronizer.synchronizeTrips();
                mySQLSynchronizer.synchronizeNotes();
                mySQLSynchronizer.synchronizeDeleted();
                mySQLSynchronizer.synchronizeExpenses();
                mySQLSynchronizer.synchronizeReports();

            }
        }

        mConnectivityListener.onConnectivityStateChange();

    }

    public static boolean isConnected(Context context) {
        ConnectivityManager
                cm =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    public interface ConnectivityListener {

        /**
         * Called when a data connection has been established. Can use to
         * trigger any waiting behaviour
         */
        public void onConnectivityStateChange();

    }



}