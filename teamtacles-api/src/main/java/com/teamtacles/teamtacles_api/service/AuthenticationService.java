package com.teamtacles.teamtacles_api.service;

import com.teamtacles.teamtacles_api.repository.UserRepository;
import com.teamtacles.teamtacles_api.exception.ResourceNotFoundException;
import com.teamtacles.teamtacles_api.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    
    public AuthenticationService(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public String authenticate(Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByUserName(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not Found."));
        return jwtService.generateToken(user);
    }
}
