package com.example.expensetracker.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.expensetracker.domain.DeletedRecord;
import com.example.expensetracker.domain.ToDoObjectWithTrip;
import com.example.expensetracker.domain.Trip;
import com.example.expensetracker.domain.User;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MySQLSynchronizer {

    private Context context;
    private DatabaseHelper db;

    public MySQLSynchronizer(Context context, DatabaseHelper db) {
        this.context = context;
        this.db = db;
    }


    public void synchronizeUsers(){
        new GetInsertedUsersReqTask().execute();
        new GetUpdatedUsersReqTask().execute();
    }


    public void synchronizeTrips(){
        new GetInsertedTripsReqTask().execute();
        new GetUpdatedTripsReqTask().execute();
    }

    public void synchronizeNotes(){
        new GetInsertedNoteReqTask().execute();
        new GetUpdatedNoteReqTask().execute();
    }

    public void synchronizeDeleted(){
        new GetDeletedRecordReqTask().execute();
    }


    // Trip

    private class GetInsertedTripsReqTask extends AsyncTask<Void, Void, Trip[]> {

        @Override
        protected Trip[] doInBackground(Void... voids) {

            Trip[] tripsFromDB = {};
            try {
                db =  DatabaseHelper.getInstance(context);
                Date lastSync = db.getLastSyncDB(1);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
                String strDate = dateFormat.format(lastSync);
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/trip";
                apiUrl = apiUrl + "?search=createDate>" + strDate + "\\members:1";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Trip[]> responseEntity = restTemplate.getForEntity(apiUrl, Trip[].class);
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


    private class GetUpdatedTripsReqTask extends AsyncTask<Void, Void, Trip[]> {

        @Override
        protected Trip[] doInBackground(Void... voids) {

            Trip[] tripsFromDB = {};
            try {
                db =  DatabaseHelper.getInstance(context);
                Date lastSync = db.getLastSyncDB(1);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
                String strDate = dateFormat.format(lastSync);
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/trip";
                apiUrl = apiUrl + "?search=modifyDate>" + strDate + "\\members:1";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Trip[]> responseEntity = restTemplate.getForEntity(apiUrl, Trip[].class);
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
                Date lastSync = db.getLastSyncDB(1);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
                String strDate = dateFormat.format(lastSync);
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/user";
                apiUrl = apiUrl + "?search=createDate>" + strDate + "\\id!1";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<User[]> responseEntity = restTemplate.getForEntity(apiUrl, User[].class);
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
                Date lastSync = db.getLastSyncDB(1);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
                String strDate = dateFormat.format(lastSync);
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/user";
                apiUrl = apiUrl + "?search=modifyDate>" + strDate;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<User[]> responseEntity = restTemplate.getForEntity(apiUrl, User[].class);
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
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                currentTrip = restTemplate.getForObject(apiUrl, Trip.class);

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
                Date lastSync = db.getLastSyncDB(1);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
                String strDate = dateFormat.format(lastSync);
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/note";
                apiUrl = apiUrl + "?search=createDate>" + strDate;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<ToDoObjectWithTrip[]> responseEntity = restTemplate.getForEntity(apiUrl, ToDoObjectWithTrip[].class);
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
                db.addNote(notesFromDB[i]);
        }
    }


    private class GetUpdatedNoteReqTask extends AsyncTask<Void, Void, ToDoObjectWithTrip[]> {

        @Override
        protected ToDoObjectWithTrip[] doInBackground(Void... voids) {

            ToDoObjectWithTrip[] notesFromDB = {};
            try {
                db =  DatabaseHelper.getInstance(context);
                Date lastSync = db.getLastSyncDB(1);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
                String strDate = dateFormat.format(lastSync);
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/note";
                apiUrl = apiUrl + "?search=modifyDate>" + strDate;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<ToDoObjectWithTrip[]> responseEntity = restTemplate.getForEntity(apiUrl, ToDoObjectWithTrip[].class);
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
                db.updateNote(notesFromDB[i]);
        }
    }

    // Expense

    // DeletedItems
    private class GetDeletedRecordReqTask extends AsyncTask<Void, Void, DeletedRecord[]> {

        @Override
        protected DeletedRecord[] doInBackground(Void... voids) {

            DeletedRecord[] deletedRecords = {};
            try {
                db =  DatabaseHelper.getInstance(context);
                Date lastSync = db.getLastSyncDB(1);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
                String strDate = dateFormat.format(lastSync);
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/deletedrecord";
                apiUrl = apiUrl + "?search=createDate>" + strDate;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<DeletedRecord[]> responseEntity = restTemplate.getForEntity(apiUrl, DeletedRecord[].class);
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

}
