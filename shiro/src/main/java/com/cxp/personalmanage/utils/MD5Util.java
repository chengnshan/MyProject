package com.cxp.personalmanage.utils;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class MD5Util {

	public static String encryptM5(String algorithmName,String password, String salt, int hashIterations) {
		SimpleHash simpleHash = new SimpleHash(algorithmName, ByteSource.Util.bytes(password), salt, hashIterations);
		return simpleHash.toString();
	}
	
	public static Object MD5Encypt(String password, String salt, int hashIterations) {
		ByteSource source = ByteSource.Util.bytes(password);
		SimpleHash simpleHash = new SimpleHash("MD5", source, salt, hashIterations);
		return simpleHash;
	}
	
	public static void main(String[] args) {
		System.out.println(encryptM5("MD5","123456", "abcdefg", 3));
	}
}
