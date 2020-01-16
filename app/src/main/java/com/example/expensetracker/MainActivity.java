package com.example.expensetracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.expensetracker.helper.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;

    // list of trips
    private ListView lv;

    // adapter for list view
    private ArrayAdapter<String> adapter;

    private EditText inputSearch;

    private Long currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getApplicationContext());
        db.getReadableDatabase();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null) {
            currentUserId = intent.getLongExtra("loggedUserId", -1);
            if (currentUserId != -1) {
                Toast.makeText(MainActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
            }
        }

        addTripButton();
        new GetTripsReqTask().execute();
    }

    private void addTripButton() {
        FloatingActionButton addTrip = (FloatingActionButton) findViewById(R.id.floatingAddTripButton);
        addTrip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, AddTrip.class);
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
                Intent myIntent = new Intent(MainActivity.this, ViewTrip.class);
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
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/trip";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Trip[]> responseEntity = restTemplate.getForEntity(apiUrl, Trip[].class);
                tripsFromDB = responseEntity.getBody();

            } catch (Exception e) {
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
}
