package com.ocr.francois.go4lunch;

import com.ocr.francois.go4lunch.models.Restaurant;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertSame;

class RestaurantsComparatorTest {

    private List<Restaurant> restaurants;

    private Restaurant restaurantA;
    private Restaurant restaurantB;
    private Restaurant restaurantC;

    @Before
    public void setUp() {
        restaurants = new ArrayList<>();

        restaurantA = new Restaurant();
        restaurantA.setNote(2);
        restaurantA.setDistance(3);
        restaurantA.setNumberOfParticipants(1);
        restaurants.add(restaurantA);

        restaurantB = new Restaurant();
        restaurantB.setNote(3);
        restaurantB.setDistance(2);
        restaurantB.setNumberOfParticipants(3);
        restaurants.add(restaurantB);

        restaurantC = new Restaurant();
        restaurantC.setNote(1);
        restaurantC.setDistance(1);
        restaurantC.setNumberOfParticipants(2);
        restaurants.add(restaurantC);
    }

    @Test
    public void ListOfRestaurantsIsOrderedByNote() {
        Collections.sort(restaurants, new Restaurant.RestaurantNotesComparator());
        assertSame(restaurants.get(0), restaurantB);
        assertSame(restaurants.get(1), restaurantA);
        assertSame(restaurants.get(2), restaurantC);
    }

    @Test
    public void ListOfRestaurantsIsOrderedByDistance() {
        Collections.sort(restaurants, new Restaurant.RestaurantDistanceComparator());
        assertSame(restaurants.get(0), restaurantC);
        assertSame(restaurants.get(1), restaurantB);
        assertSame(restaurants.get(2), restaurantA);
    }

    @Test
    public void ListOfRestaurantsIsOrderedByNumberOfParticipants() {
        Collections.sort(restaurants, new Restaurant.RestaurantParticipantsComparator());
        assertSame(restaurants.get(0), restaurantB);
        assertSame(restaurants.get(1), restaurantC);
        assertSame(restaurants.get(2), restaurantA);
    }
}
