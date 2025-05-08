package com.example.car_rental.service;

import com.example.car_rental.DTO.BookingRequest;
import com.example.car_rental.DTO.BookingResponseDTO;
import com.example.car_rental.model.Booking;
import com.example.car_rental.model.Car;
import com.example.car_rental.model.Users;
import com.example.car_rental.repository.BookingRepo;
import com.example.car_rental.repository.CarRepo;
import com.example.car_rental.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final BookingRepo bookingRepository;
    private final CarRepo carRepository;
    private final UserRepo userRepository;

    @Autowired
    public BookingService(BookingRepo bookingRepository, CarRepo carRepository, UserRepo userRepository) {
        this.bookingRepository = bookingRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<Map<String, Object>> createBooking(BookingRequest bookingRequest, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!isAuthenticated(authentication)) {
                response.put("message", "User not authenticated");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            String email = authentication.getName();
            Users user = userRepository.findByEmail(email);
            if (user == null) {
                response.put("message", "User not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Car car = carRepository.findById(bookingRequest.getCarId()).orElse(null);
            if (car == null) {
                response.put("message", "Car not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            LocalDate startDate = LocalDate.parse(bookingRequest.getStartDate());
            LocalDate endDate = LocalDate.parse(bookingRequest.getEndDate());
            if (startDate.isAfter(endDate) || startDate.isBefore(LocalDate.now())) {
                response.put("message", "Invalid date range");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            double totalPrice = days * car.getPricePerDay();

            Booking booking = new Booking();
            booking.setUser(user);
            booking.setCar(car);
            booking.setStartDate(startDate);
            booking.setEndDate(endDate);
            booking.setTotalPrice(totalPrice);

            bookingRepository.save(booking);

            response.put("message", "Car booked successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Failed to book car: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser");
    }

    public ResponseEntity<Map<String, Object>> getBookings(Authentication authentication) {

        Map<String,Object> response = new HashMap<>();
        if (!isAuthenticated(authentication)) {
            response.put("message", "User not authenticated");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String email = authentication.getName();
        Users user = userRepository.findByEmail(email);

        List<Booking> bookingList = bookingRepository.findByUserId(user.getId());
        List<BookingResponseDTO> bookingDTOs = bookingList.stream().map(booking -> {
            BookingResponseDTO dto = new BookingResponseDTO();
            dto.setId(booking.getId());
            dto.setCarId(booking.getCar().getId());
            dto.setCarMake(booking.getCar().getMake());
            dto.setCarModel(booking.getCar().getModel());
            dto.setUserId(booking.getUser().getId());
            dto.setUserEmail(booking.getUser().getEmail());
            dto.setStartDate(booking.getStartDate());
            dto.setEndDate(booking.getEndDate());
            dto.setTotalPrice(booking.getTotalPrice());
            dto.setImageUrl(booking.getCar().getImageUrls().get(1));
            return dto;
        }).collect(Collectors.toList());

        response.put("message","Bookings fetched successfully");
        response.put("data",bookingDTOs);
        return  new ResponseEntity<>(response,HttpStatus.OK);
    }
}