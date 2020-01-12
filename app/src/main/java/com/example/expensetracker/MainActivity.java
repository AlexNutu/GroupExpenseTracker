package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.expensetracker.helper.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

import static android.os.Build.VERSION_CODES.N;

public class MainActivity extends AppCompatActivity {

    // list of trips
    private ListView lv;

    // adapter for list view
    private ArrayAdapter<String> adapter;

    private EditText inputSearch;

    // arraylist for listview
    ArrayList<HashMap<String, String>> tripList;

    DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getApplicationContext());
        db.getReadableDatabase();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addTrips();
        addTripButton();
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

    public void addTrips() {
        final String trips[] = {"Trip1", "Trip2", "Trip3", "Trip4"};

        lv = (ListView) findViewById(R.id.tripsListView);
        inputSearch = (EditText) findViewById(R.id.searchET);

        // adding item to list view
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.trip_name, trips);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(MainActivity.this, ViewTrip.class);
                myIntent.putExtra("tripName", adapter.getItem(position));
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
