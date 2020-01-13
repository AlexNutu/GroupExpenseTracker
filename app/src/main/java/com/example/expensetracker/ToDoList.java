package com.example.expensetracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.expensetracker.domain.ToDoObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

public class ToDoList extends AppCompatActivity {

    private Integer tripId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            tripId = currentIntent.getIntExtra("tripId", -1);
        }

        setActions();
    }

    private void setActions() {
        // Retrieve To do's from DB and attach them to the list view
        new GetToDosReqTask().execute(tripId);

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
        AddToDoDialog addToDoDialog = new AddToDoDialog(tripIdParam);
        addToDoDialog.show(getSupportFragmentManager(), "add todo dialog");
    }

    private void setListViewItems(ToDoObject[] toDoFromDB) {

        ListView mListView = (ListView) findViewById(R.id.toDoLV);

        ArrayList<ToDoObject> toDoObjectList = new ArrayList<>();
        for (int i = 0; i < toDoFromDB.length; i++) {
            ToDoObject t = toDoFromDB[i];
            toDoObjectList.add(new ToDoObject(t.getIdNote(), t.getMessage(), t.getUser(), t.getCreateDate()));
        }

        // Adding elements into List View
        ToDoListAdapter toDoListAdapter = new ToDoListAdapter(this, R.layout.adapter_todo_view_layout, toDoObjectList, tripId);
        mListView.setAdapter(toDoListAdapter);
    }

    private class GetToDosReqTask extends AsyncTask<Integer, Void, ToDoObject[]> {

        @Override
        protected ToDoObject[] doInBackground(Integer... params) {

            ToDoObject[] toDoFromDB = {};
            int tripIdParam = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/note?search=trip:" + tripIdParam;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<ToDoObject[]> responseEntity = restTemplate.getForEntity(apiUrl, ToDoObject[].class);
                toDoFromDB = responseEntity.getBody();

            } catch (Exception e) {
                Log.e("ERROR-GET-TODOs", e.getMessage());
            }

            return toDoFromDB;
        }

        @Override
        protected void onPostExecute(ToDoObject[] toDoFromDB) {
            setListViewItems(toDoFromDB);
        }
    }


}
