package com.ajayaraj.cloudbox.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String firstName;
    private String lastName;
    private String emailId;
    private String passwordHash;
    private Date createdAt;
    private Date updatedAt;
}
