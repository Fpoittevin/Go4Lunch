package com.ocr.francois.go4lunch.utils;

public class NoteTool {

    public static int calculateNoteOfRestaurant(int nbOfUsers, int nbOfLikes) {
        float percent = ((float) nbOfLikes * 100 / nbOfUsers);

        if (percent == 0) {
            return 0;
        } else if (percent != 0 && percent < (float) (100 / 3)) {
            return 1;
        } else if (percent < (float) (100 / 3 * 2)) {
            return 2;
        } else {
            return 3;
        }
    }
}
