package com.ocr.francois.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ocr.francois.go4lunch.events.FailureEvent;
import com.ocr.francois.go4lunch.models.User;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Query getAllUsers() {
        return UserHelper.getUsersCollection();
    }

    public static void createUser(String id, String userName, String urlPicture) {
        User userToCreate = new User(id, userName, urlPicture);
        UserHelper.getUsersCollection().document(id).set(userToCreate)
                .addOnFailureListener(e -> EventBus.getDefault().post(new FailureEvent(e.getMessage())));
    }

    public static Task<Void> deleteUser(String id) {
        return UserHelper.getUsersCollection().document(id).delete()
                .addOnFailureListener(e -> EventBus.getDefault().post(new FailureEvent(e.getMessage())));
    }

    public static Task<DocumentSnapshot> getUser(String id) {
        return UserHelper.getUsersCollection().document(id).get()
                .addOnFailureListener(e -> EventBus.getDefault().post(new FailureEvent(e.getMessage())));
    }

    public static void insertLunch(String userId, String lunchRestaurantPlaceId, String lunchRestaurantName) {
        Map<String, Object> lunchData = new HashMap<>();
        lunchData.put("lunchRestaurantPlaceId", lunchRestaurantPlaceId);
        lunchData.put("lunchRestaurantName", lunchRestaurantName);
        lunchData.put("lunchTimestamp", Timestamp.now());
        UserHelper.getUsersCollection()
                .document(userId)
                .update(lunchData)
                .addOnFailureListener(e -> EventBus.getDefault().post(new FailureEvent(e.getMessage())));
    }

    public static void deleteLunch(String userId) {
        Map<String, Object> lunchData = new HashMap<>();
        lunchData.put("lunchRestaurantPlaceId", null);
        lunchData.put("lunchRestaurantName", null);
        lunchData.put("lunchTimestamp", null);
        UserHelper.getUsersCollection()
                .document(userId)
                .update(lunchData)
                .addOnFailureListener(e -> EventBus.getDefault().post(new FailureEvent(e.getMessage())));
    }
}