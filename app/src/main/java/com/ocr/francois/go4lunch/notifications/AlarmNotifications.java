package com.ocr.francois.go4lunch.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmNotifications {

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    public AlarmNotifications(Context context) {
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        this.alarmIntent = PendingIntent.getBroadcast(context, 4321, intent, 0);
    }

    public void start() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    public void stop() {
        if (alarmManager != null) alarmManager.cancel(alarmIntent);
    }
}