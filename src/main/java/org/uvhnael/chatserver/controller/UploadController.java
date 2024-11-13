package org.uvhnael.chatserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.uvhnael.chatserver.dto.ErrorResponse;
import org.uvhnael.chatserver.model.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/uploads")
public class UploadController {
    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Save the file
            String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            Path path = Paths.get(UPLOAD_DIR + date + "_" + file.getOriginalFilename());
            Files.createDirectories(path.getParent()); // Create the directory if it doesn't exist
            file.transferTo(path);

            FileUploadResponse response = new FileUploadResponse(path.toString(), file.getContentType(), file.getSize());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Internal server error", e.getMessage()));
        }
    }

    @PostMapping("/files")
    public ResponseEntity<?> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        List<FileUploadResponse> responses = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                // Save the file
                String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                Path path = Paths.get(UPLOAD_DIR + date + "_" + file.getOriginalFilename());
                Files.createDirectories(path.getParent()); // Create the directory if it doesn't exist
                file.transferTo(path);

                responses.add(new FileUploadResponse(path.toString(), file.getContentType(), file.getSize()));
            }
            return ResponseEntity.status(HttpStatus.OK).body(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Internal server error", e.getMessage()));
        }
    }

    @GetMapping("{filename}")
    public ResponseEntity<?> getFile(@PathVariable String filename) {
        try {
            Path path = Paths.get(UPLOAD_DIR + filename);
            if (!Files.exists(path)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
            }
            byte[] readAllBytes = Files.readAllBytes(path);
            return ResponseEntity.ok(readAllBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Internal server error", e.getMessage()));
        }
    }

    private Object createErrorResponse(String error, String message) {
        return new ErrorResponse(error, message);
    }

}