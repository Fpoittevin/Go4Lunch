package com.ocr.francois.go4lunch.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ocr.francois.go4lunch.api.UserHelper;
import com.ocr.francois.go4lunch.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public MutableLiveData<List<User>> getUsers() {

        MutableLiveData<List<User>> users = new MutableLiveData<>();
        List<User> usersList = new ArrayList<>();

        UserHelper.getAllUsers().addSnapshotListener((querySnapshots, e) -> {
            usersList.clear();
            if (querySnapshots != null) {
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    usersList.add(doc.toObject(User.class));
                }
                users.setValue(usersList);
            } else {
                Log.d("users", "Error getting documents");
            }
        });

        return users;
    }

    public void createUser(String id, String userName, String urlPicture) {
        UserHelper.createUser(id, userName, urlPicture);
    }

    public MutableLiveData<User> getUser(String id) {
        MutableLiveData<User> user = new MutableLiveData<>();

        UserHelper.getUsersCollection().document(id).addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null) {
                user.setValue(documentSnapshot.toObject(User.class));
            }
        });
        return user;
    }

    public void saveLunch(String userId, String lunchRestaurantPlaceId, String lunchRestaurantName) {
        UserHelper.insertLunch(userId, lunchRestaurantPlaceId, lunchRestaurantName);
    }

    public void deleteLunch(String userId) {
        UserHelper.deleteLunch(userId);
    }
}