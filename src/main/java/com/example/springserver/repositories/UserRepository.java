package com.example.springserver.repositories;


import com.example.springserver.ds.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface
UserRepository extends JpaRepository<User, Integer> {

    User findUserByEmailAndPassword(String email, String password);
}
