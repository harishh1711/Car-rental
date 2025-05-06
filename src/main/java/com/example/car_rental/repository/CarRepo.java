package com.example.car_rental.repository;

import com.example.car_rental.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepo extends JpaRepository<Car,Long> {
    List<Car> findByOwnerId(Long ownerId);

    List<Car> findByLocation(String location);
}
