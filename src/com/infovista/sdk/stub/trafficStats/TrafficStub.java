package com.infovista.sdk.stub.trafficStats;

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

import com.cisco.waas.wsc.RemoteExceptionException;
import com.cisco.waas.wsc.TrafficStatsStub;
import com.cisco.waas.wsc.TrafficStatsStub.GetMonitoredApplicationsResponse;
import com.cisco.waas.wsc.TrafficStatsStub.MonitoredApps;
import com.cisco.waas.wsc.TrafficStatsStub.RetrieveAppTrafficStats;
import com.cisco.waas.wsc.TrafficStatsStub.RetrieveAppTrafficStatsResponse;
import com.cisco.waas.wsc.TrafficStatsStub.RetrieveTrafficStats;
import com.cisco.waas.wsc.TrafficStatsStub.RetrieveTrafficStatsResponse;
import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.sdk.error.WAASErrorManager;

public class TrafficStub{
	
	private static TrafficStub trafficStub;
	protected TrafficStatsStub trafficStatsStub_;
	
	//GET INSTANCE
	public static TrafficStub getInstance(String p_configDir,String p_url,String p_userName,String p_password,long p_timeout, boolean isDataCompressionEnabled){
		if(trafficStub==null){
			synchronized(TrafficStub.class){
				if(trafficStub==null){
					trafficStub = new TrafficStub(p_configDir,p_url, p_userName, p_password,p_timeout, isDataCompressionEnabled);
				}
			}
			
		}
		return trafficStub;
	}
	
	
	private TrafficStub(String p_configDir,String p_url,String p_userName,String p_password,long p_timeout){
		try {
			synchronized (WAASErrorManager.instance().getMonitor()){
				URL repURL=this.getClass().getClassLoader().getResource("com/infovista/sdk/rampart/");
				ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromURIs(null,repURL);
					trafficStatsStub_ = new TrafficStatsStub(ctx, p_url);
					trafficStatsStub_._getServiceClient().getOptions().setTimeOutInMilliSeconds(p_timeout);
					trafficStatsStub_._getServiceClient().engageModule("rampart");
					trafficStatsStub_._getServiceClient().getOptions().setProperty(
							RampartMessageData.KEY_RAMPART_POLICY,
							loadPolicy());
					trafficStatsStub_._getServiceClient().getOptions().setUserName(p_userName);
					trafficStatsStub_._getServiceClient().getOptions().setPassword(p_password);
					
					
			}
		} catch (AxisFault exception) {
			CiscoWAASDiscoverer.LOGGER.info("The traffic stub could not be initialized :: " + exception.getMessage());
		} catch (FileNotFoundException exception) {
			CiscoWAASDiscoverer.LOGGER.info("The traffic stub could not be initialized :: " + exception.getMessage());
		} catch (XMLStreamException exception) {
			CiscoWAASDiscoverer.LOGGER.info("The traffic stub could not be initialized :: " + exception.getMessage());
		}	
		
	}
	
	private TrafficStub(String p_configDir,String p_url,String p_userName,String p_password,long p_timeout, boolean isDataCompressionEnabled){
		this(p_configDir,p_url, p_userName, p_password,p_timeout);
		try {
				if(trafficStatsStub_!=null){
					if(isDataCompressionEnabled){
						enableDataCompression();
					}
				}
		}catch (Exception exception) {
			CiscoWAASDiscoverer.LOGGER.info("The traffic stub could not be initialized :: " + exception.getMessage());
		} 
	}
	
	public RetrieveTrafficStatsResponse retrieveTrafficStatsResponse(RetrieveTrafficStats retrieveTrafficStats) throws RemoteException, RemoteExceptionException{
		return trafficStatsStub_.retrieveTrafficStats(retrieveTrafficStats);	
	}
	
	public RetrieveAppTrafficStatsResponse retrieveAppTrafficStatsResponse(RetrieveAppTrafficStats retrieveTrafficStats) throws RemoteException, RemoteExceptionException{
		return trafficStatsStub_.retrieveAppTrafficStats(retrieveTrafficStats);	
	}
	


	//getting monitored application response object
	public GetMonitoredApplicationsResponse getMonitoredApplicationsResponse() throws RemoteException, RemoteExceptionException{
		try{
			GetMonitoredApplicationsResponse response = trafficStatsStub_.getMonitoredApplications();
			return response;
		} catch (RemoteException e){
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}
	//getting monitored apps
	public MonitoredApps[] getMonitoredApplications() throws RemoteException, RemoteExceptionException{
		try{
			GetMonitoredApplicationsResponse response = trafficStatsStub_.getMonitoredApplications();
			return response.get_return();
		} catch (RemoteException e){
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}
		
	private Policy loadPolicy() throws FileNotFoundException, XMLStreamException{
		InputStream is=this.getClass().getClassLoader().getResourceAsStream("com/infovista/sdk/rampart/policy/policyWAAS.xml");
		StAXOMBuilder builder = new StAXOMBuilder(is);
		return PolicyEngine.getPolicy(builder.getDocumentElement());
	}
	
	public void enableDataCompression() {
		trafficStatsStub_._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_GZIP_REQUEST, Boolean.TRUE);
	}

}
