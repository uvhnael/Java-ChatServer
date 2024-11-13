package org.uvhnael.chatserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String image;
    private String email;
    private String password;
    private List<Friend> friends = new ArrayList<>();
    private List<FriendRequest> friendRequests = new ArrayList<>();
    private String status;

    // Getters và Setters
}

