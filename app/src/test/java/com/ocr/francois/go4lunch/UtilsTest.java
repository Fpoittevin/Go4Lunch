package com.ocr.francois.go4lunch;

import com.google.firebase.Timestamp;
import com.ocr.francois.go4lunch.utils.DateTool;
import com.ocr.francois.go4lunch.utils.NoteTool;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class UtilsTest {

    @Test
    public void dateIsToday() {
        DateTime now = new DateTime();
        Timestamp nowTimestamp = new Timestamp(now.toDate());
        assertTrue(DateTool.isToday(nowTimestamp));
    }

    @Test
    public void nowIsAfterTodayMidnight() {
        DateTime now = new DateTime();
        Timestamp nowTimestamp = new Timestamp(now.toDate());
        assertEquals(1, nowTimestamp.compareTo(DateTool.getTodayMidnightTimestamp()));
    }

    @Test
    public void noteTest() {
        int nbUsers = 100;
        int nbLikes;

        nbLikes = 0;
        assertEquals(0, NoteTool.calculateNoteOfRestaurant(nbUsers, nbLikes));

        nbLikes = 20;
        assertEquals(1, NoteTool.calculateNoteOfRestaurant(nbUsers, nbLikes));

        nbLikes = 50;
        assertEquals(2, NoteTool.calculateNoteOfRestaurant(nbUsers, nbLikes));

        nbLikes = 80;
        assertEquals(3, NoteTool.calculateNoteOfRestaurant(nbUsers, nbLikes));
    }
}