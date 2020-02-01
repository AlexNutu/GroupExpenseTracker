package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker.dialog.DemoDialog;
import com.example.expensetracker.domain.User;
import com.example.expensetracker.helper.Session;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class RegisterActivity extends AppCompatActivity {

    private Session session;

    private EditText emailRegisterET;
    private EditText firstNameET;
    private EditText lastNameET;
    private EditText passRegisterET;
    private EditText confirmRegisterET;
    private Button registerBtn;
    private TextView seeDemoTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        session = new Session(getApplicationContext());

        emailRegisterET = (EditText) findViewById(R.id.emailRegisterET);
        firstNameET = (EditText) findViewById(R.id.firstNameRegisterET);
        lastNameET = (EditText) findViewById(R.id.lastNameRegisterET);
        passRegisterET = (EditText) findViewById(R.id.passRegisterET);
        confirmRegisterET = (EditText) findViewById(R.id.confirmRegisterET);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        seeDemoTV = (TextView) findViewById(R.id.seeDemoTV);

        emailRegisterET.addTextChangedListener(mTextWatcher);
        firstNameET.addTextChangedListener(mTextWatcher);
        lastNameET.addTextChangedListener(mTextWatcher);
        passRegisterET.addTextChangedListener(mTextWatcher);
        confirmRegisterET.addTextChangedListener(mTextWatcher);

        setActions();
        checkFieldsForEmptyValues();
    }

    private void setActions() {

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailRegister = emailRegisterET.getText().toString().trim();
                String firstName = firstNameET.getText().toString().trim();
                String lastName = lastNameET.getText().toString().trim();
                String passRegister = passRegisterET.getText().toString().trim();
                String confirmRegister = confirmRegisterET.getText().toString().trim();

                if (!passRegister.equals(confirmRegister)) {
                    Toast.makeText(RegisterActivity.this, "Password and Confirm don't match!", Toast.LENGTH_SHORT).show();
                } else {
                    User userToInsert = new User();
                    userToInsert.setEmail(emailRegister);
                    userToInsert.setFirstName(firstName);
                    userToInsert.setLastName(lastName);
                    userToInsert.setPassword(passRegister);
                    User insertedUser = new User();
                    try {
                        insertedUser = new RegisterUserReqTask().execute(userToInsert).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (insertedUser.getErrorMessage() == null || insertedUser.getErrorMessage().equals("")) {
                        Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        myIntent.putExtra("fromActivity", "RegisterActivity");
                        myIntent.putExtra("currentUserObject", insertedUser);
                        startActivity(myIntent);
                    } else {
                        // Display error from BE
                        Toast toast = Toast.makeText(RegisterActivity.this, insertedUser.getErrorMessage(), Toast.LENGTH_SHORT);
                        TextView tv = (TextView) toast.getView().findViewById(android.R.id.message);
                        tv.setTextColor(Color.RED);
                        toast.show();
                    }
                }
            }
        });

        seeDemoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDemoDialog();
            }
        });
    }

    private void openDemoDialog() {
        DemoDialog demoDialog = new DemoDialog();
        demoDialog.show(getSupportFragmentManager(), "demo dialog");
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
        Button b = (Button) findViewById(R.id.registerBtn);

        String emailRegister = emailRegisterET.getText().toString().trim();
        String firstName = firstNameET.getText().toString().trim();
        String lastName = lastNameET.getText().toString().trim();
        String passRegister = passRegisterET.getText().toString().trim();
        String confirmRegister = confirmRegisterET.getText().toString().trim();

        if (emailRegister.equals("") || passRegister.equals("") || confirmRegister.equals("")
                || firstName.equals("") || lastName.equals("")) {
            b.setEnabled(false);
        } else {
            b.setEnabled(true);
        }
    }

    private class RegisterUserReqTask extends AsyncTask<User, Void, User> {

        @Override
        protected User doInBackground(User... params) {

            User userToRegisterParam = params[0];
            User resultedUser = new User();
            try {
                String apiUrl = "http://10.0.2.2:8080/register/";
                HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                        = new HttpComponentsClientHttpRequestFactory();
                clientHttpRequestFactory.setConnectTimeout(1000);
                RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<User> userObjectResult = restTemplate.postForEntity(apiUrl, userToRegisterParam, User.class);
                resultedUser = userObjectResult.getBody();
                return resultedUser;

            } catch (HttpClientErrorException e) {
                Log.e("ERROR-REGISTER", e.getMessage());
                resultedUser.setErrorMessage(e.getResponseBodyAsString() + "!");
                return resultedUser;
            }catch (Exception e){
                Log.e("ERROR-REGISTER", e.getMessage());
                resultedUser.setErrorMessage("Service is temporarily unavailable!");
                return resultedUser;
            }
        }

        @Override
        protected void onPostExecute(User resultedUser) {
        }
    }

}
