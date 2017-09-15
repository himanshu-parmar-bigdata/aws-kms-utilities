package com.parmarh.kms.utilities;

import com.amazonaws.services.kms.model.GenerateDataKeyResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by himanshupannar on 9/15/17.
 */
public class KmsUtitlitiesTest {

    KmsUtilities kmsUtilities = null;

    @Before
    public void initialize(){
        kmsUtilities = new KmsUtilities();
    }

    @Test
    public void encryptAndDecryptWithPlainTextTest() throws NoSuchPaddingException, InvalidKeyException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            BadPaddingException,
            InvalidAlgorithmParameterException {


        GenerateDataKeyResult dataKeyResult = kmsUtilities.generateDataKeyResult();

        String encryptedEmail = kmsUtilities.encrypt("demo@test.com",  dataKeyResult.getPlaintext());
        System.out.println(encryptedEmail);

        final String decryptedEmail = kmsUtilities.decrypt(encryptedEmail,
                dataKeyResult.getPlaintext());
        System.out.println(decryptedEmail);
        Assert.assertEquals("demo@test.com", decryptedEmail );
    }

    // This will not work for your environment as data key is different in your env
    @Ignore
    @Test
    public void decryptString(){
        String encodedEncString = "dXGXdxHafadfafdCyAZOaS7P1jJYTkXTXYOD9HOrdA/gRjtx4M7s0fH0X+AdmKW";
        String encodedEncDataKey = "AQIDAHhE2iV8LOvHasdffdfdgT2znucd9ayu7bXdfQE5XiLe/dbeyBxggZ58vgYkAAAAbjBsBgkqhkiG9w0BBwagXzBdAgEAMFgGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMUfDeQtJLUjSy5WCcAgEQgCuZTfKJZxITHEsSnNqdL7MhvU4wuwcDvNeN9rNzRgN3bJObXyM95aSCk6Bz";
        String expectedString = "for_testing";
        try {
            String decryptedStr = kmsUtilities.decrypt(encodedEncString, encodedEncDataKey);
            System.out.println(decryptedStr);

            Assert.assertEquals(expectedString, decryptedStr);

        } catch(Exception e){
            e.printStackTrace();

        }

    }

}
