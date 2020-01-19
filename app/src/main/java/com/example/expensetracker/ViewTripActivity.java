package com.example.expensetracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.expensetracker.dialog.AddComplexExpenseDialog;
import com.example.expensetracker.dialog.AddSimpleExpenseDialog;
import com.example.expensetracker.domain.Expense;
import com.example.expensetracker.domain.ExpenseDialogListener;
import com.example.expensetracker.domain.Trip;
import com.example.expensetracker.domain.User;
import com.example.expensetracker.helper.Session;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ViewTripActivity extends AppCompatActivity implements ExpenseDialogListener {

    private Session session;

    private TextView tripTitleTV;
    private TextView tripDestinationTV;
    private TextView tripStartDateTV;
    private TextView tripEndDateTV;

    private User currentUserObject;
    private Integer idCurrentTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);

        session = new Session(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tripTitleTV = (TextView) findViewById(R.id.tvViewTrip);
        tripDestinationTV = (TextView) findViewById(R.id.tripDestinationTV);
        tripStartDateTV = (TextView) findViewById(R.id.tripStartDateTV);
        tripEndDateTV = (TextView) findViewById(R.id.tripEndDateTV);

        init();
        configureBtnActions();
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtra("currentUserObject", this.currentUserObject);
        myIntent.putExtra("fromActivity", "ViewTripActivity");
        startActivity(myIntent);
    }

    private void init() {

        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            currentUserObject = (User) currentIntent.getSerializableExtra("currentUserObject");
            String fromActivity = currentIntent.getStringExtra("fromActivity");
            if (fromActivity != null && fromActivity.equals("AddTripActivity")) {
                // We are here from AddTripActivity
                Toast.makeText(getApplicationContext(), "Trip added successfully!", Toast.LENGTH_SHORT).show();
            }
            idCurrentTrip = currentIntent.getIntExtra("tripId", 0);
            new GetTripReqTask().execute(idCurrentTrip);
        }

        // Configure add expense buttons
        FloatingActionButton simpleExpenseBtn = findViewById(R.id.fab_action1);
        simpleExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSimpleExpenseDialog("Simple Expense");
            }
        });

        FloatingActionButton groupExpenseBtn = findViewById(R.id.fab_action2);
        groupExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComplexExpenseDialog("Group Expense");
            }
        });

        FloatingActionButton collectExpenseBtn = findViewById(R.id.fab_action3);
        collectExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComplexExpenseDialog("Collective Expense");
            }
        });
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void configureBtnActions() {
        ImageButton viewMembersBtn = (ImageButton) findViewById(R.id.viewMembersBtn);
        ImageButton genReportBtn = (ImageButton) findViewById(R.id.genReportBtn);
        ImageButton toDoListBtn = (ImageButton) findViewById(R.id.toDoListBtn);

        final Intent fromIntent = getIntent();

        viewMembersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent membersIntent = new Intent(ViewTripActivity.this, ViewMembersActivity.class);
                if (fromIntent != null) {
                    membersIntent.putExtra("tripId", fromIntent.getIntExtra("tripId", 0));
                }
                startActivity(membersIntent);
            }
        });

        genReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewTripActivity.this, ViewReportActivity.class);
                if (fromIntent != null) {
                    intent.putExtra("tripId", fromIntent.getIntExtra("tripId", 0));
                }
                startActivity(intent);
            }
        });

        toDoListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDoListIntent = new Intent(ViewTripActivity.this, ToDoListActivity.class);
                if (fromIntent != null) {
                    toDoListIntent.putExtra("currentUserObject", currentUserObject);
                    toDoListIntent.putExtra("tripId", fromIntent.getIntExtra("tripId", 0));
                }
                startActivity(toDoListIntent);
            }
        });

    }

    private void openSimpleExpenseDialog(String expenseType) {
        AddSimpleExpenseDialog addSimpleExpenseDialog = new AddSimpleExpenseDialog(expenseType);
        addSimpleExpenseDialog.show(getSupportFragmentManager(), "simple expense dialog");
    }

    private void openComplexExpenseDialog(String expenseType) {
        AddComplexExpenseDialog addComplexExpenseDialog = new AddComplexExpenseDialog(expenseType);
        addComplexExpenseDialog.show(getSupportFragmentManager(), "complex expense dialog");
    }

    private class GetTripReqTask extends AsyncTask<Integer, Void, Trip> {

        @Override
        protected Trip doInBackground(Integer... params) {

            Integer idTrip = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/trip/" + idTrip;
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Trip> currentTrip = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, Trip.class);
                return currentTrip.getBody();
            } catch (Exception e) {
                if (((HttpClientErrorException) e).getStatusCode().value() == 403)
                {
                    Intent myIntent = new Intent(ViewTripActivity.this, LoginActivity.class);
                    startActivity(myIntent);
                }
                Log.e("ERROR-GET-TRIP", e.getMessage());
            }
            return new Trip();
        }

        @Override
        protected void onPostExecute(Trip t) {
            tripTitleTV.setText(t.getName());
            tripDestinationTV.setText(t.getDestination());
            tripStartDateTV.setText(t.getStartDate());
            tripEndDateTV.setText(t.getEndDate());
        }
    }

    private class AddExpenseReqTask extends AsyncTask<Expense, Void, Expense> {

        @Override
        protected Expense doInBackground(Expense... params) {

            Expense expenseParam = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/expense";
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(expenseParam, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Expense.class);

            } catch (Exception e) {
                if (((HttpClientErrorException) e).getStatusCode().value() == 403)
                {
                    Intent myIntent = new Intent(ViewTripActivity.this, LoginActivity.class);
                    startActivity(myIntent);
                }
                Log.e("ERROR-ADD-EXPENSE", e.getMessage());
            }
            return expenseParam;
        }

        @Override
        protected void onPostExecute(Expense e) {
            showToast(e.getExpensiveType() + " added successfully");
        }
    }

    @Override
    public void addExpenseToDB(String productName, String cost, String selectedCurrency, String percentage, String expenseType) {
        Trip currentTrip = new Trip();
        currentTrip.setId(idCurrentTrip);

        Expense e = new Expense(expenseType, productName, Float.valueOf(cost), selectedCurrency, Float.valueOf(percentage), currentUserObject, currentTrip);
        new AddExpenseReqTask().execute(e);
    }

}
