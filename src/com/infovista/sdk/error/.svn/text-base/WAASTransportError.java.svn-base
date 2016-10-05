package com.infovista.sdk.error;

import java.rmi.RemoteException;

public class WAASTransportError extends WAASError {
	
	private static final long serialVersionUID = 1L;
	private String transportErrorMessage_="";
	private static String ERROR_404="Not found (404)";
	private static String ERROR_403="Forbidden (403)";
	private static String CONNECTION_REFUSED="Connection refused";

	public WAASTransportError(RemoteException p_remoteException){
		super(p_remoteException);
		String errorMessage=p_remoteException.getMessage().toLowerCase();
		if (errorMessage.indexOf("404")!=-1)
			transportErrorMessage_=ERROR_404;
		else if (errorMessage.indexOf("403")!=-1)
			transportErrorMessage_=ERROR_403;
		else if (errorMessage.indexOf("connection refused")!=-1)
			transportErrorMessage_=CONNECTION_REFUSED;
		else
			transportErrorMessage_=errorMessage;
	}

	public String getTransportErrorMessage(){
		return transportErrorMessage_;
	}
	
}
