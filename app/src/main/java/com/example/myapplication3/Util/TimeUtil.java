package com.example.myapplication3.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    private static TimeUtil instance;

    private TimeUtil() {
    }

    public static TimeUtil getInstance() {
        if (instance == null) {
            instance = new TimeUtil();
        }
        return instance;
    }

    public String getCurrentDate_1() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月");
        Date date = new Date(System.currentTimeMillis());
        String curDate = formatter.format(date);
        return curDate;
    }

    public String getCurrentDate_2() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        Date date = new Date(System.currentTimeMillis());
        String curDate = formatter.format(date);
        return curDate;
    }



}
