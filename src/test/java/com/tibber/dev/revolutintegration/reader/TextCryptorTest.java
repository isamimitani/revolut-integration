package com.tibber.dev.revolutintegration.reader;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class TextCryptorTest {

    @Test
    void testEncryptionAndDecryption() throws Exception {
        String data = "Here is my string";
        String key = "1234567891123456";
        String key2 = "1234567891123451";
        String cipher = TextCryptor.encrypt(data, key);
        String decipher = TextCryptor.decrypt(cipher, key);
        String cipher2 = TextCryptor.encrypt(data, key2);
        String decipher2 = TextCryptor.decrypt(cipher2, key2);

        assertEquals(data, decipher);
        assertEquals(data, decipher2);
        assertEquals(decipher, decipher2);
        assertNotEquals(cipher, cipher2);
        assertEquals(cipher, TextCryptor.encrypt(data, "1234567891123456"));
    }

    @Test
    void testGenerateKey() throws Exception {
        byte[] generatedKey = TextCryptor.generateKey();
        String text = Base64.getEncoder().encodeToString(generatedKey);
        System.out.println(text);
    }

}