package com.example.expensetracker;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.expensetracker.helper.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.os.Build.VERSION_CODES.N;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                TextView myAwesomeTextView = (TextView)findViewById(R.id.networkStateText);
                myAwesomeTextView.setText("You are in Online Mode");
            }
            else
            {
                TextView myAwesomeTextView = (TextView)findViewById(R.id.networkStateText);
                myAwesomeTextView.setText("You are in Offline Mode");
            }
        }
        else{
                TextView myAwesomeTextView = (TextView)findViewById(R.id.networkStateText);
                myAwesomeTextView.setText("You are in Offline Mode");
        }
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

    private void addTrips() {

        LinearLayout tripListLayout = (LinearLayout) findViewById(R.id.tripList);

        // N is 24
        final TextView[] myTextViews = new TextView[N]; // create an empty array;

        for (int i = 1; i <= N - 20; i++) {
            // create a new textview
            final TextView rowTextView = new TextView(this);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    170);
            //Setting the above params to our TextView
            rowTextView.setLayoutParams(params);

            // set some properties of rowTextView or something
            rowTextView.setText("Trip " + i);
            rowTextView.setTextSize(18);

            rowTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(MainActivity.this, ViewTrip.class);
                    startActivity(myIntent);
                }
            });

            // add the textview to the linearlayout
            tripListLayout.addView(rowTextView);

            // save a reference to the textview for later
            myTextViews[i] = rowTextView;
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
