package com.ocr.francois.go4lunch.ui.viewmodels;

import android.location.Location;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ocr.francois.go4lunch.models.Like;
import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.repositories.LikeRepository;
import com.ocr.francois.go4lunch.repositories.RestaurantRepository;
import com.ocr.francois.go4lunch.repositories.UserRepository;
import com.ocr.francois.go4lunch.utils.DateTool;
import com.ocr.francois.go4lunch.utils.NoteTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return new RestaurantsListLiveData(location, radius);
    }

    public MutableLiveData<Restaurant> getRestaurant(String placeId) {
        return new RestaurantMediatorLiveData(placeId);
    }

    // USERS

    public MutableLiveData<List<User>> getUsers() {
        return userRepository.getUsers();
    }

    public MutableLiveData<User> getCurrentUserInFirestore(String id) {
        return userRepository.getUser(id);
    }

    // LUNCHES

    public void saveLunch(String userId, String lunchRestaurantPlaceId, String lunchRestaurantName) {
        userRepository.saveLunch(userId, lunchRestaurantPlaceId, lunchRestaurantName);
    }

    public void deleteLunch(String userId) {
        userRepository.deleteLunch(userId);
    }

    // LIKES

    public void createLike(String restaurantPlaceId, String userId) {
        likeRepository.createLike(restaurantPlaceId, userId);
    }

    public void deleteLike(String restaurantPlaceId, String userId) {
        likeRepository.deleteLike(restaurantPlaceId, userId);
    }

    public MutableLiveData<List<Like>> getLikesByRestaurant(String restaurantPlaceId) {
        return likeRepository.getLikesByRestaurant(restaurantPlaceId);
    }

    // MEDIATOR LIVE DATA CLASSES

    class RestaurantsListLiveData extends MediatorLiveData<List<Restaurant>> {

        private List<Restaurant> restaurants = new ArrayList<>();
        private List<User> users = new ArrayList<>();
        private HashMap<String, Integer> notes = new HashMap<>();

        RestaurantsListLiveData(Location location, int radius) {
            this.addSource(restaurantRepository.getRestaurants(location, radius), restaurantsList -> {
                restaurants.clear();
                restaurants.addAll(restaurantsList);
                generateRestaurantsList();
            });
            this.addSource(userRepository.getUsers(), usersList -> {
                users = usersList;
                generateRestaurantsList();
            });
            this.addSource(new NotesListLiveData(), notesList -> {
                notes = notesList;
                generateRestaurantsList();
            });
        }

        private void generateRestaurantsList() {
            this.setValue(restaurants);
            addNumbersOfParticipantsInRestaurants();
            addNotesInRestaurants();
        }

        private void addNumbersOfParticipantsInRestaurants() {
            for (Restaurant restaurant : restaurants) {
                restaurant.setNumberOfParticipants(0);
                for (User user : users) {
                    if (user.getLunchTimestamp() != null
                            && DateTool.isToday(user.getLunchTimestamp())
                            && user.getLunchRestaurantPlaceId() != null
                            && user.getLunchRestaurantPlaceId().equals(restaurant.getPlaceId())) {
                        restaurant.setNumberOfParticipants(restaurant.getNumberOfParticipants() + 1);
                    }
                }
            }
        }

        private void addNotesInRestaurants() {
            for (Restaurant restaurant : restaurants) {
                restaurant.setNote(0);
                if (notes.containsKey(restaurant.getPlaceId())) {
                    Integer integer = notes.get(restaurant.getPlaceId());
                    restaurant.setNote((integer != null) ? integer : 0);
                }
            }
        }
    }

    class RestaurantMediatorLiveData extends MediatorLiveData<Restaurant> {
        private Restaurant restaurant;
        private int note = 0;
        private List<User> participants = new ArrayList<>();

        RestaurantMediatorLiveData(String placeId) {
            this.addSource(restaurantRepository.getRestaurant(placeId), newRestaurant -> {
                restaurant = newRestaurant;
                restaurant.setNote(note);
                restaurant.setParticipants(participants);
                this.setValue(restaurant);
            });
            this.addSource(new NoteLiveData(placeId), newNote -> {
                note = newNote;
                if (restaurant != null) {
                    restaurant.setNote(note);
                    this.setValue(restaurant);
                }
            });
            this.addSource(userRepository.getUsers(), users -> {
                participants.clear();
                for (User user : users) {
                    if (user.getLunchTimestamp() != null
                            && DateTool.isToday(user.getLunchTimestamp())
                            && user.getLunchRestaurantPlaceId() != null
                            && user.getLunchRestaurantPlaceId().equals(placeId)) {
                        participants.add(user);
                    }
                }
                if (restaurant != null) {
                    restaurant.setParticipants(participants);
                    this.setValue(restaurant);
                }
            });
        }
    }

    class NotesListLiveData extends MediatorLiveData<HashMap<String, Integer>> {

        private int nbUsers = 0;
        private HashMap<String, Integer> nbOfLikesByRestaurant = new HashMap<>();
        private HashMap<String, Integer> notesList = new HashMap<>();

        NotesListLiveData() {
            this.addSource(userRepository.getUsers(), users -> {
                nbUsers = users.size();
                if (!nbOfLikesByRestaurant.isEmpty()) {
                    generateNotesList();
                }
            });
            this.addSource(likeRepository.getAllLikes(), likes -> {
                for (Like like : likes) {
                    String placeId = like.getRestaurantPlaceId();

                    if (nbOfLikesByRestaurant.containsKey(placeId) && nbOfLikesByRestaurant.get(placeId) != null) {
                        Integer integer = nbOfLikesByRestaurant.get(placeId);
                        nbOfLikesByRestaurant.put(placeId, (integer != null) ? integer + 1 : 1);

                    } else {
                        nbOfLikesByRestaurant.put(placeId, 1);
                    }
                }
                generateNotesList();
            });
        }

        private void generateNotesList() {
            for (Map.Entry line : nbOfLikesByRestaurant.entrySet()) {
                notesList.put((String) line.getKey(), NoteTool.calculateNoteOfRestaurant(nbUsers, (int) line.getValue()));
            }
            this.setValue(notesList);
        }
    }

    class NoteLiveData extends MediatorLiveData<Integer> {
        private int nbOfUsers = 0;
        private int nbOfLikes = 0;

        NoteLiveData(String placeId) {
            this.addSource(userRepository.getUsers(), users -> {
                nbOfUsers = users.size();
                generateNote();
            });
            this.addSource(likeRepository.getLikesByRestaurant(placeId), likes -> {
                nbOfLikes = likes.size();
                generateNote();
            });
        }

        private void generateNote() {
            this.setValue(NoteTool.calculateNoteOfRestaurant(nbOfUsers, nbOfLikes));
        }
    }
}
