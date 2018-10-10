package com.sap.module.util;


import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class ParameterHelper {
	
	private final ModuleContext mc;
	private final AuditLogHelper audit;
	
	public ParameterHelper (ModuleContext mc, AuditLogHelper audit) {
		this.mc = mc;
		this.audit = audit;
	}

	public String getParameter(String paramName, String defaultValue, boolean outputLog) {
		String paramValue = this.mc.getContextData(paramName);
		if (paramValue == null && defaultValue != null) {
			paramValue = defaultValue;
			if(outputLog) {
				this.audit.addLog(AuditLogStatus.SUCCESS, "Parameter '" + paramName + "' is not set. Using default value = '" + paramValue + "'");
//				log.add("Parameter '" + paramName + "' is not set. Using default value = '" + paramValue + "'");
			}
		}	
		return paramValue;
	}
	
	public int getIntParameter(String paramName, int defaultValue, boolean outputLog) throws ModuleException {
		String paramValue = getParameter(paramName, Integer.toString(defaultValue), outputLog);
		try {
			int result = Integer.parseInt(paramValue);
			if (result < 0) {
				throw new ModuleException("Negative integers not allowed for "+ paramName);
			}
			return result;
		} catch (NumberFormatException e) {
			throw new ModuleException("Only integers allowed for "+ paramName);
		}
	}
	
	public boolean getBoolParameter(String paramName, String defaultValue, boolean outputLog) throws ModuleException {
		String paramValue = getParameter(paramName, defaultValue, outputLog);
		if(paramValue.equalsIgnoreCase("Y")) {
			return true;
		} else {
			return false;
		}
	}
	

}
