package com.ajayaraj.cloudbox.controller;

import com.ajayaraj.cloudbox.dto.RegisterRequest;
import com.ajayaraj.cloudbox.model.User;
import com.ajayaraj.cloudbox.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Date;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = new User();
        user.setEmailId(registerRequest.getEmailId());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPasswordHash(registerRequest.getPassword());
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.register(user));
    }

}
