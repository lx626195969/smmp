package com.ddk.smmp.adapter.web;

import org.restlet.security.SecretVerifier;

/**
 * @author leeson 2014年7月18日 下午5:24:56 li_mr_ceo@163.com <br>
 * 
 */
public class MySecretVerifier extends SecretVerifier {
	private String userName;
	private String passWord;
	
	public MySecretVerifier(String userName, String passWord) {
		super();
		this.userName = userName;
		this.passWord = passWord;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	
	@Override
	public int verify(String identifier, char[] secret) {
		System.out.printf("username:%s password:%s%n", identifier, new String(secret));
		/**
		 * 此处自定义的验证规则为：如果用户名不为空，并且用户名和密码相等则通过。否则不通过
		 */
		if (identifier == null || identifier.equals("")) {
			return SecretVerifier.RESULT_INVALID;
		} else if (identifier.equals(userName) && passWord.equals(new String(secret))) {
			return SecretVerifier.RESULT_VALID;
		} else {
			return SecretVerifier.RESULT_INVALID;
		}
	}
}