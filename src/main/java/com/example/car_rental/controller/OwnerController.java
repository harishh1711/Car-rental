package com.example.car_rental.controller;


import com.example.car_rental.model.Car;
import com.example.car_rental.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owner")
public class OwnerController {

    private final OwnerService ownerService ;
    @Autowired
    public OwnerController(OwnerService ownerService){
        this.ownerService = ownerService;
    }

    @PostMapping("/add-car")
    public ResponseEntity<Map<String, Object>> addCar(
            @RequestPart("car") Car car,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            Authentication authentication
    ) {
        System.out.println("Received car: " + car);
        if (images != null) {
            System.out.println("Received images: " + images.size());
            images.forEach(image -> System.out.println("Image: " + image.getOriginalFilename()));
        } else {
            System.out.println("No images received");
        }
        return ownerService.addCar(car, images, authentication);
    }

    @GetMapping("/my-cars")
    public ResponseEntity<Map<String, Object>> addCar(
            Authentication authentication
    ) {

        return ownerService.getOwnerCars(authentication);
    }

}
