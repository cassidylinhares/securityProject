package SecurityProject;

import javax.net.*;
import javax.net.ssl.*;
import java.io.*;

public class Server {
    final static int PORT = 3500;

    public static void main(String[] args) {
        String home = System.getProperty("user.home");

        System.setProperty("javax.net.ssl.keyStore",
                home + "/Documents/securityProject/SecurityProject/myKeyStore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "abc123");

        ServerSocketFactory serverFactory = SSLServerSocketFactory.getDefault();
        SSLServerSocket serverSocket;

        try {
            // start server
            serverSocket = (SSLServerSocket) serverFactory.createServerSocket(PORT);

            System.out.println("Server Started and waiting for connection");

            while (true) {
                // connet client to server
                SSLSocket client = (SSLSocket) serverSocket.accept();
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
