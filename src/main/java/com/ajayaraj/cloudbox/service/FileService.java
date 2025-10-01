package com.ajayaraj.cloudbox.service;

import com.ajayaraj.cloudbox.model.File;
import com.ajayaraj.cloudbox.model.User;
import com.ajayaraj.cloudbox.repository.FileRepository;
import com.ajayaraj.cloudbox.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

@Service
public class FileService {

    private FileRepository fileRepository;
    private UserRepository userRepository;
    private AmazonS3Client s3Client;
    private AuthenticationService authenticationService;

    public FileService(FileRepository fileRepository, UserRepository userRepository, AmazonS3Client s3Client, AuthenticationService authenticationService) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.s3Client = s3Client;
        this.authenticationService = authenticationService;
    }

    public String uploadFile(MultipartFile file) throws IOException {

        String path = s3Client.uploadFile(file);

        User currentUser = authenticationService.getCurrentUser();

        File uploadedFile = new File();
        uploadedFile.setFileName(file.getOriginalFilename());
        uploadedFile.setUrl(path);
        uploadedFile.setOwnerId(currentUser.getId());
        uploadedFile.setSize(file.getSize());
        uploadedFile.setOwnerName(currentUser.getFirstName() + " " + currentUser.getLastName());
        uploadedFile.setCreatedAt(new Date());

        fileRepository.save(uploadedFile);

        return path;
    }

}
