package com.basecore.util.encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.basecore.util.log.LogUtil;

public class AESUtil {
	static final public byte[] KEY_VI = { 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8 };
	public static final String bm = "UTF-8";

	public static String encrypt(String dataPassword, String cleartext) throws Exception {
		IvParameterSpec zeroIv = new IvParameterSpec(KEY_VI);
		SecretKeySpec key = new SecretKeySpec(dataPassword.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
		byte[] encryptedData = cipher.doFinal(cleartext.getBytes(bm));
		for(int i=0,size=cleartext.getBytes(bm).length;i<size;i++){
			LogUtil.getLogger().d("encry "+cleartext.getBytes(bm)[i]);
		}
		return new String(parseByte2HexStr(encryptedData));
	}

	public static String decrypt(String dataPassword, String encrypted) throws Exception {
		byte[] byteMi = parseHexStr2Byte(encrypted);
		IvParameterSpec zeroIv = new IvParameterSpec(KEY_VI);
		SecretKeySpec key = new SecretKeySpec(dataPassword.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
		byte[] decryptedData = cipher.doFinal(byteMi);
		for(int i=0,size=decryptedData.length;i<size;i++){
			LogUtil.getLogger().d("decrypt "+encrypted+"  "+decryptedData[i]);
		}
		return new String(decryptedData, bm);
	}

	/**
	 * 将16进制转换为二进制
	 * 
	 * @param hexStr
	 * @return
	 */
	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1) {
			return null;
		}
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @param buf
	 * @return
	 */
	public static String parseByte2HexStr(byte buf[]) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}
	
	
 
    
}
