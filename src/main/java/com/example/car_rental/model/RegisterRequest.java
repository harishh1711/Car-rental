package com.example.car_rental.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String confirmPassword;
    private String role;


}
