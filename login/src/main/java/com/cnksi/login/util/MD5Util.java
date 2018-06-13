package com.cnksi.login.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密组件
 * 
 * @author wbw
 * @version 1.0
 * @since 1.0
 */
public abstract class MD5Util {

	/**
	 * 字符串MD5加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return
	 */
	public static String encodeMD5Hex(String data) {
		// 执行消息摘要
		return new String(Hex.encodeHex(DigestUtils.md5(data.getBytes())));
	}

	/**
	 * 文件MD5加密
	 * 
	 * @param file
	 * @return
	 */
	public static final String getFileMD5(File file) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			return getFileMD5(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 文件MD5加密
	 * 
	 * @param fis
	 * @return
	 */
	public static final String getFileMD5(InputStream fis) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = fis.read(buffer, 0, 1024)) != -1) {
				md.update(buffer, 0, length);
			}
			BigInteger bigInt = new BigInteger(1, md.digest());
			return bigInt.toString(16);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args) {
		try {
			String encodeMD5Hex = MD5Util.encodeMD5Hex("我爱你");
			encodeMD5Hex = encodeMD5Hex + "-我爱你";
			String encrypt = AESUtil.aesEncrypt(encodeMD5Hex, AESUtil.KEY);
			System.out.println(encrypt);
			System.out.println(AESUtil.decode("yQ2XhRwvgRRgJInlXNEYOkNctxDg4PblUqwlB0SED7A7qmUo6dbvEAbB1O1/A23U"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
