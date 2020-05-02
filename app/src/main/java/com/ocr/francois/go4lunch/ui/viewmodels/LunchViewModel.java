package com.ocr.francois.go4lunch.ui.viewmodels;

import android.location.Location;

import androidx.lifecycle.LiveData;
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
        RestaurantsListMediatorLiveData restaurants = new RestaurantsListMediatorLiveData();
        return restaurants.init(location, radius);
    }

    public MutableLiveData<Restaurant> getRestaurant(String placeId) {
        RestaurantMediatorLiveData restaurant = new RestaurantMediatorLiveData();
        return restaurant.init(placeId);
    }

    public MutableLiveData<List<User>> getUsers() {
        return userRepository.getUsers();
    }

    public MutableLiveData<User> getCurrentUserInFirestore(String id) {
        return userRepository.getUser(id);
    }

    // USERS

    public void saveLunch(String userId, String lunchRestaurantPlaceId, String lunchRestaurantName) {
        userRepository.saveLunch(userId, lunchRestaurantPlaceId, lunchRestaurantName);
    }

    // LUNCHES

    public void createLike(String restaurantPlaceId, String userId) {
        likeRepository.createLike(restaurantPlaceId, userId);
    }

    public LiveData<Integer> getNoteOfRestaurant(String placeId) {
        NoteGenerator noteGenerator = new NoteGenerator();
        return noteGenerator.getNote(placeId);
    }

    private LiveData<HashMap<String, Integer>> getNotesOfRestaurants() {
        NotesGenerator notesGenerator = new NotesGenerator();
        return notesGenerator.getNotes();
    }

    class RestaurantsListMediatorLiveData {
        private List<Restaurant> restaurants = new ArrayList<>();
        private List<User> users;
        private HashMap<String, Integer> notes;

        MediatorLiveData<List<Restaurant>> init(Location location, int radius) {
            MediatorLiveData<List<Restaurant>> restaurantsListLiveData = new MediatorLiveData<>();

            restaurantsListLiveData.addSource(restaurantRepository.getRestaurants(location, radius), restaurantsList -> {
                restaurants.clear();
                restaurants.addAll(restaurantsList);
                if (users != null) {
                    addNumbersOfParticipantsInRestaurants();
                }
                if (notes != null) {
                    addNotesInRestaurants();
                }
                restaurantsListLiveData.setValue(restaurants);
            });

            restaurantsListLiveData.addSource(userRepository.getUsers(), usersList -> {
                users = usersList;
                if (!restaurants.isEmpty()) {
                    addNumbersOfParticipantsInRestaurants();
                    restaurantsListLiveData.setValue(restaurants);
                }
            });

            restaurantsListLiveData.addSource(getNotesOfRestaurants(), notesList -> {
                notes = notesList;
                if (!restaurants.isEmpty()) {
                    addNotesInRestaurants();
                    restaurantsListLiveData.setValue(restaurants);
                }
            });


            return restaurantsListLiveData;
        }

        private void addNumbersOfParticipantsInRestaurants() {
            for (Restaurant restaurant : restaurants) {
                restaurant.setNumberOfParticipants(0);

                for (User user : users) {
                    if (user.getLunchTimestamp() != null && DateTool.isToday(user.getLunchTimestamp())) {
                        if (user.getLunchRestaurantPlaceId() != null && user.getLunchRestaurantPlaceId().equals(restaurant.getPlaceId())) {
                            restaurant.setNumberOfParticipants(restaurant.getNumberOfParticipants() + 1);
                        }
                    }
                }
            }
        }

        private void addNotesInRestaurants() {
            for (Restaurant restaurant : restaurants) {
                if (notes.containsKey(restaurant.getPlaceId())) {
                    restaurant.setNote(notes.get(restaurant.getPlaceId()).intValue());
                }
            }
        }
    }

    class RestaurantMediatorLiveData {
        private Restaurant restaurant;
        private int note = 0;
        private List<User> participants = new ArrayList<>();

        MediatorLiveData<Restaurant> init(String placeId) {
            MediatorLiveData<Restaurant> restaurantLiveData = new MediatorLiveData<>();

            restaurantLiveData.addSource(restaurantRepository.getRestaurant(placeId), newRestaurant -> {
                restaurant = newRestaurant;
                restaurant.setNote(note);
                restaurant.setParticipants(participants);
                restaurantLiveData.setValue(restaurant);
            });
            restaurantLiveData.addSource(getNoteOfRestaurant(placeId), newNote -> {
                note = newNote;
                if (restaurant != null) {
                    restaurant.setNote(note);
                    restaurantLiveData.setValue(restaurant);
                }
            });
            restaurantLiveData.addSource(userRepository.getUsers(), users -> {
                participants.clear();
                for (User user : users) {
                    if (user.getLunchTimestamp() != null && DateTool.isToday(user.getLunchTimestamp())
                            && user.getLunchRestaurantPlaceId() != null && user.getLunchRestaurantPlaceId().equals(placeId)) {
                        participants.add(user);
                    }
                }
                if (restaurant != null) {
                    restaurant.setParticipants(participants);
                    restaurantLiveData.setValue(restaurant);
                }
            });
            return restaurantLiveData;
        }
    }

    class NotesGenerator {
        private int nbUsers = 0;
        private HashMap<String, Integer> notesList = new HashMap<>();
        private HashMap<String, Integer> nbLikesByRestaurant = new HashMap<>();

        MediatorLiveData<HashMap<String, Integer>> getNotes() {
            MediatorLiveData<HashMap<String, Integer>> notes = new MediatorLiveData<>();

            notes.addSource(userRepository.getUsers(), users -> {
                nbUsers = users.size();
                if (!nbLikesByRestaurant.isEmpty()) {
                    notes.setValue(generateNotesList());
                }
            });

            notes.addSource(likeRepository.getAllLikes(), likes -> {
                for (Like like : likes) {
                    String placeId = like.getRestaurantPlaceId();

                    if (nbLikesByRestaurant.containsKey(placeId)) {
                        nbLikesByRestaurant.put(placeId, nbLikesByRestaurant.get(placeId) + 1);
                    } else {
                        nbLikesByRestaurant.put(placeId, 1);
                    }
                }
                notes.setValue(generateNotesList());
            });

            return notes;
        }

        private HashMap<String, Integer> generateNotesList() {
            for (Map.Entry line : nbLikesByRestaurant.entrySet()) {
                notesList.put((String) line.getKey(), calculateNoteOfRestaurant((int) line.getValue()));
            }
            return notesList;
        }

        private int calculateNoteOfRestaurant(int nbLikes) {
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

    class NoteGenerator {
        private int nbUsers;
        private int nbLikes;

        MediatorLiveData<Integer> getNote(String placeId) {
            MediatorLiveData<Integer> note = new MediatorLiveData<>();

            nbUsers = 0;
            nbLikes = 0;
            note.addSource(userRepository.getUsers(), users -> {
                setNbUsers(users.size());
                note.setValue(calculateNoteOfRestaurant());
            });
            note.addSource(likeRepository.getLikesByRestaurant(placeId), likes -> {
                setNbLikes(likes.size());
                note.setValue(calculateNoteOfRestaurant());
            });
            return note;
        }

        void setNbUsers(int nbUsers) {
            this.nbUsers = nbUsers;
        }

        void setNbLikes(int nbLikes) {
            this.nbLikes = nbLikes;
        }

        private int calculateNoteOfRestaurant() {
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
