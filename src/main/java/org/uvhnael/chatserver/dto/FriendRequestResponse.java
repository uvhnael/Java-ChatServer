package org.uvhnael.chatserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestResponse {
    private String requestId;
    private String fromUser;
    private String username;
    private String image;
    private String status;
}
