package org.example.chat_client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientRun {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        ChatClient chatClient = new ChatClient(socket, username);

        chatClient.listenForMessage();
    }
}
