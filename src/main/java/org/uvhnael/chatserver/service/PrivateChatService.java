package org.uvhnael.chatserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uvhnael.chatserver.dto.MessageResponse;
import org.uvhnael.chatserver.exception.ChatNotFoundException;
import org.uvhnael.chatserver.model.Message;
import org.uvhnael.chatserver.model.PrivateChat;
import org.uvhnael.chatserver.repository.PrivateChatRepository;
import org.uvhnael.chatserver.repository.UserRepository;
import org.uvhnael.chatserver.websocket.WSMessage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrivateChatService {

    private final PrivateChatRepository privateChatRepository;

    private final UserRepository userRepository;

    public List<Message> getChat(String participant1, String participant2) {
        Optional<PrivateChat> chat = privateChatRepository.findByParticipants(participant1, participant2);
        if(chat.isEmpty()) {
            throw new ChatNotFoundException("Chat not found");
        }
        return chat.get().getMessages();
    }

    public List<MessageResponse> getChat(String chatId){
        Optional<PrivateChat> chat = privateChatRepository.findById(chatId);
        if(chat.isEmpty()) {
            throw new ChatNotFoundException("Chat not found");
        }
        // store userid as key and username as value
        Map<String, String> userNames = new java.util.HashMap<>(Map.of());


        chat.get().getParticipants().forEach(p -> {
            userNames.put(p, userRepository.findById(p).get().getUsername());
        });

        List<Message> messages = chat.get().getMessages();
        List<MessageResponse> messageResponses = new ArrayList<>();
        messages.forEach(m -> {
            messageResponses.add(new MessageResponse(m, userNames.get(m.getSenderId())));
        });

        return messageResponses;
    }

    public String getReceiverId(String chatId, String senderId) {
        Optional<PrivateChat> chat = privateChatRepository.findById(chatId);
        if(chat.isEmpty()) {
            throw new ChatNotFoundException("Chat not found");
        }
        PrivateChat pvChat = chat.get();
        return pvChat.getParticipants().stream().filter(p -> !p.equals(senderId)).findFirst().get();
    }

    public void saveMessage(WSMessage wsMessage) {
        Optional<PrivateChat> chat = privateChatRepository.findById(wsMessage.getChatId());
        if(chat.isEmpty()) {
            throw new ChatNotFoundException("Chat not found");
        }
        PrivateChat pvChat = chat.get();
        List<String> readBy = new ArrayList<>();
        readBy.add(wsMessage.getSenderId());
        pvChat.getMessages().add(new Message(wsMessage.getSenderId(), wsMessage.getContent(), new Timestamp(System.currentTimeMillis()).toString(),readBy, wsMessage.getAttachments()));
        privateChatRepository.save(pvChat);
    }
}
