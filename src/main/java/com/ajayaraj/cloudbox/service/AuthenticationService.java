package com.ajayaraj.cloudbox.service;

import com.ajayaraj.cloudbox.dto.RegisterRequest;
import com.ajayaraj.cloudbox.exception.ConflictException;
import com.ajayaraj.cloudbox.model.User;
import com.ajayaraj.cloudbox.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthenticationService {

    private UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(RegisterRequest registerRequest) {

        if(isEmailTaken(registerRequest.getEmailId())) {
            throw new ConflictException("Email id already exists!");
        }

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmailId(registerRequest.getEmailId());
        user.setPasswordHash(hashPassword(registerRequest.getPassword()));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        return userRepository.save(user);
    }

    public boolean isEmailTaken(String emailId) {
        User user = userRepository.findByEmailId(emailId);

        return user != null;
    }

    public String hashPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

}
