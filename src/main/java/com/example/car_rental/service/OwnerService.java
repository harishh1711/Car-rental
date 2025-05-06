package com.example.car_rental.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.car_rental.model.Car;
import com.example.car_rental.model.Role;
import com.example.car_rental.model.Users;
import com.example.car_rental.repository.CarRepo;
import com.example.car_rental.repository.UserRepo;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OwnerService {
    private final CarRepo carRepo;
    private final UserRepo userRepo;
    private final Cloudinary cloudinary;

    @Autowired
    public OwnerService(
            CarRepo carRepo,
            UserRepo userRepository,
            @Value("${cloudinary.cloud-name:}") String cloudName,
            @Value("${cloudinary.api-key:}") String apiKey,
            @Value("${cloudinary.api-secret:}") String apiSecret
    ) {
        this.carRepo = carRepo;
        this.userRepo = userRepository;
        if (cloudName.isEmpty() || apiKey.isEmpty() || apiSecret.isEmpty()) {
            throw new IllegalArgumentException("Cloudinary configuration is missing or incomplete");
        }
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public boolean authorizeUser(Authentication authentication){

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return false;
        }
        return  true;
    }

    public  boolean ownerCheck(Users owner){
        Role targetRole = Role.OWNER;
        if (owner == null || owner.getRole() != targetRole) {
            return  false;
        }
        return  true;
    }


    public ResponseEntity<Map<String, Object>> addCar(
            Car car,
            List<MultipartFile> images,
            Authentication authentication
    ) {
        Map<String,Object> response = new HashMap<>();

        try {
            if (!authorizeUser(authentication)) {
                response.put("message", "Unauthorized");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            String email = authentication.getName();
            Users owner = userRepo.findByEmail(email);
            if (!ownerCheck(owner)) {
                response.put("message", "Only owners can add cars");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            car.setOwner(owner);
            List<String> imageUrls = new ArrayList<>();
            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    if (!image.getContentType().startsWith("image/")) {
                        response.put("message", "Invalid file type: " + image.getOriginalFilename());
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                    if (image.getSize() > 10 * 1024 * 1024) { // 10MB limit
                        response.put("message", "File too large: " + image.getOriginalFilename());
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }

                    Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap(
                            "folder", "zrents/cars",
                            "resource_type", "image"
                    ));
                    imageUrls.add((String) uploadResult.get("secure_url"));
                }
            }
            car.setImageUrls(imageUrls);
            Car savedCar = carRepo.save(car);
            response.put("message", "Car added successfully");
            response.put("data", savedCar);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Internal server error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    public ResponseEntity<Map<String, Object>> getAllCars() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Car> cars = carRepo.findAll();
            response.put("message", "Cars retrieved successfully");
            response.put("data", cars);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Internal server error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> getOwnerCars(Authentication authentication) {
        Map<String,Object> response = new HashMap<>();

        try {

            if (!authorizeUser(authentication)){
                response.put("message","Unauthorized user");
                return  new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            String email = authentication.getName();
            Users owner = userRepo.findByEmail(email);

            if (!ownerCheck(owner)){
                response.put("message","Not an Owner");
                return new ResponseEntity<>(response,HttpStatus.FORBIDDEN);
            }
            List<Car> cars = carRepo.findByOwnerId(owner.getId());
            response.put("message","Cars Retrived Successfully");
            response.put("data",cars);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            response.put("message","Internal Server Error");
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}