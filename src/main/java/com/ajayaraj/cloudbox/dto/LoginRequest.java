package com.ajayaraj.cloudbox.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email Id cannot be blank")
    @Email(message = "Email must be a valid email address")
    private String emailId;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at lease 8 character length")
    private String password;

}
