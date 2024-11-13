package org.uvhnael.chatserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendResponse {
    private String id;
    private String image;
    private String username;

}
