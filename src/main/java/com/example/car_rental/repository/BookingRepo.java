package com.example.car_rental.repository;

import com.example.car_rental.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long id);


    Booking findByCarId(Long id);

    List<Booking> findAllByCarId(Long id);
}