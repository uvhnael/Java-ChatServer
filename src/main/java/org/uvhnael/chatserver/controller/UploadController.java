package org.uvhnael.chatserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.uvhnael.chatserver.dto.ErrorResponse;
import org.uvhnael.chatserver.dto.FileUploadResponse;
import org.uvhnael.chatserver.service.FileService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
public class UploadController {
    private static final String UPLOAD_DIR = "uploads/";

    private final FileService fileService;
    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(
            @RequestParam("userId") String userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("chatId") String chatId
    ) {
        try {
            // Save the file
            String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            Path path = Paths.get(UPLOAD_DIR + date + "_" + file.getOriginalFilename());
            Files.createDirectories(path.getParent()); // Create the directory if it doesn't exist
            file.transferTo(path);

            fileService.saveFile(userId, chatId, path.toString(), file.getContentType(), file.getSize());




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

    @GetMapping("/files/{chatId}")
    public ResponseEntity<?> getFilesByChatId(@PathVariable String chatId) {
        try {
            List<FileUploadResponse> files = fileService.getFilesByChatId(chatId);
            return ResponseEntity.ok(files);
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