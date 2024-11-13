package org.uvhnael.chatserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatListResponse {
    private String id;
    private String name;
    private String lastMessage;
    private String timestamp;
    private int unreadCount;
    private boolean isGroup;

}
