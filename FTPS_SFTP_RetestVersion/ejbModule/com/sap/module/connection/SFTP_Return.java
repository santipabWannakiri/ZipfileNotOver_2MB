package com.sap.module.connection;

public class SFTP_Return<ChannelSftp,Session> {

	private final ChannelSftp channelSftp;
	private final Session session;
	
	public SFTP_Return(ChannelSftp ch ,Session sn) {
		this.channelSftp = ch;
		this.session = sn;
	}

	public ChannelSftp getChannelSftp() {
		return channelSftp;
	}

	public Session getSession() {
		return session;
	}
	
	
	

}
