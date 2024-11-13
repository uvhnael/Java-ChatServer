package org.uvhnael.chatserver.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSFriendRequest {
    private long senderId;
    private long receiverId;
    private String timestamp;
}
