package com.example.booksharing.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    private static final String BUCKET_NAME = "book-sharing-app-hafiz-2026";

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) throws IOException {

        String originalFileName = file.getOriginalFilename();

        String fileName = UUID.randomUUID() + "-" + originalFileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(file.getBytes())
        );

        return fileName;
    }

    public void deleteFile(String fileName) {

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }
}