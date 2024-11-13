package org.uvhnael.chatserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uvhnael.chatserver.dto.FileUploadResponse;
import org.uvhnael.chatserver.model.File;
import org.uvhnael.chatserver.repository.FileRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    public void saveFile(String userId, String chatId, String fileUrl, String fileType, long fileSize) {
        File file = new File();
        file.setUploadedBy(userId);
        file.setChatId(chatId);
        file.setFileUrl(fileUrl);
        file.setFileType(fileType);
        file.setFileSize(fileSize);
        file.setUploadTimestamp(new Timestamp(System.currentTimeMillis()).toString());
        fileRepository.save(file);
    }

    public List<FileUploadResponse> getFilesByChatId(String chatId) {
        List<File> files = fileRepository.findByChatId(chatId);
        List<FileUploadResponse> fileUploadResponses = new ArrayList<>();
        files.sort(Comparator.comparing(File::getUploadTimestamp).reversed());
        files.forEach(f -> {
            fileUploadResponses.add(new FileUploadResponse(f.getFileUrl(), f.getFileType(), f.getFileSize()));
        });
        return fileUploadResponses;
    }
}
