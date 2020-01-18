package com.example.expensetracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.expensetracker.domain.Trip;
import com.example.expensetracker.domain.User;
import com.example.expensetracker.helper.Session;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class AddTripActivity extends AppCompatActivity {

    private Session session;

    private EditText etName;
    private EditText etDescription;
    private EditText etStartDate;
    private EditText etEndDate;
    private Button addTrip;

    private String tripNameString;
    private String tripDestinationString;
    private String tripStartDateString;
    private String tripEndDateString;

    private User currentUserObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tript);

        session = new Session(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null) {
            currentUserObject = (User) intent.getSerializableExtra("currentUserObject");
        }

        etName = (EditText) findViewById(R.id.tripName);
        etDescription = (EditText) findViewById(R.id.tripDestination);
        etStartDate = (EditText) findViewById(R.id.tripStartDate);
        etEndDate = (EditText) findViewById(R.id.tripEndDate);
        addTrip = (Button) findViewById(R.id.addBtn);

        // set listeners
        etName.addTextChangedListener(mTextWatcher);
        etDescription.addTextChangedListener(mTextWatcher);
        etStartDate.addTextChangedListener(mTextWatcher);
        etEndDate.addTextChangedListener(mTextWatcher);

        // run once to disable if empty
        checkFieldsForEmptyValues();
        addTripButtonPressed();
    }

    //  create a textWatcher member
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // check Fields For Empty Values
            checkFieldsForEmptyValues();
        }
    };

    void checkFieldsForEmptyValues() {
        Button b = (Button) findViewById(R.id.addBtn);

        tripNameString = etName.getText().toString().trim();
        tripDestinationString = etDescription.getText().toString().trim();
        tripStartDateString = etStartDate.getText().toString().trim();
        tripEndDateString = etEndDate.getText().toString().trim();

        if (tripNameString.equals("") || tripDestinationString.equals("") || tripStartDateString.equals("") || tripEndDateString.equals("")) {
            b.setEnabled(false);
        } else {
            b.setEnabled(true);
        }
    }

    private void addTripButtonPressed() {
        addTrip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Save the trip into DB
                List<User> membersList = new ArrayList<>();
                membersList.add(currentUserObject);
                Trip tripToInsert =
                        new Trip(null, tripNameString, tripDestinationString, tripStartDateString, tripEndDateString, null, null, membersList);
                Integer insertedTripId = -1;
                try {
                    insertedTripId = new AddTripReqTask().execute(tripToInsert).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent myIntent = new Intent(AddTripActivity.this, ViewTripActivity.class);
                myIntent.putExtra("tripId", insertedTripId);
                myIntent.putExtra("currentUserObject", currentUserObject);
                myIntent.putExtra("fromActivity", this.getClass().getSimpleName());

                startActivity(myIntent);
                finish();
            }
        });
    }

    private class AddTripReqTask extends AsyncTask<Trip, Void, Integer> {

        @Override
        protected Integer doInBackground(Trip... params) {

            Trip tripToAdd = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/trip";
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(tripToAdd, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Trip> tripResponse = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Trip.class);
                return tripResponse.getBody().getId();
            } catch (Exception e) {
                if (((HttpClientErrorException) e).getStatusCode().value() == 403) {
                    Intent myIntent = new Intent(AddTripActivity.this, LoginActivity.class);
                    startActivity(myIntent);
                }
                Log.e("ERROR-ADD-TRIP", e.getMessage());
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer idInsertedTrip) {

        }
    }

}
