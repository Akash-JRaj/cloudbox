package com.ajayaraj.cloudbox.service;

import com.ajayaraj.cloudbox.model.User;
import com.ajayaraj.cloudbox.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        return userRepository.save(user);
    }

}
