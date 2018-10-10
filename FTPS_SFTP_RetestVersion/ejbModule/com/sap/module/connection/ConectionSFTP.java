package com.sap.module.connection;



import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.module.util.AuditLogHelper;
import com.sap.module.util.Logger;

public class ConectionSFTP {

	private JSch jsch;

	public SFTP_Return<ChannelSftp, Session> LoginSFTP( String server,int portNumber, String username, String password,AuditLogHelper audit, Logger log) throws ModuleException {
		jsch = new JSch();
		Session session = null;
		ChannelSftp sftpChannel = null;
	
		try {
			session = jsch.getSession(username,server, portNumber);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect();
			if(session.isConnected() == false){
				audit.addLog(AuditLogStatus.ERROR,"Connection to server fail!!");
				throw new ModuleException("Connection to server fail!!");
			}
			audit.addLog(AuditLogStatus.SUCCESS,"Connecting to SFTP Server .....");
			log.add("Connecting to SFTP Server .....");
			Channel channel = session.openChannel("sftp");
			channel.connect();
			 sftpChannel = (ChannelSftp) channel;
		} catch (JSchException e) {
			throw new ModuleException(e.getMessage());
		}
		return new SFTP_Return<ChannelSftp, Session>(sftpChannel, session);

	}
	
	
	public  void LogoutSFTP(SFTP_Return<ChannelSftp, Session> sftpReturn,AuditLogHelper audit, Logger log) throws ModuleException {
		sftpReturn.getChannelSftp().exit();
		sftpReturn.getChannelSftp().disconnect();
		sftpReturn.getSession().disconnect();
		audit.addLog(AuditLogStatus.SUCCESS,"SFTPClientAccess: SFTP logout.");
		audit.addLog(AuditLogStatus.SUCCESS,"SFTPClientAccess: SFTP Disconnected.");
		log.add("SFTPClientAccess: SFTP logout.");
		log.add("SFTPClientAccess: SFTP Disconnected.");

	}
	
	
	
	

}
