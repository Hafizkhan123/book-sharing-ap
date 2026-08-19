package com.example.booksharing.controller;

import com.example.booksharing.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file) {

        try {
            String fileName = s3Service.uploadFile(file);

            return ResponseEntity.ok(
                    "File uploaded successfully: " + fileName
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<String> deleteFile(
            @PathVariable String fileName) {

        try {
            s3Service.deleteFile(fileName);

            return ResponseEntity.ok(
                    "File deleted successfully"
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Delete failed: " + e.getMessage());
        }
    }
}
