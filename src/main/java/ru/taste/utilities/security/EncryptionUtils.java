package ru.taste.utilities.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {
    private static final String SECRET_KEY = "f7e10f173e01fbc93365gacdf21595897fb09ad5a65f4gf6g1g8c8017b4gcf22";

    private static byte[] hexToBytes(String hexString) {
        int length = hexString.length();
        byte[] data = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }

        return data;
    }

    private static SecretKeySpec keySpec(String SECRET_KEY) {
        return new SecretKeySpec(hexToBytes(SECRET_KEY), "AES");
    }

    public static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec(SECRET_KEY));

        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec(SECRET_KEY));

        byte[] decodedData = Base64.getDecoder().decode(encryptedData.replace(" ", "+"));
        byte[] decryptedData = cipher.doFinal(decodedData);

        return new String(decryptedData);
    }

    public static String encrypt(String data, String SECRET_KEY) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec(SECRET_KEY));

        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData, String SECRET_KEY) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec(SECRET_KEY));

        byte[] decodedData = Base64.getDecoder().decode(encryptedData.replace(" ", "+"));
        byte[] decryptedData = cipher.doFinal(decodedData);

        return new String(decryptedData);
    }
}
