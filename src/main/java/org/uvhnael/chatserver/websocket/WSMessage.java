package org.uvhnael.chatserver.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.uvhnael.chatserver.model.Attachment;
import org.uvhnael.chatserver.model.Message;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSMessage {
    private String type = "MESSAGE";
    private String chatId;
    private String senderId;
    private String senderName;
    private String content;
    private String timestamp;
    private List<Attachment> attachments;
}
