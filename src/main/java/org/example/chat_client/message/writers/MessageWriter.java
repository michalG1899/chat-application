package org.example.chat_client.message.writers;

import org.example.chat_client.customstreams.CustomObjectOutputStream;
import org.example.chat_client.message.model.ChatMessage;

import java.io.IOException;
import java.net.Socket;

public class MessageWriter {

    private CustomObjectOutputStream writer;

    public MessageWriter(Socket socket) {
        try {
            writer = new CustomObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(ChatMessage chatMessage) {
        try {
            writer.writeObject(chatMessage);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
