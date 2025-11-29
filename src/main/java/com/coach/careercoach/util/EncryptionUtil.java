package com.coach.careercoach.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES 加密解密工具类
 */
@Component
public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    
    @Value("${encryption.secret-key:CareerCoach2024Secret1234567890}")
    private String secretKey;

    /**
     * 加密字符串
     * @param plainText 明文
     * @return Base64编码的密文
     */
    public String encrypt(String plainText) {
        try {
            if (plainText == null || plainText.isEmpty()) {
                return null;
            }
            
            // 确保密钥长度为16、24或32字节
            byte[] keyBytes = getValidKeyBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("加密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解密字符串
     * @param encryptedText Base64编码的密文
     * @return 明文
     */
    public String decrypt(String encryptedText) {
        try {
            if (encryptedText == null || encryptedText.isEmpty()) {
                return null;
            }
            
            // 确保密钥长度为16、24或32字节
            byte[] keyBytes = getValidKeyBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("解密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取有效的密钥字节数组（确保长度为16、24或32字节）
     */
    private byte[] getValidKeyBytes() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        
        // AES要求密钥长度为16、24或32字节
        int targetLength = 32; // 使用256位密钥
        byte[] validKey = new byte[targetLength];
        
        // 如果密钥太短，用0填充；如果太长，截断
        System.arraycopy(keyBytes, 0, validKey, 0, Math.min(keyBytes.length, targetLength));
        
        return validKey;
    }
}

