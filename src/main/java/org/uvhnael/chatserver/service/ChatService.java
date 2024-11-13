package org.uvhnael.chatserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uvhnael.chatserver.dto.ChatListResponse;
import org.uvhnael.chatserver.exception.ChatNotFoundException;
import org.uvhnael.chatserver.model.GroupChat;
import org.uvhnael.chatserver.model.Message;
import org.uvhnael.chatserver.model.PrivateChat;
import org.uvhnael.chatserver.model.User;
import org.uvhnael.chatserver.repository.GroupChatRepository;
import org.uvhnael.chatserver.repository.PrivateChatRepository;
import org.uvhnael.chatserver.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final PrivateChatRepository privateChatRepository;
    private final GroupChatRepository groupChatRepository;
    private final UserRepository userRepository;

    public void readMessages(String chatId, String userId, boolean isGroup) {
        if (isGroup) {
            GroupChat chat = groupChatRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException("Group chat not found"));
            if(chat.getMessages().isEmpty()) {
                return;
            }
            chat.getMessages().get(chat.getMessages().size() - 1).getReadBy().add(userId);
            groupChatRepository.save(chat);
        }
        else {
            PrivateChat chat = privateChatRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException("Private chat not found"));
            if(chat.getMessages().isEmpty()) {
                return;
            }
            chat.getMessages().get(chat.getMessages().size() - 1).getReadBy().add(userId);
            privateChatRepository.save(chat);
        }
    }

    public List<ChatListResponse> getChats(String userId) {
        List<ChatListResponse> chatList = new ArrayList<>();
        chatList.addAll(getPrivateChat(userId));
        chatList.addAll(getGroupChat(userId));
        sortChats(chatList);
        return chatList;
    }

    private List<ChatListResponse> getPrivateChat(String userId) {
        List<ChatListResponse> chatList = new ArrayList<>();
        List<PrivateChat> pvChat = privateChatRepository.findByParticipantsContaining(userId);

        for (PrivateChat chat : pvChat) {
            String friendId = chat.getParticipants().stream().filter(p -> !p.equals(userId)).findFirst().get();
            User friend = userRepository.findById(friendId).get();
            List<Message> messages = chat.getMessages();

            messages.removeIf(m -> m.getTimestamp() == null); // Remove messages with null timestamps
            messages.sort((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp()));

            Message message = messages.isEmpty() ? null : messages.get(0);
            String lastSenderUsername = "";
            if (message != null && Objects.equals(message.getSenderId(), userId))
                lastSenderUsername = "You: ";

            int unreadCount = 0;
            for (Message m : messages) {
                if (m.getReadBy().stream().noneMatch(u -> u.equals(userId)))
                    unreadCount++;
                else
                    break;
            }

            chatList.add(new ChatListResponse(
                    chat.getId(),
                    friend.getUsername(),
                    message != null ? lastSenderUsername + message.getContent() : "",
                    message != null ? message.getTimestamp() : chat.getCreatedAt(),
                    unreadCount,
                    false
            ));
        }

        return chatList;
    }

    private List<ChatListResponse> getGroupChat(String userId) {
        List<ChatListResponse> chatList = new ArrayList<>();
        List<GroupChat> gChat = groupChatRepository.findByMembersUserId(userId);

        for (GroupChat chat : gChat) {
            List<Message> messages = chat.getMessages();

            messages.removeIf(m -> m.getTimestamp() == null); // Remove messages with null timestamps
            messages.sort((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp()));

            Message message = messages.isEmpty() ? null : messages.get(0);
            String lastSenderId = message != null ? message.getSenderId() : "";
            String lastSenderUsername = "";
            if (message != null && !Objects.equals(message.getSenderId(), userId))
                lastSenderUsername = userRepository.findById(lastSenderId).get().getUsername() + ": ";
            lastSenderUsername = "You: ";

            int unreadCount = 0;
            for (Message m : messages) {
                if (m.getReadBy().stream().noneMatch(u -> u.equals(userId)))
                    unreadCount++;
                else
                    break;
            }

            chatList.add(new ChatListResponse(
                    chat.getId(),
                    chat.getName(),
                    message != null ? lastSenderUsername + message.getContent() : "",
                    message != null ? message.getTimestamp() : chat.getCreatedAt(),
                    unreadCount,
                    true
            ));
        }

        return chatList;
    }

    private void sortChats(List<ChatListResponse> chatList) {
        chatList.sort((c1, c2) -> {
            if (c1.getTimestamp() == null && c2.getTimestamp() == null) {
                return 0;
            } else if (c1.getTimestamp() == null) {
                return 1;
            } else if (c2.getTimestamp() == null) {
                return -1;
            } else {
                return c2.getTimestamp().compareTo(c1.getTimestamp());
            }
        });
    }
}
