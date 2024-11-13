package org.uvhnael.chatserver.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.uvhnael.chatserver.dto.CreateGroupRequest;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSCreateGroup {
    private String type = "CREATE_GROUP";
    private String id;
    private String name;
    private String lastMessage = "";
    private String timestamp = new Timestamp(System.currentTimeMillis()).toString();
    private int unreadCount = 1;
    private boolean isGroup = true;

    public WSCreateGroup(String id, String name) {
        this.id = id;
        this.name = name;
    }

}
