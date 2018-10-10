package com.sap.module.service;


import java.io.IOException;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

import com.sap.module.ftps.MovefileFTPS;
import com.sap.module.sftp.MovefileSFTP;

import com.sap.module.util.AuditLogHelper;
import com.sap.module.util.Logger;
import com.sap.module.util.ParameterConnection;


public class MovefileService {

	public void Unzip(AuditLogHelper audit,ArrayList<ArrayList<String>> dataSet,ParameterConnection paramConnection, Logger log) throws SocketException, IOException, Exception, ModuleException {

		MovefileFTPS movefileFTPS = new MovefileFTPS();
		MovefileSFTP movefileSFTP = new MovefileSFTP();
		audit.addLog(AuditLogStatus.SUCCESS, "Prepare to move file ...");
		log.add("Prepare to move file ...");
		String tempSrcPath = null;
		List<String> tempfileNameUnsend = new ArrayList<String>();
		String tempTargetPath = null;

		for (int i = 0; i < dataSet.size(); i++) {
			tempSrcPath = dataSet.get(i).get(1);
			tempTargetPath = dataSet.get(i).get(dataSet.get(i).size() - 2);
			for (int k = 2; k < dataSet.get(i).size() - 2; k++) {
				tempfileNameUnsend.add(dataSet.get(i).get(k));
			}
			try {
				if(paramConnection.getProtocol().equalsIgnoreCase("ftps")){
				movefileFTPS.moveFile(tempfileNameUnsend, tempSrcPath,tempTargetPath, paramConnection, log, audit);
				}
				else if (paramConnection.getProtocol().equalsIgnoreCase("sftp")){
				movefileSFTP.moveFile(tempfileNameUnsend, tempSrcPath, tempTargetPath, paramConnection, log, audit);	
				}
			} catch (SocketException e) {
				audit.addLog(AuditLogStatus.ERROR, "Error " + e.getMessage());
				log.add("Error " + e.getMessage());
				throw new ModuleException(e.getMessage());
			} catch (IOException e) {
				audit.addLog(AuditLogStatus.ERROR, "Error " + e.getMessage());
				log.add("Error " + e.getMessage());
				throw new ModuleException(e.getMessage());
			} catch (ModuleException e) {
				audit.addLog(AuditLogStatus.ERROR, "Error " + e.getMessage());
				log.add("Error " + e.getMessage());
				throw new ModuleException(e.getMessage());
			}
			tempfileNameUnsend.clear();
		}
	}

}
