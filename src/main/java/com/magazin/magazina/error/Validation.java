package com.magazin.magazina.error;

import com.magazin.magazina.models.Role;

public class Validation {

    // Validate the role and return the corresponding Role enum, default to Role.USER if invalid or null
    public Role validateRole(String role) {
        if (role == null || role.isEmpty()) {
            return Role.USER;  // Return default if role is null or empty
        }

        try {
            // Convert to uppercase to handle case insensitivity, then map to Role enum
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException ex) {
            // Return default role if the role is invalid
            return Role.USER;
        }
    }
}

