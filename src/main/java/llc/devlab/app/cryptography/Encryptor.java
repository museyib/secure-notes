package llc.devlab.app.cryptography;


import llc.devlab.app.cryptography.config.EncryptionProperties;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

@Component
public class Encryptor {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private final EncryptionProperties encryptionProperties;
    private final SecureRandom secureRandom;

    public Encryptor(EncryptionProperties encryptionProperties) {
        this.encryptionProperties = encryptionProperties;
        this.secureRandom = new SecureRandom();
    }

    public String encrypt(String input) {
        try {
            // 1. Generate a fresh random IV for this encryption
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, iv);
            byte[] cipherText = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

            // 2. Combine [12-byte IV] + [CipherText] into one byte array
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (GeneralSecurityException e) {
            throw new SecurityException("Encryption failed", e);
        }
    }

    public String decrypt(String input) {
        try {
            byte[] decoded = Base64.getDecoder().decode(input);

            // 1. Extract the first 12 bytes as the IV
            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);

            // 2. Extract the remaining bytes as the actual ciphertext
            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            // 3. Initialize cipher with the extracted IV
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE, iv);
            byte[] plainText = cipher.doFinal(cipherText);

            return new String(plainText, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new SecurityException("Decryption failed", e);
        }
    }

    private Cipher getCipher(int mode, byte[] iv) throws GeneralSecurityException {
        SecretKey key = new SecretKeySpec(
                encryptionProperties.secretKey().getBytes(StandardCharsets.UTF_8),
                "AES"
        );
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, key, spec);
        return cipher;
    }
}
