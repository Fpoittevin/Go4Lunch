package com.ocr.francois.go4lunch.repositories;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ocr.francois.go4lunch.api.LikeHelper;
import com.ocr.francois.go4lunch.models.Like;

import java.util.ArrayList;
import java.util.List;

public class LikeRepository {

    public void createLike(String restaurantPlaceId, String userId) {
        LikeHelper.createLike(restaurantPlaceId, userId);
    }

    public void deleteLike(String userId, String restaurantId) {
        LikeHelper.deleteLike(userId, restaurantId);
    }

    public MutableLiveData<List<Like>> getAllLikes() {
        MutableLiveData<List<Like>> likes = new MutableLiveData<>();
        List<Like> likesList = new ArrayList<>();

        LikeHelper.getAllLikes().addSnapshotListener((querySnapshots, e) -> {
            likesList.clear();
            if (querySnapshots != null) {
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    likesList.add(doc.toObject(Like.class));
                }
                likes.setValue(likesList);
            }
        });
        return likes;
    }

    public MutableLiveData<List<Like>> getLikesByRestaurant(String placeId) {
        MutableLiveData<List<Like>> likes = new MutableLiveData<>();
        List<Like> likesList = new ArrayList<>();

        LikeHelper.getLikesByRestaurant(placeId).addSnapshotListener((querySnapshots, e) -> {
            likesList.clear();
            if (querySnapshots != null) {
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    likesList.add(doc.toObject(Like.class));
                }
                likes.setValue(likesList);
            }
        });
        return likes;
    }
}