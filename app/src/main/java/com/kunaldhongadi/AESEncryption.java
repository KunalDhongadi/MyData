package com.kunaldhongadi;

import android.os.Build;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryption {
    public static final String TAG = "TAG";

    private static SecretKeySpec secretKey;
    private static byte[] key;

    public static String getKey() {
        FirebaseAuth fAuth;
        String userId;

        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getUid();

        String user_id = userId;
        StringBuilder a = new StringBuilder();
        a.append(user_id);
        a = a.reverse();
        String b = new String(a);

        Log.v(TAG,b);

        String [] c = b.split("");
        String key = "";
        for(int i = 0; i < b.length(); i++) {
            if(i % 2 != 0) {
                key = key + c[i];
            }
        }
        return key;
    }


    public static void setKey(String myKey)
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String strToEncrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
            } else {
                return android.util.Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")),
                        android.util.Base64.DEFAULT);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
            }else {
                return new String(cipher.doFinal(android.util.Base64.decode(strToDecrypt,android.util.Base64.DEFAULT)));
            }

        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
