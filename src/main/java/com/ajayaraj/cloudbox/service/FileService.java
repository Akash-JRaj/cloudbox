package com.ajayaraj.cloudbox.service;

import com.ajayaraj.cloudbox.model.File;
import com.ajayaraj.cloudbox.model.User;
import com.ajayaraj.cloudbox.repository.FileRepository;
import com.ajayaraj.cloudbox.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
public class FileService {

    private FileRepository fileRepository;
    private UserRepository userRepository;

    public FileService(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    public File handleUpload(MultipartFile file) {
        File uploadedFile = new File();

        String userEmailId = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmailId(userEmailId);

        uploadedFile.setFileName(file.getOriginalFilename());
        uploadedFile.setSize(file.getSize());
        uploadedFile.setOwnerId(currentUser.getId());
        uploadedFile.setOwnerName(currentUser.getFirstName());
        uploadedFile.setCreatedAt(new Date());
        uploadedFile.setUrl("/users/akash/dummy/file/start");

        return fileRepository.save(uploadedFile);
    }

}
