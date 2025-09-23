package com.ajayaraj.cloudbox.service;

import com.ajayaraj.cloudbox.dto.LoginRequest;
import com.ajayaraj.cloudbox.dto.RegisterRequest;
import com.ajayaraj.cloudbox.exception.ConflictException;
import com.ajayaraj.cloudbox.exception.NotFoundException;
import com.ajayaraj.cloudbox.model.User;
import com.ajayaraj.cloudbox.repository.UserRepository;
import com.ajayaraj.cloudbox.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthenticationService {

    private UserRepository userRepository;
    private JwtUtil jwtUtil;

    public AuthenticationService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
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

    public String login(LoginRequest loginRequest) {
        if(isValidUser(loginRequest)) {
            return jwtUtil.generateToken(loginRequest.getEmailId());
        }
        return "Email or password incorrect";
    }

    public boolean isEmailTaken(String emailId) {
        User user = userRepository.findByEmailId(emailId);

        return user != null;
    }

    public String hashPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public boolean isValidUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmailId(loginRequest.getEmailId());

        if(user == null) {
            throw new NotFoundException("User with specified email id : " + loginRequest.getEmailId() + " not found!");
        }

        return new BCryptPasswordEncoder().matches(loginRequest.getPassword(), user.getPasswordHash());
    }
}
