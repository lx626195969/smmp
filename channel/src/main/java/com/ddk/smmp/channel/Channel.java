package com.ddk.smmp.channel;

import java.io.Serializable;

import org.apache.mina.core.session.IoSession;

/**
 * 
 * @author leeson 2014年6月16日 上午11:02:36 li_mr_ceo@163.com <br>
 * 
 */
public class Channel implements Serializable {
	private static final long serialVersionUID = 6627710423263568032L;

	public static final Integer RUN_STATUS = 1;
	public static final Integer STOP_STATUS = 2;
	public static final Integer RECONNECT_STATUS = 3;

	private Integer id;
	private String name;
	private Integer type;// 类型 1联通 2移动 3电信
	private String host;// 主机
	private Integer localPort;//联通本地端口
	private Integer port;// 端口
	private Integer protocolType;// 1-SGIP1.2 2-cmpp2.0 3-cmpp3.0 4-SMGP3.0
	private String accessCode;// 接入号
	private String companyCode;// 企业代码
	private String account;// 帐号
	private String password;// 密码
	private Integer nodeId;//节点ID
	private Integer submitRate;// 提交速率
	
	private Integer supportLen;//通道支持字数
	private Integer signNum = 0;//签名占用字数
	private Integer encodeType;//长短信编码类型
	private Integer isSend;//通道数据库状态是否为运行状态
	private String submitUrl;//HTTP通道提交URL
	private Integer isBatch;//是否批量
	
	private Integer status = STOP_STATUS;
	private boolean isReConnect = false;// 是否重连
	private IoSession session = null;
	private Client client = null;
	
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public IoSession getSession() {
		synchronized (this) {
			return session;
		}
	}

	public void setSession(IoSession session) {
		synchronized (this) {
			this.session = session;
		}
	}
	
	public Integer getIsBatch() {
		return isBatch;
	}

	public void setIsBatch(Integer isBatch) {
		this.isBatch = isBatch;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubmitUrl() {
		return submitUrl;
	}

	public void setSubmitUrl(String submitUrl) {
		this.submitUrl = submitUrl;
	}

	public Integer getIsSend() {
		return isSend;
	}

	public void setIsSend(Integer isSend) {
		this.isSend = isSend;
	}

	public int getEncodeType() {
		return encodeType;
	}

	public void setEncodeType(int encodeType) {
		this.encodeType = encodeType;
	}

	public Integer getSignNum() {
		return signNum;
	}

	public void setSignNum(Integer signNum) {
		this.signNum = signNum;
	}

	public Integer getSupportLen() {
		return supportLen;
	}

	public void setSupportLen(Integer supportLen) {
		this.supportLen = supportLen;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLocalPort() {
		return localPort;
	}

	public void setLocalPort(Integer localPort) {
		this.localPort = localPort;
	}

	public boolean isReConnect() {
		synchronized (this) {
			return isReConnect;
		}
	}

	public void setReConnect(boolean isReConnect) {
		synchronized (this) {
			this.isReConnect = isReConnect;
		}
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

	public Integer getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(Integer protocolType) {
		this.protocolType = protocolType;
	}

	public String getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getSubmitRate() {
		return submitRate;
	}

	public void setSubmitRate(Integer submitRate) {
		this.submitRate = submitRate;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	/** 通道运行状态 1运行 2停止 3重连 */
	public Integer getStatus() {
		synchronized (this) {
			return status;
		}
	}

	public void setStatus(Integer status) {
		synchronized (this) {
			this.status = status;
		}
	}

	public Channel(Integer gallayId, String name, Integer type, String host,
			Integer port, Integer localPort, Integer protocolType,
			String accessCode, String companyCode, String account,
			String password, Integer submitRate, Integer nodeId,
			Integer supportLen, Integer signNum, Integer encodeType,
			Integer isSend, String submitUrl, Integer isBatch) {
		super();
		this.id = gallayId;
		this.name = name;
		this.type = type;
		this.host = host;
		this.port = port;
		this.localPort = localPort;
		this.protocolType = protocolType;
		this.accessCode = accessCode;
		this.companyCode = companyCode;
		this.account = account;
		this.password = password;
		this.submitRate = submitRate;
		this.nodeId = nodeId;
		this.supportLen = supportLen;
		this.signNum = signNum;
		this.encodeType = encodeType;
		this.isSend = isSend;
		this.submitUrl = submitUrl;
		this.isBatch = isBatch;
	}
	
	public static void main(String[] args) {
		System.out.println((int) Long.parseLong("3020001050"));
	}
}