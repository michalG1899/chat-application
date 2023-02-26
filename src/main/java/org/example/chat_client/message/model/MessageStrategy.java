package org.example.chat_client.message.model;

import lombok.extern.java.Log;
import org.example.chat_client.file.FileData;
import org.example.chat_client.file.FileSender;

@Log
public class MessageStrategy {
    public static ChatMessage build(String text, String sender) {
        String command = text.substring(0, 2);
        switch (command) {
            case "#1" -> {
                return ChatMessage.builder()
                        .messageType(MessageType.JOIN)
                        .sender(sender).message(text)
                        .build();
            }
            case "#2" -> {
                String chatName = text.split(" ")[1];
                return ChatMessage.builder()
                        .messageType(MessageType.SWITCH)
                        .sender(sender)
                        .message(chatName)
                        .build();
            }
            case "#3" -> {
                String[] textArray = text.split(" ");
                FileData fileToSend = FileSender.getFile(textArray[1], textArray[2]);
                return ChatMessage.builder()
                        .messageType(MessageType.FILE)
                        .sender(sender).file(fileToSend)
                        .build();
            }
            default -> {
                return ChatMessage.builder()
                        .messageType(MessageType.TEXT)
                        .sender(sender)
                        .message(text)
                        .build();
            }
        }
    }
}
