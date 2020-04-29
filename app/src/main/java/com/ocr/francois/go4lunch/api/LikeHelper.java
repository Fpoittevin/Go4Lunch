package com.ocr.francois.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ocr.francois.go4lunch.models.Like;

public class LikeHelper {

    private static final String COLLECTION_NAME = "likes";

    public static CollectionReference getLikesCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Query getAllLikes() {
        return LikeHelper.getLikesCollection();
    }

    public static Task<DocumentReference> createLike(String restaurantPlaceId, String userId) {
        Like likeToCreate = new Like(restaurantPlaceId, userId);
        return LikeHelper.getLikesCollection().add(likeToCreate);
    }

    public static Query getLikesByRestaurant(String placeId) {
        return LikeHelper.getLikesCollection().whereEqualTo("restaurantPlaceId", placeId);
    }
}