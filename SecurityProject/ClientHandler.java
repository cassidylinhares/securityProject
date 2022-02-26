package SecurityProject;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Random;

import javax.imageio.ImageIO;

public class ClientHandler implements Runnable {
    private final String CUTEPATH = "Assignment1/Cats/cute";
    private final String MEMEPATH = "Assignment1/Cats/meme";
    private Socket handler;

    private BufferedReader br = null;
    private PrintWriter pr = null;
    private DataOutputStream out = null;

    // gets name of random cute cat pic
    private String randomCute() {
        Random rand = new Random();
        File f = new File(CUTEPATH);

        String[] pics = f.list();
        int r = rand.nextInt(pics.length);

        return pics[r];
    }

    // gets name of random meme cat pic
    private String randomMeme() {
        Random rand = new Random();
        File f = new File(MEMEPATH);

        String[] pics = f.list();
        int r = rand.nextInt(pics.length);

        return pics[r];
    }

    // tell client their command is wrong
    private String insufficientArg() {
        return "Wrong command or Insufficient amount of arguments supplied. \nPlease use 'help' for list of commands and usage \r\n\n\0";
    }

    // return usage of all commands
    public String getCliCommands() {
        return "'catsu' - returns a random meme cat pic - USAGE: catsu" +
                "\n'floof' - returns a random cute cat pic - USAGE: floof" +
                "\n'help' 'h' - for commands and usage - USAGE: help OR h " +
                "\n'stop' 'disconnect' 'bye' - closes cat generator - USAGE: stop OR bye OR disconnect " +
                "\r\n\n\0";
    }

    // send image to client
    private void sendFile(String path, String name) {
        try {
            // open image
            System.out.println("Sending " + path);
            BufferedImage img = ImageIO.read(new File(path));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // write image to array stream
            ImageIO.write(img, "jpeg", baos);
            byte[] size = ByteBuffer.allocate(4).putInt(baos.size()).array();

            // write image size, img, and name
            out.write(size);
            out.write(baos.toByteArray());
            out.write(name.getBytes());
            out.flush();

            System.out.println("sent image...\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // constructor
    public ClientHandler(Socket clientSocket) throws IOException {
        this.handler = clientSocket;
    }

    @Override
    public void run() {
        try {
            br = new BufferedReader(new InputStreamReader(handler.getInputStream()));
            pr = new PrintWriter(handler.getOutputStream(), true);
            out = new DataOutputStream(handler.getOutputStream());

            String cmd;
            while ((cmd = br.readLine()) != null) {
                String[] tokens = cmd.split("\\s+");
                switch (tokens[0]) {
                    // help feature
                    case "help":
                    case "h":
                        pr.println(getCliCommands());
                        break;
                    // random meme cat
                    case "catsu":
                        if (tokens.length != 1) {
                            pr.println(insufficientArg());
                        } else {
                            String name = randomMeme();
                            sendFile(MEMEPATH + "/" + name, name);
                        }
                        break;
                    default:
                        // check for error otherwise random cute cat pic
                        if (tokens.length != 1) {
                            pr.println(insufficientArg());
                        } else {
                            String name = randomCute();
                            sendFile(CUTEPATH + "/" + name, name);
                        }
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
                out.close();
                pr.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}