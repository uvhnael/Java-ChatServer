package org.uvhnael.chatserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequest {
    private String requestId;
    private String fromUser;
    private String status; // pending, accepted, rejected

    // Getters và Setters
}
