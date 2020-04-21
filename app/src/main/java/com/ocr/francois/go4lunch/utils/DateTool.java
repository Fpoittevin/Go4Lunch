package com.ocr.francois.go4lunch.utils;

import org.joda.time.DateTime;
import com.google.firebase.Timestamp;

public class DateTool {

    public static Timestamp getTodayMidnightTimestamp() {
        DateTime now = new DateTime();
        DateTime todayMidnight = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0, 0);

        return new com.google.firebase.Timestamp(todayMidnight.toDate());
    }
}