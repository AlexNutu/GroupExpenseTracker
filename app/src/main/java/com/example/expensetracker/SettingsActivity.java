package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.expensetracker.domain.Trip;
import com.example.expensetracker.domain.User;
import com.example.expensetracker.helper.DatabaseHelper;
import com.example.expensetracker.helper.NetworkStateChecker;
import com.example.expensetracker.helper.Session;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;

public class SettingsActivity extends AppCompatActivity {

    private User currentUser;

    private TextView userFullNameTV;
    private TextView userEmailTV;
    private Switch notificationsSwitch;
    private TextView tvSettingsSignOut;

    private Session session;
    private DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new Session(getApplicationContext());
        db = DatabaseHelper.getInstance(this);

        Intent receivedIntent = getIntent();
        if(NetworkStateChecker.isConnected(this)) {
            if (receivedIntent != null) {
                User oldCurrentUser = (User) receivedIntent.getSerializableExtra("currentUser");
                try {
                    currentUser = new GetUserReqTask().execute(oldCurrentUser.getId()).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }else
            currentUser = db.getLoggedUser();

        userEmailTV = findViewById(R.id.tvSettingsEmail);
        userFullNameTV = findViewById(R.id.tvSettingsFullName);
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        tvSettingsSignOut = findViewById(R.id.tvSettingsSignOut);

        attachData();
    }

    private void attachData() {
        userEmailTV.setText(currentUser.getEmail());
        userFullNameTV.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        notificationsSwitch.setChecked(currentUser.getReceiveNotifications());
        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(NetworkStateChecker.isConnected(getApplicationContext())) {
                    try {
                        new UpdateUserReqTask().execute(isChecked).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    currentUser.setReceiveNotifications(isChecked);
                    db.updateLoggedUser(currentUser);
                }
            }
        });
        tvSettingsSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new LogoutReqTask().execute(currentUser).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class UpdateUserReqTask extends AsyncTask<Boolean, Void, User> {

        @Override
        protected User doInBackground(Boolean... params) {

            Boolean isChecked = params[0];
            currentUser.setReceiveNotifications(isChecked);
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/user/" + currentUser.getId();
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(currentUser, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<User> userResponse = restTemplate.exchange(apiUrl, HttpMethod.PUT, requestEntity, User.class);
                return userResponse.getBody();
            } catch (Exception e) {
                if (((HttpClientErrorException) e).getStatusCode().value() == 403) {
                    Intent myIntent = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivity(myIntent);
                }
                Log.e("ERROR-UPDATE-USER", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(User updatedUser) {

        }
    }

    private class GetUserReqTask extends AsyncTask<Long, Void, User> {

        @Override
        protected User doInBackground(Long... params) {

            Long idUser = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/user/" + idUser;
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<User> currentUser = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, User.class);
                return currentUser.getBody();
            } catch (Exception e) {
                if (((HttpClientErrorException) e).getStatusCode().value() == 403) {

                }
                Log.e("ERROR-GET-User", e.getMessage());
            }
            return new User();
        }

        @Override
        protected void onPostExecute(User u) {
        }
    }

    private class LogoutReqTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... params) {

            User userToLogoutParam = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/logout";
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(userToLogoutParam, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, null);
                if (responseEntity.getStatusCode().value() == 200) {
                    session.setCookie(null);
                    Intent myIntent = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivity(myIntent);
                }
            } catch (Exception e) {
                if (((HttpClientErrorException) e).getStatusCode().value() == 403) {
                    Intent myIntent = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivity(myIntent);
                }
                Log.e("ERROR-LOGOUT", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
        }
    }

}
