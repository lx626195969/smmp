package com.ddk.smmp.adapter.utils;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * @author leeson 2014年7月22日 上午11:55:18 li_mr_ceo@163.com <br>
 *  */
public class AesUtil {
	/**
	 * 密钥算法 java6支持56位密钥，bouncycastle支持64位
	 */
	public static final String KEY_ALGORITHM = "AES";

	/**
	 * 加密/解密算法/工作模式/填充方式
	 * 
	 * JAVA6 支持PKCS5PADDING填充方式 Bouncy castle支持PKCS7Padding填充方式
	 */
	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

	/**
	 * 生成密钥，java6只支持56位密钥，bouncycastle支持64位密钥
	 * 
	 * @return byte[] 二进制密钥
	 * @throws Exception
	 */
	private static byte[] initkey(String pwd) throws Exception {
		// 实例化密钥生成器
		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");  
		secureRandom.setSeed(pwd.getBytes());
		
		// 初始化密钥生成器，AES要求密钥长度为128位、192位、256位
		kg.init(128, secureRandom);
		//kg.init(128, new SecureRandom(pwd.getBytes()));
		// 生成密钥
		SecretKey secretKey = kg.generateKey();
		// 获取二进制密钥编码形式
		return secretKey.getEncoded();
	}

	/**
	 * 转换密钥
	 * 
	 * @param key
	 *            二进制密钥
	 * @return 密钥
	 * @throws Exception
	 */
	private static Key toKey(byte[] key) throws Exception {
		// 实例化DES密钥
		// 生成密钥
		SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
		return secretKey;
	}

	/**
	 * 加密数据
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            密钥
	 * @return byte[] 加密后的数据
	 * */
	private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		// 还原密钥
		Key k = toKey(key);
		/**
		 * 实例化 使用 PKCS7PADDING 填充方式，按如下方式实现,就是调用bouncycastle组件实现
		 * Cipher.getInstance(CIPHER_ALGORITHM,"BC")
		 */
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		
		// 初始化，设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, k);
		// 执行操作
		return cipher.doFinal(data);
	}

	/**
	 * 解密数据
	 * 
	 * @param data
	 *            待解密数据
	 * @param key
	 *            密钥
	 * @return byte[] 解密后的数据
	 * */
	private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		// 欢迎密钥
		Key k = toKey(key);
		/**
		 * 实例化 使用 PKCS7PADDING 填充方式，按如下方式实现,就是调用bouncycastle组件实现
		 * Cipher.getInstance(CIPHER_ALGORITHM,"BC")
		 */
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 初始化，设置为解密模式
		cipher.init(Cipher.DECRYPT_MODE, k);
		// 执行操作
		return cipher.doFinal(data);
	}

	public static String decrypt(String data, String pwd){
		try {
			return new String(decrypt(Base64.decodeBase64(data), initkey(pwd)), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String encrypt(String data, String pwd){
		try {
			return Base64.encodeBase64String(encrypt(data.getBytes("utf-8"), initkey(pwd)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String str = "大家好，今天下午小会议室开会，请准时参会。:{}/=";
		System.out.println("原文：" + str);
		// 初始化密钥
		String pwd = "pj1e0tMRFiZVHehT+VzrCg==";
		byte[] key = AesUtil.initkey(pwd);
		
		System.out.println("密钥：" + Base64.encodeBase64String(key));
		
		// 加密数据
		String data = AesUtil.encrypt(str, pwd);
		System.out.println("加密后：" + data);
		// 解密数据
		String data1 = AesUtil.decrypt(data, pwd);
		System.out.println("解密后：" + data1);
	}
}
