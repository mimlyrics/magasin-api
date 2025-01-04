package com.magazin.magazina.auth;

import com.magazin.magazina.config.JwtService;
import com.magazin.magazina.error.Validation;
import com.magazin.magazina.models.Role;

import com.magazin.magazina.models.User;
import com.magazin.magazina.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        // Check if the email already exists in the database
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists, use another email");
        }

        Validation validation = new Validation();
        Role role = validation.validateRole(request.getRole());
        System.out.println("role: " + role);

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .phone(request.getPhone())
                .token(jwtToken)
                .role(role)
                .build();
    }


    /*public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = userRepository.findByEmail(request.getEmail()).orElse(null);
        var jwtToken = jwtService.generateToken(user);
        if(user != null) {
            return AuthenticationResponse.builder()
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .token(jwtToken)
                    .build();
        }

       return null;

    }*/

    // Authenticate user and generate token
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            // Authenticate the user with the provided email and password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Find the user by email
            var user = userRepository.findByEmail(request.getEmail()).orElse(null);

            if (user == null) {
                return null; // Return null if user not found
            }

            System.out.println("user: " + user);

            // Generate the JWT token
            var jwtToken = jwtService.generateToken(user);

            return AuthenticationResponse.builder()
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .email(user.getEmail())
                    .phone(user.getPhone()) // Optional
                    .token(jwtToken) // Include the JWT token in the response
                    .role(user.getRole())
                    .build();

        } catch (Exception ex) {
            // Handle invalid credentials or other authentication failures
            return null; // Or return a specific error message if needed
        }
    }

}
