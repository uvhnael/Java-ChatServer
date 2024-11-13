package org.uvhnael.chatserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uvhnael.chatserver.dto.CreateGroupRequest;
import org.uvhnael.chatserver.dto.FriendResponse;
import org.uvhnael.chatserver.dto.MessageResponse;
import org.uvhnael.chatserver.exception.ChatNotFoundException;
import org.uvhnael.chatserver.model.GroupChat;
import org.uvhnael.chatserver.model.GroupMember;
import org.uvhnael.chatserver.model.Message;
import org.uvhnael.chatserver.model.User;
import org.uvhnael.chatserver.repository.GroupChatRepository;
import org.uvhnael.chatserver.repository.UserRepository;
import org.uvhnael.chatserver.websocket.WSMessage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupChatService {

    private final GroupChatRepository groupChatRepository;

    private final UserRepository userRepository;

    public List<MessageResponse> getChat(String chatId){
        Optional<GroupChat> chat = groupChatRepository.findById(chatId);
        if(chat.isEmpty()) {
            throw new ChatNotFoundException("Chat not found");
        }
        Map<String, String> userNames = new java.util.HashMap<>(Map.of());
        chat.get().getMembers().forEach(m -> {
            userNames.put(m.getUserId(), userRepository.findById(m.getUserId()).get().getUsername());
        });

        List<Message> messages = chat.get().getMessages();
        List<MessageResponse> messageResponses = new ArrayList<>();
        messages.forEach(m -> {
            messageResponses.add(new MessageResponse(m, userNames.get(m.getSenderId())));
        });
        return messageResponses;
    }
    public String createGroupChat(CreateGroupRequest request) {
        GroupChat groupChat = new GroupChat();
        groupChat.setAdminId(request.getUserId());
        groupChat.setName(request.getGroupName());
        List<GroupMember> members = new ArrayList<>();
        for (String participant : request.getParticipants()) {
            GroupMember member = new GroupMember();
            member.setUserId(participant);
            if(participant.equals(request.getUserId())) {
                member.setRole("admin");
            } else {
                member.setRole("member");
            }
            members.add(member);
        }
        groupChat.setMembers(members);
        groupChat.setCreatedAt(new Timestamp(System.currentTimeMillis()).toString());
        groupChatRepository.save(groupChat);

        return groupChat.getId();
    }

    public List<String> getReceiverId(String chatId, String senderId) {
        Optional<GroupChat> chat = groupChatRepository.findById(chatId);
        if(chat.isEmpty()) {
            throw new ChatNotFoundException("Chat not found");
        }
        return chat.get().getMembers().stream().map(GroupMember::getUserId).filter(u -> !u.equals(senderId)).collect(Collectors.toList());
    }
    public void saveMessage(WSMessage wsMessage) {
        Optional<GroupChat> chat = groupChatRepository.findById(wsMessage.getChatId());
        if(chat.isEmpty()) {
            throw new ChatNotFoundException("Chat not found");
        }
        GroupChat gChat = chat.get();
        List<String> readBy = new ArrayList<>();
        readBy.add(wsMessage.getSenderId());
        gChat.getMessages().add(new Message(wsMessage.getSenderId(), wsMessage.getContent(), new Timestamp(System.currentTimeMillis()).toString(),readBy, wsMessage.getAttachments()));
        groupChatRepository.save(gChat);
    }

    public List<FriendResponse> getParticipants(String chatId) {
        Optional<GroupChat> chat = groupChatRepository.findById(chatId);
        if(chat.isEmpty()) {
            throw new ChatNotFoundException("Chat not found");
        }
        List<FriendResponse> participants = new ArrayList<>();
        chat.get().getMembers().forEach(m -> {
            Optional<User> user = userRepository.findById(m.getUserId());
            if(user.isEmpty()) {
                return;
            }
            User u = user.get();

            participants.add(new FriendResponse(m.getUserId(), u.getImage() != null ? u.getImage() : "uploads/user.jpg" , u.getUsername()));
        });
        return participants;
    }
}
