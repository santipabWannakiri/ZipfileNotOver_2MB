package com.sap.module.util;

import java.io.File;

import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class ArchiveDelete {
	
	public  void archiveFileToFolder(File sourcePath,File fileToArchive, String file , AuditLogHelper audit ,Logger log) {
		boolean statusArchive = sourcePath.renameTo(fileToArchive);
		if (statusArchive) {
			audit.addLog(AuditLogStatus.SUCCESS,"File name : " + file + " move to archive.");
			log.add("File name : " + file + " move to archive.");
			deleteFile(sourcePath ,audit,log);
		}
	}
	
	
	public  void deleteFile(File fileTodelete , AuditLogHelper audit,Logger log) {
		fileTodelete.delete();
		if (!fileTodelete.exists()) {
			audit.addLog(AuditLogStatus.SUCCESS,"File : " + fileTodelete.getName() + " deleted.");
			log.add("File : " + fileTodelete.getName() + " deleted.");
		} else {
			audit.addLog(AuditLogStatus.SUCCESS,"Faile to delete file : " + fileTodelete.getName());
			log.add("Faile to delete file : " + fileTodelete.getName());
		}

	}

	
}
