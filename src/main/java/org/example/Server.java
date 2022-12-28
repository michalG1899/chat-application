package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Server started!");
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept(); //wstrzymuje program aż klient się połączy i zwraca Socket
                System.out.println("A new client has connected.");
                ClientHandler clientHandler = new ClientHandler(socket); //każda instancja będzie odpalana na innym wątku

                Thread thread = new Thread(clientHandler);
                thread.start();
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
