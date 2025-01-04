package com.magazin.magazina.config;

import com.magazin.magazina.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "VGhpcyBpcyBhIHZlcnkgc2VjdXJlIGtleSBvZiAyNTYtYml0cw==";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    public String generateToken(UserDetails userDetails) {
        // Extract the user-specific information (user id, username, role)
        Integer userId = ((User) userDetails).getId();  // Assuming CustomUserDetails has getUserId method
        String username = userDetails.getUsername();

        // Retrieve the role directly from the User object (assumes 'getRole' method exists in your User class)
        String role = String.valueOf(((User) userDetails).getRole());  // Assuming User class has a 'getRole' method

        // If the role is null or empty, default to "USER"
        if (role == null || role.isEmpty()) {
            role = "USER";
        }

        System.out.println("\n\n\nRole: " + role);  // Debugging line to check the role value

        // Create a map of additional claims
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", String.valueOf(userId));
        extraClaims.put("username", username);
        extraClaims.put("role", role);  // Store the role name (e.g., ADMIN, USER)

        return generateToken(extraClaims, userDetails);  // Generate the token
    }



    // Refactored method to generate token with extra claims
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        System.out.println(userDetails);
        System.out.println("Token Claims: " + extraClaims);
        return Jwts
                .builder()
                .setClaims(extraClaims) // Add extra claims to the token
                .setSubject(userDetails.getUsername()) // Set the username as the subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // Issue time of the token
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 90)) // Token expiry time (90 days)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign the token with the secret key
                .compact(); // Build and return the token
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        System.out.println("Decoded Claims: " + claims);
        System.out.println("Decoded Subject: " + claims.getSubject());
        System.out.println("Decoded UserId: " + claims.get("userId"));

        // Retrieve the userId as a String and convert it if needed
        Object userIdObject = claims.get("userId");
        if (userIdObject instanceof String) {
            return (String) userIdObject; // Return the String representation
        } else if (userIdObject instanceof Integer) {
            return userIdObject.toString(); // Convert Integer to String
        } else {
            throw new IllegalArgumentException("Unexpected type for userId claim");
        }
    }



    /*public String getUsernameFromToken(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Extract the username instead of ID if it's stored in the 'username' field
        String username = claims.get("username", String.class);
        System.out.println("Extracted username: " + username);

        return username;
    }


    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map <String, Object> extraClaims,
            UserDetails userDetails
    ) {
        System.out.println(userDetails);
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 90))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            String userId,
            String username,
            String role
    ) {
        // Add user-specific claims
        extraClaims.put("userId", userId);
        extraClaims.put("username", username);
        extraClaims.put("role", role);

        return Jwts
                .builder()
                .setClaims(extraClaims) // Set all claims
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Token issue time
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 90)) // Token expiration time
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign with key and algorithm
                .compact(); // Build the token
    }*/
    public boolean isTokenValid(String token , UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);

    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
