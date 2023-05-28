package com.example.springserver.repositories;

import com.example.springserver.ds.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface
MoneyTransferRepository extends JpaRepository<MoneyTransfer, Integer> {

    MoneyTransfer getMoneyTransferByTripUser (TripUser tripUser);

}
