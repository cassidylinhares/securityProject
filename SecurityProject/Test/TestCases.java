package SecurityProject.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.crypto.SecretKey;

import SecurityProject.*;

public class TestCases {
    // helper methods
    private static long fileEquiv(File f1, File f2) {
        int ch = 0;
        long pos = 1;

        try (BufferedInputStream fis1 = new BufferedInputStream(new FileInputStream(f1));
                BufferedInputStream fis2 = new BufferedInputStream(new FileInputStream(f2));) {
            while ((ch = fis1.read()) != -1) {
                if (ch != fis2.read()) {
                    return pos;
                }
                pos++;
            }
            if (fis2.read() == -1) {
                return -1;
            } else {
                return pos;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pos;
    }

    private static void assertB(boolean var, boolean value, String testTitle) {
        if (var == value) {
            System.out.println(testTitle + " passes. Result is: " + var);
        } else {
            System.out.println(testTitle + " failed. Result is: " + var + ". Expected: " + value);
        }
    }

    private static void assertNotL(long var, long value, String testTitle) {
        if (var != value) {
            System.out.println(testTitle + " passes. Result is: " + var);
        } else {
            System.out.println(testTitle + " failed. Result is: " + var + ". Expected: " + value);
        }
    }

    private static void assertL(long var, long value, String testTitle) {
        if (var == value) {
            System.out.println(testTitle + " passes. Result is: " + var);
        } else {
            System.out.println(testTitle + " failed. Result is: " + var + ". Expected: " + value);
        }
    }

    // test password checker
    public static void assertPasswordStrengthIsWeak1(String testTitle) {
        String password = "abc";
        boolean result = PasswordCheck.passwordChecker(password);
        assertB(result, false, testTitle);
    }

    public static void assertPasswordStrengthIsWeak2(String testTitle) {
        String password = "abcdefghi1";
        boolean result = PasswordCheck.passwordChecker(password);
        assertB(result, false, testTitle);
    }

    public static void assertPasswordStrengthIsWeak3(String testTitle) {
        String password = "Abcdefghi";
        boolean result = PasswordCheck.passwordChecker(password);
        assertB(result, false, testTitle);
    }

    public static void assertPasswordStrengthIsStrong(String testTitle) {
        String password = "Abcdefghi1";
        boolean result = PasswordCheck.passwordChecker(password);
        assertB(result, true, testTitle);
    }

    // aes
    public static void assertPasswordToKeyTrue(String testTitle) {
        String password = "howdyHey1";
        String salt = "gegerkgej";

        try {
            SecretKey key = Aes.getKeyFromPassword(password, salt);
            boolean result = key.equals(Aes.getKeyFromPassword("howdyHey1", "gegerkgej"));
            assertB(result, true, testTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void assertPasswordToKeyFalse(String testTitle) {
        String password = "howdyHey1";
        String salt = "gegerkgej";

        try {
            SecretKey key = Aes.getKeyFromPassword(password, salt);
            boolean result = key.equals(Aes.getKeyFromPassword("howdyHey1", "kjdskjdskj"));
            assertB(result, false, testTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void assertAESEncryptFiles(String testTitle) {
        File start = new File("SecurityProject/Test/meowdy.jpeg");
        File end = new File("SecurityProject/Test/encryptMeowdy.jpeg");

        try {
            SecretKey key = Aes.getKeyFromPassword("howdyHey1", "gegerkgej");
            Aes.encryptFile(key, start, end);
            long result = fileEquiv(start, end);
            assertNotL(result, -1, testTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void assertAESDecryptFiles(String testTitle) {
        File start = new File("SecurityProject/Test/encryptMeowdy.jpeg");
        File end = new File("SecurityProject/Test/decryptMeowdy.jpeg");
        File og = new File("SecurityProject/Test/meowdy.jpeg");

        try {
            SecretKey key = Aes.getKeyFromPassword("howdyHey1", "gegerkgej");
            Aes.decryptFile(key, start, end);
            long result = fileEquiv(og, end);

            assertL(result, -1, testTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        assertPasswordStrengthIsWeak1("Test 1: Assert Weak Password - too short fails:");
        assertPasswordStrengthIsWeak2("Test 2: Assert Weak Password - no uppercase letter fails:");
        assertPasswordStrengthIsWeak3("Test 3: Assert Weak Password - no number fails:");
        assertPasswordStrengthIsStrong("Test 4: Assert Strong Password passes:");
        assertPasswordToKeyFalse("Test 5: Assert same password, different salt fails:");
        assertPasswordToKeyTrue("Test 6: Assert same password, same salt passes:");
        assertAESEncryptFiles("Test 7: Assert encrypted file and original file are different:");
        assertAESDecryptFiles("Test 8: Assert decrypted file and original file are same:");
    }
}
