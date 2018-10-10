package com.sap.module.util;

public class ParameterConnection {
	

	private String protocol;
	private String server;
	private String username;
	private String password;
	private int portNumber;
	private Boolean appendLog;
	private Boolean Archive;
	private Boolean Delete;
	private Boolean srcTimestamp;
	private String srcPrefix;
	private String srcSuffix;
	private Boolean tarTimestamp;
	private String tarPrefix;
	private String tarSuffix;
	private Boolean tarTimestampAfter;
	
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
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
	public int getPortNumber() {
		return portNumber;
	}
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	public Boolean getAppendLog() {
		return appendLog;
	}
	public void setAppendLog(Boolean appendLog) {
		this.appendLog = appendLog;
	}
	public Boolean getArchive() {
		return Archive;
	}
	public void setArchive(Boolean archive) {
		Archive = archive;
	}
	public Boolean getDelete() {
		return Delete;
	}
	public void setDelete(Boolean delete) {
		Delete = delete;
	}
	public Boolean getSrcTimestamp() {
		return srcTimestamp;
	}
	public void setSrcTimestamp(Boolean srcTimestamp) {
		this.srcTimestamp = srcTimestamp;
	}
	public String getSrcPrefix() {
		return srcPrefix;
	}
	public void setSrcPrefix(String srcPrefix) {
		this.srcPrefix = srcPrefix;
	}
	public String getSrcSuffix() {
		return srcSuffix;
	}
	public void setSrcSuffix(String srcSuffix) {
		this.srcSuffix = srcSuffix;
	}
	public Boolean getTarTimestamp() {
		return tarTimestamp;
	}
	public void setTarTimestamp(Boolean tarTimestamp) {
		this.tarTimestamp = tarTimestamp;
	}
	public String getTarPrefix() {
		return tarPrefix;
	}
	public void setTarPrefix(String tarPrefix) {
		this.tarPrefix = tarPrefix;
	}
	public String getTarSuffix() {
		return tarSuffix;
	}
	public void setTarSuffix(String tarSuffix) {
		this.tarSuffix = tarSuffix;
	}
	

	public Boolean getTarTimestampAfter() {
		return tarTimestampAfter;
	}
	public void setTarTimestampAfter(Boolean tarTimestampAfter) {
		this.tarTimestampAfter = tarTimestampAfter;
	}
	@Override
	public String toString() {
		return "ParameterConnection [Archive=" + Archive + ", Delete=" + Delete
				+ ", appendLog=" + appendLog + ", password=" + password
				+ ", portNumber=" + portNumber + ", protocol=" + protocol
				+ ", server=" + server + ", srcPrefix=" + srcPrefix
				+ ", srcSuffix=" + srcSuffix + ", srcTimestamp=" + srcTimestamp
				+ ", tarPrefix=" + tarPrefix + ", tarSuffix=" + tarSuffix
				+ ", tarTimestamp=" + tarTimestamp + ", tarTimestampAfter="
				+ tarTimestampAfter + ", username=" + username + "]";
	}
	
	
}
