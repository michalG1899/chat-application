package org.example.chat_client.message.readers;

import lombok.extern.java.Log;
import org.example.chat_client.customstreams.CustomObjectInputStream;
import org.example.chat_client.file.FileSender;
import org.example.chat_client.message.model.ChatMessage;
import org.example.chat_client.message.model.MessageType;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;


@Log
public class MessageReader {

    private final Consumer<ChatMessage> onText;
    private final Runnable onExit;
    private CustomObjectInputStream reader;

    public MessageReader(Socket socket, Consumer<ChatMessage> onText, Runnable onExit) {
        this.onText = onText;
        this.onExit = onExit;
        try {
            reader = new CustomObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            log.info("Problem with input stream: " + e.getMessage());
        }
    }

    public void read() {
        ChatMessage chatMessage;
        try {
            while ((chatMessage = (ChatMessage) reader.readObject()) != null) {
                if (chatMessage.getMessageType() == MessageType.FILE) {
                    MessageReader.log.info(chatMessage.getSender() + " sent you a file: " + chatMessage.getFile().getFileName());
                    FileSender.saveFile(chatMessage);
                } else {
                    onText.accept(chatMessage);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            log.info("Problem with reading message: " + e.getMessage());
        } finally {
            if (onExit != null) {
                onExit.run();
            }
        }
    }

}
