package com.example.expensetracker.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.example.expensetracker.domain.Expense;
import com.example.expensetracker.domain.ToDoObjectWithTrip;
import com.example.expensetracker.domain.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;


public class MySqlSynchronizer {

    private Context context;
    private DatabaseHelper db;

    public MySqlSynchronizer(Context context, DatabaseHelper db) {
        this.context = context;
        this.db = db;
    }

    public void synchronizeMySQL(){
        synchronizeLoggedUser();
        synchronizeInsertedToDos();
        synchronizeUpdatedToDos();
        synchronizeDeletedToDos();
        synchronizeInsertedExpense();
    }

   public void synchronizeInsertedToDos(){
       ArrayList<ToDoObjectWithTrip> notes = db.getNotesByStatus(0);
       for(ToDoObjectWithTrip note : notes)
           new AddToDoReqTask().execute(note);
   }

   public void synchronizeUpdatedToDos(){
       ArrayList<ToDoObjectWithTrip> notes = db.getNotesByStatus(2);
       for(ToDoObjectWithTrip note : notes)
           new UpdateToDoReqTask().execute(note);
   }

   public void synchronizeDeletedToDos(){
       ArrayList<ToDoObjectWithTrip> notes = db.getNotesByStatus(3);
       for(ToDoObjectWithTrip note : notes)
           new DeleteToDoReqTask().execute(note);
   }

    public void synchronizeLoggedUser(){
        User user = db.getLoggedUser();
        if(user!=null)
            if(user.getStatus()==2)
               new UpdateUserReqTask().execute(user);
    }

    public void synchronizeInsertedExpense(){
        ArrayList<Expense> expenses = db.getExpenseReportList(null);
        for(Expense e:expenses)
            new AddExpenseReqTask().execute(e);
    }


    private class UpdateUserReqTask extends AsyncTask<User, Void, User> {

        @Override
        protected User doInBackground(User... params) {

            User currentUser = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/user/" + currentUser.getId();
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(currentUser, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<User> userResponse = restTemplate.exchange(apiUrl, HttpMethod.PUT, requestEntity, User.class);
                return userResponse.getBody();
            } catch (Exception e) {
                Log.e("ERROR-UPDATE-USER", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(User updatedUser) {
            db.updateUser(updatedUser);
        }
    }

    private class AddToDoReqTask extends AsyncTask<ToDoObjectWithTrip, Void, Void> {

        ToDoObjectWithTrip toDoParam;
        Long toDoParamId;

        @Override
        protected Void doInBackground(ToDoObjectWithTrip... params) {

            toDoParam = params[0];
            toDoParamId = toDoParam.getId();

            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/note";
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
                toDoParam.setId(null);
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
            toDoParam.setId(toDoParamId);
            db.updateNote(toDoParam,1);
            db.deleteNote(toDoParam.getId());
        }
    }

    private class UpdateToDoReqTask extends AsyncTask<ToDoObjectWithTrip, Void, Void> {

        @Override
        protected Void doInBackground(ToDoObjectWithTrip... params) {

            ToDoObjectWithTrip toDoParam = params[0];
            long noteId = toDoParam.getId();
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/note/" + noteId;
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
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
    }

    private class DeleteToDoReqTask extends AsyncTask<ToDoObjectWithTrip, Void, Void> {

        ToDoObjectWithTrip note;

        @Override
        protected Void doInBackground(ToDoObjectWithTrip... params) {

           note = params[0];
           Long toDoIdParam = note.getId();
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/note/" + toDoIdParam;
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.exchange(apiUrl, HttpMethod.DELETE, requestEntity, ToDoObjectWithTrip.class);

            } catch (Exception e) {
                Log.e("ERROR-DELETE-TODO", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            db.deleteNote(note.getId());
        }
    }

    private class AddExpenseReqTask extends AsyncTask<Expense, Void, Void> {
        Expense expenseParam;
        Long expenseId;

        @Override
        protected Void doInBackground(Expense... params) {
            expenseParam = params[0];
            expenseId = expenseParam.getId();
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/expense";
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
                expenseParam.setId(null);
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(expenseParam, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Expense.class);
            } catch (Exception e) {
                Log.e("ERROR-ADD-EXPENSE", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            db.deleteExpense(expenseId);
        }
    }
}
