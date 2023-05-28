package com.example.springserver.repositories;

import com.example.springserver.Enums.TripStatus;
import com.example.springserver.ds.Trip;
import com.example.springserver.ds.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface
TripRepository extends JpaRepository<Trip, Integer> {


    List<Trip> findTripsByDepartureContainingAndArrivalContainingAndTripStatus(String  departure, String arrival, TripStatus tripStatus);
    List<Trip> findTripsByDepartureContainingAndTripStatus(String departure, TripStatus tripStatus);
    List<Trip> findTripsByArrivalContainingAndTripStatus(String arrival, TripStatus tripStatus);
    List<Trip> findTripsByTripStatus (TripStatus tripStatus);
    List<Trip> getTripsByTripOwner (User user);
}
