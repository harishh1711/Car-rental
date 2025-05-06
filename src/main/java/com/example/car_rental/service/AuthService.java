package com.example.car_rental.service;

import com.example.car_rental.model.*;
import com.example.car_rental.repository.UserRepo;
import com.example.car_rental.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class AuthService {
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService( UserRepo userRepo, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }
    private void validateRegistration(String email, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (userRepo.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists");
        }
    }



    public void register(RegisterRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (userRepo.findByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists");
        }

        Users user = new Users();
        user.setEmail(request.getEmail());

        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepo.save(user);
    }



    public String loginUser(LoginRequest request) {
        Authentication authentication =  authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        System.out.print(request);
        Users user = userRepo.findByEmail(request.getEmail());
        System.out.print(user);

        if (user == null){
            throw  new IllegalArgumentException("User not Found");
        }
        Role targetRole = Role.USER;
        if (!user.getRole().equals(Role.USER)) {
            throw new IllegalArgumentException("Invalid role");
        }

        return jwtUtil.generateToken(request.getEmail(), String.valueOf(Role.USER));
  }

//    public String loginOwner(RegisterRequest request) {
//    }

    public String handleGoogleCallback(String role, Authentication authentication) {
        LOGGER.info("Handling Google callback for role: " + role + ", Authentication: " + authentication);
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            LOGGER.severe("Invalid authentication: " + authentication);
            throw new IllegalArgumentException("Invalid authentication");
        }

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String googleId = oauthToken.getPrincipal().getAttribute("sub");
        String email = oauthToken.getPrincipal().getAttribute("email");
        LOGGER.info("Google ID: " + googleId + ", Email: " + email);

        if (!role.equals("USER")) {
            LOGGER.severe("Google OAuth2 only supports USER role");
            throw new IllegalArgumentException("Google OAuth2 only supports USER role");
        }

        Role targetRole = Role.USER;
        Users user = userRepo.findByGoogleId(googleId);
        if (user == null) {
            user = userRepo.findByEmail(email);
            if (user != null) {
                if (user.getRole() != targetRole) {
                    LOGGER.severe("Account role " + user.getRole() + " does not match USER");
                    throw new IllegalArgumentException("Account exists with a different role. Please use email/password registration.");
                }
                user.setGoogleId(googleId);
                userRepo.save(user);
            }
        }

        if (user == null) {
            user = new Users();
            user.setEmail(email);
            user.setGoogleId(googleId);
            user.setPassword(passwordEncoder.encode("google-" + googleId));
            user.setRole(targetRole);
            userRepo.save(user);
        }

        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }



    public void ownerRegister(OwnerRegisterRequest request) {
        validateRegistration(request.getEmail(), request.getPassword(), request.getConfirmPassword());

        Users user = new Users();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.OWNER);
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        userRepo.save(user);
    }

    public String ownerLogin(LoginRequest request) {
        Authentication authentication =  authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        System.out.print(request);
        Users user = userRepo.findByEmail(request.getEmail());
        System.out.print(user);

        if (user == null){
            throw  new IllegalArgumentException("User not Found");
        }
        Role targetRole = Role.OWNER;
        if (user.getRole() != targetRole) {
            throw new IllegalArgumentException("Invalid role");
        }

        return jwtUtil.generateToken(request.getEmail(), String.valueOf(Role.OWNER));
    }
}
