package cn.phoniex.ssg.test;

import cn.phoniex.ssg.util.MD5Encoder;
import cn.phoniex.ssg.util.SHA1Encoder;
import android.test.AndroidTestCase;

public class testencoder extends AndroidTestCase {

	public testencoder()
	{
		String string = "ThisIsAapple";
		String enStr = null;
		enStr = MD5Encoder.encode(string);
		System.out.println("MD5 \r\n" + enStr);
		enStr =  SHA1Encoder.sha1Lower(string);
		System.out.println("sha1\r\n"+enStr);
		enStr =  SHA1Encoder.sha1Upper(string);
		System.out.println("SHA-1\r\n"+enStr);
		
		
	}
}
