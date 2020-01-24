package com.example.expensetracker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {

    /**
     * get datetime
     * */
    public static String getFirstDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date currentDate  = new Date();
        Date oldDate  = new Date();
        oldDate.setDate(currentDate.getDate() -180);

        return dateFormat.format(oldDate);
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date currentDate  = new Date();
        return dateFormat.format(currentDate);
    }
}
