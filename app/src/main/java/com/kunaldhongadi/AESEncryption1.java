package com.kunaldhongadi;

import android.os.Build;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryption1 {
    public static final String TAG = "TAG";

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

        Log.v(TAG,"inside func:b:" + b);

//        String [] c = b.split("");
//        Log.v(TAG,"inside func:c:" + "1:" + c[0].getClass().getName() + " 2:" +c[1] + c[2]);

        String key = "";
//        if(c[0] == ""){
//            Log.v(TAG,"inside func:if null");
//            for(int i = 0; i < b.length(); i++) {
//                if(i % 2 != 0) {
//                    key = key + c[i];
//                }
//            }
//        } else {
//            Log.v(TAG,"inside func:if not null");
//            for(int i = 0; i < b.length(); i++) {
//                if(i % 2 == 0) {
//                    key = key + c[i];
//                }
//            }
//        }

        key = b.substring(2,16);

        return key + "kd";
    }



    public static String encrypt(String Data, String secret){
        try{
            Key key = generateKey(secret);
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(Data.getBytes());
            String encryptedValue = null;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                encryptedValue = Base64.getEncoder().encodeToString(encVal);
            }else {
                encryptedValue = android.util.Base64.encodeToString(encVal,
                        android.util.Base64.DEFAULT);
            }
            return encryptedValue;
        }
        catch (Exception e){
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt, String secret) {
        try {
            Key key = generateKey(secret);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                Log.v(TAG,"decrypting:greater than equals to oreo ");
                return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
            }else {
//                Log.v(TAG,"decrypting:less than equals to oreo ");
                return new String(cipher.doFinal(android.util.Base64.decode(strToDecrypt,android.util.Base64.DEFAULT)));
            }
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }


    private static Key generateKey(String secret) throws Exception {
        byte[] decoded = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            decoded = Base64.getDecoder().decode(secret.getBytes());
        } else{
            decoded = android.util.Base64.decode(secret.getBytes(),android.util.Base64.DEFAULT);
        }
        Key key = new SecretKeySpec(decoded, "AES");
        return key;
    }


    public static String decodeKey(String str) {
        byte[] decoded = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            decoded = Base64.getDecoder().decode(str.getBytes());
        } else{
            decoded = android.util.Base64.decode(str.getBytes(),android.util.Base64.DEFAULT);
        }
        return new String(decoded);
    }


    public static String encodeKey(String str) {
        byte[] encoded = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encoded = Base64.getEncoder().encode(str.getBytes());
        } else {
            encoded = android.util.Base64.encode(str.getBytes(),
                    android.util.Base64.DEFAULT);
        }
        return new String(encoded);
    }


}
