package org.uvhnael.chatserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.uvhnael.chatserver.dto.ErrorResponse;
import org.uvhnael.chatserver.dto.FriendRequestBody;
import org.uvhnael.chatserver.dto.FriendRequestResponse;
import org.uvhnael.chatserver.dto.FriendResponse;
import org.uvhnael.chatserver.model.FriendRequest;
import org.uvhnael.chatserver.service.UserService;
import org.uvhnael.chatserver.websocket.WSFriendRequestAccept;

import java.util.List;

@Controller
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

    private final UserService userService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getFriends(@PathVariable String userId) {
        try {
            List<FriendResponse> friends = userService.getFriends(userId);
            if (friends != null) {
                return ResponseEntity.status(HttpStatus.OK).body(friends);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Unexpected error", e.getMessage()));
        }
    }

    @GetMapping("/requests/{userId}")
    public ResponseEntity<?> getFriendRequests(@PathVariable String userId) {
        try {
            List<FriendRequestResponse> friends = userService.getFriendRequests(userId);
            if (friends != null) {
                return ResponseEntity.status(HttpStatus.OK).body(friends);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Unexpected error", e.getMessage()));
        }
    }

    @PostMapping("/add/{userId}/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable String userId, @PathVariable String friendId) {
        try {
            userService.sendFriendRequest(userId, friendId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Unexpected error", e.getMessage()));
        }
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addFriendEmail(@PathVariable String userId, @RequestBody FriendRequestBody requestBody) {
        try {
            userService.sendFriendRequestEmail(userId, requestBody.getEmail());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Unexpected error", e.getMessage()));
        }
    }

    @PutMapping("/accept/{userId}/{friendId}")
    public ResponseEntity<?> acceptFriend(@PathVariable String userId, @PathVariable String friendId) {
        try {
            WSFriendRequestAccept fra = userService.acceptFriendRequest(userId, friendId);

            System.out.println("Friend request accepted: " + fra);

            simpMessagingTemplate.convertAndSendToUser(fra.getReceiverId(), "/queue/messages", fra);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Unexpected error", e.getMessage()));
        }
    }
    @PutMapping("/reject/{userId}/{friendId}")
    public ResponseEntity<?> rejectFriend(@PathVariable String userId, @PathVariable String friendId) {
        try {
            userService.rejectFriendRequest(userId, friendId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Unexpected error", e.getMessage()));
        }
    }

    private Object createErrorResponse(String error, String message) {
        return new ErrorResponse(error, message);
    }

}
