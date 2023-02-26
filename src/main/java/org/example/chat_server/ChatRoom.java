package org.example.chat_server;

import lombok.extern.java.Log;
import org.example.chat_client.message.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

@Log
public class ChatRoom {

    private final List<ChatUser> chatUsers = new ArrayList<>();

    public void add(ChatUser chatWorker) {
        chatUsers.add(chatWorker);
    }

    public void remove(ChatUser chatWorker) {
        chatUsers.remove(chatWorker);
    }

    public void broadcast(ChatMessage message, ChatUser sender) {
        try {
            chatUsers.stream()
                    .filter(user -> user.getCurrentChat().equals(sender.getCurrentChat()))
                    .forEach(user -> user.send(message));
        } catch (NullPointerException e) {
            log.info("probably exception thrown by first joining to chat room");
        }
    }

    public void broadcastSingle(ChatMessage message, ChatUser recipient) {
        recipient.send(message);
    }

    public boolean isUsernameAvailable(String userName) {
        return chatUsers.stream()
                .map(ChatUser::getName)
                .filter(name -> !name.isBlank())
                .noneMatch(name -> name.equals(userName));
    }

    public ChatUser getUser(String userName) {
        List<ChatUser> users = chatUsers.stream()
                .filter(user -> user.getName().equals(userName)).toList();
        return users.isEmpty() ? null : users.get(0);
    }
}
