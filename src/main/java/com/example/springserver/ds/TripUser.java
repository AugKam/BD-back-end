package com.example.springserver.ds;

import com.example.springserver.Enums.PassengerStatus;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"trip_id" , "passenger_id"})})
public class TripUser implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    private Trip trip;
    @OneToOne
    private User driver;
    @OneToOne
    private User passenger;
    private PassengerStatus passengerStatus;

    public TripUser(Trip trip, User driver, User passenger, PassengerStatus passengerStatus) {
        this.trip = trip;
        this.driver = driver;
        this.passenger = passenger;
        this.passengerStatus = passengerStatus;
    }

    public TripUser() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    public PassengerStatus getPassengerStatus() {
        return passengerStatus;
    }

    public void setPassengerStatus(PassengerStatus status) {
        this.passengerStatus = status;
    }


    @Override
    public String toString() {
        return "TripUsers{" +
                "id=" + id +
                ", trip=" + trip +
                ", driver=" + driver +
                ", passenger=" + passenger +
                ", passengerStatus=" + passengerStatus +
                '}';
    }
}
