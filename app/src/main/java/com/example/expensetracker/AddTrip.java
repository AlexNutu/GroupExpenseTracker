package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class AddTrip extends AppCompatActivity {

    private EditText etName, etDescription, etMembers, etStartDate, etEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tript);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etName = (EditText) findViewById(R.id.tripName);
        etDescription = (EditText) findViewById(R.id.tripDescription);
        etMembers = (EditText) findViewById(R.id.noOfMembers);
        etStartDate = (EditText) findViewById(R.id.tripStartDate);
        etEndDate = (EditText) findViewById(R.id.tripEndDate);

        // set listeners
        etName.addTextChangedListener(mTextWatcher);
        etDescription.addTextChangedListener(mTextWatcher);
        etMembers.addTextChangedListener(mTextWatcher);
        etStartDate.addTextChangedListener(mTextWatcher);
        etEndDate.addTextChangedListener(mTextWatcher);

        // run once to disable if empty
        checkFieldsForEmptyValues();


        addButtonIntent();
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

        String s1 = etName.getText().toString().trim();
        String s2 = etDescription.getText().toString().trim();
        String s3 = etMembers.getText().toString().trim();
        String s4 = etStartDate.getText().toString().trim();
        String s5 = etEndDate.getText().toString().trim();

        if (s1.equals("") || s2.equals("") || s3.equals("") || s4.equals("") || s5.equals("")) {
            b.setEnabled(false);
        } else {
            b.setEnabled(true);
        }
    }

    private void addButtonIntent() {
        Button addTrip = (Button) findViewById(R.id.addBtn);
        addTrip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String tripName = ((EditText) findViewById(R.id.tripName)).getText().toString();

                Intent myIntent = new Intent(AddTrip.this, ViewTrip.class);
                myIntent.putExtra("tripName", tripName);
                myIntent.putExtra("fromActivity", "AddTrip");
                startActivity(myIntent);
            }
        });
    }

}
