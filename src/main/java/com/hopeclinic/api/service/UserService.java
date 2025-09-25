package com.hopeclinic.api.service;
import com.hopeclinic.api.models.User;

public interface UserService {
    User save(User user);
    
    void assignRole(User user, String roleName);
}
