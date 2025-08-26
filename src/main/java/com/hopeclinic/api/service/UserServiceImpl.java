package com.hopeclinic.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hopeclinic.api.models.Role;
import com.hopeclinic.api.models.User;
import com.hopeclinic.api.repositories.RoleRepository;
import com.hopeclinic.api.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService{
    private UserRepository userRepository;
    private RoleRepository rolesRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository rolesRepository) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
    }

    @Override
    public User save(User user){
        List<Role> roles = new ArrayList<>();
        user.getRolesName().forEach(roleName -> {
            Optional<Role> role = this.rolesRepository.findByName(roleName);
            role.ifPresent(roles::add);
        });

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.userRepository.saveAndFlush(user);
    }
}
