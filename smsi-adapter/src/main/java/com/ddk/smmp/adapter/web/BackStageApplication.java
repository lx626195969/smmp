package com.ddk.smmp.adapter.web;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;

import com.ddk.smmp.adapter.web.action.BackStageAciton;
import com.ddk.smmp.adapter.web.action.IndexAction;

/**
 * @author leeson 2014年7月18日 下午3:26:03 li_mr_ceo@163.com <br>
 * 
 */
public class BackStageApplication extends Application {
	private String userName;
	private String passWord;
	
	public BackStageApplication(String userName, String passWord) {
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
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		// 绑定资源
		router.attach("/", IndexAction.class);
		router.attach("/backstage", BackStageAciton.class);
		
		// 创建认证器
		ChallengeAuthenticator authenticator = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "Smsi-adapter Backstage Server");
		authenticator.setVerifier(new MySecretVerifier(userName, passWord));
		// 将路由器放在认证器之后
		authenticator.setNext(router);
		// 返回认证器
		return authenticator;
	}
}
