package com.example.springserver.repositories;

import com.example.springserver.ds.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface
CardRepository extends JpaRepository<Card, Integer> {



}
