package com.example.expensetracker.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.expensetracker.GroupExpenseTracker;
import com.example.expensetracker.R;
import com.example.expensetracker.adapter.ToDoListAdapter;
import com.example.expensetracker.domain.ToDoObjectWithTrip;
import com.example.expensetracker.domain.Trip;
import com.example.expensetracker.domain.User;
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

public class AddToDoDialog extends AppCompatDialogFragment {

    private EditText toDoET;
    private Integer tripId;
    private Context activityContext;

    private User currentUserObject;

    private Session session;

    private AlertDialog d;

    private DatabaseHelper db;

    public AddToDoDialog(Integer tripIdParam, User currentUser) {
        this.tripId = tripIdParam;
        this.currentUserObject = currentUser;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        session = new Session(getContext());
        db = DatabaseHelper.getInstance(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_todo_dialog, null);

        builder.setView(view)
                .setTitle("Add To Do")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String toDoText = toDoET.getText().toString();

                        Trip currentTrip = new Trip();
                        currentTrip.setId(tripId);

                        ToDoObjectWithTrip toDoObjectWithTrip = new ToDoObjectWithTrip();
                        toDoObjectWithTrip.setApproved(false);
                        toDoObjectWithTrip.setMessage(toDoText);
                        toDoObjectWithTrip.setUser(currentUserObject);
                        toDoObjectWithTrip.setTrip(currentTrip);
                        if(NetworkStateChecker.isConnected(getContext()))
                             new AddToDoReqTask().execute(toDoObjectWithTrip);
                        else {
                            db.addNote(toDoObjectWithTrip, 0);
                            Toast.makeText(activityContext, "To Do added successfully in local storage!", Toast.LENGTH_SHORT).show();
                            ArrayList<ToDoObjectWithTrip> notes = db.getTripNotesList(tripId);
                            if(notes != null)
                                setListViewItems(notes.toArray(new ToDoObjectWithTrip[0]));
                            else
                                setListViewItems(new ToDoObjectWithTrip[0]);

                        }
                    }
                });

        d = builder.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });

        toDoET = view.findViewById(R.id.toDoET);
        toDoET.addTextChangedListener(mTextWatcher);

        return d;
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            final Button okButton = d.getButton(AlertDialog.BUTTON_POSITIVE);
            String toDoText = toDoET.getText().toString().trim();
            if (toDoText.equals("")) {
                okButton.setEnabled(false);
            } else {
                okButton.setEnabled(true);
            }
        }
    };

    private class AddToDoReqTask extends AsyncTask<ToDoObjectWithTrip, Void, Void> {

        @Override
        protected Void doInBackground(ToDoObjectWithTrip... params) {

            ToDoObjectWithTrip toDoParam = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/note";
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(toDoParam, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, ToDoObjectWithTrip.class);

            } catch (Exception e) {
                Log.e("ERROR-ADD-TODO", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            Toast.makeText(activityContext, "To Do added successfully", Toast.LENGTH_SHORT).show();
            // Retrieve To do's from DB and attach them to the list view
            new GetToDosReqTask().execute(tripId);
        }
    }

    private void setListViewItems(ToDoObjectWithTrip[] toDoFromDB) {

        ListView mListView = (ListView) ((Activity) activityContext).findViewById(R.id.toDoLV);

        ArrayList<ToDoObjectWithTrip> toDoObjectList = new ArrayList<>();
        for (int i = 0; i < toDoFromDB.length; i++) {
            ToDoObjectWithTrip t = toDoFromDB[i];
            toDoObjectList.add(new ToDoObjectWithTrip(t.getId(), t.getApproved(), t.getMessage(), t.getUser(),t.getTrip(), t.getCreateDate(), t.getModifyDate()));
        }

        // Adding elements into List View
        ToDoListAdapter toDoListAdapter = new ToDoListAdapter(activityContext, R.layout.adapter_todo_view_layout, toDoObjectList, tripId);
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


