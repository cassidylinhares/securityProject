package SecurityProject;

import java.net.*;
import javax.net.ssl.*;
import java.io.*;

public class Server {
    final static int PORT = 3500;

    public static void main(String[] args) {
        SSLServerSocketFactory sslServerFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket serverSocket;

        try {
            // start server
            serverSocket = (SSLServerSocket) sslServerFactory.createServerSocket(PORT);
            System.out.println("Server Started and waiting for connection");

            while (true) {
                // connet client to server
                SSLSocket client = null;// serverSocket.accept();
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
