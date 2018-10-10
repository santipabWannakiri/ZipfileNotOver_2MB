package com.sap.module.ftps;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPSClient;

import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.module.connection.ConnectionFTPS;
import com.sap.module.util.ArchiveDelete;
import com.sap.module.util.AuditLogHelper;
import com.sap.module.util.Logger;
import com.sap.module.util.ParameterConnection;
import com.sap.module.util.PrefixSuffixTimestamp;

public class ZipFileFTPS {
	
public  void ZipFile(List<String> tempfileName, String tempSrcPath,String tempTargetPath, String zipName, int zipRunning ,AuditLogHelper audit,ParameterConnection paramConnection,Logger log)throws Exception ,ModuleException{
		
		CreateDirectoryFTPS directory = new CreateDirectoryFTPS();
		ArchiveDelete  archiveDelete = new ArchiveDelete();
		PrefixSuffixTimestamp prefix_suffix_time = new PrefixSuffixTimestamp();
		
		ConnectionFTPS connectionFTPS  = new ConnectionFTPS();
		int statusOfZip = 0;
		FileInputStream fis = null;
		log.add("Prepare to login.");
		FTPSClient ftps = connectionFTPS.LoginFTPS( paramConnection.getServer(), paramConnection.getPortNumber(), paramConnection.getUsername(), paramConnection.getPassword(), audit, log);
		log.add("Already to login");
		directory.CreateDirectorysFTPS(tempSrcPath, tempTargetPath, ftps, paramConnection.getArchive() ,audit,log);

		try {
			String fileToTarget = prefix_suffix_time.Target_preFix_sufFix_timeStamp(zipName,zipRunning,audit,paramConnection, log);
			audit.addLog(AuditLogStatus.SUCCESS, "Creating " + fileToTarget + ".zip");
			log.add("Creating " + fileToTarget + ".zip");
			OutputStream outputStream = ftps.storeFileStream(tempTargetPath+ "/" + fileToTarget + ".zip");
			ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
			for (int i = 0; i < tempfileName.size(); i++) {
				File sourcePath = new File(tempSrcPath + "/"+ tempfileName.get(i));
				fis = new FileInputStream(new File(tempSrcPath + "/"+ tempfileName.get(i)));
				byte[] bs = IOUtils.toByteArray(fis);
				fis.close();
				// send to zip
				audit.addLog(AuditLogStatus.SUCCESS,"Add file: " + tempfileName.get(i));
				log.add("Add file: " + tempfileName.get(i));
				statusOfZip = addOneFileToZipArchive(zipOutputStream,tempfileName.get(i), bs);

				if (statusOfZip == 1) {
					if (paramConnection.getArchive() == true) {
						String fileArchive = prefix_suffix_time.Soure_preFix_sufFix_timeStamp(tempfileName.get(i),audit,paramConnection, log);
						String archivePath = tempSrcPath + "/archive/"+ fileArchive;
						File fileArchive_f = new File(archivePath);
						archiveDelete.archiveFileToFolder(sourcePath, fileArchive_f,tempfileName.get(i),audit, log);

					} else if (paramConnection.getDelete() == true) {
						archiveDelete.deleteFile(sourcePath,audit, log);
					}
				}
			}
			zipOutputStream.close();
			outputStream.close();
			audit.addLog(AuditLogStatus.SUCCESS,"Transfer done.");
			log.add("Transfer done.");
		} catch (IOException e) {
			audit.addLog(AuditLogStatus.ERROR, "Error : " + e.getMessage());
			log.add("Error : " + e.getMessage());
			throw new ModuleException("Error : " + e.getMessage());
		} finally {
			connectionFTPS.LogoutFTPS(ftps,audit,log);
		}
	}

	private int addOneFileToZipArchive(ZipOutputStream zipStream,String fileName, byte[] content) throws Exception {
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipStream.putNextEntry(zipEntry);
		zipStream.write(content);
		zipStream.flush();
		zipStream.closeEntry();
		return 1;
	}


}
