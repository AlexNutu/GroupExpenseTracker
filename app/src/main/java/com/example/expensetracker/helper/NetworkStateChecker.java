package com.example.expensetracker.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.expensetracker.domain.User;


public class NetworkStateChecker extends BroadcastReceiver {

    private final ConnectivityListener mConnectivityListener;

    public NetworkStateChecker(ConnectivityListener connectivityListener){
        mConnectivityListener = connectivityListener;
    }

    //context and database helper object
    private SQLiteSynchronizer sqLiteSynchronizer;
    private MySqlSynchronizer mySqlSynchronizer;
    private DatabaseHelper db;
    private Context context;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        this.db =  DatabaseHelper.getInstance(context);

        this.sqLiteSynchronizer = new SQLiteSynchronizer(context, db);
        this.mySqlSynchronizer = new MySqlSynchronizer(context, db);



        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        User loggedUser = db.getLoggedUser();
        Long userId = loggedUser.getId();
        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
               if(userId != null){
                    if(db.getLastSyncDB(userId) == null)
                        db.addOrUpdateSyncDB(userId);
                    mySqlSynchronizer.synchronizeMySQL();
                    sqLiteSynchronizer.synchronizeSQLite();
                   // db.addOrUpdateSyncDB(userId);
                   }

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