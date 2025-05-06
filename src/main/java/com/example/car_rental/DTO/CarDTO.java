package com.example.car_rental.DTO;

import lombok.Data;

import java.util.List;

@Data
public class CarDTO {
    private Long id;
    private String make;
    private String model;
    private Integer year;
    private Double pricePerDay;
    private String location;
    private List<String> imageUrls;
    private OwnerDTO owner;

    @Data
    public static class OwnerDTO {
        private Long id;
        private String email;
        private String role;
        private String phone;
        private String address;
    }
}