package org.example.chat_server;

import lombok.Data;
import lombok.extern.java.Log;
import org.example.chat_client.message.readers.MessageReader;
import org.example.chat_client.message.model.MessageType;
import org.example.chat_client.message.writers.MessageWriter;
import org.example.chat_client.message.model.ChatMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.chat_client.message.model.MessageType.*;

@Log
@Data
public class ChatUser implements Runnable {

    private static final String PUBLIC = "public";
    private final Socket socket;
    private final ChatRoom chatRoom;
    private final MessageWriter writer;

    private String name;
    private String currentChat;

    public ChatUser(Socket socket, ChatRoom chatRoom) {
        this.socket = socket;
        this.chatRoom = chatRoom;
        writer = new MessageWriter(socket);
    }

    @Override
    public void run() {
        new MessageReader(socket, this::onChatMessage, () -> chatRoom.remove(this)).read();
    }

    public void send(ChatMessage chatMessage) {
        writer.write(chatMessage);
    }

    private void onChatMessage(ChatMessage chatMessage) {
        MessageType messageType = chatMessage.getMessageType();

        if (messageType == JOIN) {
            handleJoinChat(chatMessage);
        } else if (messageType == FILE) {
            handleSendFile(chatMessage);
        } else if (messageType == SWITCH) {
            handleSwitchChat(chatMessage);
        } else {
            handleBroadcastMessage(chatMessage);
        }
    }

    private void handleSwitchChat(ChatMessage chatMessage) {
        String newChatName = chatMessage.getMessage().toLowerCase();
        if (newChatName.equals(getCurrentChat())) {
            log.log(Level.INFO, "You're already on the chat: " + newChatName);
        } else {
            if (Objects.nonNull(getCurrentChat())) {
                ChatMessage message = ChatMessage.builder()
                        .message(getName().concat(" has left the chat [".concat(getCurrentChat().concat("]")))) //fix
                        .build();
                chatRoom.broadcast(message, this);
            }
            setCurrentChat(newChatName);
            name = "".equals(getName()) ? chatMessage.getSender() : name;
            ChatMessage welcomeMessage = ChatMessage.builder()
                    .message((getName().concat(" has joined the chat [".concat(getCurrentChat().concat("]")))))
                    .build();
            chatRoom.broadcast(welcomeMessage, this);
        }
    }

    private void handleJoinChat(ChatMessage chatMessage) {
        String userName = chatMessage.getSender();
        if (chatRoom.isUsernameAvailable(userName)) {
            setName(userName);
            setCurrentChat(PUBLIC);
            ChatMessage message = ChatMessage.builder()
                    .message((getName().concat(" has joined the chat [".concat(getCurrentChat().concat("]")))))
                    .build();
            chatRoom.broadcast(message, this);
        } else {
            ChatMessage message = ChatMessage.builder()
                    .message("There's already logged in user with username: " + chatMessage.getSender())
                    .build();

            send(message);
            closeSocket();
        }
    }

    private void handleBroadcastMessage(ChatMessage chatMessage) {
        chatRoom.broadcast(chatMessage, this);
    }

    private void handleSendFile(ChatMessage chatMessage) {
        String recipientName = chatMessage.getFile().getRecipient();
        if (recipientName.equals(getName())) {
            log.log(Level.WARNING, "Cannot send file to the same user.");
        } else {
            ChatUser recipient = chatRoom.getUser(recipientName);
            if (recipient != null && recipient.getCurrentChat().equals(getCurrentChat())) {
                chatMessage.setMessageType(FILE);
                chatRoom.broadcastSingle(chatMessage, recipient);
            } else {
                log.log(Level.WARNING, "Invalid recipient name.");
            }
        }
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            log.info("Closing socket failed: " + e.getMessage());
        }
    }
}
