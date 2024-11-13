package org.uvhnael.chatserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "files")
public class File {
    @Id
    private String id;
    private String uploadedBy;
    private String fileUrl;
    private String fileType;
    private long fileSize;
    private String uploadTimestamp;
    private String chatId;

    // Getters và Setters
}