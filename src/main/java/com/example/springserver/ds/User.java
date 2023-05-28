package com.example.springserver.ds;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class User implements Serializable {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique=true)
    private String email;
    private String password;
    private String name;
    private String surname;
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean emailApproved;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Card> userCards;
    private double balance;




    public User() {

    }

    public User(int id, String email, String password, String name, String surname, Boolean emailApproved, Boolean cashSelected) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.emailApproved = emailApproved;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Boolean getEmailApproved() {
        return emailApproved;
    }

    public void setEmailApproved(Boolean emailApproved) {
        this.emailApproved = emailApproved;
    }


    public List<Card> getUserCards() {
        return userCards;
    }

    public void setUserCards(List<Card> userCards) {
        this.userCards = userCards;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", emailApproved=" + emailApproved +
                ", userCards=" + userCards +
                ", balance=" + balance +
                '}';
    }
}
