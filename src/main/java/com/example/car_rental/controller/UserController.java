package com.example.car_rental.controller;


import com.example.car_rental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService= userService;
    }

    @GetMapping("/cars")
    public ResponseEntity<Map<String,Object>> getCarsByLocation(@RequestParam String location, Authentication authentication){

        return userService.getCarsByLocation(location,authentication);

    }
    @GetMapping("/cars/{id}")
    public ResponseEntity<Map<String,Object>> getCarsById(@PathVariable int id, Authentication authentication){

        return userService.getCarsById(id,authentication);

    }
}
