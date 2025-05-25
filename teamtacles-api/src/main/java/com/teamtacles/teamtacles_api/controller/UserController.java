package com.teamtacles.teamtacles_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamtacles.teamtacles_api.dto.request.ERoleRequestDTO;
import com.teamtacles.teamtacles_api.dto.request.UserRequestDTO;
import com.teamtacles.teamtacles_api.dto.response.UserResponseDTO;
import com.teamtacles.teamtacles_api.model.User;
import com.teamtacles.teamtacles_api.service.UserService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO){
        User userCreated = userService.createUser(userRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(userCreated);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id_user}/exchangepaper")
    public ResponseEntity<User> exchangepaperUser(@PathVariable("id_user") Long id, @Valid @RequestBody ERoleRequestDTO eRoleRequestDTO){
        User userChanged = userService.exchangepaperUser(id, eRoleRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(userChanged);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}