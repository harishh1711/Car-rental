package com.example.car_rental.controller;

import com.example.car_rental.DTO.BookingRequest;
import com.example.car_rental.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    public ResponseEntity<Map<String, Object>> createBooking(
            @RequestBody BookingRequest bookingRequest,
            Authentication authentication
    ) {
        return bookingService.createBooking(bookingRequest, authentication);
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<Map<String, Object>> getBookings(
                     Authentication authentication
    ) {
        return bookingService.getBookings( authentication);
    }
}