package com.budgetBook.common;

import java.security.SecureRandom;

public class CreateSalt {
	
	
	public static String CreateSaltToString() {
		// 개인 salt 추가
		SecureRandom secureRandom = new SecureRandom();
		byte[] byteSalt = new byte[20];
		// 난수 생성
		secureRandom.nextBytes(byteSalt);
		// byte to String
		StringBuffer sb = new StringBuffer();
		for(byte b : byteSalt) {
			sb.append(String.format("%02x", b));
		}
		String salt = sb.toString();
		
		return salt;
	}
}
