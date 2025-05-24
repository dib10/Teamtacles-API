package com.teamtacles.teamtacles_api.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.teamtacles.teamtacles_api.dto.request.UserRequestDTO;
import com.teamtacles.teamtacles_api.dto.response.UserResponseDTO;
import com.teamtacles.teamtacles_api.model.Role;
import com.teamtacles.teamtacles_api.model.User;
import com.teamtacles.teamtacles_api.model.enums.ERole;
import com.teamtacles.teamtacles_api.repository.RoleRepository;
import com.teamtacles.teamtacles_api.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO){
        if(userRepository.existsByUserName(userRequestDTO.getUserName())){
            throw new RuntimeException("Erro username"); 
        }
        if(userRepository.existsByEmail(userRequestDTO.getEmail())){
            throw new RuntimeException("Erro email");
        }
        if(!userRequestDTO.getPassword().equals(userRequestDTO.getPasswordConfirm())){
            throw new RuntimeException("Erro senha diferente");
        }

        User convertedUser = modelMapper.map(userRequestDTO, User.class);
        // Role userRole = roleRepository.findByRoleName(ERole.USER)
            //.orElseThrow(() -> new RuntimeException("Error: Role USER not found."));

        // convertedUser.setRole(userRole);
        User cretaedUser = userRepository.save(convertedUser);
        return modelMapper.map(cretaedUser, UserResponseDTO.class);
    }
}