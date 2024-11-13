package org.uvhnael.chatserver.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.uvhnael.chatserver.model.File;

public interface FileRepository extends MongoRepository<File, String> {}