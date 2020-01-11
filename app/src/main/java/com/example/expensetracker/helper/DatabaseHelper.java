package com.example.expensetracker.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private static final String TABLE_NOTIFICATION= "notification";


    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_STATUS = "status";
    private static final String KEY_CREATE_DATE = "create_date";
    private static final String KEY_MODIFY_DATE = "modify_date";

    // USER_PROFILE Table - column nmaes
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name ";

    // TRIP Table - column names
    private static final String KEY_NAME = "name";
    private static final String KEY_DESTINATION = "destination";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";
    //private static final String KEY_FOUNDER_ID = "founder_id ";

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


    // NOTIFICATION Table - column names
    private static final String KEY_TITLE = "title";
    private static final String KEY_SENT = "sent";


    // Table Create Statements
    // USER_PROFILE table create statement
    private static final String CREATE_TABLE_USER = "CREATE TABLE "
            + TABLE_USER_PROFILE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CREATE_DATE + " DATETIME,"
            + KEY_MODIFY_DATE + " DATETIME,"
            + KEY_EMAIL + " TEXT,"
            + KEY_PASSWORD + " TEXT,"
            + KEY_FIRST_NAME + " TEXT,"
            + KEY_LAST_NAME + " TEXT" + ")";

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
            + KEY_STATUS + " NUMERIC,"
            + " FOREIGN KEY ("+KEY_TRIP_ID+") REFERENCES "+TABLE_TRIP+"("+KEY_ID+"),"
            + " FOREIGN KEY ("+KEY_USER_ID+") REFERENCES "+TABLE_USER_PROFILE+"("+KEY_ID+"));";

    // NOTIFICATION table create statement
    private static final String CREATE_TABLE_NOTIFICATION = "CREATE TABLE " + TABLE_NOTIFICATION
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CREATE_DATE + " DATETIME,"
            + KEY_MODIFY_DATE + " DATETIME,"
            + KEY_TITLE + " TEXT,"
            + KEY_MESSAGE + " TEXT,"
            + KEY_USER_ID + " INTEGER,"
            + KEY_SENT + " NUMERIC,"
            + KEY_STATUS + " NUMERIC,"
            + " FOREIGN KEY ("+KEY_USER_ID+") REFERENCES "+TABLE_USER_PROFILE+"("+KEY_ID+"));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_TRIP);
        db.execSQL(CREATE_TABLE_EXPENSE);
        db.execSQL(CREATE_TABLE_USER_TRIP);
        db.execSQL(CREATE_TABLE_NOTE);
        db.execSQL(CREATE_TABLE_NOTIFICATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_TRIP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);

        // create new tables
        onCreate(db);
    }


    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) db.close();
    }

    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}