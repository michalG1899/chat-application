package org.example.chat_server;

import lombok.extern.java.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log
public class ServerRun {
    private final ChatRoom chatRoom = new ChatRoom();
    private final ServerSocket serverSocket;
    private ExecutorService pool = Executors.newFixedThreadPool(5);


    public ServerRun(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Server started!");
        ServerSocket serverSocket = new ServerSocket(1234);
        ServerRun serverRun = new ServerRun(serverSocket);
        serverRun.startServer();
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                log.info("New connection established...");
                log.info("creating ChatUser...");
                ChatUser chatUser = new ChatUser(socket, chatRoom);
                chatRoom.add(chatUser);
                pool.execute(chatUser);
            }
        } catch (IOException e) {
            e.printStackTrace();
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
