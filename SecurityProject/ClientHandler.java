package SecurityProject;

import java.io.*;
import java.net.*;
import javax.net.ssl.SSLSocket;

public class ClientHandler implements Runnable {
    private final static String SERVERPATH = "SecurityProject/ServerImages/";
    private Socket handler;

    private BufferedReader br = null;
    private PrintWriter pr = null;
    private InputStream is = null;
    private OutputStream outStream = null;

    // tell client their command is wrong
    private String insufficientArg() {
        return "Wrong command or Insufficient amount of arguments supplied. \nPlease use 'help' for list of commands and usage \r\n\n\0";
    }

    // return usage of all commands
    public String getCliCommands() {
        return "'get <path-to-image>' - get an encrypted image from server - USAGE: get <path-to-image>" +
                "\n'send <path-to-image>' - send an encrypted image to server - USAGE: send <path-to-image>" +
                "\n'help' 'h' - for commands and usage - USAGE: help OR h " +
                "\n'stop' 'disconnect' 'bye' - closes cat generator - USAGE: stop OR bye OR disconnect " +
                "\r\n\n\0";
    }

    // send image to client
    private void sendFile(String path) {
        BufferedInputStream bis;

        try {
            // open image
            System.out.println("Sending " + path);
            File f = new File(path);
            byte[] b = new byte[(int) f.length()];

            bis = new BufferedInputStream(new FileInputStream(path));

            bis.read(b, 0, b.length);

            // write image size, img, and name
            outStream.write(b);
            System.out.println("sent image...\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // get image from client
    private void recieveFile(String path) {
        try {
            byte[] b = new byte[8000];
            is.read(b);
            File fileDown = new File(path);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileDown.getPath()));
            bos.write(b, 0, b.length);
            bos.flush();
            bos.close();
            // write image to destination
            System.out.println("Received file\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // constructor
    public ClientHandler(SSLSocket clientSocket) throws IOException {
        this.handler = clientSocket;
    }

    @Override
    public void run() {
        try {
            br = new BufferedReader(new InputStreamReader(handler.getInputStream()));
            pr = new PrintWriter(handler.getOutputStream(), true);
            outStream = handler.getOutputStream();
            is = handler.getInputStream();

            String cmd;
            while ((cmd = br.readLine()) != null) {
                String[] tokens = cmd.split("\\s+");
                switch (tokens[0]) {
                    // recieve from client
                    case "send":
                        if (tokens.length != 2) {
                            pr.println(insufficientArg());
                        } else {
                            String name = tokens[1];
                            recieveFile(SERVERPATH + name);
                        }
                        break;
                    // send to client
                    case "get":
                        if (tokens.length != 2) {
                            pr.println(insufficientArg());
                        } else {
                            String name = tokens[1];
                            sendFile(SERVERPATH + name);
                        }
                        break;
                    // help feature
                    case "help":
                    case "h":
                    default:
                        // check give the help commands
                        pr.println(getCliCommands());
                        break;
                }
            }
            // Log and close client
            System.out.println("Closing client");
            handler.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                is.close();
                pr.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}