package org.uvhnael.chatserver.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.uvhnael.chatserver.model.File;

import java.util.List;

public interface FileRepository extends MongoRepository<File, String> {
    List<File> findByChatId(String chatId);
}