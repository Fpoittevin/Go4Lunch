package com.ocr.francois.go4lunch.api;

import com.ocr.francois.go4lunch.models.GoogleDetailResult;
import com.ocr.francois.go4lunch.models.GoogleSearchResults;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesService {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET("nearbysearch/json?type=restaurant&key=AIzaSyAwcLs-t_e1sfK1Fjkfwo3Ndr2AeJBu7JE")
    Call<GoogleSearchResults> getNearbyPlaces(@Query("location") String location, @Query("radius") int radius);

    @GET("details/json?key=AIzaSyAwcLs-t_e1sfK1Fjkfwo3Ndr2AeJBu7JE")
    Call<GoogleDetailResult> getPlaceDetails(@Query("place_id") String placeId);

    //TODO : ne demander que les infos nécessaires
    //TODO : ajouter le radius choisi dans les préférences utilisateurs
}