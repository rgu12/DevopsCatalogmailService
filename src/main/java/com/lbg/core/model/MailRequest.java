package com.lbg.core.model;

import org.springframework.stereotype.Component;

@Component
public class MailRequest {
	private String host;
	private String fromMailId;
	private String toMailId;
	private String platform;
	private String project;
	private String GHEOrg;
	private String password;
	private String subject;
	private String message;
	private String port;

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getGHEOrg() {
		return GHEOrg;
	}

	public void setGHEOrg(String gHEOrg) {
		GHEOrg = gHEOrg;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getFromMailId() {
		return fromMailId;
	}

	public void setFromMailId(String fromMailId) {
		this.fromMailId = fromMailId;
	}

	public String getToMailId() {
		return toMailId;
	}

	public void setToMailId(String toMailId) {
		this.toMailId = toMailId;
	}

}
