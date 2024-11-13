package org.uvhnael.chatserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.uvhnael.chatserver.dto.*;
import org.uvhnael.chatserver.exception.ChatNotFoundException;
import org.uvhnael.chatserver.model.Message;
import org.uvhnael.chatserver.service.ChatService;
import org.uvhnael.chatserver.service.GroupChatService;
import org.uvhnael.chatserver.service.PrivateChatService;
import org.uvhnael.chatserver.websocket.WSCreateGroup;
import org.uvhnael.chatserver.websocket.WSMessage;

import java.util.List;

@Controller
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final PrivateChatService privateChatService;

    private final GroupChatService groupChatService;

    private final ChatService chatService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/private")
    public void sendMessage(WSMessage wsMessage) {
        privateChatService.saveMessage(wsMessage);

        String receiverId = privateChatService.getReceiverId(wsMessage.getChatId(), wsMessage.getSenderId());

        simpMessagingTemplate.convertAndSendToUser(receiverId, "/queue/messages", wsMessage);
    }

    @MessageMapping("/group")
    public void sendGroupMessage(WSMessage wsMessage) {
        groupChatService.saveMessage(wsMessage);

       List<String> receiverIds = groupChatService.getReceiverId(wsMessage.getChatId(), wsMessage.getSenderId());

        for (String receiverId : receiverIds) {
            simpMessagingTemplate.convertAndSendToUser(receiverId, "/queue/messages", wsMessage);
        }
    }



    @GetMapping("/private/{userId1}/{userId2}")
    public ResponseEntity<?> getPrivateChat(@PathVariable String userId1, @PathVariable String userId2) {
        try {
            List<Message> messages = privateChatService.getChat(userId1, userId2);
            return ResponseEntity.ok(messages);
        } catch (ChatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse("Chat not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Unexpected error", e.getMessage()));
        }
    }

    @GetMapping("/private/{chatId}")
    public ResponseEntity<?> getPrivateChat(@PathVariable String chatId) {
        try {
            List<MessageResponse> messages = privateChatService.getChat(chatId);
            return ResponseEntity.ok(messages);
        } catch (ChatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse("Chat not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Unexpected error", e.getMessage()));
        }
    }

    @GetMapping("/group/{chatId}")
    public ResponseEntity<?> getGroupChat(@PathVariable String chatId) {
        try {
            List<MessageResponse> messages = groupChatService.getChat(chatId);
            return ResponseEntity.ok(messages);
        } catch (ChatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse("Chat not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Unexpected error", e.getMessage()));
        }
    }

    @GetMapping("/group/{chatId}/participants")
    public ResponseEntity<?> getGroupParticipants(@PathVariable String chatId) {
        try {
            List<FriendResponse> participants = groupChatService.getParticipants(chatId);
            return ResponseEntity.ok(participants);
        } catch (ChatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse("Chat not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Unexpected error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getChats(@PathVariable String userId) {
        try {
            List<ChatListResponse> chats = chatService.getChats(userId);
            return ResponseEntity.ok(chats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Unexpected error", e.getMessage()));
        }
    }

    @PostMapping("/group/create")
    public ResponseEntity<?> createGroupChat(@RequestBody CreateGroupRequest request) {
        try {
            String id = groupChatService.createGroupChat(request);

            WSCreateGroup wsCreateGroup = new WSCreateGroup(id, request.getGroupName());

            request.getParticipants().forEach(p -> {
                simpMessagingTemplate.convertAndSendToUser(p, "/queue/messages", wsCreateGroup);
            });

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Unexpected error", e.getMessage()));
        }
    }

    @PutMapping("{userId}/read/{chatId}")
    public ResponseEntity<?> readMessages(@PathVariable String userId, @PathVariable String chatId, @RequestParam boolean isGroup) {
        try {
            chatService.readMessages(chatId, userId, isGroup);
            return ResponseEntity.ok().build();
        } catch (ChatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse("Chat not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Unexpected error", e.getMessage()));
        }
    }

    private Object createErrorResponse(String error, String message) {
        return new ErrorResponse(error, message);
    }

}
