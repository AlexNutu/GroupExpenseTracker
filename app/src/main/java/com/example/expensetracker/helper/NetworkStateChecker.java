package com.example.expensetracker.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.example.expensetracker.domain.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;


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
                   User response = null;
                   try {
                       response = new GetUserReqTask().execute(userId).get();
                   } catch (ExecutionException e) {
                       e.printStackTrace();
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   if(db.getLastSyncDB(userId) == null)
                        db.addOrUpdateSyncDB(userId);
                   if(response.getId() != null) {
                       mySqlSynchronizer.synchronizeMySQL();
                       sqLiteSynchronizer.synchronizeSQLite();
                       // db.addOrUpdateSyncDB(userId);
                   }
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

    private class GetUserReqTask extends AsyncTask<Long, Void, User> {

        @Override
        protected User doInBackground(Long... params) {

            Long idUser = params[0];

            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/user/" + idUser;
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                        = new HttpComponentsClientHttpRequestFactory();
                clientHttpRequestFactory.setConnectTimeout(1000);
                RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<User> currentUser = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, User.class);
                return currentUser.getBody();
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().value() == 403) {

                }
                Log.e("ERROR-GET-User", e.getMessage());
            }
            catch (Exception e){
                Log.e("ERROR-GET-User", e.getMessage());
            }
            return new User();
        }

        @Override
        protected void onPostExecute(User u) {
        }
    }



}