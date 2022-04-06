package SecurityProject;

import java.io.*;
import java.util.Scanner;

import javax.crypto.SecretKey;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Client {
    // constants
    private final static String SERVERPATH = "SecurityProject/ServerImages/";
    private final static String CLIENTPATH = "SecurityProject/ClientImages/";

    final static int PORT = 3500;
    final static String HOST = "localhost";

    private static InputStream is = null;
    private static Scanner sc = null;
    private static BufferedReader br = null;
    private static PrintWriter pr = null;

    // recieve encrypted pic from server and decrypt
    private static void recieveFile(String path, String name, String password) {
        File outputFile = new File(CLIENTPATH + name);
        File inputFile = new File(path);
        try {


            Boolean checkDecryptionError = true;

            // decrypt image
            SecretKey key = Aes.getKeyFromPassword(password, "jdsfrkjehr");
            //If return true, then decryption successful
            checkDecryptionError = Aes.decryptFile(key, inputFile, outputFile);
            System.out.println("Decrypted image: " + outputFile.getPath() + "\n");


            if (inputFile.exists()&&checkDecryptionError) {
                inputFile.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // encrypt and send encrypted pic to server
    private static void sendFile(String path, String name, String password) {
        String newPath = SERVERPATH + name;
        File inputFile = new File(path);
        File outputFile = new File(newPath);

        // encrypt image
        try {
            SecretKey key = Aes.getKeyFromPassword(password, "jdsfrkjehr");
            Aes.encryptFile(key, inputFile, outputFile);
            System.out.println("Encrypted image sent to: " + outputFile.getPath() + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (inputFile.exists()) {
            inputFile.delete();
        }
    }

    public static void main(String[] args) {
        // welcome msg
        System.out.println(" /\\_/\\ \n( o.o )\n > ^ <");
        System.out.println("WELCOME TO PHOTO LOCKER!\n\n");

        System.setProperty("javax.net.ssl.trustStore",
                "SecurityProject/myTrustStore.jts");
        System.setProperty("javax.net.ssl.trustStorePassword", "abc123");

        try {
            // init socket
            SocketFactory factory = SSLSocketFactory.getDefault();
            SSLSocket client = (SSLSocket) factory.createSocket(HOST, PORT);

            // init IO
            is = client.getInputStream();
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            pr = new PrintWriter(client.getOutputStream(), true);
            sc = new Scanner(System.in);

            String cmd; // holds commands from user
            String res; // response from server
            while ((cmd = sc.nextLine()) != null) {
                String[] tokens = cmd.split("\\s+");
                switch (tokens[0]) {
                    // close client
                    case "stop":
                    case "bye":
                    case "disconnect":
                        cmd = null;
                        break;
                    case "get":
                        // send command
                        pr.println(cmd);
                        String getReponse =br.readLine();
                        System.out.println(getReponse);

                        // check for error or recieve pic
                        if (tokens.length == 2) {

                            String name = tokens[1];
                            if (!(new File(CLIENTPATH + name).exists()||getReponse.equals("File does not exist in server"))) {
                                System.out.print("Please enter the password you encrypted image with: ");
                                String password = sc.nextLine();
                                recieveFile(SERVERPATH + name, name, password);
                            } else {
                                System.out.println("file already exist in client");
                            }
                        } else {
                            while (!(res = br.readLine()).equals("\0")) {
                                System.out.println(res);
                            }
                        }
                        break;
                    case "send":
                        // send command
                        pr.println(cmd);
                        String sendReponse =br.readLine();
                        System.out.println(sendReponse);
                        // check for error or recieve pic
                        if (tokens.length == 2) {
                            String name = tokens[1];
                            if (new File(CLIENTPATH + name).exists()){
                                if(sendReponse.equals("File does not exist in server")) {
                                    System.out.print("Please enter a password to encrypt image with: ");
                                    String password = sc.nextLine();
                                    while (!PasswordCheck.passwordChecker(password)) {
                                        System.out.print("Please enter a stronger password: ");
                                        password = sc.nextLine();
                                    }
                                    sendFile(CLIENTPATH + name, name, password);
                                }
                            } else {
                                System.out.println("file does not exist in client");
                            }
                        } else {
                            while (!(res = br.readLine()).equals("\0")) {
                                System.out.println(res);
                            }
                        }
                        break;
                    // help
                    case "help":
                    case "h":
                    default:
                        // send command
                        pr.println(cmd);
                        // receive from server
                        while (!(res = br.readLine()).equals("\0")) {
                            System.out.println(res);
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
                is.close();
                sc.close();
                pr.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}