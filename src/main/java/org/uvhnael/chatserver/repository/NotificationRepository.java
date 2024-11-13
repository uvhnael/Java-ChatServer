package org.uvhnael.chatserver.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.uvhnael.chatserver.model.Notification;

public interface NotificationRepository extends MongoRepository<Notification, String> {}

