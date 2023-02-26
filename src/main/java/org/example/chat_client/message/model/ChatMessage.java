package org.example.chat_client.message.model;

import lombok.Builder;
import lombok.Data;
import org.example.chat_client.file.FileData;

import java.io.Serializable;
@Data
@Builder
public class ChatMessage implements Serializable {
    private String sender;
    private String recipient;
    private String message;
    private MessageType messageType;
    private FileData file;

    @Override
    public String toString() {
        return sender != null ? sender.concat(": ").concat(message) : message;
    }
}
