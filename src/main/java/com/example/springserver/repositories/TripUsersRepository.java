package com.example.springserver.repositories;

import com.example.springserver.ds.Trip;
import com.example.springserver.ds.TripUser;
import com.example.springserver.ds.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripUsersRepository extends JpaRepository<TripUser, Integer> {

    List<TripUser> getTripUsersByPassenger (User user);
    List<TripUser> getTripUsersByPassengerOrDriver (User passenger, User driver);
    List<TripUser> getTripUsersByTrip (Trip trip);


}
