package com.example.springserver.controllers;

import com.example.springserver.Enums.PassengerStatus;
import com.example.springserver.Enums.TripStatus;
import com.example.springserver.ds.*;
import com.example.springserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class TripController {

    @Autowired
    private TripRepository repository;
    @Autowired
    private TripUsersRepository tripUsersRepository;
    @Autowired
    private MoneyTransferRepository moneyTransferRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TripRepository tripRepository;

    public TripController(TripRepository repository, TripUsersRepository tripUsersRepository, MoneyTransferRepository moneyTransferRepository, UserRepository userRepository, TripRepository tripRepository) {
        this.repository = repository;
        this.tripUsersRepository = tripUsersRepository;
        this.moneyTransferRepository = moneyTransferRepository;
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
    }

    @Scheduled(cron = "0 */1 * ? * *")
    public List<Trip> runEveryMinute() {
        return repository.saveAll(finishTripsWithoutPassengers(updateTripStatus(repository.findAll())));
    }

    public List<Trip> updateTripStatus(List<Trip> trips) {
        LocalDateTime now = LocalDateTime.now();
        int diff;
        List<Trip> updatedTrips = new ArrayList<>();
        for (Trip temp :trips) {
            diff = now.compareTo(temp.getDepartureDate());
            if (diff >= 0 && temp.getTripStatus() == TripStatus.ACTIVE)
            {
                temp.setTripStatus(TripStatus.ONGOING);
                List<TripUser> tripUsers = tripUsersRepository.getTripUsersByTrip(temp);
                for (TripUser tripUserTemp : tripUsers){
                    if(tripUserTemp.getPassengerStatus() == PassengerStatus.AWAITING)
                    {
                        tripUserTemp.setPassengerStatus(PassengerStatus.REJECTED);
                        MoneyTransfer moneyTransfer = new MoneyTransfer();
                        moneyTransfer.setTripUser(tripUserTemp);
                        MoneyTransferController moneyTransferController = new MoneyTransferController(moneyTransferRepository, userRepository, tripUsersRepository, tripRepository);
                        moneyTransferController.cancelReservation(moneyTransfer);
                    }
                    tripUsersRepository.save(tripUserTemp);
                }

            }

            updatedTrips.add(temp);
        }
        return updatedTrips;

    }
    public List<Trip> finishTripsWithoutPassengers(List<Trip> trips) {
        List<Trip> updatedTripList = new ArrayList<>();
        for (Trip trip : trips)
        {
            List<TripUser> tripUserList = tripUsersRepository.getTripUsersByTrip(trip);
            boolean finishTrip = true;

            if (tripUserList.isEmpty() && trip.getTripStatus() == TripStatus.ONGOING)
                trip.setTripStatus(TripStatus.CANCELED);
            else
            {
                for (TripUser tripUserTemp : tripUserList) {
                    if (tripUserTemp.getTrip().getTripStatus() == TripStatus.ONGOING) {
                        if (tripUserTemp.getPassengerStatus() == PassengerStatus.CONFIRMED || tripUserTemp.getPassengerStatus() == PassengerStatus.DELIVERED) {
                            finishTrip = false;
                        }
                        if (finishTrip)
                        {
                            trip.setTripStatus(TripStatus.CANCELED);
                        }
                    }
                }
            }
            updatedTripList.add(trip);
        }
        return updatedTripList;
    }

    @PostMapping("cancelTrip")
    public @ResponseBody
    Trip cancelTrip(@RequestBody Trip trip){
        List<TripUser> tripUserList = tripUsersRepository.getTripUsersByTrip(trip);
        for (TripUser tripUser : tripUserList)
        {
            MoneyTransfer moneyTransfer = moneyTransferRepository.getMoneyTransferByTripUser(tripUser);
            MoneyTransferController moneyTransferController = new MoneyTransferController(moneyTransferRepository, userRepository, tripUsersRepository, tripRepository);
            if (moneyTransfer.getTripUser().getPassengerStatus() != PassengerStatus.CANCELED)
            moneyTransfer.getTripUser().setPassengerStatus(PassengerStatus.REJECTED);
            moneyTransferController.cancelReservation(moneyTransfer);
        }
        repository.save(trip);
        return trip;
    }

    @PostMapping("saveTrip")
    public @ResponseBody
    Trip saveTrip(@RequestBody Trip trip){
        User user = userRepository.findById(trip.getTripOwner().getId()).get();
        trip.setTripOwner(user);
        repository.save(trip);
        return trip;
    }

    @PostMapping("updateTrip")
    public @ResponseBody
    Trip updateTrip(@RequestBody Trip trip){
        repository.save(trip);
        return trip;
    }

    @GetMapping("allTrips")
    public @ResponseBody
    List<Trip> getAllTrips(){
        return repository.findAll();
    }

    @RequestMapping(value = "filterTrips", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<Trip> filterTrips(@RequestBody Trip trip) {
        List<Trip> trips = new ArrayList<>();
        for (Trip temp : repository.findTripsByDepartureContainingAndArrivalContainingAndTripStatus(trip.getDeparture(), trip.getArrival(), trip.getTripStatus())){
            if (temp.getNumOfSeats() > 0)
            {
                trips.add(temp);
            }
        }
        return trips;
    }

    @RequestMapping(value = "filterTripsByDeparture", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<Trip> filterTripsByDeparture(@RequestBody Trip trip) {
        List<Trip> trips = new ArrayList<>();
        for (Trip temp : repository.findTripsByDepartureContainingAndTripStatus(trip.getDeparture(), trip.getTripStatus())) {
            if (temp.getNumOfSeats() > 0)
            {
                trips.add(temp);
            }
        }
        return trips;
    }

    @RequestMapping(value = "filterTripsByArrival", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<Trip> filterTripsByArrival(@RequestBody Trip trip) {
        List<Trip> trips = new ArrayList<>();
        for (Trip temp : repository.findTripsByArrivalContainingAndTripStatus(trip.getArrival(), trip.getTripStatus())) {
            if (temp.getNumOfSeats() > 0)
            {
                trips.add(temp);
            }
        }
        return trips;
    }

    @RequestMapping(value = "getAllActiveTrips", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<Trip> getAllActiveTrips(@RequestBody Trip trip) {
        List<Trip> trips = new ArrayList<>();

        for (Trip temp : repository.findTripsByTripStatus(trip.getTripStatus())) {
            if (temp.getNumOfSeats() > 0)
            {
                trips.add(temp);
            }
        }
        return trips;
    }

    @RequestMapping(value = "/getTripById/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Trip getTripById(@PathVariable int id){

        Optional<Trip> trip = repository.findById(id);
        if (trip.isPresent()){
            return trip.get();
        }
        else return null;
    }

    @RequestMapping(value = "getActiveTripsByTripOwner", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<Trip> getActiveTripByTripOwner(@RequestBody User user) {
        List<Trip> trips = new ArrayList<>();

        for (Trip temp : repository.getTripsByTripOwner(user)) {

            if (temp.getTripStatus() == TripStatus.ACTIVE || temp.getTripStatus() == TripStatus.ONGOING)
            {
                trips.add(temp);
            }
        }
        return trips;
    }

    @RequestMapping(value = "getInactiveTripsByTripOwner", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<Trip> getInactiveTripByTripOwner(@RequestBody User user) {
        List<Trip> trips = new ArrayList<>();

        for (Trip temp : repository.getTripsByTripOwner(user)) {

            if (temp.getTripStatus() == TripStatus.FINISHED || temp.getTripStatus() == TripStatus.CANCELED)
            {
                trips.add(temp);
            }
        }
        return trips;
    }

}

