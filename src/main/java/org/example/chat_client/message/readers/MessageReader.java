package org.example.chat_client.message.readers;

import lombok.extern.java.Log;
import org.example.chat_client.file.FileSender;
import org.example.chat_client.message.model.MessageType;
import org.example.chat_client.message.model.ChatMessage;
import org.example.chat_client.customstreams.CustomObjectInputStream;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log
public class MessageReader {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final Consumer<ChatMessage> onText;
    private CustomObjectInputStream reader;
    private final Runnable onClose;

    public MessageReader(Socket socket, Consumer<ChatMessage> onText, Runnable onClose) {
        this.onText = onText;
        this.onClose = onClose;
        try {
            reader = new CustomObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Creating input stream failed: " + e.getMessage());
        }
    }

    public void read() {
        ChatMessage chatMessage;
        try {
            while ((chatMessage = (ChatMessage) reader.readObject()) != null) {
                if (chatMessage.getMessageType() == MessageType.FILE) {
                    log.info(chatMessage.getSender() + " sent you a file: " + chatMessage.getFile().getFileName());
                    FileSender.saveFile(chatMessage);
                } else {
                    onText.accept(chatMessage);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Read message failed: " + e.getMessage());
        } finally {
            if (onClose != null) {
                onClose.run();
            }
        }
    }

}
