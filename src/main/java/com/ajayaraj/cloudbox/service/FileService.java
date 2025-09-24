package com.ajayaraj.cloudbox.service;

import com.ajayaraj.cloudbox.repository.FileRepository;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    private FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

}
