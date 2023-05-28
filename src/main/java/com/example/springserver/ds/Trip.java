package com.example.springserver.ds;

import com.example.springserver.Enums.TripStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Trip implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String departure;
    private String arrival;
    private LocalDateTime departureDate;
    private Double price;
    private int numOfSeats;
    private TripStatus tripStatus;
    @OneToOne
    private User tripOwner;


    public Trip(String departure, String arrival, LocalDateTime departureDate) {
        this.departure = departure;
        this.arrival = arrival;
        this.departureDate = departureDate;
    }

    public Trip(String departure, String arrival, LocalDateTime departureDate, Double price, int numOfSeats, TripStatus tripStatus, User tripOwner) {
        this.departure = departure;
        this.arrival = arrival;
        this.departureDate = departureDate;
        this.price = price;
        this.numOfSeats = numOfSeats;
        this.tripStatus = tripStatus;
        this.tripOwner = tripOwner;
    }

    public User getTripOwner() {
        return tripOwner;
    }

    public void setTripOwner(User tripOwner) {
        this.tripOwner = tripOwner;
    }

    public Trip() {

    }


    public TripStatus getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(TripStatus tripStatus) {
        this.tripStatus = tripStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }


    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getNumOfSeats() {
        return numOfSeats;
    }

    public void setNumOfSeats(int numOfSeats) {
        this.numOfSeats = numOfSeats;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", departure='" + departure + '\'' +
                ", arrival='" + arrival + '\'' +
                ", departureDate=" + departureDate +
                ", price=" + price +
                ", numOfSeats=" + numOfSeats +
                ", tripStatus=" + tripStatus +
                ", tripOwner=" + tripOwner +
                '}';
    }
}
