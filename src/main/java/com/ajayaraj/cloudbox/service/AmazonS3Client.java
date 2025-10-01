package com.ajayaraj.cloudbox.service;

import com.ajayaraj.cloudbox.exception.S3ClientException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AmazonS3Client {

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

    public void createBucket(String bucketName) {
        try {
            CreateBucketRequest request = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(request);
        } catch (S3Exception ex) {
            throw new S3ClientException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        try {
            UUID currentUserId = authenticationService.getCurrentUserId();
            String objectKey = currentUserId + "/" + file.getOriginalFilename().replaceAll(" ", "_");

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            return objectKey;
        } catch (IOException exception) {
            throw new IOException(exception.getMessage());
        } catch (NoSuchBucketException ex) {
            throw new S3ClientException("Bucket not found! Bucket name : " + bucketName, HttpStatus.NOT_FOUND);
        } catch (S3Exception ex) {
            throw new S3ClientException("S3 Error : " + ex.awsErrorDetails().errorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            throw new S3ClientException("Unexpected error while uploading to s3 : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public byte[] downloadFile(String objectKey) {
        try {

            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            ResponseBytes<GetObjectResponse> response = s3Client.getObject(request, ResponseTransformer.toBytes());

            return response.asByteArray();

        } catch (NoSuchKeyException ex) {
            throw new S3ClientException("File not found : " + objectKey, HttpStatus.NOT_FOUND);
        } catch (NoSuchBucketException ex) {
            throw new S3ClientException("Bucket not found! Bucket name : " + bucketName, HttpStatus.NOT_FOUND);
        } catch (S3Exception ex) {
            throw new S3ClientException("S3 Error : " + ex.awsErrorDetails().errorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            throw new S3ClientException("Unexpected error while uploading to s3", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteFile(String objectKey) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.deleteObject(request);
        } catch (S3Exception ex) {
            throw new S3ClientException("S3 Error : " + ex.awsErrorDetails().errorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            throw new S3ClientException("Unexpected error while uploading to s3", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<String> listObjects() {
        try {
            List<String> list = new ArrayList<>();
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);

            response.contents().forEach(s3Object -> {
                list.add("Key : " + s3Object.key());
            });

            return list;

        } catch (NoSuchBucketException ex) {
            throw new S3ClientException("Bucket not found! Bucket name : " + bucketName, HttpStatus.NOT_FOUND);
        } catch (S3Exception ex) {
            throw new S3ClientException("S3 Error : " + ex.awsErrorDetails().errorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            throw new S3ClientException("Unexpected error while uploading to s3", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void copyObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey) {
        try {

            CopyObjectRequest request = CopyObjectRequest.builder()
                    .sourceBucket(sourceBucket)
                    .sourceKey(sourceKey)
                    .destinationBucket(destinationBucket)
                    .destinationKey(destinationKey)
                    .build();

            CopyObjectResponse response = s3Client.copyObject(request);

        } catch (NoSuchBucketException ex) {
            throw new S3ClientException("Bucket not found!" + ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (S3Exception ex) {
            throw new S3ClientException("S3 Error : " + ex.awsErrorDetails().errorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            throw new S3ClientException("Unexpected error while uploading to s3", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public URL generatePresignedGetUrl(String objectKey) {
        try {

            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(request)
                    .signatureDuration(Duration.ofMinutes(5))
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

            return presignedRequest.url();

        } catch (NoSuchKeyException ex) {
            throw new S3ClientException("File not found : " + objectKey, HttpStatus.NOT_FOUND);
        } catch (NoSuchBucketException ex) {
            throw new S3ClientException("Bucket not found! Bucket name : " + bucketName, HttpStatus.NOT_FOUND);
        } catch (S3Exception ex) {
            throw new S3ClientException("S3 Error : " + ex.awsErrorDetails().errorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            throw new S3ClientException("Unexpected error while uploading to s3", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public URL generatePresignedPutUrl(String fileName) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(authenticationService.getCurrentUserId() + "/" + fileName)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .putObjectRequest(request)
                    .signatureDuration(Duration.ofMinutes(5))
                    .build();

            PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(presignRequest);

            return presignedPutObjectRequest.url();

        } catch (NoSuchBucketException ex) {
            throw new S3ClientException("Bucket not found! Bucket name : " + bucketName, HttpStatus.NOT_FOUND);
        } catch (S3Exception ex) {
            throw new S3ClientException("S3 Error : " + ex.awsErrorDetails().errorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            throw new S3ClientException("Unexpected error while uploading to s3", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
