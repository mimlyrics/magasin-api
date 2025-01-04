package com.magazin.magazina.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

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
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired
    private AuthenticationService service;

    @Value("${env}")
    private String env;


    @PostMapping("/register")
    public ResponseEntity < AuthenticationResponse > register(
        @RequestBody RegisterRequest request
    ) {
        //return ResponseEntity.ok(service.register(request));
        // Retrieve the token using getToken()
        AuthenticationResponse authResponse = service.register(request);
        System.out.println(authResponse);
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", authResponse.getToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .path("/")
                .maxAge(30 * 24 * 60 * 60*100)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(authResponse);
    }


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response,
            @CookieValue(name = "jwt", required = false) String existingJwt) {

        AuthenticationResponse authResponse = service.authenticate(request);

        if (authResponse == null) {
            return ResponseEntity.status(401).body(null);  // Unauthorized if authentication fails
        }

        // Clear existing JWT cookie if it exists
        if (existingJwt != null) {
            clearCookie("jwt", response);
        }

        // Set domain dynamically based on the environment
        String domain = "localhost"; // Default for local development

// Check if we are in production environment
        if (isProduction()) {
            domain = ".onrender.com"; // Production domain for subdomains
        }

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", authResponse.getToken())
                .httpOnly(true)
                .secure(true) // Ensure the cookie is secure when using HTTPS
                .sameSite("None") // Required for cross-origin requests
                .path("/") // Available to all paths
                .domain(domain)
                .maxAge(30 * 24 * 60 * 60)
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(authResponse);
    }

    private boolean isProduction() {
        // Check if the application is running in production (e.g., using an environment variable)
        return env != null && env.equals("production");
    }


    // Utility method to clear the cookie
    private void clearCookie(String cookieName, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire the cookie immediately
        response.addCookie(cookie);
    }


    // Logout method
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Clear the JWT cookie by setting its max age to 0 (immediate expiration)
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false) // Set to true in production
                .sameSite("None")
                .path("/")
                .maxAge(0) // Immediately expire the cookie
                .build();

        // Return a response that clears the cookie
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .build();
    }

}
