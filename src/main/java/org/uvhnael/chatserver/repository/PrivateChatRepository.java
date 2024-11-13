package org.uvhnael.chatserver.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.uvhnael.chatserver.model.PrivateChat;

import java.util.List;
import java.util.Optional;

public interface PrivateChatRepository extends MongoRepository<PrivateChat, String> {

    @Query("{ 'participants': { $all: [?0, ?1] } }")
    Optional<PrivateChat> findByParticipants(String participant1, String participant2);

    List<PrivateChat> findByParticipantsContaining(String participant);
}