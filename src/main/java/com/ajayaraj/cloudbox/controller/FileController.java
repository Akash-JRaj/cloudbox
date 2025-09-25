package com.ajayaraj.cloudbox.controller;

import com.ajayaraj.cloudbox.model.File;
import com.ajayaraj.cloudbox.service.AmazonClient;
import com.ajayaraj.cloudbox.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileController {

    private FileService fileService;
    private AmazonClient amazonClient;

    public FileController(FileService fileService, AmazonClient amazonClient) {
        this.fileService = fileService;
        this.amazonClient = amazonClient;
    }

    @PostMapping("/upload")
    public ResponseEntity<File> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fileService.handleUpload(file));
    }

    @PostMapping("/upload/s3")
    public ResponseEntity<String> uploadFileS3(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(amazonClient.uploadFile(file));
    }
}
