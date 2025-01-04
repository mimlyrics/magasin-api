package com.magazin.magazina.controllers;

import com.magazin.magazina.config.JwtService;
import com.magazin.magazina.models.User;
import com.magazin.magazina.repositories.UserRepository;
import com.magazin.magazina.request.UpdateUserRequest;
import com.magazin.magazina.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(
        origins = {
                "https://magazina.onrender.com",
                "http://localhost:3000"
        }, // Multiple allowed origins
        allowedHeaders = "*", // Allow all headers
        methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.OPTIONS}, // Allowed methods
        allowCredentials = "true" // Allow credentials like cookies
)
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @GetMapping
    public List <User> getAllUser() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PutMapping("/edit")
    public ResponseEntity<?> updateUserDetails(
            @RequestBody UpdateUserRequest updateRequest,
            @RequestHeader("Authorization") String authorizationHeader) {

        // Extract the token from the Authorization header
        String token = authorizationHeader.replace("Bearer ", "").trim(); // Assuming Bearer token format
        System.out.println(token);

        // Extract the authenticated user's email and ID from the token
        String authenticatedEmail = jwtService.extractUsername(token); // Extract email
        System.out.println(authenticatedEmail);
        String authenticatedUserId = jwtService.getUserIdFromToken(token); // Extract user ID

        Integer userId = Integer.parseInt(authenticatedUserId); // Convert to Integer if necessary
        System.out.println(userId);

        // Fetch the user from the repository
        User authenticatedUser = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // Ensure the authenticated user's ID matches the ID from the token
        if (!authenticatedUser.getId().equals(userId)) {
            return ResponseEntity.status(403).body("You are not authorized to update this user's details");
        }

        // Update fields
        authenticatedUser.setFirstname(updateRequest.getFirstname());
        authenticatedUser.setLastname(updateRequest.getLastname());
        authenticatedUser.setPhone(updateRequest.getPhone());

        // Save changes to the repository
        userRepository.save(authenticatedUser);

        return ResponseEntity.ok("User details updated successfully");
    }




}
