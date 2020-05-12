package com.ocr.francois.go4lunch.ui.viewmodels;

import androidx.lifecycle.ViewModel;

import com.ocr.francois.go4lunch.repositories.UserRepository;

public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(String id, String userName, String urlPicture) {
        this.userRepository.createUser(id, userName, urlPicture);
    }
}