package com.example.springserver.controllers;

import com.example.springserver.Constants.Constants;
import com.example.springserver.EncryptDecrypt.EncryptDecrypt;
import com.example.springserver.ds.Card;
import com.example.springserver.ds.User;
import com.example.springserver.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;
import java.util.*;


@RestController
public class UserController {

    @Autowired
    private UserRepository repository;

    @PostMapping("saveUser")
    public @ResponseBody
    User saveUser(@RequestBody User user){
        repository.save(user);
        return user;
    }

    @PostMapping("addCard")
    public @ResponseBody
    User addCard(@RequestBody User data)  {
        Optional<User> user = repository.findById(data.getId());
        List <Card> encodedCardList = user.get().getUserCards();
        Card card = data.getUserCards().get(0);
        card.setCardNumber(EncryptDecrypt.encrypt(Constants.KEY, data.getUserCards().get(0).getCardNumber()));
        card.setCvc(EncryptDecrypt.encrypt(Constants.KEY, data.getUserCards().get(0).getCvc()));
        card.setExpireDate(EncryptDecrypt.encrypt(Constants.KEY, data.getUserCards().get(0).getExpireDate()));
        encodedCardList.add(card);
        user.get().setUserCards(encodedCardList);

        repository.save(user.get());
        return user.get();
    }

    @PostMapping("updateUserBalance")
    public @ResponseBody
    User updateUserBalance(@RequestBody User data){
        Optional<User> user = repository.findById(data.getId());
        user.get().setBalance(data.getBalance());

        repository.save(user.get());
        return user.get();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public User login(@RequestBody String request) throws Exception {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        User user = repository.findUserByEmailAndPassword(data.getProperty("email"), data.getProperty("password"));

        if (user == null) return null;
        return user;
    }

    @RequestMapping(value = "/getUserById/{id}", method = RequestMethod.GET)
    @ResponseBody
    public User getUserById(@PathVariable int id) {
        Optional<User> user = repository.findById(id);
        if (user.get().getUserCards() != null){
            List<Card> cardList = decryptCardInformation(user.get().getUserCards());
            user.get().setUserCards(cardList);
        }

        if (user.get() == null) return null;
        return user.get();
    }

    @RequestMapping(value = "/getUserCardsByUserId/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List <Card> getUserCardsByUserId(@PathVariable int id) throws Exception {

        User user = repository.findById(id).get();
        if (user.getUserCards() != null){
            List<Card> cardList = decryptCardInformation(user.getUserCards());
            return cardList;
        }
        else return null;
    }

    private List<Card> decryptCardInformation(List<Card> userCards) {

        List<Card> encryptedCards = new ArrayList<>();
        for (Card temp : userCards)
        {
            temp.setCardNumber(EncryptDecrypt.decrypt(Constants.KEY, temp.getCardNumber()));
            temp.setCvc(EncryptDecrypt.decrypt(Constants.KEY, temp.getCvc()));
            temp.setExpireDate(EncryptDecrypt.decrypt(Constants.KEY, temp.getExpireDate()));

            encryptedCards.add(temp);
        }

        return encryptedCards;
    }


}
