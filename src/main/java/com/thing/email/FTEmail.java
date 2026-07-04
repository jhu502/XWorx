package com.thing.email;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.flame.type.ServiceType;
import com.flame.annotations.XConfig;
import com.flame.annotations.XService;
import com.thing.entity.ConnectableEntity;
import com.flame.type.XBaseType;

@Entity
@Table(name = "ft_email", uniqueConstraints = {})
public class FTEmail extends ConnectableEntity {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "server_type", length = 20)
	@XConfig(name = "serverType", friendlyName = "Server Type", baseType = XBaseType.STRING, description = "Server Type", defaultValue = "POP3", required = true)
	private String serverType;
	@Basic
	@Column(name = "encoding", length = 20)
	@XConfig(name = "encoding", friendlyName = "Default Encoding", baseType = XBaseType.STRING, description = "Default Encoding", defaultValue = "utf-8", required = true)
	private String encoding;
	@Basic
	@Column(name = "smtp_host", length = 200)
	@XConfig(name = "smtpHost", friendlyName = "SMTP Host", baseType = XBaseType.STRING, description = "SMTP Host", required = true)
	protected String smtpHost = "";
	@Basic
	@Column(name = "pop3_host", length = 200)
	@XConfig(name = "pop3Host", friendlyName = "POP3 Host", baseType = XBaseType.STRING, description = "POP3 Host", required = true)
	protected String pop3Host = "";
	@Basic
	@Column(name = "imap_host", length = 200)
	@XConfig(name = "imapHost", friendlyName = "IMAP Host", baseType = XBaseType.STRING, description = "IMAP Host", required = true)
	protected String imapHost = "";
	@Basic
	@Column(name = "port")
	@XConfig(name = "port", friendlyName = "Port", baseType = XBaseType.INTEGER, description = "Port", defaultValue = "25", required = true)
	protected Integer port = 25;
	@Basic
	@Column(name = "username", length = 100)
	@XConfig(name = "userName", friendlyName = "Email User Name", baseType = XBaseType.STRING, description = "Email User Name", required = true)
	protected String userName = "";
	@Basic
	@Column(name = "password", length = 100)
	@XConfig(name = "password", friendlyName = "Email Password", baseType = XBaseType.PASSWORD, description = "Email Password", required = true)
	protected String password = "";

	@XService(name = "getServerType", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	@XService(name = "getEncoding", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@XService(name = "getSmtpHost", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	@XService(name = "getPop3Host", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getPop3Host() {
		return pop3Host;
	}

	public void setPop3Host(String pop3Host) {
		this.pop3Host = pop3Host;
	}

	@XService(name = "getImapHost", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getImapHost() {
		return imapHost;
	}

	public void setImapHost(String imapHost) {
		this.imapHost = imapHost;
	}

	@XService(name = "getPort", serviceType = ServiceType.Local, resultType = XBaseType.INTEGER)
	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@XService(name = "getUserName", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@XService(name = "getPassword", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
