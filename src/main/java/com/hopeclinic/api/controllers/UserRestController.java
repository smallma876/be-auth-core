package com.hopeclinic.api.controllers;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hopeclinic.api.Dtos.UserResponseDto;
import com.hopeclinic.api.models.User;
import com.hopeclinic.api.service.UserService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        userService.assignRole(user, "ROLE_PATIENT");
        User savedUser = userService.save(user);
        return buildResponse(savedUser, "✅ Paciente registrado con éxito");
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        User savedUser = userService.save(user);
        return buildResponse(savedUser, "✅ Empleado registrado con éxito");
    }
    
    private ResponseEntity<?> buildResponse(User savedUser, String message) {
        List<String> roles = savedUser.getRoles()
                                      .stream()
                                      .map(role -> role.getName())
                                      .collect(Collectors.toList());

        UserResponseDto responseDto = new UserResponseDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getProfilePictureUrl(),
                roles,
                message
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> {
            errors.put(error.getField(),
                    "El campo " + error.getField() + " " + error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
