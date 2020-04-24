package com.ocr.francois.go4lunch.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ocr.francois.go4lunch.api.UserHelper;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.utils.DateTool;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private MutableLiveData<List<User>> users = new MutableLiveData<>();

    public MutableLiveData<List<User>> getUsers() {

        List<User> usersList = new ArrayList<>();

        UserHelper.getAllUsers().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException e) {
                if (querySnapshots != null) {
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        usersList.add(doc.toObject(User.class));
                    }
                    users.postValue(usersList);
                    //users.setValue(usersList);
                } else {
                    Log.d("users", "Error getting documents");
                }
            }
        });
        return users;
    }



    public MutableLiveData<List<User>> getUsersByLunch(String placeId) {
        MutableLiveData<List<User>> usersByLunch = new MutableLiveData<>();

        UserHelper.getUsersCollection()
                .whereEqualTo("lunchRestaurantPlaceId", placeId)
                .whereGreaterThanOrEqualTo("lunchTimestamp", DateTool.getTodayMidnightTimestamp())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException e) {
                        List<User> usersList = new ArrayList<>();

                        if (querySnapshots != null) {
                            for (QueryDocumentSnapshot doc : querySnapshots) {
                                usersList.add(doc.toObject(User.class));
                            }
                            usersByLunch.setValue(usersList);
                        }
                    }
                });
        return usersByLunch;
    }

    public void createUser(String id, String userName, String urlPicture) {
        UserHelper.createUser(id, userName, urlPicture);
    }

    public MutableLiveData<User> getUser(String id) {
        MutableLiveData<User> user = new MutableLiveData<>();

        UserHelper.getUser(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user.setValue(documentSnapshot.toObject(User.class));
            }
        });

        return user;
    }


    public void saveLunch(String userId, String lunchRestaurantPlaceId, String lunchRestaurantName) {
        UserHelper.insertLunch(userId, lunchRestaurantPlaceId, lunchRestaurantName);
    }
}