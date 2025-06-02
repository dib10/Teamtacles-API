package com.teamtacles.teamtacles_api.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.teamtacles.teamtacles_api.dto.authentication.AuthenticationDTO;
import com.teamtacles.teamtacles_api.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Authenticate User", description = "Authenticates a user using the provided username and password, and returns a JWT token.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful, returns JWT token."),
        @ApiResponse(responseCode = "404", description = "Not found: Muser not found."),
        @ApiResponse(responseCode = "500", description = "Internal Server Error: Unmapped error.")
    })
    @PostMapping("authenticate")
    public String authenticate(@RequestBody @Parameter(description = "User credentials for authentication (username and password).") AuthenticationDTO request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword());
        return authenticationService.authenticate(authentication);
    }
}