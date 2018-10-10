package com.sap.module.sftp;

import java.io.File;
import java.io.IOException;

import java.net.SocketException;
import java.util.List;


import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.module.connection.ConectionSFTP;
import com.sap.module.connection.SFTP_Return;
import com.sap.module.util.ArchiveDelete;
import com.sap.module.util.AuditLogHelper;
import com.sap.module.util.Logger;
import com.sap.module.util.ParameterConnection;
import com.sap.module.util.PrefixSuffixTimestamp;

public class MovefileSFTP {
	public  void moveFile(List<String> tempfileName,String tempSrcPath, String tempTargetPath,ParameterConnection paramConnection,Logger log,AuditLogHelper audit) throws SocketException, IOException, ModuleException {
		ConectionSFTP connectionSFTP = new ConectionSFTP();
		CreateDirectorySFTP directory = new CreateDirectorySFTP();
		ArchiveDelete  archiveDelete = new ArchiveDelete();
		PrefixSuffixTimestamp prefix_suffix_time = new PrefixSuffixTimestamp();
		
		SFTP_Return<ChannelSftp, Session> getConnection = null;
		ChannelSftp sftpChannel;
		int dummyZipruning = -1;
		
		getConnection = connectionSFTP.LoginSFTP(paramConnection.getServer(), paramConnection.getPortNumber(), paramConnection.getUsername(), paramConnection.getPassword(), audit, log);
		sftpChannel = getConnection.getChannelSftp();
		directory.CreateDirectorysSFTP(tempSrcPath, tempTargetPath, sftpChannel, paramConnection.getArchive() ,audit,log);
		
		try {
			// ======================= Upload File =========================
			for (int i = 0; i < tempfileName.size(); i++) {
				File sourcePath = new File(tempSrcPath + "/" + tempfileName.get(i));
				if (sourcePath.exists() && !sourcePath.isDirectory()) {
					audit.addLog( AuditLogStatus.SUCCESS,"Uploading : " + tempfileName.get(i));
					log.add("Uploading : " + tempfileName.get(i));
					String fileToTarget = prefix_suffix_time.Target_preFix_sufFix_timeStamp(tempfileName.get(i),dummyZipruning,audit,paramConnection, log);
			
					try{
						audit.addLog( AuditLogStatus.SUCCESS,"Target file is : "+ tempTargetPath + "/"+ fileToTarget);
					    sftpChannel.put(tempSrcPath + "/" + tempfileName.get(i), tempTargetPath + "/"+ fileToTarget, 3);
					
						audit.addLog( AuditLogStatus.SUCCESS,"File uploaded successfully.");
						log.add("File uploaded successfully.");
						if (paramConnection.getArchive() == true) {
							String fileArchive = prefix_suffix_time.Soure_preFix_sufFix_timeStamp(tempfileName.get(i),audit,paramConnection, log);
							String archivePath = tempSrcPath + "/archive/"+ fileArchive;
							File fileArchive_f = new File(archivePath);
							archiveDelete.archiveFileToFolder(sourcePath, fileArchive_f,tempfileName.get(i),audit, log);
						} else if (paramConnection.getDelete() == true) {
							archiveDelete.deleteFile(sourcePath,audit, log);
						}
					}catch(SftpException e){
						audit.addLog( AuditLogStatus.ERROR,"Error while upload : " + tempfileName.get(i)+". The message is : " + e.getMessage());
						log.add("Error while upload : " + tempfileName.get(i)+". The message is : " + e.getMessage());
					}
					
				} else {
					audit.addLog( AuditLogStatus.WARNING,"File not found. Plase check file name : "+ tempfileName.get(i));
					log.add("File not found. Plase check file name : "+ tempfileName.get(i));
				}
			}
		} catch (IOException ex) {
			audit.addLog( AuditLogStatus.ERROR, "Error: "+ ex.getMessage());
			log.add("Error: " + ex.getMessage());
			throw new ModuleException(ex.getMessage());
		} finally {
			connectionSFTP.LogoutSFTP(getConnection, audit, log);;
		}
	}	
}
