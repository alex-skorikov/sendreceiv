package com.skorikov.sendReceiv.utils;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.springframework.stereotype.Service;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.springframework.util.ResourceUtils;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Arrays;

@Service
@Slf4j
public class KeyService {

    public PrivateKey getPrivateKey(String file, char[] password, String storeType, String alias) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(storeType);
        File storeFile = ResourceUtils.getFile(file);
        keyStore.load(new FileInputStream(storeFile), password);
        return (PrivateKey) keyStore.getKey(alias, password);
    }

    public PublicKey getPublicKey(String file, char[] password, String storeType, String alias) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(storeType);
        File storeFile = ResourceUtils.getFile(file);
        keyStore.load(new FileInputStream(storeFile), password);
        Certificate certificate = keyStore.getCertificate(alias);
        return certificate.getPublicKey();
    }

    public byte[] sign(byte[] message, String signingAlgorithm, PrivateKey signingKey) throws SecurityException {
        try {
            Signature signature = Signature.getInstance(signingAlgorithm);
            signature.initSign(signingKey);
            signature.update(message);
            return signature.sign();
        } catch (GeneralSecurityException exp) {
            log.error("Sign - Error during signature generation", exp);
            return null;
        }
    }

    public boolean verifySign(byte[] messageBytes, String signingAlgorithm, PublicKey publicKey, byte[] signedData) {
        try {
            Signature signature = Signature.getInstance(signingAlgorithm);
            signature.initVerify(publicKey);
            signature.update(messageBytes);
            return signature.verify(signedData);
        } catch (GeneralSecurityException exp) {
            log.error("Error during verifying sign.", exp);
            return false;
        }
    }

    public byte[] decipher(byte[] messageBytes, String hashingAlgorithm, PrivateKey privateKey) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashingAlgorithm);
            byte[] messageHash = md.digest(messageBytes);
            DigestAlgorithmIdentifierFinder hashAlgorithmFinder = new DefaultDigestAlgorithmIdentifierFinder();
            AlgorithmIdentifier hashingAlgorithmIdentifier = hashAlgorithmFinder.find(hashingAlgorithm);
            DigestInfo digestInfo = new DigestInfo(hashingAlgorithmIdentifier, messageHash);
            byte[] hashToEncrypt = digestInfo.getEncoded();

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(hashToEncrypt);
        } catch (GeneralSecurityException | IOException exp) {
            log.error("Decipher - Error during signature generation", exp);
            return null;
        }
    }

    public boolean verifyDecipher(byte[] messageBytes, String hashingAlgorithm, PublicKey publicKey, byte[] encryptedMessageHash) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashingAlgorithm);
            byte[] newMessageHash = md.digest(messageBytes);
            DigestAlgorithmIdentifierFinder hashAlgorithmFinder = new DefaultDigestAlgorithmIdentifierFinder();
            AlgorithmIdentifier hashingAlgorithmIdentifier = hashAlgorithmFinder.find(hashingAlgorithm);
            DigestInfo digestInfo = new DigestInfo(hashingAlgorithmIdentifier, newMessageHash);
            byte[] hashToEncrypt = digestInfo.getEncoded();

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] decryptedMessageHash = cipher.doFinal(encryptedMessageHash);
            return Arrays.equals(decryptedMessageHash, hashToEncrypt);
        } catch (GeneralSecurityException | IOException exp) {
            log.error("Error during verifying decipher.", exp);
            return false;
        }
    }
}
