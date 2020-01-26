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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.expensetracker.R;
import com.example.expensetracker.adapter.ToDoListAdapter;
import com.example.expensetracker.domain.ToDoObjectWithTrip;
import com.example.expensetracker.domain.ToDoObjectWithTrip;
import com.example.expensetracker.helper.DatabaseHelper;
import com.example.expensetracker.helper.NetworkStateChecker;
import com.example.expensetracker.helper.Session;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

public class CheckToDoDialog extends AppCompatDialogFragment {

    private TextView toDoQuestionTV;
    private Integer tripId;
    private ToDoObjectWithTrip toUpdateToDo;
    private Context activityContext;

    private Session session;
    private DatabaseHelper db;

    public CheckToDoDialog(ToDoObjectWithTrip ToDoObjectWithTrip, Integer tripIdParam) {
        this.tripId = tripIdParam;
        this.toUpdateToDo = ToDoObjectWithTrip;
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
        db = DatabaseHelper.getInstance(getContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_todo_dialog, null);

        builder.setView(view)
                .setTitle("Update To Do")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(NetworkStateChecker.isConnected(getContext()))
                            new UpdateToDoReqTask().execute(toUpdateToDo);
                        else {
                            db.updateNote(toUpdateToDo, 2);
                            Toast.makeText(activityContext, "To Do was updated!", Toast.LENGTH_SHORT).show();
                            ArrayList<ToDoObjectWithTrip> notes = db.getTripNotesList(tripId);
                            if(notes != null)
                                setListViewItems(notes.toArray(new ToDoObjectWithTrip[0]));
                            else
                                setListViewItems(new ToDoObjectWithTrip[0]);

                        }
                    }
                });

        toDoQuestionTV = view.findViewById(R.id.toDoQuestionTV);
        if (toUpdateToDo.getApproved()) {
            toDoQuestionTV.setText("Check this item?");
        } else {
            toDoQuestionTV.setText("Uncheck this item?");
        }

        return builder.create();
    }

    private class UpdateToDoReqTask extends AsyncTask<ToDoObjectWithTrip, Void, Void> {

        @Override
        protected Void doInBackground(ToDoObjectWithTrip... params) {

            ToDoObjectWithTrip toDoParam = params[0];
            long noteId = toDoParam.getId();
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/note/" + noteId;
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(toDoParam, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.exchange(apiUrl, HttpMethod.PUT, requestEntity, ToDoObjectWithTrip.class);
            } catch (Exception e) {
                Log.e("ERROR-UPDATE-TODO", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            Toast.makeText(activityContext, "To Do was updated!", Toast.LENGTH_SHORT).show();
            // Retrieve To do's from DB and attach them to the list view
            new GetToDosReqTask().execute(tripId);
        }
    }

    private void setListViewItems(ToDoObjectWithTrip[] toDoFromDB) {

        ListView mListView = (ListView) ((Activity) activityContext).findViewById(R.id.toDoLV);

        ArrayList<ToDoObjectWithTrip> ToDoObjectWithTripList = new ArrayList<>();
        for (int i = 0; i < toDoFromDB.length; i++) {
            ToDoObjectWithTrip t = toDoFromDB[i];
            ToDoObjectWithTripList.add(new ToDoObjectWithTrip(t.getId(), t.getApproved(), t.getMessage(), t.getUser(), t.getTrip(), t.getCreateDate(), t.getModifyDate()));
        }

        // Adding elements into List View
        ToDoListAdapter toDoListAdapter = new ToDoListAdapter(activityContext, R.layout.adapter_todo_view_layout, ToDoObjectWithTripList, tripId);
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
