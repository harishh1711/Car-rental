package com.example.car_rental.DTO;

import lombok.Data;

@Data
public class BookingRequest {
    private Long carId;
    private String startDate;
    private String endDate;
}