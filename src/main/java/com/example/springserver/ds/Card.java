package com.example.springserver.ds;

import com.example.springserver.Enums.CardStatus;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class Card implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String cardNumber;
    private String cvc;
    private String expireDate;
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private CardStatus cardStatus;


    public Card(String cardNumber, String cvc, String expireDate, CardStatus cardStatus) {
        this.cardNumber = cardNumber;
        this.cvc = cvc;
        this.expireDate = expireDate;
        this.cardStatus = cardStatus;
    }

    public Card() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public CardStatus getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(CardStatus cardStatus) {
        this.cardStatus = cardStatus;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", cardNumber=" + cardNumber +
                ", cvc=" + cvc +
                ", expireDate=" + expireDate +
                ", cardStatus=" + cardStatus +
                '}';
    }
}
