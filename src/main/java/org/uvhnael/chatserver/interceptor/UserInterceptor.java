package org.uvhnael.chatserver.interceptor;

import java.util.List;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.uvhnael.chatserver.principal.User;

public class UserInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            Object rawHeaders = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);

            if (rawHeaders instanceof Map) {
                Map<String, List<String>> headers = (Map<String, List<String>>) rawHeaders;
                List<String> userIdList = headers.get("userId");

                if (userIdList != null && !userIdList.isEmpty()) {
                    String userId = userIdList.get(0);
                    accessor.setUser(new User(userId)); // Ensure this sets the Principal
                    System.out.println("User ID: " + userId);
                }
            }
        }
        return message;
    }
}