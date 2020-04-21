package com.ocr.francois.go4lunch.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private MutableLiveData<List<User>> users;

    public MutableLiveData<List<User>> getUsers() {

        if (users == null) {
            users = new MutableLiveData<>();
        }

        List<User> usersList = new ArrayList<>();

        UserHelper.getAllUsers().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        usersList.add(document.toObject(User.class));
                    }
                    users.setValue(usersList);
                } else {
                    Log.d("users", "Error getting documents: ", task.getException());
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

                        if (!querySnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot doc : querySnapshots) {
                                usersList.add(doc.toObject(User.class));
                            }
                            usersByLunch.setValue(usersList);
                        }
                    }
                });
        return usersByLunch;
    }
}
