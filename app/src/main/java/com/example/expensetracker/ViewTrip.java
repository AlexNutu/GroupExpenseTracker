package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class ViewTrip extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        configureBtnActions();
    }

    private void configureBtnActions() {
        ImageButton viewMembersBtn = (ImageButton) findViewById(R.id.viewMembersBtn);
        ImageButton genReportBtn = (ImageButton) findViewById(R.id.genReportBtn);


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
    }
}
