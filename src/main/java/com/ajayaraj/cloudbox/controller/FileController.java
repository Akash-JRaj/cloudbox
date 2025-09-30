package com.ajayaraj.cloudbox.controller;

import com.ajayaraj.cloudbox.service.AmazonS3Client;
import com.ajayaraj.cloudbox.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    private FileService fileService;
    private AmazonS3Client amazonS3Client;

    public FileController(FileService fileService, AmazonS3Client amazonS3Client) {
        this.fileService = fileService;
        this.amazonS3Client = amazonS3Client;
    }

    @PostMapping("/s3/upload")
    public ResponseEntity<String> uploadFileToS3(@RequestBody MultipartFile file) throws IOException {
        String url = amazonS3Client.uploadFile(file);

        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

    @GetMapping("/s3/download")
    public ResponseEntity<byte[]> downloadFileFromS3(@RequestParam String key) {
        byte[] fileBytes = amazonS3Client.downloadFile(key);

        return ResponseEntity.status(HttpStatus.OK).body(fileBytes);
    }

    @DeleteMapping("/s3/delete")
    public ResponseEntity<Void> deleteFileFromS3(@RequestParam String key) {
        amazonS3Client.deleteFile(key);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/s3/list")
    public ResponseEntity<List<String>> listObjectsFromBucket() {
        return ResponseEntity.status(HttpStatus.OK).body(amazonS3Client.listObjects());
    }

    @GetMapping("/s3/presignedget")
    public ResponseEntity<URL> getPresignedGetUrl(@RequestParam String key) {
        URL url = amazonS3Client.generatePresignedGetUrl(key);

        return ResponseEntity.status(HttpStatus.OK).body(url);
    }

    @GetMapping("/s3/presignedput")
    public ResponseEntity<URL> getPresignedPutUrl(@RequestParam String fileName) {
        URL url = amazonS3Client.generatePresignedPutUrl(fileName);

        return ResponseEntity.status(HttpStatus.OK).body(url);
    }
}
