package com.infovista.sdk.error;

import java.rmi.RemoteException;

public class WAASPermissionDeniedError extends WAASError {
	
	private static final long serialVersionUID = 1L;

	public WAASPermissionDeniedError(String p_remoteException){
		super(p_remoteException);
	}

	public WAASPermissionDeniedError(RemoteException p_remoteException){
		super(p_remoteException);
	}

}
