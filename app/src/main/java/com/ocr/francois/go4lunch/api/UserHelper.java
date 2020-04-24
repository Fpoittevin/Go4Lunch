package com.ocr.francois.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ocr.francois.go4lunch.models.User;

import java.util.HashMap;
import java.util.Map;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Query getAllUsers() {
        return UserHelper.getUsersCollection();
    }

    public static Task<Void> createUser(String id, String userName, String urlPicture) {
        User userToCreate = new User(id, userName, urlPicture);
        return UserHelper.getUsersCollection().document(id).set(userToCreate);
    }

    public static Task<DocumentSnapshot> getUser(String id) {
        return UserHelper.getUsersCollection().document(id).get();
    }

    public static Task<Void> insertLunch(String userId, String lunchRestaurantPlaceId, String lunchRestaurantName) {
        Map<String, Object> lunchData = new HashMap<>();
        lunchData.put("lunchRestaurantPlaceId", lunchRestaurantPlaceId);
        lunchData.put("lunchRestaurantName", lunchRestaurantName);
        lunchData.put("lunchTimestamp", Timestamp.now());
        return UserHelper.getUsersCollection().document(userId).update(lunchData);
    }
}