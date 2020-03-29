package it.eng.ms.camundademo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Base64;

import org.apache.commons.io.IOUtils;

public class UTF8 {
	private final static String utf8 = "UTF-8";
	public static String toBase64(String str) {
		if (str==null)
			return null;
		try {
			byte x[] = str.getBytes(utf8);
			byte y[] = Base64.getEncoder().encode(x);
			
			return new String(y, utf8);
		} catch (Exception e) {
			return null;
		}
	}

	public static String fromBase64(String str) {
		if (str==null)
			return null;
		try {
			byte x[] = str.getBytes(utf8);
			byte y[] = Base64.getDecoder().decode(x);
			
			return new String(y, utf8);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String string(byte[] b) {
		try {
			return new String(b, utf8);
		} 
		catch (Exception e) {
			return null;
		}
	}
	
	public static byte[] bytes(String b) {
		try {
			return b.getBytes(utf8);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String urlEncode(String x) {
		try {
			return URLEncoder.encode(x, utf8);
		} catch (Exception e) {
			return null;
		}
	}

	public static String streamToString(InputStream is) {
		try {
			return IOUtils.toString(is, utf8);
		} 
		catch (IOException e) {
			return "Error: "+e.getMessage();
		} 
	}
}
