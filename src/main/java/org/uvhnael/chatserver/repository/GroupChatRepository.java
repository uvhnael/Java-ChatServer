package org.uvhnael.chatserver.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.uvhnael.chatserver.model.GroupChat;

import java.util.List;

public interface GroupChatRepository extends MongoRepository<GroupChat, String> {
    GroupChat findGroupChatById(String id);
    GroupChat findGroupChatByName(String name);
    GroupChat findGroupChatByAdminId(String adminId);

    List<GroupChat> findByMembersUserId(String userId);

}