package com.ocr.francois.go4lunch.api;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ocr.francois.go4lunch.models.Like;

import java.util.Objects;

public class LikeHelper {

    private static final String COLLECTION_NAME = "likes";

    private static CollectionReference getLikesCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Query getAllLikes() {
        return LikeHelper.getLikesCollection();
    }

    public static void createLike(String restaurantPlaceId, String userId) {
        Like likeToCreate = new Like(restaurantPlaceId, userId);
        LikeHelper.getLikesCollection().add(likeToCreate);
    }

    public static void deleteLike(String restaurantPlaceId, String userId) {
        LikeHelper.getLikesCollection()
                .whereEqualTo("restaurantPlaceId", restaurantPlaceId)
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            LikeHelper.getLikesCollection().document(document.getId()).delete();
                        }
                    }
                });
    }

    public static Query getLikesByRestaurant(String placeId) {
        return LikeHelper.getLikesCollection().whereEqualTo("restaurantPlaceId", placeId);
    }
}