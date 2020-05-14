package com.ocr.francois.go4lunch.ui.viewmodels;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.ocr.francois.go4lunch.repositories.LikeRepository;
import com.ocr.francois.go4lunch.repositories.UserRepository;

public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public UserViewModel(UserRepository userRepository, LikeRepository likeRepository) {
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    public void createUser(String id, String userName, String urlPicture) {
        this.userRepository.createUser(id, userName, urlPicture);
    }

    public Task<Void> deleteUser(String id) {
        likeRepository.deleteAllLikesOfUser(id);
        return this.userRepository.deleteUser(id);
    }
}