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
@Document(collection = "private_chats")
public class PrivateChat {
    @Id
    private String id;
    private List<String> participants = new ArrayList<>();
    private List<Message> messages= new ArrayList<>();
    private String createdAt ;

    // Getters và Setters
}
