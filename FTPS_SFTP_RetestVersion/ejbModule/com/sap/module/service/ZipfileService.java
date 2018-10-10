package com.sap.module.service;

import java.io.File;

import java.util.ArrayList;
import java.util.List;




import com.sap.aii.af.lib.mp.module.ModuleException;

import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

import com.sap.module.ftps.ZipFileFTPS;
import com.sap.module.sftp.ZipFileSFTP;

import com.sap.module.util.AuditLogHelper;
import com.sap.module.util.Logger;
import com.sap.module.util.ParameterConnection;


public class ZipfileService {

	public void Zipfile_Check(AuditLogHelper audit,ArrayList<ArrayList<String>> dataSet,ParameterConnection paraConnection, Logger log) throws Exception,ModuleException {
		ZipFileFTPS zipFTPS = new ZipFileFTPS();
		ZipFileSFTP zipSFTP = new ZipFileSFTP();
		
		// Check size file should not over 2 MB
		audit.addLog(AuditLogStatus.SUCCESS, "Prepare to zip file ...");
		log.add("Prepare to zip file ...");
		String tempSrcPath = null;
		List<String> tempfileNameZip = new ArrayList<String>();
		List<String> fileForZip = new ArrayList<String>();
		List<String> tempCheckFile = new ArrayList<String>();
		String tempTargetPath = null;
		String zipName = null;

		double sizeFile = 0.00000000;
		double MaxZip = 1.99;
		int zipRunning = 1;
	
		for (int i = 0; i < dataSet.size(); i++) {
			audit.addLog(AuditLogStatus.SUCCESS, "dataset is .." + dataSet.size());
			tempSrcPath = dataSet.get(i).get(1);
			tempTargetPath = dataSet.get(i).get(dataSet.get(i).size() - 2);
			zipName = dataSet.get(i).get(dataSet.get(i).size() - 1);
			for (int k = 2; k < dataSet.get(i).size() - 2; k++) {
				tempCheckFile.add(dataSet.get(i).get(k));
			}
			// Check file in directory
			log.add("Checking file in directory.......");
			audit.addLog(AuditLogStatus.SUCCESS,"Checking file in directory.......");
			for (int loopFile = 0; loopFile < tempCheckFile.size(); loopFile++) {
				File getFile = new File(tempSrcPath + "/" + tempCheckFile.get(loopFile));
				if (getFile.exists() && !getFile.isDirectory()) {
					tempfileNameZip.add(tempCheckFile.get(loopFile));
				} else {
					log.add("File not found!!. Please check file name : " + tempCheckFile.get(loopFile));
					audit.addLog(AuditLogStatus.WARNING,"File not found!!. Please check file name : " + tempCheckFile.get(loopFile));
				}
			}
			tempCheckFile.clear();
			
			// Check size not over 2 mb and send to zip
			for (int y = 0; y < tempfileNameZip.size(); y++) {
				File fileDownload = new File(tempSrcPath + "/" + tempfileNameZip.get(y));
				double megabytes = (double) fileDownload.length()/ (1024 * 1024);
				if (megabytes >= MaxZip) {
					audit.addLog(AuditLogStatus.WARNING, "The file name :"+ tempfileNameZip.get(y) + " size over 2 MB!!");
					log.add("The file name :" + tempfileNameZip.get(y) + " size over 2 MB!!");
					break;
				}
				sizeFile = sizeFile + megabytes;
				if (sizeFile <= MaxZip) {
					fileForZip.add(tempfileNameZip.get(y));
						if (y == (tempfileNameZip.size() - 1) && sizeFile <= MaxZip) { // check last index
							try {
								audit.addLog(AuditLogStatus.WARNING, "Protocal is : " +paraConnection.getProtocol() );
								if (paraConnection.getProtocol().equalsIgnoreCase("ftps")) {
									zipFTPS.ZipFile(fileForZip, tempSrcPath,tempTargetPath, zipName, zipRunning, audit,paraConnection, log);
								} else if (paraConnection.getProtocol().equalsIgnoreCase("sftp")) {
									zipSFTP.ZipFile(fileForZip, tempSrcPath,tempTargetPath, zipName, zipRunning, audit,paraConnection, log);
								}
							} catch (Exception e) {
								audit.addLog(AuditLogStatus.ERROR, "Error : " + e.getMessage());
								log.add("Error : " + e.getMessage());
								throw new ModuleException(e.getMessage());
							}
							fileForZip.clear();
							sizeFile = 0.00000000;
						}
				} else {
					try {
						if (paraConnection.getProtocol().equalsIgnoreCase("ftps")) {
							zipFTPS.ZipFile(fileForZip, tempSrcPath,tempTargetPath, zipName, zipRunning, audit,paraConnection, log);
						} else if (paraConnection.getProtocol().equalsIgnoreCase("sftp")) {
							zipSFTP.ZipFile(fileForZip, tempSrcPath,tempTargetPath, zipName, zipRunning, audit,paraConnection, log);
						}
					} catch (Exception e) {
						audit.addLog(AuditLogStatus.ERROR, "Error : " + e.getMessage());
						log.add("Error : " + e.getMessage());
						throw new ModuleException(e.getMessage());
					}
					fileForZip.clear();
					sizeFile = 0.00000000;
					zipRunning++;
					y--;
				}
			}
			tempfileNameZip.clear();
			tempCheckFile.clear();
			zipRunning = 1;
		}
	}
}
