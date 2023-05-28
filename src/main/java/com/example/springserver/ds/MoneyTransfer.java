package com.example.springserver.ds;

import com.example.springserver.Enums.PaymentMethod;
import com.example.springserver.Enums.PaymentStatus;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class MoneyTransfer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    private TripUser tripUser;
    private PaymentStatus paymentStatus;
    private double finalPrice;
    private double usedVirtualMoney;
    private PaymentMethod paymentMethod;


    public TripUser getTripUser() {
        return tripUser;
    }

    public void setTripUser(TripUser tripUser) {
        this.tripUser = tripUser;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public double getUsedVirtualMoney() {
        return usedVirtualMoney;
    }

    public void setUsedVirtualMoney(double usedVirtualMoney) {
        this.usedVirtualMoney = usedVirtualMoney;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }


    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public MoneyTransfer() {

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MoneyTransfer{" +
                "id=" + id +
                ", tripUser=" + tripUser +
                ", paymentStatus=" + paymentStatus +
                ", finalPrice=" + finalPrice +
                ", usedVirtualMoney=" + usedVirtualMoney +
                ", paymentMethod=" + paymentMethod +
                '}';
    }
}
