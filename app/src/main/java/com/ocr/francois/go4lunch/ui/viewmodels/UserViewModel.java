package com.ocr.francois.go4lunch.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.repositories.UserRepository;

import java.util.List;

public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MutableLiveData<List<User>> getWorkmates() {
        //TODO: tous les users sauf current user;
        return userRepository.getUsers();
    }
}