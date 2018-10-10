package com.sap.module.util;

import java.util.ArrayList;
import java.util.List;

import com.sap.aii.af.lib.mp.module.ModuleContext;


public class Logger {
	private ModuleContext mc;
	private AuditLogHelper audit;
	
	private  List<String> Logs = new ArrayList<String>();
	
	public  Logger(ModuleContext mc,AuditLogHelper audit){
		this.mc = mc;
		this.audit = audit;
	}
	
	public List<String> getLogs() {
		return Logs;
	}

	public void add(String tmpLogS) {
		Logs.add(tmpLogS);
	}
	
	public void clearData(){
		Logs.clear();
	}
	

}
