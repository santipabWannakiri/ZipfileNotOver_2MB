package com.sap.module;

import java.io.InputStream;
import java.util.ArrayList;

import javax.ejb.Local;
import javax.ejb.LocalHome;
import javax.ejb.Remote;
import javax.ejb.RemoteHome;
import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.aii.af.lib.mp.module.ModuleHome;
import com.sap.aii.af.lib.mp.module.ModuleLocal;
import com.sap.aii.af.lib.mp.module.ModuleLocalHome;
import com.sap.aii.af.lib.mp.module.ModuleRemote;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.module.service.MovefileService;
import com.sap.module.service.ZipfileService;
import com.sap.module.util.Logger;
import com.sap.module.util.ParameterConnection;



@Stateless(name = "FTPS_SFTP_RetestVersion")
@Local(value = { ModuleLocal.class })
@Remote(value = { ModuleRemote.class })
@LocalHome(value = ModuleLocalHome.class)
@RemoteHome(value = ModuleHome.class)
public class AdapterModuleImp extends AbstractAdapterModule{
	
	protected  ArrayList<ArrayList<String>> dataSet = new ArrayList<ArrayList<String>>();
	protected  byte[] b_content;
	
	private ZipfileService zipfileService;
	private MovefileService unzipfileService;
	private ParameterConnection paramConnection = new ParameterConnection();

	@Override
	protected void processModule() throws ModuleException {
		try{
			b_content = payload.getContent();
			DocumentBuilderFactory factory;
			factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse((InputStream) payload.getInputStream()); // Parse InoutStream to document xml
			Element rootNode = document.getDocumentElement();
			audit.addLog(AuditLogStatus.SUCCESS,"Convert data from XML ... ");
			log.add("Convert data from XML ... ");
			if (rootNode != null) {
				// Define Node element
				Node n = document.getFirstChild();
				NodeList nl = n.getChildNodes();

				String tmpValue = "";
				String tmpColumnName = "";

				for (int i = 0; i < nl.getLength(); i++) { // Loop document at
					// row level
					Node an = nl.item(i);
					if (an.getNodeType() == Node.ELEMENT_NODE) {
						NodeList nl1 = an.getChildNodes();
						ArrayList<String> tempSetData = new ArrayList<String>();
						for (int ii = 0; ii < nl1.getLength(); ii++) {
							if (nl1.item(ii).getNodeType() == Node.ELEMENT_NODE) {
								tmpColumnName = nl1.item(ii).getNodeName();

								tmpValue = nl1.item(ii).getTextContent().trim();
								if (tmpColumnName.equals("action")) {
									tempSetData.add(tmpValue);
								}
								if (tmpColumnName.equals("srcPath")) {
									tempSetData.add(tmpValue);
								}
								if (tmpColumnName.equals("item")) {
									tempSetData.add(tmpValue);
								}
								if (tmpColumnName.equals("tarPath")) {
									tempSetData.add(tmpValue);
								}
								if (tmpColumnName.equals("tarFile")) {
									tempSetData.add(tmpValue);
								}
							}
						}
						dataSet.add(tempSetData);
					}
				}
			}
		}catch(Exception e){
			throw new ModuleException(e.getMessage());
		}
		
		paramConnection.setProtocol(this.param.getParameter("Protocol", "ftps", true));
		paramConnection.setServer(this.param.getParameter("receiverServer", "", true));
		paramConnection.setUsername(this.param.getParameter("Username", "", true));
		paramConnection.setPassword(this.param.getParameter("pwdPassword", "", true));
		paramConnection.setPortNumber(this.param.getIntParameter("Port", 0, true));
		paramConnection.setAppendLog(this.param.getBoolParameter("appendLog", "Y", true));
		paramConnection.setArchive(this.param.getBoolParameter("Archive", "Y", true));
		paramConnection.setDelete(this.param.getBoolParameter("Delete", "N", true));
		paramConnection.setSrcTimestamp(this.param.getBoolParameter("srcTimestamp", "N", true));
		paramConnection.setSrcPrefix(this.param.getParameter("srcPrefix", "", true));
		paramConnection.setSrcSuffix(this.param.getParameter("srcSuffix", "", true));
		paramConnection.setTarTimestamp(this.param.getBoolParameter("tarTimestamp", "N", true));
		paramConnection.setTarTimestampAfter(this.param.getBoolParameter("tarTimestampAfterFileName", "N", true));
		paramConnection.setTarPrefix(this.param.getParameter("tarPrefix", "", true));
		paramConnection.setTarSuffix(this.param.getParameter("tarSuffix", "", true));

		audit.addLog(AuditLogStatus.SUCCESS,"== Adapter mode == \n Archive mode : " + paramConnection.getArchive() + "\n Delete mode : " + paramConnection.getDelete() + "\n appendLog mode : " + paramConnection.getAppendLog());
		log.add("== Adapter mode == \n Archive mode : " + paramConnection.getArchive() + "\n Delete mode : " + paramConnection.getDelete() + "\n appendLog mode : " + paramConnection.getAppendLog());
		audit.addLog(AuditLogStatus.SUCCESS,"== Source mode == \n TimeStamp : " + paramConnection.getSrcTimestamp() + "\n srcPrefix : " + paramConnection.getSrcPrefix() + "\n Suffix : " + paramConnection.getSrcSuffix());
		log.add("== Source mode == \n TimeStamp : " + paramConnection.getSrcTimestamp() + "\n srcPrefix : " + paramConnection.getSrcPrefix() + "\n Suffix : " + paramConnection.getSrcSuffix());
		audit.addLog(AuditLogStatus.SUCCESS,"== Target mode == \n TimeStamp : " + paramConnection.getTarTimestamp()+ "\n srcPrefix : " + paramConnection.getTarPrefix() + "\n Suffix : "+ paramConnection.getTarSuffix());
		log.add("== Target mode == \n TimeStamp : " + paramConnection.getTarTimestamp()+ "\n srcPrefix : " + paramConnection.getTarPrefix() + "\n Suffix : "+ paramConnection.getTarSuffix());
		// Print action
		audit.addLog(AuditLogStatus.SUCCESS, "The action is : " + (dataSet.get(0).get(0)));
		log.add("The action is : " + (dataSet.get(0).get(0)));
		audit.addLog(AuditLogStatus.SUCCESS, "Archive : " + paramConnection.getArchive());
	}
	
	
	@Override
	protected void ZipUnzipProcess() throws ModuleException {
		zipfileService = new ZipfileService();
		unzipfileService = new MovefileService();
		
		if (dataSet.get(0).get(0).equalsIgnoreCase("ZIP")) {
			try {
				zipfileService.Zipfile_Check(audit,dataSet,paramConnection,log);
			} catch (Exception e) {
				log.add(e.getMessage());
				audit.addLog(AuditLogStatus.ERROR,  e.getMessage());
			}
		}else if (dataSet.get(0).get(0).equalsIgnoreCase("UNZIP")){
			try {
				unzipfileService.Unzip(audit, dataSet, paramConnection, log);
			} catch (Exception e) {
				log.add(e.getMessage());
				audit.addLog(AuditLogStatus.ERROR,  e.getMessage());
			}
		}
		
		
		
		
	}


	@Override
	protected void WriteLog(Logger log) throws ModuleException {
		log.add( "write log size"+ log.getLogs().size());
		if (paramConnection.getAppendLog() == true) {
			try {
				String strOutput = new String(b_content, "UTF-8");
				String tmpLogWrite = "\r\n==FTPSClientAccess Log==============\r\n";
				for (int i = 0; i < log.getLogs().size(); i++) {
					tmpLogWrite += log.getLogs().get(i) + "\r\n";
				}
				strOutput = strOutput + tmpLogWrite;
				payload.setContent(strOutput.getBytes("UTF-8"));
			} catch (Exception ex) {
				audit.addLog(AuditLogStatus.ERROR  , "Error "+ ex.getMessage());
				log.add("Error " + ex.getMessage());
			}
		}
		log.clearData();
		dataSet.clear();
		
	}
	
}
