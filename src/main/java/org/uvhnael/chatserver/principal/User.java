package org.uvhnael.chatserver.principal;

import java.security.Principal;

public class User implements Principal {
    private String id;

    public User(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return id;
    }

}
