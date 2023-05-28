package com.example.springserver.controllers;

import com.example.springserver.Constants.Constants;
import com.example.springserver.EncryptDecrypt.EncryptDecrypt;
import com.example.springserver.Enums.CardStatus;
import com.example.springserver.ds.Card;
import com.example.springserver.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;


@RestController
public class CardController {

    @Autowired
    private CardRepository repository;

    public CardController(CardRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 0 0 1 1/1 *")
    public List<Card> runEveryMonth() {
        return repository.saveAll(updateUserCardStatus(repository.findAll()));
    }

    public List<Card> updateUserCardStatus(List<Card> cardList) {
        Calendar rightNow = Calendar.getInstance();
        int nowDate = (rightNow.get(Calendar.YEAR) -2000) *100 + rightNow.get(Calendar.MONTH) + 1;
            for(Card cardTemp : cardList)
            {

                if (cardTemp.getCardStatus() == CardStatus.ACTIVE)
                {
                    int expireDate = Integer.valueOf(EncryptDecrypt.decrypt(Constants.KEY, cardTemp.getExpireDate()));
                    if (expireDate < nowDate)
                    {
                        cardTemp.setCardStatus(CardStatus.INACTIVE);
                    }
                }
            }
        return cardList;
    }
}
