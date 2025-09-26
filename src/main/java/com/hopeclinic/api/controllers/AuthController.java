package com.hopeclinic.api.controllers;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hopeclinic.api.Dtos.LoginRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
        "RXdKSps6tFLqkWgdIKGs4EFo696TnYjn7mR+6s+dSHo=".getBytes()
    );

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        org.springframework.security.core.userdetails.User user =
            (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        String username = user.getUsername();

        List<String> roleNames = authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        Claims claims = Jwts.claims()
            .add("authorities", roleNames) 
            .add("username", username)
            .build();

        String jws = Jwts.builder()
            .subject(username)
            .claims(claims)
            .expiration(new Date(System.currentTimeMillis() + 3600000)) 
            .issuedAt(new Date(System.currentTimeMillis()))
            .signWith(SECRET_KEY)
            .compact();

        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("token", jws);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);

        Map<String, Object> body = Map.of(
            "token", jws,
            "username", username,
            "roles", roleNames,
            "message", String.format("Hola %s, has iniciado sesión con éxito", username)
        );

        return ResponseEntity.ok(body);
    }
}
