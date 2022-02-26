package SecurityProject;

import java.net.*;
import java.io.*;

public class Server {
    final static int PORT = 3500;

    public static void main(String[] args) {
        ServerSocket serverSocket;

        try {
            // start server
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server Started and waiting for connection");

            while (true) {
                // connet client to server
                Socket client = serverSocket.accept();
                System.out.println("Client connected from " + client.getLocalAddress() + ":" + client.getLocalPort());

                // start a new thread for client
                ClientHandler handler = new ClientHandler(client);
                new Thread(handler).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
