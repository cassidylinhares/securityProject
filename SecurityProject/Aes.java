package SecurityProject;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

public class Aes {
    private static SecretKey secretKey;
    private static byte[] key;
    public static void main(String[] argv) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, IOException, BadPaddingException, InvalidKeyException {
        //this void function is for demotration purposes and can be deleted
        String keyPassword = "crypto";
        String salt = "sdfksdflk";
        System.out.println("key password: " + keyPassword + " salt: "+salt);
        System.out.println("Get Key for AES using Password-Based Key Derivation Function(PBKDF2)");
        secretKey = getKeyFromPassword(keyPassword,salt);
        decryptFile(secretKey,new File("SecurityProject/Cats/meme2/anger.jpeg"),new File("SecurityProject/Cats/cute2/anger.jpeg"));
        System.out.println("encrypting");
        String encrypt = encrypt("HelloWorld",keyPassword);

        System.out.println(encrypt);
        System.out.println("decrypting");
        String decrypt = decrypt(encrypt, keyPassword);
        System.out.println(decrypt);

    }
    public static void setKey(final String myKey) {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public static SecretKey getKeyFromPassword(String password, String salt) throws InvalidKeySpecException, NoSuchAlgorithmException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return secret;
    }

    public static String encrypt(final String strToEncrypt, final String secret) {
        try {
            //setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(final String strToDecrypt, final String secret) {
        try {
            //setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder()
                    .decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
    public static void encryptFile(SecretKey key,File inputFile, File outputFile) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException {

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }
    public static void decryptFile(SecretKey key,File inputFile, File outputFile) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException {
        System.out.println("Decrypting image "+inputFile.getName());
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, key);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
        System.out.println("Finished Decrypting");
    }
    //this function is used to populate the server with encrypted images
    //Images in meme2 folder are encrypted images of meme folder images
    //Cute folder images still need to be encrypted
//    public static void sendFile(SecretKey key){
//
//            File f = new File("SecurityProject/Cats/meme");
//            File[] files = f.listFiles();
//            if (files != null) {
//                for (File child : files) {
//                    try {
//                        // Do something with child
//                        // open image
//                        System.out.println("Sending " + child.getName());
//                        //BufferedImage img = ImageIO.read(child);
//                        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        encryptFile(key, child, new File("SecurityProject/Cats/meme2/"+child.getName()));
//                        // write image to array stream
////                    ImageIO.write(img, "jpeg", baos);
////                    byte[] size = ByteBuffer.allocate(4).putInt(baos.size()).array();
////
////                    // write image size, img, and name
////                    out.write(size);
////                    out.write(baos.toByteArray());
////                    out.write(child.getName().getBytes());
////                    out.flush();
//
//                        System.out.println("sent image...\n");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (IllegalBlockSizeException e) {
//                        e.printStackTrace();
//                    } catch (NoSuchPaddingException e) {
//                        e.printStackTrace();
//                    } catch (BadPaddingException e) {
//                        e.printStackTrace();
//                    } catch (NoSuchAlgorithmException e) {
//                        e.printStackTrace();
//                    } catch (InvalidKeyException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//    }
}
