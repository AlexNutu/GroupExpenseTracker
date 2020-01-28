package com.example.expensetracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.domain.NotificationDB;
import com.example.expensetracker.domain.Trip;
import com.example.expensetracker.domain.User;
import com.example.expensetracker.helper.DatabaseHelper;
import com.example.expensetracker.helper.NetworkStateChecker;
import com.example.expensetracker.helper.Session;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class LoginActivity extends AppCompatActivity {

    public static final String CHANNEL_1_ID = "channel1";
    private Session session;//global variable
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new Session(getApplicationContext());
        db = DatabaseHelper.getInstance(this);

        Intent intent = getIntent();
        String fromActivity = intent.getStringExtra("fromActivity");
        if (fromActivity != null && fromActivity.equals("RegisterActivity")) {
            Toast.makeText(LoginActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
        }

        createNotificationChannels();

        if(NetworkStateChecker.isConnected(this)){
            setActions();
        }
        else{
            User loggedUser = db.getLoggedUser();
            if(loggedUser.getId()!=null) {
                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                myIntent.putExtra("currentUserObject", loggedUser);
                myIntent.putExtra("fromActivity", "LoginActivity");
                startActivity(myIntent);
                finish();
            }else {
                setActions();
                Toast.makeText(LoginActivity.this, "You have been logged out!", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void setActions() {
        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        TextView goToRegisterTV = (TextView) findViewById(R.id.goToRegisterTV);
        final EditText emailET = (EditText) findViewById(R.id.emailLoginET);
        final EditText passET = (EditText) findViewById(R.id.passLoginET);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (emailET.getText().toString().trim().equals("")
                        && passET.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "Email and password are empty!", Toast.LENGTH_SHORT).show();

                } else if (emailET.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "Password is empty!", Toast.LENGTH_SHORT).show();

                } else if (passET.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "Password is empty!", Toast.LENGTH_SHORT).show();

                } else {
                    User resultedUser = new User();
                    User userToLogIn = new User();
                    userToLogIn.setEmail(emailET.getText().toString());
                    userToLogIn.setPassword(passET.getText().toString());

                    try {
                        resultedUser = new LoginUserReqTask().execute(userToLogIn).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (resultedUser.getErrorMessage() == null || resultedUser.getErrorMessage().equals("")) {
                        db.addLoggedUser(resultedUser);
                        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                        myIntent.putExtra("currentUserObject", resultedUser);
                        myIntent.putExtra("fromActivity", "LoginActivity");
                        startActivity(myIntent);
                        finish();
                    } else {
                        // Display error from BE
                        Toast toast = Toast.makeText(LoginActivity.this, resultedUser.getErrorMessage(), Toast.LENGTH_SHORT);
                        TextView tv = (TextView) toast.getView().findViewById(android.R.id.message);
                        tv.setTextColor(Color.RED);
                        toast.show();
                    }
                }


            }
        });
        goToRegisterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(myIntent);
            }
        });


    }

    private class LoginUserReqTask extends AsyncTask<User, Void, User> {

        @Override
        protected User doInBackground(User... params) {

            User userToLoginParam = params[0];
            User resultedUser = new User();
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/login";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<User> userObjectResult = restTemplate.postForEntity(apiUrl, userToLoginParam, User.class);
                resultedUser = userObjectResult.getBody();
                session.setCookie(userObjectResult.getHeaders().get("Set-Cookie").get(0).split("=")[1].split(";")[0]);
                return resultedUser;

            } catch (Exception e) {
                Log.e("ERROR-LOGIN", e.getMessage());
                resultedUser.setErrorMessage(((HttpClientErrorException) e).getResponseBodyAsString() + "!");
                return resultedUser;
            }
        }

        @Override
        protected void onPostExecute(User resultedUser) {
        }
    }

    // NotificationDB code
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");


            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }
}
