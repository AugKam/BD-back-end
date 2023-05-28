package com.example.springserver.controllers;

import com.example.springserver.Enums.PassengerStatus;
import com.example.springserver.ds.Trip;
import com.example.springserver.ds.TripUser;
import com.example.springserver.ds.User;
import com.example.springserver.repositories.TripRepository;
import com.example.springserver.repositories.TripUsersRepository;
import com.example.springserver.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class TripUsersController {

    @Autowired
    private TripUsersRepository repository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private UserRepository userRepository;



    @PostMapping("saveTripUsers")
    public @ResponseBody
    TripUser save(@RequestBody TripUser tripUser){
        repository.save(tripUser);
        return tripUser;
    }

    @PostMapping("createReservation")
    public @ResponseBody
    TripUser createReservation(@RequestBody TripUser data){
        TripUser tripUser = new TripUser();

        Optional<Trip> trip = tripRepository.findById(data.getTrip().getId());
        Optional<User> driver = userRepository.findById(trip.get().getTripOwner().getId());
        Optional<User> passenger = userRepository.findById(data.getPassenger().getId());
        tripUser.setPassengerStatus(PassengerStatus.AWAITING);
        tripUser.setTrip(trip.get());
        tripUser.setPassenger(passenger.get());
        tripUser.setDriver(driver.get());

        repository.save(tripUser);
        return tripUser;
    }

    @RequestMapping(value = "getActiveTripsByPassenger", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<TripUser> getActiveTripsByPassenger(@RequestBody User user) {
        List<TripUser> tripUsers = new ArrayList<>();

        for (TripUser temp : repository.getTripUsersByPassenger(user)) {

            if (temp.getPassengerStatus() == PassengerStatus.AWAITING || temp.getPassengerStatus() == PassengerStatus.CONFIRMED || temp.getPassengerStatus() == PassengerStatus.DELIVERED)
            {
                tripUsers.add(temp);
            }
        }
        return tripUsers;
    }

    @RequestMapping(value = "getInactiveTripsByPassenger", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<TripUser> getInactiveTripsByPassenger(@RequestBody User user) {
        List<TripUser> tripUsers = new ArrayList<>();

        for (TripUser temp : repository.getTripUsersByPassenger(user)) {

            if (temp.getPassengerStatus() == PassengerStatus.REJECTED || temp.getPassengerStatus() == PassengerStatus.CANCELED || temp.getPassengerStatus() == PassengerStatus.FINISHED)
            {
                tripUsers.add(temp);
            }
        }
        return tripUsers;
    }

    @RequestMapping(value = "getTripUsersByTrip", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<TripUser> getTripUsersByTrip(@RequestBody Trip trip) {
        List<TripUser> tripUsers;
        tripUsers = repository.getTripUsersByTrip(trip);

        if (tripUsers == null) return null;

        return tripUsers;
    }

}
