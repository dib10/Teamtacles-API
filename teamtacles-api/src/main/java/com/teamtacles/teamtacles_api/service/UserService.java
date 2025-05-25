package com.teamtacles.teamtacles_api.service;

import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.teamtacles.teamtacles_api.dto.request.UserRequestDTO;
import com.teamtacles.teamtacles_api.dto.response.UserResponseDTO;
import com.teamtacles.teamtacles_api.exception.EmailAlreadyExistsException;
import com.teamtacles.teamtacles_api.exception.PasswordMismatchException;
import com.teamtacles.teamtacles_api.exception.UsernameAlreadyExistsException;
import com.teamtacles.teamtacles_api.model.Role;
import com.teamtacles.teamtacles_api.model.User;
import com.teamtacles.teamtacles_api.model.enums.ERole;
import com.teamtacles.teamtacles_api.repository.RoleRepository;
import com.teamtacles.teamtacles_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public User createUser(UserRequestDTO userRequestDTO){
        if(userRepository.existsByUserName(userRequestDTO.getUserName())){
            throw new UsernameAlreadyExistsException("Username already exists"); 
        }
        if(userRepository.existsByEmail(userRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if(!userRequestDTO.getPassword().equals(userRequestDTO.getPasswordConfirm())){
            throw new PasswordMismatchException("Password and confirmation don't match");
        }

        User user = new User();
        user.setUserName(userRequestDTO.getUserName());
        user.setEmail(userRequestDTO.getEmail());
        user.setUserName(passwordEncoder.encode(userRequestDTO.getPassword()));
        Role userRole = roleRepository.findByRoleName(ERole.USER)
            .orElseThrow(() -> new RuntimeException("Error: Role USER not found."));
        user.setRoles(Set.of(userRole));
        
        return userRepository.save(user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}