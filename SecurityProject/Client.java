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
    private static OutputStream outStream = null;
    private static Scanner sc = null;
    private static BufferedReader br = null;
    private static PrintWriter pr = null;

    // recieve encrypted pic from server and decrypt
    private static void recieveFile(String path, String name, String password) {
        File outputFile = new File(path);
        File inputFile = new File(CLIENTPATH + "encrypt" + outputFile.getName());
        BufferedOutputStream bos = null;

        //this is sus
        //server sends the encrypted image first tho?
        //and the client also recieve the image from a file name decrypt something?
        // get encrypted image
//        try {
//            FileInputStream fis = new FileInputStream(
//                    "C:\\Users\\lenovo\\Pictures\\logo4.png");
//            // get data from server
//            byte[] b = new byte[8000];
//            is.read(b);
//
//            bos = new BufferedOutputStream(new FileOutputStream(inputFile.getPath()));
//            bos.write(b, 0, b.length);
//            bos.flush();
//            // write image to destination
//            System.out.println("Received file\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // decrypt image
        try {
            SecretKey key = Aes.getKeyFromPassword(password, "jdsfrkjehr");
            if(inputFile.exists()) {
                Aes.decryptFile(key, inputFile, outputFile);
            }

            //bos.close();
        } catch (Exception e) {

            if(inputFile.delete()){
                System.out.println("detled");
            }
            else{
                System.out.println("detled232");
            }
            e.printStackTrace();
        }
        if(inputFile.exists()) {
            System.out.println("exist");
            if (inputFile.delete()) {
                System.out.println("detled");
            } else {
                System.out.println("detled232");
            }
        }
    }

    // encrypt and send encrypted pic to server
    private static void sendFile(String path, String name, String password) {
        BufferedInputStream bis;
        //String newPath = SERVERPATH + "encrypt" + name;
        String newPath = SERVERPATH + name;
        File inputFile = new File(path);
        File outputFile = new File(newPath);

        // encrypt image
        try {
            SecretKey key = Aes.getKeyFromPassword(password, "jdsfrkjehr");
            Aes.encryptFile(key, inputFile, outputFile);
            System.out.println("Encrypted image: " + outputFile.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // send encrypted image
//        try {
//            // open image
//            System.out.println("Sending " + path);
//            byte[] b = new byte[(int) outputFile.length()];
//
//            bis = new BufferedInputStream(new FileInputStream(newPath));
//
//            bis.read(b, 0, b.length);

            // write image size, img, and name
//            outStream.write(b);
//            System.out.println("sent image...\n");

            //when you upload a non-encrypted image, u dont delete the orginal image file like when users submit a file, it copies the file and sends it
            //idk why u delete the outputfile too
            // delete non-encrypted images
//            inputFile.delete();
//            outputFile.delete();
//            bis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static void main(String[] args) {
        // welcome msg
        System.out.println(" /\\_/\\ \n( o.o )\n > ^ <");
        System.out.println("WELCOME TO CATSU!\nThe random cat generator \n\n");

        String home = System.getProperty("user.home");

        System.setProperty("javax.net.ssl.trustStore",
                "SecurityProject/myTrustStore.jts");
        System.setProperty("javax.net.ssl.trustStorePassword", "abc123");

        try {
            // init socket
            SocketFactory factory = SSLSocketFactory.getDefault();
            SSLSocket client = (SSLSocket) factory.createSocket(HOST, PORT);

            // init IO
            is = client.getInputStream();
            outStream = client.getOutputStream();
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
                        System.out.println(br.readLine());

                        // check for error or recieve pic
                        if (tokens.length == 2) {

                            String name = tokens[1];
                            if(new File(CLIENTPATH + name).exists()) {
                                System.out.print("Please enter the password you encrypted image with: ");
                                String password = sc.nextLine();
                                recieveFile(CLIENTPATH + name, name, password);
                            }
                            else{
                                System.out.println("file does not exist in client");
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
                        // check for error or recieve pic
                        if (tokens.length == 2) {
                            String name = tokens[1];
                            if(new File(CLIENTPATH + name).exists()) {
                                System.out.print("Please enter a password to encrypt image with: ");
                                String password = sc.nextLine();
                                while (!PasswordCheck.passwordChecker(password)) {
                                    System.out.print("Please enter a stronger password: ");
                                    password = sc.nextLine();
                                }
                                System.out.println(name + " " + password);
                                sendFile(CLIENTPATH + name, name, password);
                            }
                            else{
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