package com.teamtacles.teamtacles_api.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.teamtacles.teamtacles_api.dto.authentication.AuthenticationDTO;
import com.teamtacles.teamtacles_api.service.AuthenticationService;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("authenticate")
    public String authenticate(@RequestBody AuthenticationDTO request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword());
        return authenticationService.authenticate(authentication);
    }
}