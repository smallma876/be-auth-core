package com.hopeclinic.api.config;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hopeclinic.api.models.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
    private AuthenticationManager authenticationManager;
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
        "RXdKSps6tFLqkWgdIKGs4EFo696TnYjn7mR+6s+dSHo=".getBytes()  
    );

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        User user = null;
        String username = null;
        String password = null;
        try{
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            username = user.getUsername();
            password = user.getPassword();
        } catch (StreamReadException e){
            e.printStackTrace();
        } catch (DatabindException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return this.authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authResult.getPrincipal();
        String username = user.getUsername();
        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();
        Claims claims = Jwts.claims()
            .add("authorities", roles) 
            .add("username", username)
            .build(); 
        String jws = Jwts.builder()
            .subject(username)
            .claims(claims) 
            .expiration(new Date(System.currentTimeMillis() + 3600000)) 
            .issuedAt(new Date()) 
            .signWith(SECRET_KEY) 
            .compact(); 
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("token", jws);
        cookie.setHttpOnly(true);   
        cookie.setSecure(false);    
        cookie.setPath("/");
        cookie.setMaxAge(3600);  
        response.addCookie(cookie);
        Map<String, String> body = Map.of(
            "token", jws, 
            "username", username, 
            "message", String.format("Hola %s has iniciado sesión con éxito", username)
        );
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setContentType("application/json");
        response.setStatus(200); 
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        Map<String, String> body = Map.of(
            "message", "Error en la autenticación: Usuario y/o clave incorrectos", 
            "error", failed.getMessage() 
        );
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setContentType("application/json");
        response.setStatus(401);
    }
}
