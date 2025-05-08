package com.example.car_rental.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingResponseDTO {
    private Long id;
    private Long carId;
    private String carMake;
    private String carModel;
    private String imageUrl;
    private Long userId;
    private String userEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalPrice;
}