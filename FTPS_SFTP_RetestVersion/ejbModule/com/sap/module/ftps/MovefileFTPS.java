package com.sap.module.ftps;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.List;

import org.apache.commons.net.ftp.FTPSClient;

import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.module.connection.ConnectionFTPS;
import com.sap.module.util.ArchiveDelete;
import com.sap.module.util.AuditLogHelper;
import com.sap.module.util.Logger;
import com.sap.module.util.ParameterConnection;
import com.sap.module.util.PrefixSuffixTimestamp;

public class MovefileFTPS {

	public  void moveFile(List<String> tempfileName,String tempSrcPath, String tempTargetPath,ParameterConnection paramConnection,Logger log,AuditLogHelper audit) throws SocketException, IOException, ModuleException {
		CreateDirectoryFTPS directory = new CreateDirectoryFTPS();
		ArchiveDelete  archiveDelete = new ArchiveDelete();
		PrefixSuffixTimestamp prefix_suffix_time = new PrefixSuffixTimestamp();
		ConnectionFTPS connectionFTPS  = new ConnectionFTPS();
		int dummyZipruning = -1;
		
		FTPSClient ftps = connectionFTPS.LoginFTPS( paramConnection.getServer(), paramConnection.getPortNumber(), paramConnection.getUsername(), paramConnection.getPassword(), audit, log);
		directory.CreateDirectorysFTPS(tempSrcPath, tempTargetPath, ftps, paramConnection.getArchive() ,audit,log);
		try {
			// ======================= Upload File =========================
			for (int i = 0; i < tempfileName.size(); i++) {
				File sourcePath = new File(tempSrcPath + "/" + tempfileName.get(i));
				if (sourcePath.exists() && !sourcePath.isDirectory()) {
					audit.addLog( AuditLogStatus.SUCCESS,"Uploading : " + tempfileName.get(i));
					InputStream inputStream = new FileInputStream(sourcePath);

					String fileToTarget = prefix_suffix_time.Target_preFix_sufFix_timeStamp(tempfileName.get(i),dummyZipruning,audit,paramConnection, log);
					
					OutputStream outputStream = ftps.storeFileStream(tempTargetPath + "/"+ fileToTarget);
					byte[] bytesIn = new byte[4096];
					int read = 0;
					while ((read = inputStream.read(bytesIn)) != -1) {
						outputStream.write(bytesIn, 0, read);
					}
					inputStream.close();
					outputStream.close();

					boolean completed = ftps.completePendingCommand();
					if (completed) {
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
			connectionFTPS.LogoutFTPS(ftps,audit,log);
		}
	}	
}
