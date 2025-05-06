package com.example.car_rental.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class LoginRequest {
    private String email;
    private String password;


}
