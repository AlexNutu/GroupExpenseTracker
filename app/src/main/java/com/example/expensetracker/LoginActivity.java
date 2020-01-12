package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.expensetracker.domain.Notification;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setActions();
    }

    private void setActions() {
        Button addTrip = (Button) findViewById(R.id.loginBtn);
        TextView signUp = (TextView) findViewById(R.id.goToRegisterTV);
        Button testRest = (Button) findViewById(R.id.testRestBtn);

        addTrip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(myIntent);
            }
        });

        testRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new HttpReqTask().execute();

                Intent intent = new Intent(LoginActivity.this, ViewReport.class);
                startActivity(intent);
            }
        });

    }

    private class HttpReqTask extends AsyncTask<Void, Void, Notification> {
        @Override
        protected Notification doInBackground(Void... voids) {

            try {
                String apiUrl = "http://10.0.2.2:8080/trackerApi/notification";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Notification not = restTemplate.getForObject(apiUrl, Notification.class);
                return not;
            } catch (Exception e) {
                Log.e("", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Notification notification) {
            super.onPostExecute(notification);

            Log.i("NOTIFICATION: ", "##########");
            Log.i("title: ", notification.getTitle());
            Log.i("message: ", notification.getMessage());
            Log.i("datetime: ", notification.getNotificationDate().toString());
        }
    }
}
