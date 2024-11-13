package org.uvhnael.chatserver.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSFileMessage {
    private long senderId;
    private long receiverId;
    private String fileName;
    private String fileData;
    private String timestamp;
}
