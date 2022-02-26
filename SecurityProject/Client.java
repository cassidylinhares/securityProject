package SecurityProject;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Client {
    // constants
    final static int PORT = 3500;
    final static String HOST = "localhost";

    public static SSLSocket client = null;
    private static DataInputStream in = null;
    private static Scanner sc = null;
    private static BufferedReader br = null;
    private static PrintWriter pr = null;
    private static SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

    // recieve cat pic from server String fileName
    private static void recieveFile() {
        try {
            // get pic size
            byte[] sizeAr = new byte[4];
            in.read(sizeAr);
            int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

            // get picture
            byte[] imageAr = new byte[size];
            in.read(imageAr);

            // get filename
            byte[] name = new byte[20];
            in.read(name);
            String fileName = new String(name, StandardCharsets.UTF_8);
            fileName = fileName.trim();

            // make image
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));

            // write image to destination
            System.out.println("Received " + fileName + "\n");
            String home = System.getProperty("user.home");
            ImageIO.write(image, "jpeg", new File(home + "/Downloads/" + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // welcome msg
        System.out.println(" /\\_/\\ \n( o.o )\n > ^ <");
        System.out.println("WELCOME TO CATSU!\nThe random cat generator \n\n");

        try {
            // init io and socket
            client = (SSLSocket) sslFactory.createSocket(HOST, PORT);
            in = new DataInputStream(client.getInputStream());
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            pr = new PrintWriter(client.getOutputStream(), true);
            sc = new Scanner(System.in);

            // start handshake
            client.startHandshake();

            if (pr.checkError()) { // check for error
                System.out.println("SSLSocketClient:  java.io.PrintWriter error");
            }

            String cmd; // holds commands from user
            String res; // response from server
            while ((cmd = sc.nextLine()) != null) {
                String[] tokens = cmd.split("\\s+");
                switch (tokens[0]) {
                    // help
                    case "help":
                    case "h":
                        // send command
                        pr.println(cmd);
                        // receive from server
                        while (!(res = br.readLine()).equals("\0")) {
                            System.out.println(res);
                        }
                        break;

                    // close client
                    case "stop":
                    case "bye":
                    case "disconnect":
                        cmd = null;
                        break;

                    // get catpic
                    default:
                        // send command
                        pr.println(cmd);

                        // check for error or recieve pic
                        if (tokens.length == 1) {
                            recieveFile();
                        } else {
                            while (!(res = br.readLine()).equals("\0")) {
                                System.out.println(res);
                            }
                        }
                        break;
                }
                if (cmd == null) {
                    break;
                }
            }
            // Tell the user Byeee
            System.out.println("Byeee");
            System.out.println(
                    "      |\\      _,,,---,,_\nZZZzz /,`.-'`'    -.  ;-;;,_\n     |,4-  ) )-,_. ,\\ (  `'-'\n    '---''(_/--'  `-'\\_)  ");
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                sc.close();
                pr.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}