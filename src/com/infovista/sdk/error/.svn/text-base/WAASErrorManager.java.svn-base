package com.infovista.sdk.error;

import java.rmi.RemoteException;

public class WAASErrorManager {

	private static WAASErrorManager waasErrorManager_;
	private Object monitor_;
	
	private WAASErrorManager(){
		monitor_=new Object();
	}
	
	public static WAASErrorManager instance(){
		if (waasErrorManager_==null)
			waasErrorManager_=new WAASErrorManager();
		return waasErrorManager_;
	}
	
	public RemoteException getWAASError(RemoteException p_exception){
		String errorMessage=p_exception.getMessage().toLowerCase();
		if (errorMessage.indexOf("security processing failed")!=-1)
			return new WAASAuthenticationError(p_exception);
		if (errorMessage.indexOf("no permission to access")!=-1)
			return new WAASPermissionDeniedError(p_exception);
		if (errorMessage.toLowerCase().indexOf("transport error")!=-1)
			return new WAASTransportError(p_exception);
		if (errorMessage.toLowerCase().indexOf("connection refused")!=-1)
			return new WAASTransportError(p_exception);
		if (errorMessage.toLowerCase().indexOf("timed out")!=-1)
			return new WAASTimeoutError(p_exception);
		return p_exception;
	}
	
	public Object getMonitor(){
		return monitor_;
	}
	
}
