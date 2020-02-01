package com.example.expensetracker.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.expensetracker.domain.DeletedRecord;
import com.example.expensetracker.domain.Expense;
import com.example.expensetracker.domain.ToDoObjectWithTrip;
import com.example.expensetracker.domain.Trip;
import com.example.expensetracker.domain.User;
import com.example.expensetracker.utils.DateTimeUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "groupExpanseTracker.db";

    // Table Names
    private static final String TABLE_USER_PROFILE = "user_profile";
    private static final String TABLE_TRIP = "trip";
    private static final String TABLE_USER_TRIP = "user_trip";
    private static final String TABLE_EXPENSE = "expense";
    private static final String TABLE_NOTE= "note";
    private static final String TABLE_SYNC_DB = "sync_db";
    private static final String TABLE_LOGGED_USER = "logged_user";
    private static final String TABLE_REPORT = "report";


    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_STATUS = "status";
    private static final String KEY_CREATE_DATE = "create_date";
    private static final String KEY_MODIFY_DATE = "modify_date";

    // USER_PROFILE Table - column nmaes
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";

    // TRIP Table - column names
    private static final String KEY_NAME = "name";
    private static final String KEY_DESTINATION = "destination";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";

    // USER_TRIP Table - column names
    private static final String KEY_TRIP_ID = "trip_id";
    private static final String KEY_USER_ID = "user_id";

    // EXPENSE Table - column names
    private static final String KEY_EXPENSE_TYPE = "expense_type";
    private static final String KEY_SUM = "sum";
    private static final String KEY_CURRENCY = "currency";
    private static final String KEY_PERCENT = "percent";
    private static final String KEY_PRODUCT = "product";

    // NOTE Table - column names
    private static final String KEY_MESSAGE= "message";
    private static final String KEY_APPROVED = "approved";

    // SYNC_DB Table - column names
    private static final String KEY_LAST_SYNC_DATE = "last_sync_date";

    // LOGGED_USER Table - column names
    private static final String KEY_RECEIVE_NOTIFICATION = "receive_notification";

    // Table Create Statements
    // USER_PROFILE table create statement
    private static final String CREATE_TABLE_USER = "CREATE TABLE "
            + TABLE_USER_PROFILE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CREATE_DATE + " DATETIME,"
            + KEY_MODIFY_DATE + " DATETIME,"
            + KEY_EMAIL + " TEXT,"
            + KEY_PASSWORD + " TEXT,"
            + KEY_FIRST_NAME + " TEXT,"
            + KEY_LAST_NAME + " TEXT"
            + KEY_RECEIVE_NOTIFICATION + "NUMERIC"+")";

    // TRIP table create statement
    private static final String CREATE_TABLE_TRIP = "CREATE TABLE " + TABLE_TRIP
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CREATE_DATE + " DATETIME,"
            + KEY_MODIFY_DATE + " DATETIME,"
            + KEY_NAME + " TEXT,"
            + KEY_DESTINATION + " TEXT,"
            + KEY_START_DATE + " DATETIME,"
            + KEY_END_DATE + " DATETIME,"
            + KEY_STATUS + " NUMERIC" + ")";


    // USER_TRIP table create statement
    private static final String CREATE_TABLE_USER_TRIP = "CREATE TABLE " + TABLE_USER_TRIP
            + "(" + KEY_TRIP_ID + " INTEGER,"
            + KEY_USER_ID + " INTEGER,"
            + KEY_STATUS + " NUMERIC,"
            + " PRIMARY KEY(" + KEY_TRIP_ID + "," + KEY_USER_ID + "),"
            + " FOREIGN KEY ("+KEY_TRIP_ID+") REFERENCES "+TABLE_TRIP+"("+KEY_ID+"),"
            + " FOREIGN KEY ("+KEY_USER_ID+") REFERENCES "+TABLE_USER_PROFILE+"("+KEY_ID+"));";



    // EXPENSE table create statement
    private static final String CREATE_TABLE_EXPENSE = "CREATE TABLE " + TABLE_EXPENSE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CREATE_DATE + " DATETIME,"
            + KEY_MODIFY_DATE + " DATETIME,"
            + KEY_EXPENSE_TYPE + " TEXT,"
            + KEY_SUM + " REAL,"
            + KEY_CURRENCY + " TEXT,"
            + KEY_PERCENT + " REAL,"
            + KEY_PRODUCT + " TEXT,"
            + KEY_USER_ID + " INTEGER,"
            + KEY_TRIP_ID + " INTEGER,"
            + KEY_STATUS + " NUMERIC,"
            + " FOREIGN KEY ("+KEY_TRIP_ID+") REFERENCES "+TABLE_TRIP+"("+KEY_ID+"),"
            + " FOREIGN KEY ("+KEY_USER_ID+") REFERENCES "+TABLE_USER_PROFILE+"("+KEY_ID+"));";


    // NOTE table create statement
    private static final String CREATE_TABLE_NOTE = "CREATE TABLE " + TABLE_NOTE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CREATE_DATE + " DATETIME,"
            + KEY_MODIFY_DATE + " DATETIME,"
            + KEY_MESSAGE + " TEXT,"
            + KEY_TRIP_ID + " INTEGER,"
            + KEY_USER_ID + " INTEGER,"
            + KEY_APPROVED + " NUMERIC,"
            + KEY_STATUS + " NUMERIC,"
            + " FOREIGN KEY ("+KEY_TRIP_ID+") REFERENCES "+TABLE_TRIP+"("+KEY_ID+"),"
            + " FOREIGN KEY ("+KEY_USER_ID+") REFERENCES "+TABLE_USER_PROFILE+"("+KEY_ID+"));";


    // NOTIFICATION table create statement
    private static final String CREATE_TABLE_SYNC_DB = "CREATE TABLE " + TABLE_SYNC_DB
            + "(" + KEY_USER_ID + " INTEGER PRIMARY KEY,"
            + KEY_LAST_SYNC_DATE + " DATETIME);";

    //LOGGED_USER table create statement
    private static final String CREATE_TABLE_LOGGED_USER = "CREATE TABLE " + TABLE_LOGGED_USER
            + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_CREATE_DATE + " DATETIME,"
            + KEY_MODIFY_DATE + " DATETIME,"
            + KEY_EMAIL + " TEXT,"
            + KEY_PASSWORD + " TEXT,"
            + KEY_FIRST_NAME + " TEXT,"
            + KEY_LAST_NAME + " TEXT,"
            + KEY_RECEIVE_NOTIFICATION + " NUMERIC,"
            + KEY_STATUS + " NUMERIC"+")";
    private static DatabaseHelper sInstance;

    //REPORT table create statement
    private static final String CREATE_TABLE_REPORT = "CREATE TABLE " + TABLE_REPORT
            + "(" + KEY_TRIP_ID + " INTEGER,"
            + KEY_USER_ID + " INTEGER,"
            + KEY_EXPENSE_TYPE + " TEXT,"
            + KEY_SUM + " REAL,"
            + KEY_CURRENCY + " TEXT,"
            + KEY_PRODUCT + " TEXT" + ")";

    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_TRIP);
        db.execSQL(CREATE_TABLE_EXPENSE);
        db.execSQL(CREATE_TABLE_USER_TRIP);
        db.execSQL(CREATE_TABLE_NOTE);
        db.execSQL(CREATE_TABLE_SYNC_DB);
        db.execSQL(CREATE_TABLE_REPORT);
        db.execSQL(CREATE_TABLE_LOGGED_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_TRIP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYNC_DB);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORT);

        // create new tables
        onCreate(db);
    }


    // SYNC_DB

    // get last synchronization date
    public  Date getLastSyncDB(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SYNC_DB, new String[] {
                        KEY_LAST_SYNC_DATE }, KEY_USER_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        String datetime = null;
        if (cursor != null) {
            if(cursor.moveToNext())
             datetime = cursor.getString(0);
        }
        Date date = null;
        if(datetime != null) {
            DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = iso8601Format.parse(datetime);
            } catch (ParseException e) {
                Log.e(TAG, "Parsing ISO8601 datetime failed", e);
            }
        }
        cursor.close();
        return date;
    }

    public void addOrUpdateSyncDB(Long id) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long dbId = -1;

        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_LAST_SYNC_DATE, DateTimeUtils.getDateTime());

            // updating row
            int rows = db.update(TABLE_SYNC_DB, values, KEY_USER_ID + " = ?",
                    new String[] { String.valueOf(id) });
            db.setTransactionSuccessful();

            // Check if update succeeded
            if (rows != 1) {
                values = new ContentValues();
                values.put(KEY_LAST_SYNC_DATE, DateTimeUtils.getFirstDateTime());
                values.put(KEY_USER_ID, id);

                // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
                db.insertOrThrow(TABLE_SYNC_DB, null, values);
                db.setTransactionSuccessful();

            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update sync date");
        } finally {
            db.endTransaction();
        }

    }


    // TRIP
    // code to add the new trip
    public void addTrip(Trip trip) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, trip.getId());
            values.put(KEY_CREATE_DATE, trip.getCreateDate());
            values.put(KEY_MODIFY_DATE, trip.getModifyDate());
            values.put(KEY_NAME, trip.getName());
            values.put(KEY_DESTINATION, trip.getDestination());
            values.put(KEY_START_DATE, trip.getStartDate());
            values.put(KEY_END_DATE, trip.getEndDate());
            values.put(KEY_STATUS, 1);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_TRIP, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add trip to database");
        } finally {
            db.endTransaction();
        }

    }


    // update Trip
    public void updateTrip(Trip trip) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CREATE_DATE, trip.getCreateDate());
            values.put(KEY_MODIFY_DATE, trip.getModifyDate());
            values.put(KEY_NAME, trip.getName());
            values.put(KEY_DESTINATION, trip.getDestination());
            values.put(KEY_START_DATE, trip.getStartDate());
            values.put(KEY_END_DATE, trip.getEndDate());
            values.put(KEY_STATUS, 1);

           db.update(TABLE_TRIP, values, KEY_ID + " = ?",
                    new String[] { String.valueOf(trip.getId())});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update trip to database");
        } finally {
            db.endTransaction();
        }

    }

    public List<Trip> getAllTrips() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_TRIP ;
        List<Trip> trips = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Trip trip = new Trip();
                trip.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                trip.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                trip.setStartDate(cursor.getString(cursor.getColumnIndex(KEY_START_DATE)));
                trip.setEndDate(cursor.getString(cursor.getColumnIndex(KEY_END_DATE)));
                trip.setDestination(cursor.getString(cursor.getColumnIndex(KEY_DESTINATION)));
                trips.add(trip);
            } while (cursor.moveToNext());
        }

        // close db connection
        cursor.close();

        return trips;
    }

    public List<Integer> getAllTripsId() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT id FROM " + TABLE_TRIP ;
        List<Integer> tids = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                tids.add(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            } while (cursor.moveToNext());
        }

        // close db connection
        cursor.close();

        return tids;
    }

    public Trip getTripById(Integer id){

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_TRIP + " WHERE "
                + KEY_ID + " = " + id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        Trip trip = new Trip();
        if(c != null && c.moveToFirst()) {
            trip.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            trip.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            trip.setDestination(c.getString(c.getColumnIndex(KEY_DESTINATION)));
            trip.setStartDate(c.getString(c.getColumnIndex(KEY_START_DATE)));
            trip.setEndDate(c.getString(c.getColumnIndex(KEY_END_DATE)));
            trip.setCreateDate(c.getString(c.getColumnIndex(KEY_CREATE_DATE)));
        }

        c.close();
        return trip;
    }

    // User
    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_EMAIL, user.getEmail());
            values.put(KEY_FIRST_NAME, user.getFirstName());
            values.put(KEY_LAST_NAME, user.getLastName());
           // values.put(KEY_STATUS, 1);
            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.update(TABLE_TRIP, values, KEY_ID + " = ?",
                    new String[] { String.valueOf(user.getId())});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add user to database");
        } finally {
            db.endTransaction();
        }
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, user.getId());
            values.put(KEY_EMAIL, user.getEmail());
            values.put(KEY_FIRST_NAME, user.getFirstName());
            values.put(KEY_LAST_NAME, user.getLastName());
           // values.put(KEY_STATUS, 1);
            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_USER_PROFILE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add user to database");
        } finally {
            db.endTransaction();
        }
    }


    // USER_TRIP
    public void addUserTrip(Integer userId, Integer tripId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, userId);
            values.put(KEY_TRIP_ID, tripId);
            values.put(KEY_STATUS, 1);
            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_USER_TRIP, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add trip member to database");
        } finally {
            db.endTransaction();
        }
    }



    public ArrayList<User> getTripMembers(Integer tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT id, first_name, last_name, email FROM " + TABLE_USER_PROFILE + " where id in(select user_id from user_trip where trip_id = ?)" ;
        ArrayList<User> members = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(tripId)});

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                User member = new User();
                member.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                member.setFirstName(cursor.getString(cursor.getColumnIndex(KEY_FIRST_NAME)));
                member.setLastName(cursor.getString(cursor.getColumnIndex(KEY_LAST_NAME)));
                member.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                members.add(member);
            } while (cursor.moveToNext());
        }

        // close db connection
        cursor.close();

        return members;
    }

    // DELETE RECORDS
    public void deleteRecord(DeletedRecord record){

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(record.getTableName(), KEY_ID + " = ?",
                    new String[] { String.valueOf(record.getRecordId())});

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete records");
        } finally {
            db.endTransaction();
        }
    }


    // NOTE

    public Integer getNoteStatusById(Long noteId){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTE, new String[] {
                        KEY_STATUS }, KEY_ID + "=?",
                new String[] { String.valueOf(noteId) }, null, null, null, null);

        Integer status = null;

        if (cursor != null) {
            if(cursor.moveToNext())
                status = cursor.getInt(0);
        }
        cursor.close();

        return status;
    }

    public void addNote(ToDoObjectWithTrip note, Integer status) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, note.getUser().getId());
            values.put(KEY_TRIP_ID, note.getTrip().getId());
            if(status==1)
                values.put(KEY_ID, note.getId());
            values.put(KEY_MESSAGE, note.getMessage());
            values.put(KEY_CREATE_DATE, note.getCreateDate());
            values.put(KEY_MODIFY_DATE, note.getModifyDate());
            values.put(KEY_MESSAGE, note.getMessage());
            values.put(KEY_APPROVED, note.getApproved());
            values.put(KEY_STATUS, status);
            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_NOTE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add note to database");
        } finally {
            db.endTransaction();
        }
    }

    public void updateNote(ToDoObjectWithTrip note, Integer status) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        Integer currentStatus = getNoteStatusById(note.getId());
        if(currentStatus == 0)
            status = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, note.getUser().getId());
            values.put(KEY_MESSAGE, note.getMessage());
            values.put(KEY_CREATE_DATE, note.getCreateDate());
            values.put(KEY_MODIFY_DATE, note.getModifyDate());
            values.put(KEY_MESSAGE, note.getMessage());
            values.put(KEY_APPROVED, note.getApproved());
            values.put(KEY_STATUS, status);
            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.update(TABLE_NOTE, values, KEY_ID + " = ?",
                    new String[] { String.valueOf(note.getId())});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update note to database");
        } finally {
            db.endTransaction();
        }
    }


    public ArrayList<ToDoObjectWithTrip> getTripNotesList(Integer tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select n.id, n.create_date, n.modify_date, n.message, n.approved, n.trip_id,u.first_name, u.last_name, u.email, n.user_id\n" +
                " from note n, user_profile u \n" +
                " where n.user_id = u.id and n.status<>3 and n.trip_id = ?" ;
        ArrayList<ToDoObjectWithTrip> notes = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(tripId)});

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)));
                user.setFirstName(cursor.getString(cursor.getColumnIndex(KEY_FIRST_NAME)));
                user.setLastName(cursor.getString(cursor.getColumnIndex(KEY_LAST_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                Trip trip = new Trip();
                trip.setId(cursor.getInt(cursor.getColumnIndex(KEY_TRIP_ID)));
                ToDoObjectWithTrip note = new ToDoObjectWithTrip();
                note.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                note.setCreateDate(cursor.getString(cursor.getColumnIndex(KEY_CREATE_DATE)));
                note.setModifyDate(cursor.getString(cursor.getColumnIndex(KEY_MODIFY_DATE)));
                note.setMessage(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE)));
                note.setApproved(cursor.getInt(cursor.getColumnIndex(KEY_APPROVED))!=0);
                note.setUser(user);
                note.setTrip(trip);
                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        cursor.close();

        return notes;
    }

    public ArrayList<ToDoObjectWithTrip> getNotesByStatus(Integer status) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select n.id, n.create_date, n.modify_date, n.message, n.approved, n.trip_id, n.user_id\n" +
                " from note n \n" +
                " where n.status = ?" ;
        ArrayList<ToDoObjectWithTrip> notes = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(status)});

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)));
                Trip trip = new Trip();
                trip.setId(cursor.getInt(cursor.getColumnIndex(KEY_TRIP_ID)));
                ToDoObjectWithTrip note = new ToDoObjectWithTrip();
                note.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                note.setCreateDate(cursor.getString(cursor.getColumnIndex(KEY_CREATE_DATE)));
                note.setModifyDate(cursor.getString(cursor.getColumnIndex(KEY_MODIFY_DATE)));
                note.setMessage(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE)));
                note.setApproved(cursor.getInt(cursor.getColumnIndex(KEY_APPROVED))!=0);
                note.setUser(user);
                note.setTrip(trip);
                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        cursor.close();

        return notes;
    }

    public void deleteNote(Long noteId){
        String sql;
        if(getNoteStatusById(noteId)==0)
             sql = "delete from " + TABLE_NOTE + " where id= ?";
        else
            sql = "update note set status = 3 where id= ?";
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.beginTransaction();
            db.execSQL(sql,new String[]{String.valueOf(noteId)});
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.d(TAG, "Error while trying to delete note from database");
        }finally {
            db.endTransaction();
        }
    }

    // Expense
    public void addExpense(Expense expense, Integer status) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, expense.getUser().getId());
            values.put(KEY_TRIP_ID, expense.getTrip().getId());
            if(status == 1)
                values.put(KEY_ID, expense.getId());
            values.put(KEY_EXPENSE_TYPE, expense.getExpensiveType());
            values.put(KEY_CREATE_DATE, expense.getCreateDate());
            values.put(KEY_MODIFY_DATE, expense.getModifyDate());
            values.put(KEY_SUM, expense.getSum());
            values.put(KEY_CURRENCY, expense.getCurrency());
            values.put(KEY_PERCENT, expense.getPercent());
            values.put(KEY_PRODUCT, expense.getProduct());
            values.put(KEY_STATUS, status);
            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_EXPENSE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add expense to database");
        } finally {
            db.endTransaction();
        }
    }

    public void updateExpense(Expense expense, Integer status) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, expense.getUser().getId());
            values.put(KEY_TRIP_ID, expense.getTrip().getId());
            values.put(KEY_EXPENSE_TYPE, expense.getExpensiveType());
            values.put(KEY_CREATE_DATE, expense.getCreateDate());
            values.put(KEY_MODIFY_DATE, expense.getModifyDate());
            values.put(KEY_SUM, expense.getSum());
            values.put(KEY_CURRENCY, expense.getCurrency());
            values.put(KEY_PERCENT, expense.getPercent());
            values.put(KEY_PRODUCT, expense.getProduct());
            values.put(KEY_STATUS, status);
            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.update(TABLE_EXPENSE, values, KEY_ID + " = ?",
                    new String[] { String.valueOf(expense.getId())});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update expense to database");
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Expense> getExpenseReportList(Integer tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select  e.*, u.last_name, u.first_name, u.email from expense e, user_profile u where e.user_id = u.id and trip_id = ?" ;
        String sql2 = "select  e.*, u.last_name, u.first_name, u.email from expense e, user_profile u where e.user_id = u.id and status = 0" ;
        ArrayList<Expense> expenses = new ArrayList<>();

        Cursor cursor;
        if(tripId!= null)
             cursor = db.rawQuery(sql, new String[]{String.valueOf(tripId)});
        else
            cursor = db.rawQuery(sql2,new String[]{});

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)));
                user.setFirstName(cursor.getString(cursor.getColumnIndex(KEY_FIRST_NAME)));
                user.setLastName(cursor.getString(cursor.getColumnIndex(KEY_LAST_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                Expense expense = new Expense();
                expense.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                expense.setCreateDate(cursor.getString(cursor.getColumnIndex(KEY_CREATE_DATE)));
                expense.setModifyDate(cursor.getString(cursor.getColumnIndex(KEY_MODIFY_DATE)));
                expense.setCurrency(cursor.getString(cursor.getColumnIndex(KEY_CURRENCY)));
                expense.setExpensiveType(cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_TYPE)));
                expense.setSum(cursor.getFloat(cursor.getColumnIndex(KEY_SUM)));
                expense.setProduct(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT)));
                expense.setPercent(cursor.getFloat(cursor.getColumnIndex(KEY_PERCENT)));
                expense.setUser(user);
                Trip trip = new Trip();
                trip.setId(cursor.getInt(cursor.getColumnIndex(KEY_TRIP_ID)));
                expense.setTrip(trip);
                expenses.add(expense);
            } while (cursor.moveToNext());
        }

        // close db connection
        cursor.close();

        return expenses;
    }

    public void deleteExpense(Long expenseId){
        String sql = "delete from " + TABLE_EXPENSE + " where id= ?";
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.beginTransaction();
            db.execSQL(sql,new String[]{String.valueOf(expenseId)});
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.d(TAG, "Error while trying to delete expense from database");
        }finally {
            db.endTransaction();
        }
    }


    // Report
    public void deleteAllReports(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "delete from report";

        try{
            db.beginTransaction();
            db.execSQL(sql);
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.d(TAG, "Error while trying to delete reports from database");
        }finally {
            db.endTransaction();
        }
    }

    public void addReport(Expense report) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, report.getUser().getId());
            values.put(KEY_TRIP_ID, report.getTrip().getId());
            values.put(KEY_EXPENSE_TYPE, report.getExpensiveType());
            values.put(KEY_SUM, report.getSum());
            values.put(KEY_CURRENCY, report.getCurrency());
            values.put(KEY_PRODUCT, report.getProduct());
            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_REPORT, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add expense to database");
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Expense> getUnperformedReportList(Integer tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select  e.*, u.last_name, u.first_name, u.email from report e, user_profile u where e.user_id = u.id and trip_id = ?" ;
        ArrayList<Expense> expenses = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(tripId)});

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)));
                user.setFirstName(cursor.getString(cursor.getColumnIndex(KEY_FIRST_NAME)));
                user.setLastName(cursor.getString(cursor.getColumnIndex(KEY_LAST_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                Expense expense = new Expense();
                expense.setCurrency(cursor.getString(cursor.getColumnIndex(KEY_CURRENCY)));
                expense.setExpensiveType(cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_TYPE)));
                expense.setSum(cursor.getFloat(cursor.getColumnIndex(KEY_SUM)));
                expense.setProduct(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT)));
                expense.setUser(user);
                expenses.add(expense);
            } while (cursor.moveToNext());
        }

        // close db connection
        cursor.close();

        return expenses;
    }

    //LOGGED_USER
    public void addLoggedUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, user.getId());
            values.put(KEY_EMAIL, user.getEmail());
            values.put(KEY_FIRST_NAME, user.getFirstName());
            values.put(KEY_LAST_NAME, user.getLastName());
            values.put(KEY_RECEIVE_NOTIFICATION, user.getReceiveNotifications());
            values.put(KEY_STATUS, 1);
            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_LOGGED_USER, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add user to database");
        } finally {
            db.endTransaction();
        }
    }

    public User getLoggedUser(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGGED_USER;

        Cursor c = db.rawQuery(selectQuery, null);

        User loggedUser = new User();
        if(c != null && c.moveToFirst()) {
            loggedUser.setId(c.getLong(c.getColumnIndex(KEY_ID)));
            loggedUser.setFirstName(c.getString(c.getColumnIndex(KEY_FIRST_NAME)));
            loggedUser.setLastName(c.getString(c.getColumnIndex(KEY_LAST_NAME)));
            loggedUser.setEmail(c.getString(c.getColumnIndex(KEY_EMAIL)));
            loggedUser.setReceiveNotifications(c.getInt(c.getColumnIndex(KEY_RECEIVE_NOTIFICATION))!=0);
            loggedUser.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
        }

        c.close();
        return loggedUser;
    }

    public void updateLoggedUser(User loggedUser){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_RECEIVE_NOTIFICATION, loggedUser.getReceiveNotifications());
            values.put(KEY_STATUS, 2);
            db.update(TABLE_LOGGED_USER, values, KEY_ID + " = ?", new String[] { String.valueOf(loggedUser.getId())});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update logged user to database");
        } finally {
            db.endTransaction();
        }
    }

    // Delete all
    public void deleteAllRecords()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String delete_expense = "delete from expense";
        String delete_user_trip = "delete from user_trip";
        String delete_note = "delete from note";
        String delete_report = "delete from report";
        String delete_trip = "delete from trip";
        String delete_logged = "delete from logged_user";
        String delete_user = "delete from user_profile";
        String delete_sync_db = "delete from sync_db";

        try {
            db.beginTransaction();
            db.execSQL(delete_user_trip);
            db.execSQL(delete_expense);
            db.execSQL(delete_report);
            db.execSQL(delete_note);
            db.execSQL(delete_trip);
            db.execSQL(delete_logged);
            db.execSQL(delete_user);
            db.execSQL(delete_sync_db);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all records from database");
        } finally {
            db.endTransaction();
        }
    }
}