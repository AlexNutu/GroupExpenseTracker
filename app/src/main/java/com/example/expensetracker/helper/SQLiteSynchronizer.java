package com.example.expensetracker.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.expensetracker.domain.DeletedRecord;
import com.example.expensetracker.domain.Expense;
import com.example.expensetracker.domain.Report;
import com.example.expensetracker.domain.ToDoObjectWithTrip;
import com.example.expensetracker.domain.Trip;
import com.example.expensetracker.domain.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SQLiteSynchronizer {

    private Context context;
    private DatabaseHelper db;

    public SQLiteSynchronizer(Context context, DatabaseHelper db) {
        this.context = context;
        this.db = db;
    }

    public void synchronizeSQLite(){
        synchronizeUsers();
        Long userId = db.getLoggedUser().getId();
        synchronizeTrips(userId);
        synchronizeNotes();
        synchronizeDeleted();
        synchronizeExpenses();
        synchronizeReports();
    }

    public void synchronizeUsers(){
        new GetInsertedUsersReqTask().execute();
        new GetUpdatedUsersReqTask().execute();
    }


    public void synchronizeTrips(Long userId){
        new GetInsertedTripsReqTask().execute(userId);
        new GetUpdatedTripsReqTask().execute(userId);
    }

    public void synchronizeNotes(){
        new GetInsertedNoteReqTask().execute();
        new GetUpdatedNoteReqTask().execute();
    }

    public void synchronizeExpenses(){
        new GetInsertedExpenseReqTask().execute();
        new GetUpdatedExpenseReqTask().execute();
    }
    public void synchronizeDeleted(){
        new GetDeletedRecordReqTask().execute();
    }
    public void synchronizeReports(){
        db =  DatabaseHelper.getInstance(context);
        db.deleteAllReports();
        List<Integer> trips = db.getAllTripsId();
        for (int i = 0; i < trips.size(); i++) {
            new GetTripReportTask().execute(trips.get(i));
        }
    }


    // Trip

    private class GetInsertedTripsReqTask extends AsyncTask<Long, Void, Trip[]> {

        @Override
        protected Trip[] doInBackground(Long... params) {

            Long userId = params[0];
            Trip[] tripsFromDB = {};
            try {
                db =  DatabaseHelper.getInstance(context);
                Date lastSync = db.getLastSyncDB(2);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
                String strDate = dateFormat.format(lastSync);
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/trip";
                apiUrl = apiUrl + "?search=createDate>" + strDate + "\\members:"+userId;
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Trip[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, Trip[].class);
                tripsFromDB = responseEntity.getBody();

            } catch (Exception e) {
                Log.e("ERROR-GET-INS-TRIPS", e.getMessage());
            }

            return tripsFromDB;
        }

        @Override
        protected void onPostExecute(Trip[] tripsFromDB) {
            syncInsertedTrips(tripsFromDB);
        }
    }

    public void syncInsertedTrips(final Trip[] tripsFromDB) {
        final String[] tripStrings = new String[tripsFromDB.length];
        for (int i = 0; i < tripsFromDB.length; i++) {
            db.addTrip(tripsFromDB[i]);
            new GetTripReqTask().execute(tripsFromDB[i].getId());
        }
    }


    private class GetUpdatedTripsReqTask extends AsyncTask<Long, Void, Trip[]> {

        @Override
        protected Trip[] doInBackground(Long... params) {

            Long userId = params[0];
            Trip[] tripsFromDB = {};
            try {
                db =  DatabaseHelper.getInstance(context);
                Date lastSync = db.getLastSyncDB(2);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
                String strDate = dateFormat.format(lastSync);
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/trip";
                apiUrl = apiUrl + "?search=modifyDate>" + strDate + "\\members:"+userId;
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Trip[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, Trip[].class);
                tripsFromDB = responseEntity.getBody();

            } catch (Exception e) {
                Log.e("ERROR-GET-UPDATE-TRIPS", e.getMessage());
            }

            return tripsFromDB;
        }

        @Override
        protected void onPostExecute(Trip[] tripsFromDB) {
            syncUpdatedTrips(tripsFromDB);
        }
    }

    public void syncUpdatedTrips(final Trip[] tripsFromDB) {
        for (int i = 0; i < tripsFromDB.length; i++) {
            db.updateTrip(tripsFromDB[i]);
            new GetTripReqTask().execute(tripsFromDB[i].getId());
            }
    }

    // Synchronize Users
    private class GetInsertedUsersReqTask extends AsyncTask<Void, Void, User[]> {

        @Override
        protected User[] doInBackground(Void... voids) {

            User[] usersFromDB = {};
            try {
                db =  DatabaseHelper.getInstance(context);
                Date lastSync = db.getLastSyncDB(2);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
                String strDate = dateFormat.format(lastSync);
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/user";
                apiUrl = apiUrl + "?search=createDate>" + strDate;
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<User[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, User[].class);
                usersFromDB = responseEntity.getBody();

            } catch (Exception e) {
                Log.e("ERROR-GET-INS-USERS", e.getMessage());
            }

            return usersFromDB;
        }

        @Override
        protected void onPostExecute(User[] usersFromDB) {
            syncInsertedUsers(usersFromDB);
        }
    }

    public void syncInsertedUsers(final User[] usersFromDB) {
        for (int i = 0; i < usersFromDB.length; i++) {
            db.addUser(usersFromDB[i]);
        }
    }


    private class GetUpdatedUsersReqTask extends AsyncTask<Void, Void, User[]> {

        @Override
        protected User[] doInBackground(Void... voids) {

            User[] usersFromDB = {};
            try {
                db =  DatabaseHelper.getInstance(context);
                Date lastSync = db.getLastSyncDB(2);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
                String strDate = dateFormat.format(lastSync);
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/user";
                apiUrl = apiUrl + "?search=modifyDate>" + strDate;
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<User[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, User[].class);
                usersFromDB = responseEntity.getBody();

            } catch (Exception e) {
                Log.e("ERROR-GET-UPDATE-USERS", e.getMessage());
            }

            return usersFromDB;
        }

        @Override
        protected void onPostExecute(User[] tripsFromDB) {
            syncUpdatedTrips(tripsFromDB);
        }
    }

    public void syncUpdatedTrips(final User[] usersFromDB) {
        for (int i = 0; i < usersFromDB.length; i++) {
            db.updateUser(usersFromDB[i]);
        }
    }

    // Members
    private class GetTripReqTask extends AsyncTask<Integer, Void, Trip> {

        @Override
        protected Trip doInBackground(Integer... params) {
            Trip currentTrip = new Trip();
            Integer idTrip = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/trip/" + idTrip;
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Trip> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, Trip.class);
                currentTrip = responseEntity.getBody();
            } catch (Exception e) {
                Log.e("ERROR-GET-TRIP", e.getMessage());
            }
            return currentTrip;
        }

        @Override
        protected void onPostExecute(Trip t) {
            syncTripMembers(t);
        }
    }

    private void syncTripMembers(Trip t){
        for(User user:t.getMembers()){
            db.addUserTrip(user.getId().intValue(),t.getId());
        }

    }

    // Note
    private class GetInsertedNoteReqTask extends AsyncTask<Void, Void, ToDoObjectWithTrip[]> {

        @Override
        protected ToDoObjectWithTrip[] doInBackground(Void... voids) {

            ToDoObjectWithTrip[] notesFromDB = {};
            try {
                db =  DatabaseHelper.getInstance(context);
                Date lastSync = db.getLastSyncDB(2);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
                String strDate = dateFormat.format(lastSync);
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/note";
                apiUrl = apiUrl + "?search=createDate>" + strDate;
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<ToDoObjectWithTrip[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, ToDoObjectWithTrip[].class);
                notesFromDB = responseEntity.getBody();

            } catch (Exception e) {
                Log.e("ERROR-GET-INS-NOTES", e.getMessage());
            }

            return notesFromDB;
        }

        @Override
        protected void onPostExecute(ToDoObjectWithTrip[] notesFromDB) {
            syncInsertedNotes(notesFromDB);
        }
    }

    public void syncInsertedNotes(final ToDoObjectWithTrip[] notesFromDB) {
        List<Integer> trips = db.getAllTripsId();
        for (int i = 0; i < notesFromDB.length; i++) {
            if (trips.contains(notesFromDB[i].getTrip().getId()))
                db.addNote(notesFromDB[i],1);
        }
    }


    private class GetUpdatedNoteReqTask extends AsyncTask<Void, Void, ToDoObjectWithTrip[]> {

        @Override
        protected ToDoObjectWithTrip[] doInBackground(Void... voids) {

            ToDoObjectWithTrip[] notesFromDB = {};
            try {
                db =  DatabaseHelper.getInstance(context);
                Date lastSync = db.getLastSyncDB(2);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
                String strDate = dateFormat.format(lastSync);
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/note";
                apiUrl = apiUrl + "?search=modifyDate>" + strDate;
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<ToDoObjectWithTrip[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, ToDoObjectWithTrip[].class);
                notesFromDB = responseEntity.getBody();

            } catch (Exception e) {
                Log.e("ERROR-GET-UPDATE-NOTES", e.getMessage());
            }

            return notesFromDB;
        }

        @Override
        protected void onPostExecute(ToDoObjectWithTrip[] notesFromDB) {
            syncUpdatedNotes(notesFromDB);
        }
    }


    public void syncUpdatedNotes(final ToDoObjectWithTrip[] notesFromDB) {
        List<Integer> trips = db.getAllTripsId();
        for (int i = 0; i < notesFromDB.length; i++) {
            if(trips.contains(notesFromDB[i].getTrip().getId()))
                db.updateNote(notesFromDB[i], 1);
        }
    }

    // Expense
    private class GetInsertedExpenseReqTask extends AsyncTask<Void, Void, Expense[]> {

        @Override
        protected Expense[] doInBackground(Void... voids) {

            Expense[] expensesFromDB = {};
            try {
                db =  DatabaseHelper.getInstance(context);
                Date lastSync = db.getLastSyncDB(2);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
                String strDate = dateFormat.format(lastSync);
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/expense";
                apiUrl = apiUrl + "?search=createDate>" + strDate;
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Expense[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, Expense[].class);
                expensesFromDB = responseEntity.getBody();

            } catch (Exception e) {
                Log.e("ERROR-GET-INS-EXPENSES", e.getMessage());
            }

            return expensesFromDB;
        }

        @Override
        protected void onPostExecute(Expense[] expensesFromDB) {
            syncInsertedExpenses(expensesFromDB);
        }
    }

    public void syncInsertedExpenses(final Expense[] expensesFromDB) {
        List<Integer> trips = db.getAllTripsId();
        for (int i = 0; i < expensesFromDB.length; i++) {
            if (trips.contains(expensesFromDB[i].getTrip().getId()))
                db.addExpense(expensesFromDB[i],1);
        }
    }


    private class GetUpdatedExpenseReqTask extends AsyncTask<Void, Void, Expense[]> {

        @Override
        protected Expense[] doInBackground(Void... voids) {

            Expense[] expensesFromDB = {};
            try {
                db =  DatabaseHelper.getInstance(context);
                Date lastSync = db.getLastSyncDB(2);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
                String strDate = dateFormat.format(lastSync);
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/expense";
                apiUrl = apiUrl + "?search=modifyDate>" + strDate;
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Expense[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, Expense[].class);
                expensesFromDB = responseEntity.getBody();

            } catch (Exception e) {
                Log.e("ERROR-GET-UPD-EXPENSES", e.getMessage());
            }

            return expensesFromDB;
        }

        @Override
        protected void onPostExecute(Expense[] expensesFromDB) {
            syncUpdatedExpenses(expensesFromDB);
        }
    }


    public void syncUpdatedExpenses(final Expense[] expensesFromDB) {
        List<Integer> trips = db.getAllTripsId();
        for (int i = 0; i < expensesFromDB.length; i++) {
            if(trips.contains(expensesFromDB[i].getTrip().getId()))
                db.updateExpense(expensesFromDB[i],1);
        }
    }

    // DeletedItems
    private class GetDeletedRecordReqTask extends AsyncTask<Void, Void, DeletedRecord[]> {

        @Override
        protected DeletedRecord[] doInBackground(Void... voids) {

            DeletedRecord[] deletedRecords = {};
            try {
                db =  DatabaseHelper.getInstance(context);
                Date lastSync = db.getLastSyncDB(2);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
                String strDate = dateFormat.format(lastSync);
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/deletedrecord";
                apiUrl = apiUrl + "?search=createDate>" + strDate;
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<DeletedRecord[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, DeletedRecord[].class);
                deletedRecords = responseEntity.getBody();

            } catch (Exception e) {
                Log.e("ERROR-GET-DELETED", e.getMessage());
            }

            return deletedRecords;
        }

        @Override
        protected void onPostExecute(DeletedRecord[] deletedRecords) {
            syncDeletedRecords(deletedRecords);
        }
    }

    public void syncDeletedRecords(final DeletedRecord[] deletedRecords) {
        for (int i = 0; i < deletedRecords.length; i++) {
            db.deleteRecord(deletedRecords[i]);
        }
    }

    // REPORTS
    private class GetTripReportTask extends AsyncTask<Integer, Void, Expense[]> {

        @Override
        protected Expense[] doInBackground(Integer... params) {
            Expense[] reportedFromDB = {};
            Integer idTrip = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/expense/report/trip/" +  idTrip;
                HttpHeaders requestHeaders = new HttpHeaders();
                Session session = new Session(context);
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Expense[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, Expense[].class);
                reportedFromDB = responseEntity.getBody();
            } catch (Exception e) {
                Log.e("ERROR-GET-REPORTS", e.getMessage());
            }
            for(Expense report:reportedFromDB){
                Trip t = new Trip();
                t.setId(idTrip);
                report.setTrip(t);
            }
            return reportedFromDB;
        }

        @Override
        protected void onPostExecute(Expense[] t) {
            syncReports(t);
        }
    }

    private void syncReports(Expense[] reports){
        for(Expense report:reports){
            db.addReport(report);
        }
    }

}
