package com.example.car_rental.service;

import com.example.car_rental.DTO.CarDTO;
import com.example.car_rental.model.Car;
import com.example.car_rental.model.Role;
import com.example.car_rental.model.Users;
import com.example.car_rental.repository.CarRepo;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    private CarRepo carRepo;

    public  UserService(CarRepo carRepo) {
        this.carRepo = carRepo;
    }


    public boolean authorizeUser(Authentication authentication){

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return false;
        }
        return  true;
    }

    public  boolean userCheck(Users user){
        Role targetRole = Role.USER;
        if (user == null || user.getRole() != targetRole) {
            return  false;
        }
        return  true;
    }
    public ResponseEntity<Map<String, Object>> getCarsByLocation(String location, Authentication authentication) {

        Map<String,Object> response = new HashMap<>();

        try {
            if (!authorizeUser(authentication)){
                response.put("message","User is  not authenticated");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            List<Car> cars = carRepo.findByLocation(location);
            List<CarDTO> carDTOs = cars.stream().map(car -> {
                CarDTO dto = new CarDTO();
                dto.setId(car.getId());
                dto.setMake(car.getMake());
                dto.setModel(car.getModel());
                dto.setYear(car.getYear());
                dto.setPricePerDay(car.getPricePerDay());
                dto.setLocation(car.getLocation());
                dto.setImageUrls(car.getImageUrls());
                CarDTO.OwnerDTO ownerDTO = new CarDTO.OwnerDTO();
                ownerDTO.setId(car.getOwner().getId());
                ownerDTO.setEmail(car.getOwner().getEmail());
                ownerDTO.setRole(String.valueOf(car.getOwner().getRole()));
                ownerDTO.setPhone(car.getOwner().getPhone());
                ownerDTO.setAddress(car.getOwner().getAddress());
                dto.setOwner(ownerDTO);
                return dto;
            }).collect(Collectors.toList());

            response.put("message", "Cars Fetched Successfully");
            response.put("data", carDTOs);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            response.put("message","Internal server Error");
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);        }

    }

    public ResponseEntity<Map<String, Object>> getCarsById(int id, Authentication authentication) {
        Map<String,Object> response = new HashMap<>();


        try {
            if (!authorizeUser(authentication)){
                response.put("message","User is  not authenticated");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            Car car = carRepo.findById(Long.valueOf(id)).orElse(null);

            if (car == null){
                response.put("message","Car not found");
                return  new ResponseEntity<>(response,HttpStatus.FORBIDDEN);
            }
            CarDTO dto = new CarDTO();
            dto.setId(car.getId());
            dto.setMake(car.getMake());
            dto.setModel(car.getModel());
            dto.setYear(car.getYear());
            dto.setPricePerDay(car.getPricePerDay());
            dto.setLocation(car.getLocation());
            dto.setImageUrls(car.getImageUrls());
            CarDTO.OwnerDTO ownerDTO = new CarDTO.OwnerDTO();
            ownerDTO.setId(car.getOwner().getId());
            ownerDTO.setEmail(car.getOwner().getEmail());
            ownerDTO.setRole(String.valueOf(car.getOwner().getRole()));
            ownerDTO.setPhone(car.getOwner().getPhone());
            ownerDTO.setAddress(car.getOwner().getAddress());
            dto.setOwner(ownerDTO);

            response.put("message","Car Found");
            response.put("data",dto);

            return  new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {

            response.put("message","Internal server error");
            return  new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
