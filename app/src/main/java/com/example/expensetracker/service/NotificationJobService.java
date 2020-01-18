package com.example.expensetracker.service;

import android.app.Notification;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.NotificationDB;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static com.example.expensetracker.LoginActivity.CHANNEL_1_ID;

public class NotificationJobService extends JobService {

    private static final String TAG = "NotificationJobService";

    private Long currentUserId;
    private boolean jobCanceled = false;
    private NotificationManagerCompat notificationManager;


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

                for (int i = 0; i < Integer.MAX_VALUE; i++) {

                    Log.d(TAG, "running:  " + i);
                    currentUserId = params.getExtras().getLong("currentUserId");

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

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
                    });
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

}
