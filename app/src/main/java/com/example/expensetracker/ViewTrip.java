package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class ViewTrip extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        configureBtnActions();
    }

    private void init() {
        // attaching text to the title
        TextView title = (TextView) findViewById(R.id.tvViewTrip);
        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            String fromActivity = currentIntent.getStringExtra("fromActivity");
            if (fromActivity != null && fromActivity.equals("AddTrip")) {
                // We are here from AddTripActivity
                Toast.makeText(getApplicationContext(), "Trip added successfully!", Toast.LENGTH_SHORT).show();
            }
            title.setText(currentIntent.getStringExtra("tripName"));
        }
        // add expense buttons
        FloatingActionButton individualExpenseBtn = findViewById(R.id.fab_action1);

        individualExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Individual Expense");
            }
        });
        FloatingActionButton groupExpenseBtn = findViewById(R.id.fab_action2);

        groupExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Group Expense");
            }
        });
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void configureBtnActions() {
        ImageButton viewMembersBtn = (ImageButton) findViewById(R.id.viewMembersBtn);
        ImageButton genReportBtn = (ImageButton) findViewById(R.id.genReportBtn);
        ImageButton toDoListBtn = (ImageButton) findViewById(R.id.toDoListBtn);

        viewMembersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent membersIntent = new Intent(ViewTrip.this, ViewMembers.class);
                startActivity(membersIntent);
            }
        });

        genReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent genReportIntent = new Intent(ViewTrip.this, GenerateReport.class);
                startActivity(genReportIntent);
            }
        });

        toDoListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDoListIntent = new Intent(ViewTrip.this, ToDoList.class);
                startActivity(toDoListIntent);
            }
        });

    }
}
