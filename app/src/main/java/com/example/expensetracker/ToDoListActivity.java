package com.example.expensetracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.expensetracker.adapter.ToDoListAdapter;
import com.example.expensetracker.dialog.AddToDoDialog;
import com.example.expensetracker.domain.ToDoObjectWithTrip;
import com.example.expensetracker.domain.User;
import com.example.expensetracker.helper.DatabaseHelper;
import com.example.expensetracker.helper.NetworkStateChecker;
import com.example.expensetracker.helper.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

public class ToDoListActivity extends AppCompatActivity {

    private Session session;

    private Integer tripId = -1;
    private User currentUserObject;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new Session(getApplicationContext());
        db = DatabaseHelper.getInstance(this);

        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            tripId = currentIntent.getIntExtra("tripId", -1);
            currentUserObject = (User) currentIntent.getSerializableExtra("currentUserObject");
        }

        setActions();
    }

    private void setActions() {
        // Retrieve To do's from DB and attach them to the list view
        if(NetworkStateChecker.isConnected(this)){
            new GetToDosReqTask().execute(tripId);
        }
        else {
            ListView mListView = (ListView) findViewById(R.id.toDoLV);
            ArrayList<ToDoObjectWithTrip> notes = db.getTripNotesList(tripId);
            if(notes != null)
                setListViewItems(notes.toArray(new ToDoObjectWithTrip[0]));
            else
                setListViewItems(new ToDoObjectWithTrip[0]);

        }


        FloatingActionButton addToDo = (FloatingActionButton) findViewById(R.id.floatingAddToDoButton);
        final Integer finalTripIdParam = tripId;
        addToDo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openAddToDoDialog(finalTripIdParam);
            }
        });
    }

    private void openAddToDoDialog(Integer tripIdParam) {
        AddToDoDialog addToDoDialog = new AddToDoDialog(tripIdParam, currentUserObject);
        addToDoDialog.show(getSupportFragmentManager(), "add todo dialog");
    }

    private void setListViewItems(ToDoObjectWithTrip[] toDoFromDB) {

        ListView mListView = (ListView) findViewById(R.id.toDoLV);

        ArrayList<ToDoObjectWithTrip> toDoObjectList = new ArrayList<>();
        for (int i = 0; i < toDoFromDB.length; i++) {
            ToDoObjectWithTrip t = toDoFromDB[i];
            toDoObjectList.add(new ToDoObjectWithTrip(t.getId(), t.getApproved(), t.getMessage(), t.getUser(), t.getTrip(), t.getCreateDate(), t.getModifyDate()));
        }

        // Adding elements into List View
        ToDoListAdapter toDoListAdapter = new ToDoListAdapter(this, R.layout.adapter_todo_view_layout, toDoObjectList, tripId);
        mListView.setAdapter(toDoListAdapter);
    }

    private class GetToDosReqTask extends AsyncTask<Integer, Void, ToDoObjectWithTrip[]> {

        @Override
        protected ToDoObjectWithTrip[] doInBackground(Integer... params) {

            ToDoObjectWithTrip[] toDoFromDB = {};
            int tripIdParam = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/note?search=trip:" + tripIdParam;
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<ToDoObjectWithTrip[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, ToDoObjectWithTrip[].class);
                toDoFromDB = responseEntity.getBody();

            } catch (Exception e) {
                Log.e("ERROR-GET-TODOs", e.getMessage());
            }

            return toDoFromDB;
        }

        @Override
        protected void onPostExecute(ToDoObjectWithTrip[] toDoFromDB) {
            setListViewItems(toDoFromDB);
        }
    }


}
