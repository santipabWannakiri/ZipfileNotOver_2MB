package com.sap.module.connection;

import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;


import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.module.util.AuditLogHelper;
import com.sap.module.util.Logger;

public class ConnectionFTPS {
	
	private FTPSClient ftps;
    
	public  FTPSClient LoginFTPS(String server ,int portNumber ,String username , String password  ,AuditLogHelper audit,Logger log) throws ModuleException {

		ftps = new FTPSClient("SSL");
		ftps.setConnectTimeout(1000); // Set timeout 60 sec
		ftps.setAutodetectUTF8(true); // Set UTF8 handling
		int ftpReply = 0;
		try {
			ftps.connect(server, portNumber);
			ftpReply = ftps.getReplyCode();
			if (!FTPReply.isPositiveCompletion(ftpReply)) {
				audit.addLog(AuditLogStatus.ERROR,"FTPSClientAccess: " + ftps.getReplyString());
				log.add("FTPSClientAccess: " + ftps.getReplyString());
			} else {
				audit.addLog(AuditLogStatus.SUCCESS,"FTPSClientAccess: FTPS Connected "+ ftps.getReplyString());
				log.add("FTPSClientAccess: FTPS Connected "+ ftps.getReplyString());
			}
			ftps.setBufferSize(1000);
			boolean loginCheck = ftps.login(username, password);

			if (loginCheck == true) {
				// Set FTPS Environment
				ftps.enterLocalPassiveMode();
				ftps.setFileType(FTP.BINARY_FILE_TYPE);
				ftps.sendCommand("OPTS UTF8 ON");
				audit.addLog(AuditLogStatus.SUCCESS,"AutodetectUTF8 Reply "+ String.valueOf(ftps.getAutodetectUTF8()));
				log.add("AutodetectUTF8 Reply "+ String.valueOf(ftps.getAutodetectUTF8()));
			} else {
				audit.addLog(AuditLogStatus.ERROR,"FTPSClientAccess: " + ftps.getReplyString());
				log.add("FTPSClientAccess: " + ftps.getReplyString());

			}
		} catch (IOException e) {
			audit.addLog(AuditLogStatus.ERROR,e.getMessage());
			log.add(e.getMessage());
			throw new ModuleException(e.getMessage());
		}
		return ftps;
	}
	
	
	public  void LogoutFTPS(FTPSClient ftps ,AuditLogHelper audit, Logger log) throws ModuleException {
		try {
			ftps.noop();
			ftps.logout();
			ftps.disconnect();
			audit.addLog(AuditLogStatus.SUCCESS,"FTPSClientAccess: FTPS logout.");
			audit.addLog(AuditLogStatus.SUCCESS,"FTPSClientAccess: FTPS Disconnected.");
			log.add("FTPSClientAccess: FTPS logout.");
			log.add("FTPSClientAccess: FTPS Disconnected.");
		} catch (IOException e) {
			audit.addLog(AuditLogStatus.ERROR,e.getMessage());
			log.add(e.getMessage());
			throw new ModuleException(e.getMessage());
		}

	}

}
