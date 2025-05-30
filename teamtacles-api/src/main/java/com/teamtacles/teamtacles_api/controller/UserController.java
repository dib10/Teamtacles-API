package com.teamtacles.teamtacles_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamtacles.teamtacles_api.dto.page.PagedResponse;
import com.teamtacles.teamtacles_api.dto.request.RoleRequestDTO;
import com.teamtacles.teamtacles_api.dto.request.UserRequestDTO;
import com.teamtacles.teamtacles_api.dto.response.UserResponseDTO;
import com.teamtacles.teamtacles_api.model.User;
import com.teamtacles.teamtacles_api.service.UserService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO){
        UserResponseDTO userCreated = userService.createUser(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id_user}/exchangepaper")
    public ResponseEntity<UserResponseDTO> exchangepaperUser(@PathVariable("id_user") Long id, @Valid @RequestBody RoleRequestDTO roleRequestDTO){
        UserResponseDTO userChanged = userService.exchangepaperUser(id, roleRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(userChanged);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PagedResponse<UserResponseDTO>> getAllUsers(Pageable pageable) {
        PagedResponse<UserResponseDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
}