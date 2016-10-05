package com.infovista.sdk.stub.deviceStatus;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.RampartMessageData;

import com.cisco.waas.wsc.DeviceStatusStub;
import com.cisco.waas.wsc.DeviceStatusStub.GetMonitoredAOs;
import com.cisco.waas.wsc.DeviceStatusStub.GetMonitoredAOsByWaeIds;
import com.cisco.waas.wsc.DeviceStatusStub.GetMonitoredAOsByWaeIdsResponse;
import com.cisco.waas.wsc.DeviceStatusStub.GetMonitoredAOsResponse;
import com.cisco.waas.wsc.DeviceStatusStub.MonitoredAO;
import com.cisco.waas.wsc.RemoteExceptionException;
import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.sdk.error.WAASErrorManager;
import com.infovista.statsRetriever.StatsPoller;

public class DeviceStatus {

	// ///////////////////////////////////////////////////////////////////////////////
	// Attributes
	//
	private DeviceStatusStub deviceStatusStub_;
	private StatsPoller sPoller;

	// ///////////////////////////////////////////////////////////////////////////////
	// Constructor
	//
	public DeviceStatus(String p_configDir, String p_url, String p_userName, String p_password, long p_timeout) {
		try {
			synchronized (WAASErrorManager.instance().getMonitor()) {
				URL repURL = this.getClass().getClassLoader().getResource("com/infovista/sdk/rampart/");
				ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromURIs(null, repURL);
				deviceStatusStub_ = new DeviceStatusStub(ctx, p_url);
				deviceStatusStub_._getServiceClient().getOptions().setTimeOutInMilliSeconds(p_timeout);
				deviceStatusStub_._getServiceClient().engageModule("rampart");
				deviceStatusStub_._getServiceClient().getOptions().setProperty(RampartMessageData.KEY_RAMPART_POLICY, loadPolicy());
				deviceStatusStub_._getServiceClient().getOptions().setUserName(p_userName);
				deviceStatusStub_._getServiceClient().getOptions().setPassword(p_password);
			}
		} catch (AxisFault e) {
			CiscoWAASDiscoverer.LOGGER.info("The Device Status Stub could not be initailized :: " + e.getMessage());
		} catch (FileNotFoundException e) {
			CiscoWAASDiscoverer.LOGGER.info("The Device Status Stub could not be initailized :: " + e.getMessage());

		} catch (XMLStreamException e) {
			CiscoWAASDiscoverer.LOGGER.info("The Device Status Stub could not be initailized :: " + e.getMessage());

		}

	}
	
	// Constructor
	public DeviceStatus(String p_configDir,String p_url,String p_userName,String p_password,long p_timeout, boolean isDataCompressionEnabled){
		this(p_configDir, p_url, p_userName, p_password, p_timeout);
		if(deviceStatusStub_!=null){
			if(isDataCompressionEnabled){
				enableDataCompression();
			}
			
		}
	}
		
	// Constructor
	public DeviceStatus(String p_configDir,String p_url,String p_userName,String p_password,long p_timeout, boolean isDataCompressionEnabled, StatsPoller callback){
		this(p_configDir, p_url, p_userName, p_password, p_timeout, isDataCompressionEnabled);
		sPoller = callback;
	}

	// /////////////////////////////////////////////////////////////////////////////////
	// Public methods
	//

	public MonitoredAO[] getMonitoredAOs(String p_waeName, String p_objectType) throws RemoteException, RemoteExceptionException {
		try {
			GetMonitoredAOs getMonitoredAOs = new GetMonitoredAOs();
			getMonitoredAOs.setName(p_waeName);
			getMonitoredAOs.setObjType(p_objectType);
			GetMonitoredAOsResponse response = deviceStatusStub_.getMonitoredAOs(getMonitoredAOs);
			return response.get_return();
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	public GetMonitoredAOsByWaeIdsResponse getMonitoredAOsByWaeIdsResponse(long[] p_ids) throws RemoteException, RemoteExceptionException {
		try {
			GetMonitoredAOsByWaeIds getMonitoredAOsByWaeIds = new GetMonitoredAOsByWaeIds();
			getMonitoredAOsByWaeIds.setIds(p_ids);
			GetMonitoredAOsByWaeIdsResponse response = deviceStatusStub_.getMonitoredAOsByWaeIds(getMonitoredAOsByWaeIds);
			return response;
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	public MonitoredAO[] getMonitoredAOsByWaeIds(long[] p_ids) throws RemoteException, RemoteExceptionException {
		try {
			GetMonitoredAOsByWaeIds getMonitoredAOsByWaeIds = new GetMonitoredAOsByWaeIds();
			getMonitoredAOsByWaeIds.setIds(p_ids);
			GetMonitoredAOsByWaeIdsResponse response = deviceStatusStub_.getMonitoredAOsByWaeIds(getMonitoredAOsByWaeIds);
			return response.get_return();
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////
	// Private methods
	//

	/**
	 * Load a WAAS security policy
	 * 
	 * @param xmlPath
	 *            the path to the WAAS security policy configuration file
	 * @return the corresponding policy
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	private Policy loadPolicy() throws FileNotFoundException, XMLStreamException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("com/infovista/sdk/rampart/policy/policyWAAS.xml");
		StAXOMBuilder builder = new StAXOMBuilder(is);
		return PolicyEngine.getPolicy(builder.getDocumentElement());
	}

	public void enableDataCompression() {
		deviceStatusStub_._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_GZIP_REQUEST, Boolean.TRUE);
	}
}
