package org.uvhnael.chatserver.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSFriendRequestAccept {
    private String type = "FRIEND_REQUEST_ACCEPTED";
    private String receiverId;
    private String id;
    private String name;
    private String lastMessage = "";
    private String timestamp = new Timestamp(System.currentTimeMillis()).toString();
    private int unreadCount = 1;
    private boolean isGroup = true;
}
