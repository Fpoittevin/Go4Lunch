package com.ocr.francois.go4lunch.repositories;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

    public MutableLiveData<Boolean> userLikeRestaurant(String userId, String restaurantId) {
        MutableLiveData<Boolean> userLikeRestaurant = new MutableLiveData<>();

        LikeHelper.getLikesByRestaurant(restaurantId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException e) {
                        for (QueryDocumentSnapshot doc : querySnapshots) {
                            Like like = doc.toObject(Like.class);
                            Log.e("LIKE", like.getRestaurantPlaceId() + " / " + like.getUserId());
                        }

                    }
                });

        return userLikeRestaurant;
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