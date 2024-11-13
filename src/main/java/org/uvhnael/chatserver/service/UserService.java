package org.uvhnael.chatserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.uvhnael.chatserver.Utility.JwtUtility;
import org.uvhnael.chatserver.dto.AuthRequest;
import org.uvhnael.chatserver.dto.AuthResponse;
import org.uvhnael.chatserver.dto.FriendRequestResponse;
import org.uvhnael.chatserver.dto.FriendResponse;
import org.uvhnael.chatserver.exception.FriendException;
import org.uvhnael.chatserver.exception.UserNotFoundException;
import org.uvhnael.chatserver.model.Friend;
import org.uvhnael.chatserver.model.FriendRequest;
import org.uvhnael.chatserver.model.PrivateChat;
import org.uvhnael.chatserver.model.User;
import org.uvhnael.chatserver.repository.PrivateChatRepository;
import org.uvhnael.chatserver.repository.UserRepository;
import org.uvhnael.chatserver.websocket.WSFriendRequestAccept;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PrivateChatRepository privateChatRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtility jwtUtility;

    public AuthResponse signUp(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setFriends(new ArrayList<>());
        user.setFriendRequests(new ArrayList<>());
        user.setStatus("offline");
        User savedUser = userRepository.save(user);
        String token = jwtUtility.generateToken(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
        return new AuthResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), token);
    }
    public AuthResponse signIn(AuthRequest authRequest) {
        User user = userRepository.findByEmail(authRequest.getEmail());
        if (user == null) {
            throw new UserNotFoundException("Username or password is incorrect");
        }
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Username or password is incorrect");
        }

        String token = jwtUtility.generateToken(user.getId(), user.getUsername(), user.getEmail());
        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), token);
    }

    public List<FriendRequestResponse> getFriendRequests(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        User user = userOptional.get();
        List<FriendRequestResponse> friendResponses = new ArrayList<>();
        user.getFriendRequests().forEach(friendRequest -> {
            if(!friendRequest.getStatus().equals("pending")) return;
            userRepository.findById(friendRequest.getFromUser()).ifPresent(friendUser -> {
                friendResponses.add(new FriendRequestResponse(friendRequest.getRequestId(), friendUser.getId(), friendUser.getUsername(), friendUser.getImage(), friendRequest.getStatus()));
                System.out.println("Found friend user: " + friendUser.getUsername());
            });
        });

        return friendResponses;
    }

    public List<FriendResponse> getFriends(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with id: " + userId);
            // Add logic to retrieve and return the list of friends
        }
        User user = userOptional.get();
        List<FriendResponse> friendResponses = new ArrayList<>();
        user.getFriends().forEach(friend -> {
            userRepository.findById(friend.getFriendId()).ifPresent(friendUser -> {
                friendResponses.add(new FriendResponse(friendUser.getId(), friendUser.getImage() != null ? friendUser.getImage() : "uploads/user.jpg", friendUser.getUsername()));
                System.out.println("Found friend user: " + friendUser.getUsername());
            });
        });

        return friendResponses;
    }

    public void sendFriendRequest(String userId, String friendId) {
//        add friend request to friend
        Optional<User> friendOptional = userRepository.findById(friendId);
        if (friendOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        User friend = friendOptional.get();
        if (friend.getFriends().stream().anyMatch(f -> f.getFriendId().equals(userId))) {
            throw new FriendException("Already friends");
        }
        if (friend.getFriendRequests().stream().anyMatch(request -> request.getFromUser().equals(userId))) {
            throw new FriendException("Friend request already sent");
        }
        friend.getFriendRequests().add(new FriendRequest(UUID.randomUUID().toString(), userId, "pending"));

        userRepository.save(friend);
//        add friend to user
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        User user = userOptional.get();
        user.getFriends().add(new Friend(friendId, "pending"));

        userRepository.save(user);
    }

    public void sendFriendRequestEmail(String userId, String email) {
        User friend = userRepository.findByEmail(email);
        if (friend == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        sendFriendRequest(userId, friend.getId());
    }

    public WSFriendRequestAccept acceptFriendRequest(String userId, String friendRequestId) {

//        add friend to user
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        User user = userOptional.get();
        FriendRequest friendRequest = user.getFriendRequests().stream()
                .filter(request -> request.getRequestId().equals(friendRequestId))
                .findFirst()
                .orElseThrow(() -> new FriendException("Friend request not found with id: " + friendRequestId));

        user.getFriends().add(new Friend( friendRequest.getFromUser(), "accepted"));
        user.getFriendRequests().remove(friendRequest);

        userRepository.save(user);
//        add friend to friend
        Optional<User> friendOptional = userRepository.findById(friendRequest.getFromUser());
        if (friendOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        User friend = friendOptional.get();
        friend.getFriends().stream().filter(f -> f.getFriendId().equals(userId)).findFirst().ifPresent(f -> f.setStatus("accepted"));
        userRepository.save(friend);

        privateChatRepository.save(new PrivateChat(null, List.of(userId, friend.getId()), new ArrayList<>(), new Timestamp(System.currentTimeMillis()).toString()));


        Optional<PrivateChat> privateChat = privateChatRepository.findByParticipants(userId, friend.getId());
        if(privateChat.isEmpty()) {
            throw new UserNotFoundException("Chat not found");
        }
        PrivateChat chat = privateChat.get();

        WSFriendRequestAccept wsFRA = new WSFriendRequestAccept();
        wsFRA.setId(chat.getId());
        wsFRA.setName(friend.getUsername());
        wsFRA.setReceiverId(friend.getId());

        return wsFRA;
    }

    public void rejectFriendRequest(String userId, String friendRequestId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        User user = userOptional.get();
        FriendRequest friendRequest = user.getFriendRequests().stream()
                .filter(request -> request.getRequestId().equals(friendRequestId))
                .findFirst()
                .orElseThrow(() -> new FriendException("Friend request not found with id: " + friendRequestId));
        friendRequest.setStatus("rejected");

        userRepository.save(user);
    }

    public void setStatus(String userId, String status) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        User user = userOptional.get();
        user.setStatus(status);
        userRepository.save(user);
    }

}
