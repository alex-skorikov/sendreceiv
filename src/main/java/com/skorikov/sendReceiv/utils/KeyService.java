package com.skorikov.sendReceiv.utils;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.springframework.util.ResourceUtils;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
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

    @Value(value = "${server.key-store}")
    private String file;

    @Value(value = "${server.key-store-password}")
    private String password;

    @Value(value = "${server.key-store-type}")
    private String storeType;

    @Value(value = "${server.key-alias}")
    private String alias;

    @Value(value = "${ssl.signature.algorithm}")
    private String signingAlgorithm;

    @Value(value = "${ssl.sigmature.hashing.algorithm}")
    private String hashingAlgorithm;

    public PrivateKey getPrivateKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(storeType);
        File storeFile = ResourceUtils.getFile(file);
        keyStore.load(new FileInputStream(storeFile), password.toCharArray());
        return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
    }

    public PublicKey getPublicKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(storeType);
        File storeFile = ResourceUtils.getFile(file);
        keyStore.load(new FileInputStream(storeFile), password.toCharArray());
        Certificate certificate = keyStore.getCertificate(alias);
        return certificate.getPublicKey();
    }

    public byte[] sign(byte[] message) {
        try {
            Signature signature = Signature.getInstance(signingAlgorithm);
            signature.initSign(getPrivateKey());
            signature.update(message);
            return signature.sign();
        } catch (Exception exp) {
            log.error("Sign - Error during signature generation", exp);
            return null;
        }
    }

    public boolean verifySign(byte[] messageBytes, byte[] signedData) {
        try {
            Signature signature = Signature.getInstance(signingAlgorithm);
            signature.initVerify(getPublicKey());
            signature.update(messageBytes);
            return signature.verify(signedData);
        } catch (Exception exp) {
            log.error("Error during verifying sign.", exp);
            return false;
        }
    }

    public byte[] decipher(byte[] messageBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashingAlgorithm);
            byte[] messageHash = md.digest(messageBytes);
            DigestAlgorithmIdentifierFinder hashAlgorithmFinder = new DefaultDigestAlgorithmIdentifierFinder();
            AlgorithmIdentifier hashingAlgorithmIdentifier = hashAlgorithmFinder.find(hashingAlgorithm);
            DigestInfo digestInfo = new DigestInfo(hashingAlgorithmIdentifier, messageHash);
            byte[] hashToEncrypt = digestInfo.getEncoded();

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey());
            return cipher.doFinal(hashToEncrypt);
        } catch (Exception exp) {
            log.error("Decipher - Error during signature generation", exp);
            return null;
        }
    }

    public boolean verifyDecipher(byte[] messageBytes, byte[] encryptedMessageHash) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashingAlgorithm);
            byte[] newMessageHash = md.digest(messageBytes);
            DigestAlgorithmIdentifierFinder hashAlgorithmFinder = new DefaultDigestAlgorithmIdentifierFinder();
            AlgorithmIdentifier hashingAlgorithmIdentifier = hashAlgorithmFinder.find(hashingAlgorithm);
            DigestInfo digestInfo = new DigestInfo(hashingAlgorithmIdentifier, newMessageHash);
            byte[] hashToEncrypt = digestInfo.getEncoded();

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, getPublicKey());
            byte[] decryptedMessageHash = cipher.doFinal(encryptedMessageHash);
            return Arrays.equals(decryptedMessageHash, hashToEncrypt);
        } catch (Exception exp) {
            log.error("Error during verifying decipher.", exp);
            return false;
        }
    }
}
