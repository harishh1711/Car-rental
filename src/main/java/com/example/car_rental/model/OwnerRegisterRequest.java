package com.example.car_rental.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class OwnerRegisterRequest {
    private String email;
    private String password;
    private String confirmPassword;
    private String phone;
    private String address;
}