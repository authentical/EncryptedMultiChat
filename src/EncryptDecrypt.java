// From http://www.adeveloperdiary.com/java/how-to-easily-encrypt-and-decrypt-text-in-java/
// I do not believe this is a compliant (really secure) way to handle the encryption but its
// my first go.

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class EncryptDecrypt {


    // ENCRYPT
    public static byte[] encrypt(String strClearText, String encryptionKey) {

        byte[] encryptedText={};

        try {
            Key key = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
            encryptedText = cipher.doFinal(strClearText.getBytes());


        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedText;
    }


    // DECRYPT
    public static String decrypt(byte[] strEncrypted, String encryptionKey) {
        String decryptedText = "";

        try {
            Key key = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
            decryptedText = new String(cipher.doFinal(strEncrypted));


        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }
}
