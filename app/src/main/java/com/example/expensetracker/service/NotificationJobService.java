package com.example.expensetracker.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.expensetracker.R;
import com.example.expensetracker.SettingsActivity;
import com.example.expensetracker.ViewTripActivity;
import com.example.expensetracker.domain.NotificationDB;
import com.example.expensetracker.domain.User;
import com.example.expensetracker.helper.NetworkStateChecker;
import com.example.expensetracker.helper.Session;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;

import static com.example.expensetracker.LoginActivity.CHANNEL_1_ID;

public class NotificationJobService extends JobService {

    private static final String TAG = "NotificationJobService";

    private Long currentUserId;
    private User currentUserObject;
    private boolean jobCanceled = false;
    private NotificationManagerCompat notificationManager;

    private Session session;


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job Started");
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                session = new Session(getApplicationContext());

                for (int i = 0; i < Integer.MAX_VALUE; i++) {

                    Log.d(TAG, "running:  " + i);
                    currentUserId = params.getExtras().getLong("currentUserId");
                    try {
                        currentUserObject = new GetUserReqTask().execute(currentUserId).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if(currentUserObject != null){
                        String msg = currentUserObject.getErrorMessage();
                        if(msg!=null)
                            if(msg.equals("FAILURE"))
                            return;
                    }
                    if (currentUserObject != null && NetworkStateChecker.isConnected(getApplicationContext())) {

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                if (currentUserObject.getReceiveNotifications()) {

                                    if (notificationManager == null) {
                                        notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                    }

                                    NotificationDB[] notificationDBList = {};
                                    try {
                                        notificationDBList = new GetNotificationsForCurrentUserReqTask().execute().get();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    for (int i = 0; i < notificationDBList.length; i++) {
                                        try {
                                            // Send notifications at 2 secondds distance
                                            Thread.sleep(3000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        sendNotificationCh1(notificationDBList[i].getMessage());
                                    }
                                }
                            }
                        });
                    }

                    if (jobCanceled) {
                        return;
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job canceled before completion");
        jobCanceled = true;
        return true;
    }

    public void sendNotificationCh1(String message) {
        String title = "Group Expense Tracker";

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_notifications_green)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }

    private class GetNotificationsForCurrentUserReqTask extends AsyncTask<Void, Void, NotificationDB[]> {

        @Override
        protected NotificationDB[] doInBackground(Void... params) {

            NotificationDB[] notificationsFromDB = {};
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/notification?search=user:"
                        + currentUserId + ",sent:false";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<NotificationDB[]> responseEntity = restTemplate.getForEntity(apiUrl, NotificationDB[].class);
                notificationsFromDB = responseEntity.getBody();

            } catch (Exception e) {
                Log.e("ERROR-GET-NOTIFICATIONS", e.getMessage());
            }

            return notificationsFromDB;
        }

        @Override
        protected void onPostExecute(NotificationDB[] notificationDBS) {
        }
    }

    private class GetUserReqTask extends AsyncTask<Long, Void, User> {

        @Override
        protected User doInBackground(Long... params) {

            Long idUser = params[0];
            final int MAX_RETRY=3;
            int iLoop;
            User user = null;

            for (iLoop=0; iLoop<MAX_RETRY; iLoop++) {
                try {
                    String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/user/" + idUser;
                    HttpHeaders requestHeaders = new HttpHeaders();
                    requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                    HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                            = new HttpComponentsClientHttpRequestFactory();
                     clientHttpRequestFactory.setConnectTimeout(1000);
                    RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    ResponseEntity<User> currentUser = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, User.class);
                    iLoop = 0;
                    return currentUser.getBody();
                } catch (HttpClientErrorException e) {
                    if (e.getStatusCode().value() == 403) {

                    }
                    Log.e("ERROR-GET-User", e.getMessage());
                } catch (Exception e) {
                    Log.e("ERROR-GET-User", e.getMessage());
                    user = new User();
                    user.setErrorMessage("FAILURE");
                }
            }

                return user;
        }

        @Override
        protected void onPostExecute(User u) {
        }
    }

}
