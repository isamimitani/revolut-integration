package com.tibber.dev.revolutintegration.reader;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * A class to encrypt/decrypt text data
 *
 * @version 1.0
 * @auther Isami Mitani
 */
public class TextCryptor {

    private static byte[] iv = "0000000000000000".getBytes();

    /**
     * Decrypts encrypted String with given key
     *
     * @param encrypted encrypted text data
     * @param key
     * @return decrypted String
     * @throws Exception
     */
    public static String decrypt(String encrypted, String key)
            throws Exception {
        byte[] keyb = key.getBytes("utf-8");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] thedigest = md.digest(keyb);
        SecretKeySpec skey = new SecretKeySpec(thedigest, "AES");
        Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        dcipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(iv));
        byte[] clearbyte = dcipher.doFinal(DatatypeConverter.parseHexBinary(encrypted));
        return new String(clearbyte);
    }

    /**
     * Encrypts content with given key
     *
     * @param content text data to encrypt
     * @param key
     * @return encrypted String
     * @throws Exception
     */
    public static String encrypt(String content, String key) throws Exception {
        byte[] input = content.getBytes("utf-8");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] thedigest = md.digest(key.getBytes("utf-8"));
        SecretKeySpec skc = new SecretKeySpec(thedigest, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skc, new IvParameterSpec(iv));
        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        ctLength += cipher.doFinal(cipherText, ctLength);
        return DatatypeConverter.printHexBinary(cipherText);
    }

    /**
     * Generates random key
     *
     * @return generated random key as byte array
     * @throws NoSuchAlgorithmException
     */
    public static byte[] generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom();
        int keyBitSize = 256;
        keyGenerator.init(keyBitSize, secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }

}
