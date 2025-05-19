package com.teamtacles.teamtacles_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserResponseDTO{
    private Long id;
    private String userName;
    private String email;
    private String password;
    private String role;
}