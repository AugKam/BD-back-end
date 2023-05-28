package com.example.springserver.controllers;

import com.example.springserver.Enums.PassengerStatus;
import com.example.springserver.Enums.PaymentMethod;
import com.example.springserver.Enums.PaymentStatus;
import com.example.springserver.Enums.TripStatus;
import com.example.springserver.ds.*;
import com.example.springserver.repositories.MoneyTransferRepository;
import com.example.springserver.repositories.TripRepository;
import com.example.springserver.repositories.TripUsersRepository;
import com.example.springserver.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MoneyTransferController {

    @Autowired
    private MoneyTransferRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TripUsersRepository tripUsersRepository;
    @Autowired
    private TripRepository tripRepository;


    public MoneyTransferController(MoneyTransferRepository moneyTransferRepository, UserRepository userRepository, TripUsersRepository tripUsersRepository, TripRepository tripRepository) {
        this.repository = moneyTransferRepository;
        this.userRepository = userRepository;
        this.tripUsersRepository = tripUsersRepository;
        this.tripRepository = tripRepository;
    }

    @PostMapping("cancelReservation")
    public @ResponseBody
    MoneyTransfer cancelReservation(@RequestBody MoneyTransfer data){
        MoneyTransfer moneyTransfer = repository.getMoneyTransferByTripUser(data.getTripUser());
        double finalPrice, usedVirtualMoney, balance;
        User passenger = moneyTransfer.getTripUser().getPassenger();
        finalPrice = moneyTransfer.getFinalPrice();
        usedVirtualMoney = moneyTransfer.getUsedVirtualMoney();
        balance = passenger.getBalance();

        if (moneyTransfer.getPaymentStatus() != PaymentStatus.CANCELED) {
            moneyTransfer.setPaymentStatus(PaymentStatus.CANCELED);
            moneyTransfer.getTripUser().setPassengerStatus(data.getTripUser().getPassengerStatus());
            balance = balance + usedVirtualMoney;
            if (moneyTransfer.getPaymentMethod() == PaymentMethod.CARD || moneyTransfer.getPaymentMethod() == PaymentMethod.VIRTUAL_MONEY) {
                balance = balance + finalPrice;
            }
            moneyTransfer.getTripUser().getPassenger().setBalance(balance);
            passenger.setBalance(balance);
        }
        userRepository.save(passenger);
        repository.save(moneyTransfer);
        return moneyTransfer;
    }

    @PostMapping("createMoneyTransfer")
    public @ResponseBody
    MoneyTransfer createMoneyTransfer(@RequestBody MoneyTransfer moneyTransfer){
        double tripPrice = moneyTransfer.getTripUser().getTrip().getPrice();
        double balance = moneyTransfer.getTripUser().getPassenger().getBalance();

        moneyTransfer.setPaymentStatus(PaymentStatus.RESERVED);
        if (tripPrice >= balance) {
            moneyTransfer.setFinalPrice(tripPrice - balance);
            moneyTransfer.setUsedVirtualMoney(balance);
            moneyTransfer.getTripUser().getPassenger().setBalance(0);
        }
        else {
            if (tripPrice < balance) {
                moneyTransfer.setFinalPrice(0);
                moneyTransfer.setUsedVirtualMoney(tripPrice);
                moneyTransfer.getTripUser().getPassenger().setBalance(balance - tripPrice);
            }
        }
        User passenger = moneyTransfer.getTripUser().getPassenger();
        userRepository.save(passenger);
        repository.save(moneyTransfer);
        return moneyTransfer;
    }

    @PostMapping("getPaymentMethod")
    public @ResponseBody
    MoneyTransfer getPaymentMethod(@RequestBody TripUser tripUser){
        MoneyTransfer moneyTransfer = repository.getMoneyTransferByTripUser(tripUser);
        return moneyTransfer;
    }

    @PostMapping("finishTripForPassenger")
    public @ResponseBody
    MoneyTransfer finishTripForPassenger(@RequestBody MoneyTransfer data){
        MoneyTransfer moneyTransfer = repository.getMoneyTransferByTripUser(data.getTripUser());
        moneyTransfer.setPaymentStatus(PaymentStatus.COMPLETED);
        User driver = moneyTransfer.getTripUser().getDriver();
        TripUser tripUser = moneyTransfer.getTripUser();
        tripUser.setPassengerStatus(PassengerStatus.FINISHED);
        tripUsersRepository.save(tripUser);

        if (moneyTransfer.getPaymentMethod() == PaymentMethod.CARD ||moneyTransfer.getPaymentMethod() == PaymentMethod.VIRTUAL_MONEY)
        {
            driver.setBalance(driver.getBalance() + moneyTransfer.getFinalPrice() + moneyTransfer.getUsedVirtualMoney());
        }
        if (moneyTransfer.getPaymentMethod() == PaymentMethod.CASH)
        {
            driver.setBalance(driver.getBalance() + moneyTransfer.getUsedVirtualMoney());
        }
        userRepository.save(driver);
        Trip trip = tripUser.getTrip();
        finishTripIfAllPassengersFinished(trip);
        repository.save(moneyTransfer);

        return moneyTransfer;
    }

    private void finishTripIfAllPassengersFinished(Trip trip) {
        List<TripUser> tripUserList = tripUsersRepository.getTripUsersByTrip(trip);
        boolean finishTrip = true;
        for (TripUser tripUserTemp : tripUserList)
        {
            if (tripUserTemp.getPassengerStatus().equals(PassengerStatus.DELIVERED) || tripUserTemp.getPassengerStatus() == PassengerStatus.CONFIRMED)
                {
                finishTrip = false;
            }
        }
        if (finishTrip)
        {
            trip.setTripStatus(TripStatus.FINISHED);
        }
        tripRepository.save(trip);
    }

    @PostMapping("getMoneyTransferByUser")
    public @ResponseBody
    List<MoneyTransfer> getMoneyTransferByUser(@RequestBody User user){
        List<MoneyTransfer> moneyTransferList = new ArrayList<>();
        List<TripUser> tripUserList = tripUsersRepository.getTripUsersByPassengerOrDriver(user, user);
        for (TripUser tripUserTemp : tripUserList)
        {
            moneyTransferList.add(repository.getMoneyTransferByTripUser(tripUserTemp));
        }

        return moneyTransferList;
    }
}
