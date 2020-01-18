package com.example.expensetracker.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.expensetracker.R;
import com.example.expensetracker.adapter.ToDoListAdapter;
import com.example.expensetracker.domain.ToDoObject;
import com.example.expensetracker.helper.Session;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

public class DeleteToDoDialog extends AppCompatDialogFragment {

    private Long toDoId;
    private Integer tripId;
    private Context activityContext;

    private Session session;

    public DeleteToDoDialog(Long toDoIdParam, Integer tripIdParam) {
        this.toDoId = toDoIdParam;
        this.tripId = tripIdParam;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        session = new Session(getContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_todo_dialog, null);

        builder.setView(view)
                .setTitle("Delete To Do")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteToDoReqTask().execute(toDoId);
                    }
                });

        return builder.create();
    }

    private class DeleteToDoReqTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... params) {

            Long toDoIdParam = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/note/" + toDoIdParam;
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.exchange(apiUrl, HttpMethod.DELETE, requestEntity, ToDoObject.class);

            } catch (Exception e) {
                Log.e("ERROR-DELETE-TODO", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            Toast.makeText(activityContext, "Deleted successfully", Toast.LENGTH_SHORT).show();
            // Retrieve To do's from DB and attach them to the list view
            new GetToDosReqTask().execute(tripId);
        }
    }

    private void setListViewItems(ToDoObject[] toDoFromDB) {

        ListView mListView = (ListView) ((Activity) activityContext).findViewById(R.id.toDoLV);

        ArrayList<ToDoObject> toDoObjectList = new ArrayList<>();
        for (int i = 0; i < toDoFromDB.length; i++) {
            ToDoObject t = toDoFromDB[i];
            toDoObjectList.add(new ToDoObject(t.getIdNote(), t.getApproved(), t.getMessage(), t.getUser(), t.getCreateDate()));
        }

        // Adding elements into List View
        ToDoListAdapter toDoListAdapter = new ToDoListAdapter(activityContext, R.layout.adapter_todo_view_layout, toDoObjectList, tripId);
        mListView.setAdapter(toDoListAdapter);
    }

    private class GetToDosReqTask extends AsyncTask<Integer, Void, ToDoObject[]> {

        @Override
        protected ToDoObject[] doInBackground(Integer... params) {

            ToDoObject[] toDoFromDB = {};
            int tripIdParam = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/note?search=trip:" + tripIdParam;
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<ToDoObject[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, ToDoObject[].class);
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
