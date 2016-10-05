package com.infovista.sdk.stub.nfsStats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.commons.io.FileUtils;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.RampartMessageData;

import com.cisco.waas.wsc.NfsStatsStub;
import com.cisco.waas.wsc.CIFSStatsStub.DiskCapacityStats;
import com.cisco.waas.wsc.NfsStatsStub.GetSessionCount;
import com.cisco.waas.wsc.NfsStatsStub.GetSessionCountResponse;
import com.cisco.waas.wsc.NfsStatsStub.NfsReqTypeStats;
import com.cisco.waas.wsc.NfsStatsStub.NfsRespTypeStats;
import com.cisco.waas.wsc.NfsStatsStub.NfsSessionCount;
import com.cisco.waas.wsc.NfsStatsStub.NfsTypeStats;
import com.cisco.waas.wsc.NfsStatsStub.RetrieveNFSTypeStats;
import com.cisco.waas.wsc.NfsStatsStub.RetrieveNFSTypeStatsResponse;
import com.cisco.waas.wsc.NfsStatsStub.RetrieveRequestTypeStats;
import com.cisco.waas.wsc.NfsStatsStub.RetrieveResponseStats;
import com.cisco.waas.wsc.NfsStatsStub.RetrieveResponseStatsResponse;
import com.cisco.waas.wsc.NfsStatsStub.TimeFrameFilter;
import com.cisco.waas.wsc.TrafficStatsStub.RetrieveAppTrafficStats;
import com.cisco.waas.wsc.RemoteExceptionException;
import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.common.classes.*;
import com.infovista.common.interfaces.I_Result;
import com.infovista.configs.CommonConfig;
import com.infovista.fileutils.CsvWriter;
import com.infovista.fileutils.FileNameRetriever;
import com.infovista.instances.Keyword;
import com.infovista.sdk.error.WAASErrorManager;
import com.infovista.statsRetriever.AbstractStatsProcessor;
import com.infovista.statsRetriever.StatsPoller;

public class NfsStub extends AbstractStatsProcessor{

	private NfsStatsStub nfsStatsStub_;
	private Map<String, Map<Long, I_Result>> resultMap;
	private boolean isMultitenant;
	private String[] cols = {"#Tag", "TimeStamp", "AuthFlavor2Count", "AuthFlavor3Count", "AuthFlavorUnknown", "Avg-Local-Resp", "Avg-Remote-Resp", "Local-Resp", "Remote-Resp", "NFS-V2-Count", "NFS-V3-Count", "NFS-V4-Count", "NFS-Unknown", "Session-Count" };
	private TimeFrameFilter timeFrameFilter;
	
	// Constructor
	public NfsStub(String p_configDir,String p_url,String p_userName,String p_password,long p_timeout){
		resultMap = new TreeMap<>(new IPStringComparator());
		timeFrameFilter = new TimeFrameFilter();
		try {
				synchronized (WAASErrorManager.instance().getMonitor()){
					URL repURL=this.getClass().getClassLoader().getResource("com/infovista/sdk/rampart/");
					ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromURIs(null,repURL);
					nfsStatsStub_ = new NfsStatsStub(ctx, p_url);
					nfsStatsStub_._getServiceClient().getOptions().setTimeOutInMilliSeconds(p_timeout);
					nfsStatsStub_._getServiceClient().engageModule("rampart");
					nfsStatsStub_._getServiceClient().getOptions().setProperty(
							RampartMessageData.KEY_RAMPART_POLICY,
							loadPolicy());
					nfsStatsStub_._getServiceClient().getOptions().setUserName(p_userName);
					nfsStatsStub_._getServiceClient().getOptions().setPassword(p_password);
				}
			} catch (AxisFault e) {
				CiscoWAASDiscoverer.LOGGER.info("The NFs Stub could not be initialized :: " + e.getMessage());
			} catch (FileNotFoundException e) {
				CiscoWAASDiscoverer.LOGGER.info("The NFs Stub could not be initialized :: " + e.getMessage());
			} catch (XMLStreamException e) {
				CiscoWAASDiscoverer.LOGGER.info("The NFs Stub could not be initialized :: " + e.getMessage());
			}	
		
	}
	
	// Constructor
	public NfsStub(String p_configDir,String p_url,String p_userName,String p_password,long p_timeout, boolean isDataCompressionEnabled){
		this(p_configDir, p_url, p_userName, p_password, p_timeout);
		if(nfsStatsStub_!=null){
			if(isDataCompressionEnabled){
				enableDataCompression();
			}
			
		}
	}
	
	@Override
	protected void initTimeFrame(TimeSlot timeSlot) {
		CommonParams.setTimeFrameFilterNFS(timeFrameFilter, timeSlot);
	}
	
	@Override
	public String getServiceName() {
		return Keyword.NFS;
	}
	
	@Override
	public void updateTimeResult() {
		timeResult.setServiceName(getServiceName());
		timeResult.setLastTimeStamp(subTimeSlot.getEndTime().getTimeInMillis());
	}
	
	@Override
	public void writeStats() {
		writeNFSStats();	
	}
	
	@Override
	protected void cleanup() {
		resultMap = new HashMap<>();
	}
	
	@Override
	public void retrieveStats(){
		CiscoWAASDiscoverer.LOGGER.info("Free Memory available to the JVM before NFS " + Runtime.getRuntime().freeMemory());
		List<String[]> waeList = getCapableWaes(Keyword.NFS);
		int noOfWaeProcessed=waeList.size();
		
		CiscoWAASDiscoverer.LOGGER.debug("Total no .of NFS Tasks submitted :: " + noOfWaeProcessed);
		CiscoWAASDiscoverer.LOGGER.debug("No. of properties to be collected for NFS Stats :: " + cols.length);
		
		for(String[] waeNameArray: waeList){
			if(!isMultitenant){
				if(waeNameArray.length>0){
					retrieveStats(waeNameArray);
				}
			}
		}
					
		CiscoWAASDiscoverer.LOGGER.info("Free Memory available to the JVM after NFS " + Runtime.getRuntime().freeMemory());
	}
	
	private void retrieveStats(String[] waeNameArray){
		if(istimeStampValid(getServiceName())){
			requestNfsReqTypeStats(waeNameArray);
			requestNfsRespTypeStats(waeNameArray);
			requestNfsTypeStats(waeNameArray);
			requestNfsSessionCount(waeNameArray);
		}else{
			CiscoWAASDiscoverer.LOGGER.info("Stats for timstamp :: " + subTimeSlot.toString() + " has already been calculated for service :: " + getServiceName());
		}
	}
	
	private void requestNfsReqTypeStats(String[] waeNameArray){
		NfsReqTypeStats[] statArray = getReqTypeArray(waeNameArray, Keyword.WAE_TYPE);
		
		if(statArray!=null){
			CiscoWAASDiscoverer.LOGGER.debug("NO. of responses Obtained for Nfs waes  " + statArray.length);
			for(NfsReqTypeStats stats : statArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(stats.getDeviceName())){
							Long timeStamp = stats.getEndtime().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(stats.getDeviceName());
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new NfsStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new NfsStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(stats);
								}
							}
						}
						
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("The Object IN RESPONSEArray Returned is NULL for NfsSessionCount  :: ");
				}
			}
		}

	}
	
	private void requestNfsRespTypeStats(String[] waeNameArray){
		NfsRespTypeStats[] statArray = getRespTypeArray(waeNameArray, Keyword.WAE_TYPE);
		
		if(statArray!=null){
			CiscoWAASDiscoverer.LOGGER.debug("NO. of responses Obtained for Nfs waes  " + statArray.length);
			for(NfsRespTypeStats stats : statArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(stats.getDeviceName())){
							Long timeStamp = stats.getEndtime().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(stats.getDeviceName());
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new NfsStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new NfsStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(stats);
								}
							}
						}
						
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("The Object IN RESPONSEArray Returned is NULL for NfsSessionCount  :: ");
				}
			}
		}

	}
	
	private void requestNfsTypeStats(String[] waeNameArray){
		NfsTypeStats[] statArray = getTypeStatArray(waeNameArray, Keyword.WAE_TYPE);

		if(statArray!=null){
			CiscoWAASDiscoverer.LOGGER.debug("NO. of responses Obtained for Nfs waes  " + statArray.length);
			for(NfsTypeStats stats : statArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(stats.getDeviceName())){
							Long timeStamp = stats.getEndtime().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(stats.getDeviceName());
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new NfsStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new NfsStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(stats);
								}
							}
						}
						
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("The Object IN RESPONSEArray Returned is NULL for NfsSessionCount  :: ");
				}
			}
		}
	}
	
	private void requestNfsSessionCount(String[] waeNameArray){
		NfsSessionCount[] statArray = getSessionCountArray(waeNameArray, Keyword.WAE_TYPE);
		
		if(statArray!=null){
			CiscoWAASDiscoverer.LOGGER.debug("NO. of responses Obtained for Nfs waes  " + statArray.length);
			for(NfsSessionCount stats : statArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(stats.getDeviceName())){
							Long timeStamp = stats.getEndtime().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(stats.getDeviceName());
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new NfsStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new NfsStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(stats);
								}
							}
						}
						
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("The Object IN RESPONSEArray Returned is NULL for NfsSessionCount  :: ");
				}
			}
		}
	}
	
	//writing stats
	private void writeNFSStats(){
		CsvWriter.checkAndWrite(FileNameRetriever.getNFSFileName() ,getServiceName(), resultMap);
	}
	
	private NfsReqTypeStats[] getReqTypeArray(String [] wae, String deviceType){
		try{
			CiscoWAASDiscoverer.LOGGER.debug("Sending NfsReqTypeStats request for NFS stats");
			
			RetrieveRequestTypeStats nStats = new RetrieveRequestTypeStats();
			nStats.setName(wae);
			nStats.setObjType(deviceType);
			nStats.setTimeframe(timeFrameFilter);
			if(nStats!=null){
				OMElement omElement = nStats.getOMElement(RetrieveRequestTypeStats.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("NfsReqTypeStats_Request_"+subTimeSlot.getEndTimeStamp()+ wae[0])), omElement.toString());
			}
			com.cisco.waas.wsc.NfsStatsStub.RetrieveRequestTypeStatsResponse response =nfsStatsStub_.retrieveRequestTypeStats(nStats);
			if(response!=null){
				OMElement omElement = response.getOMElement(com.cisco.waas.wsc.NfsStatsStub.RetrieveRequestTypeStatsResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("NfsReqTypeStats_Response"+ subTimeSlot.getEndTimeStamp()+ wae[0])), omElement.toString());
				
				CiscoWAASDiscoverer.LOGGER.debug("NfsReqTypeStats response for NFS stats retrieved");
				return response.get_return();
			}else{
				CiscoWAASDiscoverer.LOGGER.info("Unable to get  NfsReqTypeStats Stats");
				return null;
			}
			
				
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing NfsReqTypeStats" , e);
		}
		return null;
		
	}
	
	private NfsRespTypeStats[] getRespTypeArray(String [] wae, String deviceType){
		try{
			CiscoWAASDiscoverer.LOGGER.debug("Sending NfsRespTypeStats request for NFS stats");
			
			RetrieveResponseStats nStats = new RetrieveResponseStats();
			nStats.setName(wae);
			nStats.setObjType(deviceType);
			nStats.setTimeframe(timeFrameFilter);
			if(nStats!=null){
				OMElement omElement = nStats.getOMElement(RetrieveResponseStats.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("NfsRespTypeStats_Request_"+subTimeSlot.getEndTimeStamp()+ wae[0])), omElement.toString());
			}
			RetrieveResponseStatsResponse response =nfsStatsStub_.retrieveResponseStats(nStats);
			if(response!=null){
				OMElement omElement = response.getOMElement(RetrieveResponseStatsResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("NfsRespTypeStats_Response"+subTimeSlot.getEndTimeStamp()+wae[0])), omElement.toString());
				
				CiscoWAASDiscoverer.LOGGER.debug("NfsRespTypeStats response for NFS stats retrieved");
				return response.get_return();
			}else{
				CiscoWAASDiscoverer.LOGGER.info("Unable to get  NfsRespTypeStats Stats for wae :: " + wae[0]);
				return null;
			}
			
			
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing NfsRespTypeStats for wae :: " + wae[0] , e);
		}
		return null;
		
	}
	
	private NfsTypeStats[] getTypeStatArray(String [] wae, String deviceType){
		try{
			CiscoWAASDiscoverer.LOGGER.debug("Sending NfsTypeStats request for NFS stats");

			RetrieveNFSTypeStats nStats = new RetrieveNFSTypeStats();
			nStats.setName(wae);
			nStats.setObjType(deviceType);
			nStats.setTimeframe(timeFrameFilter);
			if(nStats!=null){
				OMElement omElement = nStats.getOMElement(RetrieveNFSTypeStats.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("NfsTypeStats_Request_"+subTimeSlot.getEndTimeStamp()+ wae[0])), omElement.toString());
			}
			RetrieveNFSTypeStatsResponse response =nfsStatsStub_.retrieveNFSTypeStats(nStats);
			if(response!=null){
				OMElement omElement = response.getOMElement(RetrieveNFSTypeStatsResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("NfsTypeStats_response"+subTimeSlot.getEndTimeStamp()+wae[0])), omElement.toString());
				CiscoWAASDiscoverer.LOGGER.debug("NfsTypeStats response for NFS stats retrieved");

				return response.get_return();
			}else{
				CiscoWAASDiscoverer.LOGGER.info("Unable to get  NfsTypeStats Stats for wae :: " + wae[0]);
				return null;
			}
			
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing NfsTypeStats for wae :: " + wae[0] , e);
		}
		return null;
		
	}
	
	private NfsSessionCount[] getSessionCountArray(String [] wae, String deviceType){
		try{
			CiscoWAASDiscoverer.LOGGER.debug("Sending NfsSessionCount request for NFS stats");

			GetSessionCount nStats = new GetSessionCount();
			nStats.setName(wae);
			nStats.setObjType(deviceType);
			nStats.setTimeframe(timeFrameFilter);
			if(nStats!=null){
				OMElement omElement = nStats.getOMElement(GetSessionCount.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("NfsSessionCount_Request_"+subTimeSlot.getEndTimeStamp()+ wae[0])), omElement.toString());
			}
			GetSessionCountResponse response =nfsStatsStub_.getSessionCount(nStats);
			if(response!=null){
				OMElement omElement = response.getOMElement(GetSessionCountResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("NfsSessionCount_response"+subTimeSlot.getEndTimeStamp()+wae[0])), omElement.toString());
				
				CiscoWAASDiscoverer.LOGGER.debug("NfsSessionCount response for NFS stats retrieved");
				return response.get_return();
			}else{
				CiscoWAASDiscoverer.LOGGER.info("Unable to get  NfsSessionCount Stats for wae :: " + wae[0]);
				return null;
			}
			
			
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing NfsSessionCount for wae :: " + wae[0] , e);
		}
		return null;
		
	}
	
	
	

	// Public methods
	public NfsRespTypeStats[] retrieveHistoricalStats(String p_waeName,String p_objectType,
			TimeFrameFilter p_timeFrameFilter) throws RemoteException, RemoteExceptionException{
		try {
			RetrieveResponseStats retrieveResponseStats=new RetrieveResponseStats();
			retrieveResponseStats.setName(new String[] {p_waeName});
			retrieveResponseStats.setObjType(p_objectType);
			retrieveResponseStats.setTimeframe(p_timeFrameFilter);
			RetrieveResponseStatsResponse response=nfsStatsStub_.retrieveResponseStats(retrieveResponseStats);
			return response.get_return();
		} catch (RemoteException e){
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}
	
	// Private methods
	/**
	 * Load a WAAS security policy
	 * @param xmlPath the path to the WAAS security policy configuration file
	 * @return the corresponding policy
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	private Policy loadPolicy() throws FileNotFoundException, XMLStreamException{
		InputStream is=this.getClass().getClassLoader().getResourceAsStream("com/infovista/sdk/rampart/policy/policyWAAS.xml");
		StAXOMBuilder builder = new StAXOMBuilder(is);
		return PolicyEngine.getPolicy(builder.getDocumentElement());
	}

	public void enableDataCompression() {
		nfsStatsStub_._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_GZIP_REQUEST, Boolean.TRUE);
	}
}
