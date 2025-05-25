package com.teamtacles.teamtacles_api.service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import com.teamtacles.teamtacles_api.model.User;
import com.teamtacles.teamtacles_api.model.UserAuthenticated;
import com.teamtacles.teamtacles_api.repository.UserRepository;

@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    private UserDetailService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user = userRepository.findByUserNameIgnoreCase(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username " + username));

        return new UserAuthenticated(user);
    }
}
