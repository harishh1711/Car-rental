package com.example.car_rental.repository;

import com.example.car_rental.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<Users, Long> {

    Users findByEmail(String email);
    Users findByGoogleId(String googleId);

}
