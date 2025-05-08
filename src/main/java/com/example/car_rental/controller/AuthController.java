package com.example.car_rental.controller;


import com.example.car_rental.DTO.LoginRequest;
import com.example.car_rental.DTO.OwnerRegisterRequest;
import com.example.car_rental.DTO.RegisterRequest;
import com.example.car_rental.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger LOGGER = Logger.getLogger(AuthController.class.getName());

    private final AuthService authService;
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> userLogin(@RequestBody LoginRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            String token = authService.loginUser(request);
            response.put("message", "Login Success");
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            response.put("message", "Invalid email or password");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/owner/register")
    public ResponseEntity<Map<String, String>> ownerRegister(@RequestBody OwnerRegisterRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            authService.ownerRegister(request);
            response.put("message", "Owner registration successful");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("message", "Internal server error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/owner/login")
    public ResponseEntity<Map<String, String>> ownerLogin(@RequestBody LoginRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            String token = authService.ownerLogin(request);
            response.put("message", "Login Success");
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            response.put("message", "Invalid email or password");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

}
