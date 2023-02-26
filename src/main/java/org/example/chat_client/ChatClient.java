package org.example.chat_client;


import lombok.extern.java.Log;
import org.example.chat_client.message.readers.ConsoleReader;
import org.example.chat_client.message.readers.MessageReader;
import org.example.chat_client.message.writers.MessageWriter;
import org.example.chat_client.message.model.MessageStrategy;

import java.net.Socket;
import java.util.function.Consumer;

@Log
public class ChatClient {
    private final Socket clientSocket;
    private final Runnable readFromSocket;
    private final Runnable readFromConsole;
    private final Consumer<String> onText;

    public ChatClient(Socket clientSocket, String name) {
        this.clientSocket = clientSocket;
        this.onText = text -> new MessageWriter(clientSocket).write(MessageStrategy.build(text, name));
        this.readFromConsole = () -> new ConsoleReader(System.in, onText).read();
        this.readFromSocket = () -> new MessageReader(clientSocket, System.out::println, () -> {
        }).read();
    }

    public void listenForMessage() {
        new Thread(readFromSocket).start();
        Thread consoleMessageReader = new Thread(readFromConsole);
        consoleMessageReader.setDaemon(true);
        consoleMessageReader.start();
        System.out.println(showOptions());
    }

    private String showOptions() {
        return """
                #1 - JOIN COMMON GROUP CHAT
                #2 - CREATE/JOIN PRIVATE ROOM (#2 <room_name>)
                #3 - SEND FILE IN ROOM (#3 <file_name> <recipient_nick>)
                """;
    }
}
