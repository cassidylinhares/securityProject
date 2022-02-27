package SecurityProject;

import javax.net.*;
import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;

public class Server {
    final static int PORT = 3500;

    public static void main(String[] args) {
        ServerSocketFactory serverFactory = SSLServerSocketFactory.getDefault();
        SSLServerSocket serverSocket;

        try {
            // start server
            serverSocket = (SSLServerSocket) serverFactory.createServerSocket(PORT);
            serverSocket.setNeedClientAuth(true);
            serverSocket.setEnabledCipherSuites(new String[] { "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256" });
            serverSocket.setEnabledProtocols(new String[] { "TLSv1.2" });

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
