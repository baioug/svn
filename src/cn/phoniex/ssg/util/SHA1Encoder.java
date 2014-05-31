package cn.phoniex.ssg.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1Encoder {

	public static String sha1Upper(String s) {
        MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        digest.reset();
        byte[] data = digest.digest(s.getBytes());
        return String.format("%0" + (data.length*2) + "X", new BigInteger(1, data));
}

	public static String sha1Lower(String string)
	
	{
		try {
			MessageDigest  digest = MessageDigest.getInstance("SHA-1");
			byte[]  bytes = digest.digest(string.getBytes());
			StringBuffer sb = new  StringBuffer();
			for(int i = 0;i<bytes.length;i++){
				String s = Integer.toHexString(0xff&bytes[i]);
				
				if(s.length()==1){
					sb.append("0"+s);
				}else{
					sb.append(s);
				}
			}
			
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("buhuifasheng");
		}
	}
}
