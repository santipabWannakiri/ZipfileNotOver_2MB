package com.sap.module.sftp;

import java.io.File;


import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.module.util.AuditLogHelper;
import com.sap.module.util.Logger;

public class CreateDirectorySFTP {

	
	public void CreateDirectorysSFTP(String tempSrcPath, String tempTargetPath,ChannelSftp sftpChannel, Boolean Archive, AuditLogHelper audit,Logger log) throws ModuleException {
		// ==================== Create Directory ====================
		audit.addLog(AuditLogStatus.SUCCESS, "Sorce path AL11 :" + tempSrcPath);
		log.add("Sorce path AL11 :" + tempSrcPath);
		audit.addLog(AuditLogStatus.SUCCESS, "Archive :" + Archive);
		log.add("Archive :" + Archive);
		audit.addLog(AuditLogStatus.SUCCESS,"Create Directory : FTPS target path :" + tempTargetPath);
		log.add("Create Directory : FTPS target path :" + tempTargetPath);

		// Create archive folder
		if (Archive == true) {
			File files = new File(tempSrcPath + "/archive");
			if (!files.exists()) {
				if (files.mkdirs()) {
					audit.addLog(AuditLogStatus.SUCCESS,"Create Directory : Archive folder : " + tempSrcPath + "/archive");
					log.add("Create Directory : Archive folder : " + tempSrcPath + "/archive");
				} else {
					audit.addLog(AuditLogStatus.SUCCESS,"Failed to create archive folder!");
					log.add("Failed to create archive folder!");
				}
			} else {
				audit.addLog(AuditLogStatus.SUCCESS,"The directory archive exists!!");
				log.add("The directory archive exists!!");
			}
		}
		
		// Create FTPS target path
		audit.addLog(AuditLogStatus.SUCCESS, "Start to create directory target.");
		log.add("Start to create directory target.");
		String[] directories = tempTargetPath.split("/");
		try {
			sftpChannel.cd("/");
		} catch (SftpException e) {
			audit.addLog(AuditLogStatus.ERROR,"Error : Can't change directory to root!! " );
			log.add("Error : Can't change directory to root!!" );
			throw new ModuleException(e.getMessage());
		}
		boolean dirExists = true;

		for (String dir : directories) {
			if (!dir.isEmpty()) {
				if (dirExists) {
					try {
						sftpChannel.cd(dir);
						dirExists = true;
						audit.addLog(AuditLogStatus.WARNING, "Directory \""
								+ dir + "\" exists");
						log.add("Directory \"" + dir + "\" exists");
					} catch (SftpException e) {
						dirExists = false;
					}
				}
				if (dirExists == false) {
					try {
						sftpChannel.mkdir(dir);
						audit.addLog(AuditLogStatus.WARNING,"Create directory : " + dir);
						log.add("Create directory : " + dir);
					} catch (SftpException e) {
						audit.addLog(AuditLogStatus.ERROR,"Error : Can't create directory : " + dir);
						log.add("Error : Can't create directory : " + dir);
						throw new ModuleException(e.getMessage());
					}
					if (dirExists == false) {
						try {
							sftpChannel.cd(dir);
						} catch (SftpException e) {
							audit.addLog(AuditLogStatus.ERROR,"Error: Can't change directory to " + dir);
							log.add("Error: Can't change directory to " + dir);
							throw new ModuleException(e.getMessage());
						}
					}

				}
			}
		}

	}

}

