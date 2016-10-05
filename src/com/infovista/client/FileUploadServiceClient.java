package com.infovista.client;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.databinding.ADBException;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.cisco.waas.wsc.FileUploadWSIServiceStub;
import com.cisco.waas.wsc.FileUploadWSIServiceStub.GetVersion;
import com.cisco.waas.wsc.FileUploadWSIServiceStub.GetVersionResponse;
import com.infovista.sdk.ssl.FakeSSLProtocolSocketFactory;

public class FileUploadServiceClient {
	public void upload() throws RemoteException {
		Protocol.unregisterProtocol("https");
		Protocol.registerProtocol("https", new Protocol("https",
				new FakeSSLProtocolSocketFactory(), 443));
		FileUploadWSIServiceStub stub = new FileUploadWSIServiceStub(
				"https://finanzonline.bmf.gv.at:443/fon/services/FileUploadWSI");
		GetVersion getVersion = new GetVersion();
		GetVersionResponse version = stub.getVersion(getVersion);
		CiscoWAASDiscoverer.LOGGER.info(version.getVersion());
		try {
			OMElement omElement = version.getOMElement(
					GetVersionResponse.MY_QNAME,
					OMAbstractFactory.getOMFactory());
			CiscoWAASDiscoverer.LOGGER.info(omElement.toString());
			try {
				FileUtils.writeStringToFile(new File("output","getDeviceGroups.xml"), omElement.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				CiscoWAASDiscoverer.LOGGER.error("Error",e);
			}
		} catch (ADBException e) {
			// TODO Auto-generated catch block
			CiscoWAASDiscoverer.LOGGER.error("Error",e);
		}
	}

	public static void main(String[] args) {
		FileUploadServiceClient serviceClient = new FileUploadServiceClient();
		try {
			serviceClient.upload();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			CiscoWAASDiscoverer.LOGGER.error("Error",e);
		}
	}
}
