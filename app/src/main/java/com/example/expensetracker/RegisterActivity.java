package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private Button registerBtn;
    private TextView seeDemoTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerBtn = (Button) findViewById(R.id.registerBtn);
        seeDemoTV = (TextView) findViewById(R.id.seeDemoTV);

        setActions();
    }

    private void setActions() {

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

        seeDemoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    private void openDialog() {
        DemoDialog demoDialog = new DemoDialog();
        demoDialog.show(getSupportFragmentManager(), "example dialog");
    }
}
