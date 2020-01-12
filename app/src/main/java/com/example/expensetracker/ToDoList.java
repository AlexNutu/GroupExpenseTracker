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
import com.example.expensetracker.domain.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

public class ToDoList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setActions();
    }

    private void setActions() {
        Intent currentIntent = getIntent();
        Integer tripIdParam = -1;
        if (currentIntent != null) {
            tripIdParam = currentIntent.getIntExtra("tripId", -1);
        }

        // Retrieve To do's from DB and attach them to the list view
        new GetToDosReqTask().execute(tripIdParam);

        FloatingActionButton addToDo = (FloatingActionButton) findViewById(R.id.floatingAddToDoButton);
        final Integer finalTripIdParam = tripIdParam;
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
            toDoObjectList.add(new ToDoObject(t.getMessage(), t.getUser(), t.getCreateDate()));
        }

        // Adding elements into List View
        ToDoListAdapter toDoListAdapter = new ToDoListAdapter(this, R.layout.adapter_todo_view_layout, toDoObjectList);
        mListView.setAdapter(toDoListAdapter);
    }

    private class GetToDosReqTask extends AsyncTask<Integer, Void, ToDoObject[]> {

        @Override
        protected ToDoObject[] doInBackground(Integer... tripIdParam) {

            ToDoObject[] toDoFromDB = {};
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
