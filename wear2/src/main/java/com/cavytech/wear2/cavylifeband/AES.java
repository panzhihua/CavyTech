package com.cavytech.wear2.cavylifeband;

import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by blacksmith on 2016/4/18.
 */
public class AES {
    private static String algorithm = "AES";
    private static byte[] AESkey=new byte[]  {'1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','0'};


    public static byte[] DecryptAES(byte[] text)
    {
        try {

            SecretKeySpec skeySpec = new SecretKeySpec(AESkey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            byte[] original = cipher.doFinal(text);
            return original;
        } catch (Exception e) {
            // TODO: handle exception
            Log.d("gatt_show11", e.getMessage());
            return null;
        }
    }


}
