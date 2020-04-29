package com.ocr.francois.go4lunch.ui.viewmodels;

import android.location.Location;

import androidx.annotation.Nullable;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.ocr.francois.go4lunch.models.Like;
import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.repositories.LikeRepository;
import com.ocr.francois.go4lunch.repositories.RestaurantRepository;
import com.ocr.francois.go4lunch.repositories.UserRepository;
import com.ocr.francois.go4lunch.utils.DateTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LunchViewModel extends ViewModel {

    private RestaurantRepository restaurantRepository;
    private UserRepository userRepository;
    private LikeRepository likeRepository;

    public LunchViewModel(RestaurantRepository restaurantRepository, UserRepository userRepository, LikeRepository likeRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    // RESTAURANTS

    public MutableLiveData<List<Restaurant>> getRestaurants(Location location, int radius) {
        return restaurantRepository.getRestaurants(location, radius);
    }

    public MutableLiveData<Restaurant> getRestaurant(String placeId) {

        MediatorLiveData<Restaurant> observableRestaurant = new MediatorLiveData<>();

        observableRestaurant.addSource(getRestaurant(placeId), new Observer<Restaurant>() {
            @Override
            public void onChanged(Restaurant restaurant) {
                observableRestaurant.setValue(restaurant);
            }
        });
        observableRestaurant.addSource(userRepository.getUsersByLunch(placeId), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                Objects.requireNonNull(observableRestaurant.getValue()).setParticipants(users);
            }
        });
        observableRestaurant.addSource(getNote(placeId), new Observer<NoteGenerator>() {
            @Override
            public void onChanged(NoteGenerator noteGenerator) {
                observableRestaurant.getValue().setNote(noteGenerator.getNote());
            }
        });

        return observableRestaurant;
    }


    public MutableLiveData<NoteGenerator> getNote(String placeId) {
        MediatorLiveData<NoteGenerator> noteGenerator = new MediatorLiveData<>();
        noteGenerator.setValue(new NoteGenerator());

        noteGenerator.addSource(userRepository.getUsers(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                noteGenerator.getValue().setNbUsers(users.size());
            }
        });

        noteGenerator.addSource(getLikesByRestaurant(placeId), new Observer<List<Like>>() {
            @Override
            public void onChanged(List<Like> likes) {
                noteGenerator.getValue().setNbLikes(likes.size());
            }
        });

        return noteGenerator;
    }

    private int calculateNoteOfRestaurant(int nbLikes, int nbUsers) {
        float percent = ((float) nbLikes * 100 / nbUsers);

        if (percent == 0) {
            return 0;
        } else if (percent != 0 && percent < (float) (100 / 3)) {
            return 1;
        } else if (percent < (float) (100 / 3 * 2)) {
            return 2;
        } else {
            return 3;
        }
    }

    public MutableLiveData<List<User>> getUsers() {
        return userRepository.getUsers();
    }

    // USERS

    public MutableLiveData<User> getCurrentUserInFirestore(String id) {
        return userRepository.getUser(id);
    }

    public int getNumbersOfUsersByRestaurant(Restaurant restaurant, List<User> users) {

        return getWorkmatesByRestaurant(restaurant, users, false, null).size();
    }

    // LUNCHES

    public List<User> getWorkmatesByRestaurant(Restaurant restaurant, List<User> users, Boolean includeCurrentUser, @Nullable String currentUserId) {

        List<User> workmatesByRestaurant = new ArrayList<>();

        if (users != null && !users.isEmpty()) {
            for (User user : users) {

                if (user.getLunchTimestamp() != null && user.getLunchRestaurantPlaceId() != null) {
                    if (DateTool.isToday(user.getLunchTimestamp()) && user.getLunchRestaurantPlaceId().equals(restaurant.getPlaceId())) {

                        if (!includeCurrentUser && user.getId().equals(currentUserId)) {
                            continue;
                        }
                        workmatesByRestaurant.add(user);
                    }
                }
            }
        }
        return workmatesByRestaurant;
    }

    private void addParticipantInRestaurant(Restaurant restaurant, List<User> users) {
        restaurant.getParticipants().clear();
        restaurant.setParticipants(getWorkmatesByRestaurant(restaurant, users, true, null));
    }

    public void addParticipantsInAllRestaurants(List<Restaurant> restaurants, List<User> users) {
        if (restaurants != null && !restaurants.isEmpty()) {
            for (Restaurant restaurant : restaurants) {
                addParticipantInRestaurant(restaurant, users);
            }
        }
    }

    public void saveLunch(String userId, String lunchRestaurantPlaceId, String lunchRestaurantName) {
        userRepository.saveLunch(userId, lunchRestaurantPlaceId, lunchRestaurantName);
    }

    public void createLike(String restaurantPlaceId, String userId) {
        likeRepository.createLike(restaurantPlaceId, userId);
    }

    // LIKES

    public MutableLiveData<List<Like>> getAllLikes() {
        return likeRepository.getAllLikes();
    }

    public MutableLiveData<List<Like>> getLikesByRestaurant(String placeId) {
        return likeRepository.getLikesByRestaurant(placeId);
    }

    public void addNotesInAllRestaurants(List<Restaurant> restaurants, List<Like> likes, int nbUsers) {
        for (Restaurant restaurant : restaurants) {
            addNoteInRestaurant(restaurant, likes, nbUsers);
        }
    }

    public void addNoteInRestaurant(Restaurant restaurant, List<Like> likes, int nbUsers) {
        int nbLikes = 0;

        for (Like like : likes) {
            if (like.getRestaurantPlaceId().equals(restaurant.getPlaceId())) {
                ++nbLikes;
            }
        }

        restaurant.setNote(getNoteOfRestaurant(nbLikes, nbUsers));
    }

    private int getNoteOfRestaurant(int nbLikes, int nbUsers) {
        float percent = ((float) nbLikes * 100 / nbUsers);

        if (percent == 0) {
            return 0;
        } else if (percent != 0 && percent < (float) (100 / 3)) {
            return 1;
        } else if (percent < (float) (100 / 3 * 2)) {
            return 2;
        } else {
            return 3;
        }
    }

    class NoteGenerator {
        int nbUsers = 0;
        int nbLikes = 0;

        public void setNbUsers(int nbUsers) {
            this.nbUsers = nbUsers;
        }

        public void setNbLikes(int nbLikes) {
            this.nbLikes = nbLikes;
        }

        public int getNote() {
            float percent = ((float) nbLikes * 100 / nbUsers);

            if (percent == 0) {
                return 0;
            } else if (percent != 0 && percent < (float) (100 / 3)) {
                return 1;
            } else if (percent < (float) (100 / 3 * 2)) {
                return 2;
            } else {
                return 3;
            }
        }
    }
}
