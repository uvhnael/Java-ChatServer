package org.uvhnael.chatserver.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.uvhnael.chatserver.model.User;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
    User findByEmail(String email);

    User findByUsernameAndPassword(String username, String password);
    User findByEmailAndPassword(String email, String password);
}
