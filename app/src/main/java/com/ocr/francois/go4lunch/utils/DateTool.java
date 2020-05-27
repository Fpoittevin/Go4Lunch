package com.ocr.francois.go4lunch.utils;

import com.google.firebase.Timestamp;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

public class DateTool {

    public static Timestamp getTodayMidnightTimestamp() {
        DateTime now = new DateTime();
        DateTime todayMidnight = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0, 0);

        return new Timestamp(todayMidnight.toDate());
    }

    public static boolean isToday(Timestamp timestamp) {
        DateTime now = new DateTime();
        DateTime dateToCompare = new DateTime(timestamp.toDate());
        return DateTimeComparator.getDateOnlyInstance().compare(now, dateToCompare) == 0;
    }
}