package org.uvhnael.chatserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.uvhnael.chatserver.model.Attachment;
import org.uvhnael.chatserver.model.Message;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String senderId;
    private String senderName;
    private String content;
    private String timestamp;
    private List<String> readBy = new ArrayList<>();
    private List<Attachment> attachments = new ArrayList<>();

    public MessageResponse(String senderId, String senderName, String content, String timestamp, List<Attachment> attachments) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
        this.attachments = attachments;
    }

    public MessageResponse(Message m, String senderName) {
        this.senderId = m.getSenderId();
        this.senderName = senderName;
        this.content = m.getContent();
        this.timestamp = m.getTimestamp();
        this.readBy = m.getReadBy();
        this.attachments = m.getAttachments();
    }

    // Getters và Setters
}