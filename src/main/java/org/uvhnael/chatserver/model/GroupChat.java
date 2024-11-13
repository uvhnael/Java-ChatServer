package org.uvhnael.chatserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "group_chats")
public class GroupChat {
    @Id
    private String id;
    private String name;
    private String adminId;
    private List<GroupMember> members = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
    private String createdAt;


    // Getters và Setters
}

