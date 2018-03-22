package service;

import java.security.MessageDigest;

public class Protocol {

	public static String PUTCHUNK = "PUTCHUNK";
	public static String VERSION = "1.0";
	public static String CRLF = "\r" + "\n";
	
	public static void decypherMsg(byte[] msg) {
		// see if it's PUTCHUNK, STORED, etc..
	}
	
}
