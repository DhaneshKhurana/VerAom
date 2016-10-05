package com.infovista.sdk.error;

import java.rmi.RemoteException;

public class WAASAuthenticationError extends WAASError {
	
	private static final long serialVersionUID = 1L;

	public WAASAuthenticationError(RemoteException p_remoteException){
		super(p_remoteException);
	}

}
