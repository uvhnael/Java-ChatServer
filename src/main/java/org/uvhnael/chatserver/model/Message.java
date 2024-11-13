package org.uvhnael.chatserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String senderId;
    private String content;
    private String timestamp;
    private List<String> readBy = new ArrayList<>();
    private List<Attachment> attachments = new ArrayList<>();

    public Message(String senderId, String content, String timestamp, List<Attachment> attachments) {
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
        this.attachments = attachments;
    }

    // Getters và Setters
}
