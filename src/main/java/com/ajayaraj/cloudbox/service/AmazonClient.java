package com.ajayaraj.cloudbox.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class AmazonClient {

    @Value("${aws.endpointUrl}")
    private String endpointUrl;

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    private S3Client s3Client;

    private S3Presigner s3Presigner;

    @Autowired
    private AuthenticationService authenticationService;

    @PostConstruct
    public void init() {

        AwsBasicCredentials credentials = AwsBasicCredentials.create(this.accessKey, this.secretKey);

        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        s3Client = S3Client.builder()
                .region(Region.EU_NORTH_1)
                .credentialsProvider(credentialsProvider)
                .build();

        s3Presigner = S3Presigner.builder()
                .region(Region.EU_NORTH_1)
                .credentialsProvider(credentialsProvider)
                .build();
    }

    public String uploadFile(MultipartFile file) {
        String fileUrl = null;
        try {
            UUID currentUserId = authenticationService.getCurrentUserId();
            String key = currentUserId + "/" + file.getOriginalFilename().replaceAll(" ", "_");

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(this.bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            fileUrl = endpointUrl + "/" + bucketName + "/" + key;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return fileUrl;
    }

    public byte[] downloadFile(String objectKey) throws Exception {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key("abebefdb-d878-4955-b0ef-9889b8e3c406/Akash_Jayaraj_SSE.pdf")
                    .build();

            ResponseInputStream<GetObjectResponse> file = s3Client.getObject(getObjectRequest);

            return file.readAllBytes();
        }
        catch (IOException e) {
            throw new IOException(e);
        }
        catch (Exception e) {
            throw new Exception(e);
        }
    }

    public String getUrl(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key("abebefdb-d878-4955-b0ef-9889b8e3c406/Akash_Jayaraj_SSE.pdf")
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofSeconds(30))
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedGetObjectRequest.url().toString();
    }


}
