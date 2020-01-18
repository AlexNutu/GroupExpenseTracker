package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.expensetracker.domain.ToDoObject;
import com.example.expensetracker.domain.ToDoObjectWithTrip;
import com.example.expensetracker.domain.User;
import com.example.expensetracker.helper.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class ViewMembers extends AppCompatActivity {

    private Session session;

    private SpinnerDialog spinnerDialog;
    private Integer tripId;
    private User[] tripMembersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_members);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new Session(getApplicationContext());

        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            tripId = currentIntent.getIntExtra("tripId", -1);
        }

        try {
            setActions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setActions() throws ExecutionException, InterruptedException {

        // Retrieve the members list and attach them to the listView
        final User[] membersListFromDB = new GetMembersReqTask().execute(tripId).get();

        final User[] usersListFromDB = new GetAllUsersReqTask().execute().get();

        ArrayList<String> fullNameList = new ArrayList<>();
        for (int i = 0; i < usersListFromDB.length; i++) {
            fullNameList.add(usersListFromDB[i].getFirstName() + " " + usersListFromDB[i].getLastName());
        }
        spinnerDialog = new SpinnerDialog(ViewMembers.this, fullNameList, "Select member");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String selectedUserFullName, int position) {

                boolean memberAlreadyExists = false;
                for (int i = 0; i < membersListFromDB.length; i++) {
                    String memberFullNameFromDB = membersListFromDB[i].getFirstName() + " " + membersListFromDB[i].getLastName();
                    if (memberFullNameFromDB.equals(selectedUserFullName)) {
                        memberAlreadyExists = true;
                    }
                }
                if (!memberAlreadyExists) {
                    User memberToAdd = new User();
                    for (int i = 0; i < usersListFromDB.length; i++) {
                        String userFullNameFromDB = usersListFromDB[i].getFirstName() + " " + usersListFromDB[i].getLastName();
                        if (userFullNameFromDB.equals(selectedUserFullName)) {
                            memberToAdd = usersListFromDB[i];
                        }
                    }
                    try {
                        new AddMemberReqTask().execute(memberToAdd).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(ViewMembers.this, selectedUserFullName + " is now member!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewMembers.this, "Warning! " + selectedUserFullName + " already exists!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton addMemberBtn = (FloatingActionButton) findViewById(R.id.floatingAddMemberBtn);
        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });

    }

    private class AddMemberReqTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... params) {

            User memberToInsert = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/trip/ " + tripId + "/member";
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(memberToInsert, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, User.class);

            } catch (Exception e) {
                if (((HttpClientErrorException) e).getStatusCode().value() == 403)
                {
                    Intent myIntent = new Intent(ViewMembers.this, LoginActivity.class);
                    startActivity(myIntent);
                }
                Log.e("ERROR-ADD-MEMBER", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            new GetMembersReqTask().execute(tripId);
        }
    }

    private class GetAllUsersReqTask extends AsyncTask<Void, Void, User[]> {

        @Override
        protected User[] doInBackground(Void... params) {

            User[] usersList = {};
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/user";
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<User[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, User[].class);
                usersList = responseEntity.getBody();

            } catch (Exception e) {
                if (((HttpClientErrorException) e).getStatusCode().value() == 403)
                {
                    Intent myIntent = new Intent(ViewMembers.this, LoginActivity.class);
                    startActivity(myIntent);
                }
                Log.e("ERROR-GET-MEMBERS", e.getMessage());
            }

            return usersList;
        }

        @Override
        protected void onPostExecute(User[] usersList) {
        }
    }

    private class GetMembersReqTask extends AsyncTask<Integer, Void, User[]> {

        @Override
        protected User[] doInBackground(Integer... params) {

            User[] membersList = {};
            int tripIdParam = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/user?search=tripList:" + tripIdParam;
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<User[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, User[].class);
                membersList = responseEntity.getBody();

            } catch (Exception e) {
                if (((HttpClientErrorException) e).getStatusCode().value() == 403)
                {
                    Intent myIntent = new Intent(ViewMembers.this, LoginActivity.class);
                    startActivity(myIntent);
                }
                Log.e("ERROR-GET-MEMBERS", e.getMessage());
            }

            return membersList;
        }

        @Override
        protected void onPostExecute(User[] membersList) {
            setListViewItems(membersList);
        }
    }

    public void setListViewItems(User[] membersList) {
        ListView mListView = (ListView) findViewById(R.id.membersLV);

        ArrayList<User> membersObjectList = new ArrayList<>();
        for (int i = 0; i < membersList.length; i++) {
            User u = membersList[i];
            membersObjectList.add(new User(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getPassword(), ""));
        }

        MembersListAdapter membersListAdapter = new MembersListAdapter(this, R.layout.member_item, membersObjectList);
        mListView.setAdapter(membersListAdapter);
    }

}
