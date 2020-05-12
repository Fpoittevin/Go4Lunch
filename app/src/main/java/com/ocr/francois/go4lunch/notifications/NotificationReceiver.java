package com.ocr.francois.go4lunch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.api.PlacesService;
import com.ocr.francois.go4lunch.api.UserHelper;
import com.ocr.francois.go4lunch.models.GoogleDetailResult;
import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.ui.restaurantDetails.RestaurantDetailsActivity;
import com.ocr.francois.go4lunch.utils.DateTool;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationReceiver extends BroadcastReceiver {
    private Context context;
    private Restaurant restaurant;
    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        getDataForNotification();
    }

    private void getDataForNotification() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            UserHelper.getUser(FirebaseAuth.getInstance().getUid()).addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot != null) {
                    User currentUser = documentSnapshot.toObject(User.class);

                    if (currentUser != null) {
                        if (currentUser.getLunchTimestamp() != null && DateTool.isToday(currentUser.getLunchTimestamp())) {
                            if (currentUser.getLunchRestaurantPlaceId() != null) {
                                String placeId = currentUser.getLunchRestaurantPlaceId();

                                PlacesService placesService = PlacesService.retrofit.create(PlacesService.class);

                                Call<GoogleDetailResult> call = placesService.getPlaceDetails(placeId);
                                call.enqueue(new Callback<GoogleDetailResult>() {
                                    @Override
                                    public void onResponse(@NonNull Call<GoogleDetailResult> call, @NonNull Response<GoogleDetailResult> response) {
                                        if (response.body() != null) {
                                            restaurant = response.body().getRestaurant();

                                            UserHelper.getUsersCollection()
                                                    .whereEqualTo("lunchRestaurantPlaceId", placeId)
                                                    .whereGreaterThanOrEqualTo("lunchTimestamp", DateTool.getTodayMidnightTimestamp())
                                                    .addSnapshotListener((querySnapshots, e) -> {
                                                        List<User> usersList = new ArrayList<>();

                                                        if (querySnapshots != null) {
                                                            for (QueryDocumentSnapshot doc : querySnapshots) {

                                                                User user = doc.toObject(User.class);
                                                                if (!user.getId().equals(currentUser.getId())) {
                                                                    usersList.add(doc.toObject(User.class));
                                                                }
                                                            }
                                                            restaurant.setParticipants(usersList);
                                                        }
                                                        generateNotification();
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<GoogleDetailResult> call, @NonNull Throwable t) {
                                        Log.e("Notification", "error to get place details");
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }

    private void generateNotification() {

        String notificationContent = "";
        StringBuilder stringBuilder = new StringBuilder(notificationContent);

        if (restaurant.getName() != null) {
            stringBuilder.append(restaurant.getName());
        }
        if (restaurant.getVicinity() != null) {
            stringBuilder.append(", ").append(restaurant.getVicinity());
        }

        if (restaurant.getParticipants().size() > 0) {
            stringBuilder.append(" ").append(context.getResources().getString(R.string.with)).append(" ");

            for (int i = 0; i < restaurant.getParticipants().size(); i++) {

                User user = restaurant.getParticipants().get(i);

                if (user.getUserName() != null) {
                    stringBuilder.append(user.getUserName());

                    if (!(i == restaurant.getParticipants().size() - 1)) {
                        stringBuilder.append(", ");
                    }
                }
            }
            stringBuilder.append(".");
        }

        sendNotification(stringBuilder.toString());
    }

    private void sendNotification(String notificationContent) {
        Intent intent = new Intent(context, RestaurantDetailsActivity.class);
        intent.putExtra("placeId", restaurant.getPlaceId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = context.getString(R.string.default_notification_channel_id);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setContentTitle(context.getString(R.string.notification_title))
                        .setAutoCancel(true)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(notificationContent)
                        );

        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence channelName = "go4lunch_channel";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);

                notificationManager.createNotificationChannel(mChannel);
            }

            String notificationTag = "GO4LUNCH";
            int notificationId = 123;
            notificationManager.notify(notificationTag, notificationId, notificationBuilder.build());
        }
    }
}
