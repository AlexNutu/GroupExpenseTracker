package com.example.expensetracker;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.expensetracker.domain.Trip;
import com.example.expensetracker.domain.User;
import com.example.expensetracker.helper.DatabaseHelper;
import com.example.expensetracker.service.NotificationJobService;
import com.example.expensetracker.helper.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    DatabaseHelper db;

    private Session session;

    // list of trips
    private ListView lv;
    // adapter for list view
    private ArrayAdapter<String> adapter;
    private EditText inputSearch;

    private User currentUserObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getApplicationContext());
        db.getReadableDatabase();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new Session(getApplicationContext());

        Intent intent = getIntent();
        if (intent != null) {
            currentUserObject = (User) intent.getSerializableExtra("currentUserObject");
            if (intent.getStringExtra("fromActivity").equals("LoginActivity")) {
                Toast.makeText(MainActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
            } else if (intent.getStringExtra("fromActivity").equals("RegisterActivity")) {
                Toast.makeText(MainActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
            }
        }

        addTripButton();

        new GetTripsReqTask().execute();

        // Start job for sending notifications
        scheduleJob(findViewById(android.R.id.content).getRootView());
    }

    private void addTripButton() {
        FloatingActionButton addTrip = (FloatingActionButton) findViewById(R.id.floatingAddTripButton);
        addTrip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, AddTripActivity.class);
                myIntent.putExtra("currentUserObject", currentUserObject);
                startActivity(myIntent);
            }
        });
    }

    public void addTrips(final Trip[] tripsFromDB) {
        final String[] tripStrings = new String[tripsFromDB.length];
        for (int i = 0; i < tripsFromDB.length; i++) {
            tripStrings[i] = tripsFromDB[i].getName();
        }

        lv = (ListView) findViewById(R.id.tripsListView);
        inputSearch = (EditText) findViewById(R.id.searchET);

        // adding item to list view
        adapter = new ArrayAdapter<String>(this, R.layout.trip_list_item, R.id.trip_name, tripStrings);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(MainActivity.this, ViewTripActivity.class);
                myIntent.putExtra("currentUserObject", currentUserObject);
                Integer tripIdToSend = -1;
                for (int i = 0; i < tripsFromDB.length; i++) {
                    if (tripsFromDB[i].getName().equals(adapter.getItem(position))) {
                        tripIdToSend = tripsFromDB[i].getId();
                    }
                }
                myIntent.putExtra("tripId", tripIdToSend);
                startActivity(myIntent);
            }
        });

        // enable search
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                MainActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private class GetTripsReqTask extends AsyncTask<Void, Void, Trip[]> {

        @Override
        protected Trip[] doInBackground(Void... voids) {

            Trip[] tripsFromDB = {};
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/trip?search=members:" + currentUserObject.getId();
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Trip[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, Trip[].class);
                tripsFromDB = responseEntity.getBody();

            } catch (Exception e) {
                if (((HttpClientErrorException) e).getStatusCode().value() == 403)
                {
                    Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(myIntent);
                }
                Log.e("ERROR-GET-TRIPS", e.getMessage());
            }

            return tripsFromDB;
        }

        @Override
        protected void onPostExecute(Trip[] tripsFromDB) {
            addTrips(tripsFromDB);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Scheduled job code
    public void scheduleJob(View v) {
        ComponentName componentName = new ComponentName(this, NotificationJobService.class);
        PersistableBundle paramsBundle = new PersistableBundle();
        paramsBundle.putLong("currentUserId", currentUserObject.getId());
        JobInfo jobInfo = new JobInfo.Builder(123, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .setExtras(paramsBundle)
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(jobInfo);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled!");
        } else {
            Log.d(TAG, "Job scheduling failed!");
        }
    }

    public void cancelJob(View v) {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d(TAG, "Job canceled!");
    }


}
