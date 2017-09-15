package com.parmarh.kms.utilities;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyResult;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


/**
 * Created by himanshupannar on 7/13/17.
 */

public class KmsUtilities {
    AWSKMSClient aWSKms = null;

    public KmsUtilities() {

        aWSKms = new AWSKMSClient(new DefaultAWSCredentialsProviderChain());
    }

    // replace this with your AWS KMS data key
    // Please make sure that your user must have permission to access this key
    private static final String KMS_MASTER_KEY = "arn:aws:kms:us-east-1:11111111111:key/00000000-0000-0000-0000-000000000";


    private static final String KEY_SPEC_AES_256 = "AES_256";

    private static final String KEY_SPEC_AES = "AES";

    /**
     * Generates a new GenerateDataKeyResult which includes plaintext key
     * and encrypted key
     *
     * @return
     */
    public GenerateDataKeyResult generateDataKeyResult() {

        GenerateDataKeyRequest dataKeyRequest = new GenerateDataKeyRequest();
        dataKeyRequest.setKeyId(KMS_MASTER_KEY);
        dataKeyRequest.setKeySpec(KEY_SPEC_AES_256);
        GenerateDataKeyResult dataKeyResult = aWSKms
                .generateDataKey(dataKeyRequest);
        // return encrypted data key
        ByteBuffer plaintext = dataKeyResult.getPlaintext();

        return dataKeyResult;

    }

    /**
     * Returns the encoded string from given byteBuffer
     * @param byteBuffer
     *
     * @return
     */
    public String encodeString(ByteBuffer byteBuffer) {
        return Base64.getEncoder().encodeToString(getByteArray(byteBuffer));
    }

    /**
     * Returns decoded byte array from given encoded string
     * @param encodedStr
     * @return
     */
    public byte[] decodeString(String encodedStr) {
        return Base64.getDecoder().decode(encodedStr);
    }


    public static SecretKeySpec makeKey(ByteBuffer key) {
        return new SecretKeySpec(getByteArrayUsingDuplicate(key), KEY_SPEC_AES);
    }

    /**
     * Decrypt encrypted data key with KMS Master key
     * @param encryptedDataKey
     * @return
     */
    protected ByteBuffer decyptDatatKey(String encryptedDataKey) {
        // Decode Encrypted data key

        byte[] base64DecodedKey = Base64.getDecoder().decode(encryptedDataKey);
        ByteBuffer decodedDataKey = ByteBuffer.allocate(base64DecodedKey.length);
        decodedDataKey.put(base64DecodedKey);
        decodedDataKey.flip();

        GenerateDataKeyRequest dataKeyRequest = new GenerateDataKeyRequest();
        dataKeyRequest.setKeyId(KMS_MASTER_KEY);
        dataKeyRequest.setKeySpec(KEY_SPEC_AES_256);

        DecryptRequest decryptRequest = new DecryptRequest()
                .withCiphertextBlob(decodedDataKey);

        ByteBuffer plaintext = aWSKms.decrypt(decryptRequest).getPlaintext();

        return plaintext;

    }

    /**
     * Retruns byte array from provided ByteBuffer using duplicate
     * element
     * @param byteBuffer
     * @return
     */
    public static byte[] getByteArrayUsingDuplicate(ByteBuffer byteBuffer) {
        byte[] byteArray = new byte[byteBuffer.capacity()];
        ((ByteBuffer) byteBuffer.duplicate().clear()).get(byteArray);
        return byteArray;
    }

    /**
     * Retruns byte array from provided ByteBuffer
     *
     * @param byteBuffer
     * @return
     */
    public static byte[] getByteArray(ByteBuffer byteBuffer) {
        byte[] byteArray = new byte[byteBuffer.remaining()];
        byteBuffer.get(byteArray);
        return byteArray;
    }

    /**
     * Encrypts the provided string with provided plainText Key
     *
     * @param email
     * @param plainTextKey
     * @return
     */
    public String encrypt(String email, ByteBuffer plainTextKey) {
        return encrypt(email, makeKey(plainTextKey));
    }

    /**
     * Encrypts the provided string with provided SecretKeySpec
     *
     * @param email
     * @param key
     * @return
     */
    public String encrypt(String email, SecretKeySpec key) {

        byte[] enc = null;

        try {
            Cipher cipher = Cipher.getInstance(KEY_SPEC_AES);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            enc = cipher.doFinal(email.getBytes());
        } catch (Exception e) {
           System.out.println("Error while encrypting " +  e +  e.getLocalizedMessage());
        }
        // encoded source data string
        return Base64.getEncoder().encodeToString(enc);

    }


    /**
     * Encrypts the user string using encrypted data key
     * @param src
     * @param encryptedDataKey
     * @return
     */
    public String encrypt(String src, String encryptedDataKey) {

        ByteBuffer plainTextKey = decyptDatatKey(encryptedDataKey);
        return encrypt(src,plainTextKey);

    }

    /**
     * Encrypts the provided string using provided datakeyResult
     * @param src
     * @param dataKeyResult
     * @return
     */
    public String encrypt(String src, GenerateDataKeyResult dataKeyResult) {

        ByteBuffer plainTextKey =  dataKeyResult.getPlaintext();
        return encrypt(src,plainTextKey);
    }

    /**
     * generates a new encrypted data key
     * @return
     */
    public String generateEncryptedDataKeyStr() {

        GenerateDataKeyResult dataKeyResult = generateDataKeyResult();

        ByteBuffer ciphertextBlob = dataKeyResult.getCiphertextBlob();

        String encryptedEncodedDataKey = Base64.getEncoder().encodeToString(getByteArray(ciphertextBlob));

        // You can save (serialize) this encrypted encoded data key in String fomrat outside for future use

        return encryptedEncodedDataKey;

    }

    public String decrypt(String encryptedStr, SecretKeySpec key) throws
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {

        byte[] decodeBase64src = decodeString(encryptedStr);

        // decrypt with secret key
        Cipher cipher = Cipher.getInstance(KEY_SPEC_AES);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(decodeBase64src));

    }

    public String decrypt(String encryptedStr, ByteBuffer plainTextKey) throws
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {

        return decrypt(encryptedStr, makeKey(plainTextKey));

    }

    public  String decrypt(String encryptedStr, String encodedEncryptedKeyStr)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException {

        // decode and decrypt data key
        ByteBuffer plainTextKey = decyptDatatKey(encodedEncryptedKeyStr);

        return decrypt(encryptedStr, plainTextKey);

    }
}
