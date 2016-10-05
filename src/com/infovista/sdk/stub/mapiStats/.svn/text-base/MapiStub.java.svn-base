package com.infovista.sdk.stub.mapiStats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import com.cisco.waas.wsc.MapiStatsStub;
import com.cisco.waas.wsc.MapiStatsStub.GetSessionCount;
import com.cisco.waas.wsc.MapiStatsStub.GetSessionCountResponse;
import com.cisco.waas.wsc.MapiStatsStub.MapiClientConnCount;
import com.cisco.waas.wsc.MapiStatsStub.MapiDataReadStats;
import com.cisco.waas.wsc.MapiStatsStub.MapiRequestTypeStats;
import com.cisco.waas.wsc.MapiStatsStub.MapiResponseStats;
import com.cisco.waas.wsc.MapiStatsStub.MapiSessionCount;
import com.cisco.waas.wsc.MapiStatsStub.RetrieveClientConnCount;
import com.cisco.waas.wsc.MapiStatsStub.RetrieveClientConnCountResponse;
import com.cisco.waas.wsc.MapiStatsStub.RetrieveDataReadStats;
import com.cisco.waas.wsc.MapiStatsStub.RetrieveDataReadStatsResponse;
import com.cisco.waas.wsc.MapiStatsStub.RetrieveRequestTypeStats;
import com.cisco.waas.wsc.MapiStatsStub.RetrieveRequestTypeStatsResponse;
import com.cisco.waas.wsc.MapiStatsStub.RetrieveResponseStats;
import com.cisco.waas.wsc.MapiStatsStub.RetrieveResponseStatsResponse;
import com.cisco.waas.wsc.MapiStatsStub.TimeFrameFilter;
import com.cisco.waas.wsc.RemoteExceptionException;
import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.configs.CommonConfig;
import com.infovista.fileutils.CsvWriter;
import com.infovista.fileutils.FileNameRetriever;
import com.infovista.instances.Keyword;
import com.infovista.sdk.error.WAASErrorManager;
import com.infovista.statsRetriever.AbstractStatsProcessor;
import com.infovista.common.classes.CommonParams;
import com.infovista.common.classes.IPStringComparator;
import com.infovista.common.classes.TimeSlot;
import com.infovista.common.interfaces.*;
import com.infovista.statsRetriever.StatsPoller;

public class MapiStub extends AbstractStatsProcessor{

	private MapiStatsStub mapiStatsStub_;
	private TimeFrameFilter timeFrameFilter;
	private Map<String, Map<Long, I_Result>> resultMap;
	private boolean isMultitenant;
	
	private String cols[] = {"#Tag", "TimeStamp", "Local-Resp", "Remote-Resp", "Avg-Local-Resp", "Avg-Remote-Resp", "Avg-read-Ahead", "Avg-Read-Stream", "Client-2k", "Client-2k3", "Client-2k7", "HigherThan-2k7", "LowerThan-2k", "Secured-Connections", "Session-Count"};

	// Constructor
	public MapiStub(String p_configDir, String p_url, String p_userName, String p_password, long p_timeout) {
		timeFrameFilter = new TimeFrameFilter();
		resultMap = new TreeMap<>(new IPStringComparator());
		try {
			synchronized (WAASErrorManager.instance().getMonitor()) {
				URL repURL = this.getClass().getClassLoader().getResource("com/infovista/sdk/rampart/");
				ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromURIs(null, repURL);
				mapiStatsStub_ = new MapiStatsStub(ctx, p_url);
				mapiStatsStub_._getServiceClient().getOptions().setTimeOutInMilliSeconds(p_timeout);
				mapiStatsStub_._getServiceClient().engageModule("rampart");
				mapiStatsStub_._getServiceClient().getOptions().setProperty(RampartMessageData.KEY_RAMPART_POLICY, loadPolicy());
				mapiStatsStub_._getServiceClient().getOptions().setUserName(p_userName);
				mapiStatsStub_._getServiceClient().getOptions().setPassword(p_password);
			}
		} catch (AxisFault e) {
			CiscoWAASDiscoverer.LOGGER.info("The MAPI Stub could not be initialized :: " + e.getMessage());
		} catch (FileNotFoundException e) {
			CiscoWAASDiscoverer.LOGGER.info("The MAPI Stub could not be initialized :: " + e.getMessage());
		} catch (XMLStreamException e) {
			CiscoWAASDiscoverer.LOGGER.info("The MAPI Stub could not be initialized :: " + e.getMessage());
		}

	}

	// Constructor
	public MapiStub(String p_configDir,String p_url,String p_userName,String p_password,long p_timeout, boolean isDataCompressionEnabled){
		this(p_configDir, p_url, p_userName, p_password, p_timeout);
		if(mapiStatsStub_!=null){
			if(isDataCompressionEnabled){
				enableDataCompression();
			}
			
		}
	}
	

	// Constructor
	public MapiStub(String p_configDir, String p_url, String p_userName, String p_password, long p_timeout, TimeSlot subTimeSlot) {
		super(subTimeSlot);
		timeFrameFilter = new TimeFrameFilter();
		try {
			synchronized (WAASErrorManager.instance().getMonitor()) {
				URL repURL = this.getClass().getClassLoader().getResource("com/infovista/sdk/rampart/");
				ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromURIs(null, repURL);
				mapiStatsStub_ = new MapiStatsStub(ctx, p_url);
				mapiStatsStub_._getServiceClient().getOptions().setTimeOutInMilliSeconds(p_timeout);
				mapiStatsStub_._getServiceClient().engageModule("rampart");
				mapiStatsStub_._getServiceClient().getOptions().setProperty(RampartMessageData.KEY_RAMPART_POLICY, loadPolicy());
				mapiStatsStub_._getServiceClient().getOptions().setUserName(p_userName);
				mapiStatsStub_._getServiceClient().getOptions().setPassword(p_password);
			}
		} catch (AxisFault e) {
			CiscoWAASDiscoverer.LOGGER.info("The MAPI Stub could not be initialized :: " + e.getMessage());
		} catch (FileNotFoundException e) {
			CiscoWAASDiscoverer.LOGGER.info("The MAPI Stub could not be initialized :: " + e.getMessage());
		} catch (XMLStreamException e) {
			CiscoWAASDiscoverer.LOGGER.info("The MAPI Stub could not be initialized :: " + e.getMessage());
		}

	}

	// Constructor
	public MapiStub(String p_configDir,String p_url,String p_userName,String p_password,long p_timeout, boolean isDataCompressionEnabled, TimeSlot subTimeSlot){
		this(p_configDir, p_url, p_userName, p_password, p_timeout, subTimeSlot);
		if(mapiStatsStub_!=null){
			if(isDataCompressionEnabled){
				enableDataCompression();
			}
			
		}
	}
	
	@Override
	protected void initTimeFrame(TimeSlot timeSlot) {
		CommonParams.setTimeFrameFilterMAPI(timeFrameFilter, timeSlot);
	}

	@Override
	public String getServiceName() {
		return Keyword.MAPI;
	}
	
	@Override
	public void updateTimeResult() {
		timeResult.setServiceName(getServiceName());
		timeResult.setLastTimeStamp(subTimeSlot.getEndTime().getTimeInMillis());
	}
	
	@Override
	public void writeStats() {
		writeMAPIStats(resultMap);	
	}
	
	@Override
	protected void cleanup() {
		resultMap = new HashMap<>();
	}
	
	@Override
	public void retrieveStats() {
		CiscoWAASDiscoverer.LOGGER.info("Free Memory available to the JVM before Mapi " + Runtime.getRuntime().freeMemory());
		if (!isMultitenant) {
			List<String[]> waeList = getCapableWaes(Keyword.MAPI);
			if(waeList!=null && waeList.size()>0){
				for(String[] waeNameArray : waeList){
					if(waeNameArray.length>0){
						retrieveStats(waeNameArray, resultMap);
					}else{
						CiscoWAASDiscoverer.LOGGER.debug("WaeName array is empty for Mapi stats");
					}
				}
				
			}else{
				CiscoWAASDiscoverer.LOGGER.info("NO MAPI CAPABALE WAE FOUND!");
			}
		}
		CiscoWAASDiscoverer.LOGGER.info("Free Memory available to the JVM after Mapi " + Runtime.getRuntime().freeMemory());
	}

	
	public void retrieveStats(String[] waeNameArray, Map<String, Map<Long, I_Result>> resultMap ){
			requestAndFillStats(waeNameArray, resultMap);	
	}
	

	private void requestAndFillStats(String[] waeNameArray, Map<String, Map<Long, I_Result>> resultMap){				
		if(istimeStampValid(getServiceName())){
			requestMapiResponseStats(waeNameArray, resultMap);
			requestMapiDataReadStats(waeNameArray, resultMap);
			requestMapiClientConnCount(waeNameArray, resultMap);
			requestMapiRequestTypeStats(waeNameArray, resultMap);
			requestMapiSessionCount(waeNameArray, resultMap);
		}else{
			CiscoWAASDiscoverer.LOGGER.info("Stats for timstamp :: " + subTimeSlot.toString() + " has already been calculated for service :: " + getServiceName());
		}
		
		
	}
	
	
	private void requestMapiResponseStats(String[] waeNameArray, Map<String, Map<Long,I_Result>> resultMap){
		MapiResponseStats[] mResponseArray = getResponseArray(waeNameArray, Keyword.WAE_TYPE);
		
		if(mResponseArray!=null){
			for(MapiResponseStats stats : mResponseArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(stats.getDeviceName())){
							Long timeStamp = stats.getEndtime().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(stats.getDeviceName());
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new MapiStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new MapiStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(stats);
								}
							}
						}
						
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("MapiResponseStats object returned is null");
				}
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.debug("MapiResponseStats were not retrieved");
		}
	}

	private void requestMapiDataReadStats(String[] waeNameArray, Map<String, Map<Long,I_Result>> resultMap){
		MapiDataReadStats[] mDataReadArray = getMapiDataArray(waeNameArray, Keyword.WAE_TYPE);
	
		if(mDataReadArray!=null){
			for(MapiDataReadStats stats : mDataReadArray){
				if(stats!=null){
					if(wae_Name_Ip_Map.containsKey(stats.getDeviceName())){
						if(isFrequencyValid(stats.getFrequency())){
							String deviceIP = wae_Name_Ip_Map.get(stats.getDeviceName());
							Long timeStamp = stats.getEndtime().getTimeInMillis();
							if(!resultMap.containsKey(deviceIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new MapiStatResult().setProperties(stats));
								resultMap.put(deviceIP, localMap);
							}else{
								if(!resultMap.get(deviceIP).containsKey(timeStamp)){
									resultMap.get(deviceIP).put(timeStamp, new MapiStatResult().setProperties(stats));
								}else{
									resultMap.get(deviceIP).get(timeStamp).setProperties(stats);
								}
							}
						}
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("MapiDataReadStats object was null");
				}
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.debug("MapiDataReadStats were not retrieved");
		}
	}
	
	private void requestMapiClientConnCount(String[] waeNameArray, Map<String, Map<Long,I_Result>> resultMap){
		MapiClientConnCount[] mClientConnArray = getMapiClientConnArray(waeNameArray, Keyword.WAE_TYPE);
		
		if(mClientConnArray!=null){
			for(MapiClientConnCount stats : mClientConnArray){
				if(stats!=null){
					if(wae_Name_Ip_Map.containsKey(stats.getDeviceName())){
						if(isFrequencyValid(stats.getFrequency())){
							Long timeStamp = stats.getEndtime().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(stats.getDeviceName());
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new MapiStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new MapiStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(new MapiStatResult().setProperties(stats));
								}
							}
						}
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("MapiClientConnCount object was null");
				}
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.debug("MapiClientConnCount array was null");
		}
	}
	
	private void requestMapiRequestTypeStats(String[] waeNameArray, Map<String, Map<Long,I_Result>> resultMap){
		MapiRequestTypeStats[] mRequestArray = getMapiRequestTypeArray(waeNameArray, Keyword.WAE_TYPE);
		
		if(mRequestArray!=null){
			for(MapiRequestTypeStats stats : mRequestArray){
				if(stats!=null){
					if(wae_Name_Ip_Map.containsKey(stats.getDeviceName())){
						if(isFrequencyValid(stats.getFrequency())){
							Long timeStamp = stats.getEndtime().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(stats.getDeviceName());
							if(!resultMap.containsKey(waeIP)){
								TreeMap<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new MapiStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new MapiStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(new MapiStatResult().setProperties(stats));
								}
							}
						}
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("MapiRequestTypeStats object was null");
				}
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.debug("MapiRequestTypeStats array was null");
		}
	}
	
	private void requestMapiSessionCount(String[] waeNameArray, Map<String, Map<Long,I_Result>> resultMap){
		MapiSessionCount[] mSessionCount = getMapiSessionCountArray(waeNameArray, Keyword.WAE_TYPE);
		
		if(mSessionCount!=null){
			for(MapiSessionCount stats : mSessionCount){
				if(stats!=null){
					if(wae_Name_Ip_Map.containsKey(stats.getDeviceName())){
						if(isFrequencyValid(stats.getFrequency())){
							Long timeStamp = stats.getEndtime().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(stats.getDeviceName());
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new MapiStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new MapiStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(stats);
								}
							}
						}
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("MapiSessionCount object was null");
				}
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.debug("MapiSessionCount array was null");
		}
	}
	
					
	// writing stats
	private void writeMAPIStats(Map<String, Map<Long, I_Result>> resultMap){
		CsvWriter.checkAndWrite(FileNameRetriever.getMAPIFileName() , getServiceName(), resultMap);
	}

	private MapiResponseStats[] getResponseArray(String[] wae, String deviceType){
		try{
			RetrieveResponseStats rStats = new RetrieveResponseStats();
			rStats.setName(wae);
			rStats.setObjType(deviceType);
			rStats.setTimeframe(timeFrameFilter);
			
			RetrieveResponseStatsResponse response =  mapiStatsStub_.retrieveResponseStats(rStats);
			if(response!=null){
				OMElement omElement = response.getOMElement(RetrieveResponseStatsResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("MapiResponseStats_"+ subTimeSlot.getEndTimeStamp()+ wae[0])), omElement.toString());
				return response.get_return();
			}else{
				CiscoWAASDiscoverer.LOGGER.info("Unable to get  MapiResponseStats Stats for wae :: " + wae[0]);
				return null;
			}
				
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing MapiResponseStats for wae :: " + wae[0] , e);
		}
		return null;
		
	}

	private MapiDataReadStats[] getMapiDataArray(String[] wae, String deviceType) {
		try{
			RetrieveDataReadStats rStats = new RetrieveDataReadStats();
			rStats.setName(wae);
			rStats.setObjType(deviceType);
			rStats.setTimeframe(timeFrameFilter);
			
			RetrieveDataReadStatsResponse response =  mapiStatsStub_.retrieveDataReadStats(rStats);
			if(response!=null){
				OMElement omElement = response.getOMElement(RetrieveDataReadStatsResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("MapiDataReadStats"+subTimeSlot.getEndTimeStamp() +wae[0])), omElement.toString());
				return response.get_return();
			}else{
				CiscoWAASDiscoverer.LOGGER.info("Unable to get  MapiDataReadStats Stats for wae :: " + wae[0]);
				return null;
			}
			
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing MapiDataReadStats for wae :: " + wae[0] , e);
		}
		return null;
		
	}

	private MapiClientConnCount[] getMapiClientConnArray(String[] wae, String deviceType){
		try{
			RetrieveClientConnCount rStats = new RetrieveClientConnCount();
			rStats.setName(wae);
			rStats.setObjType(deviceType);
			rStats.setTimeframe(timeFrameFilter);
			
			RetrieveClientConnCountResponse response =  mapiStatsStub_.retrieveClientConnCount(rStats);
			if(response!=null){
				OMElement omElement = response.getOMElement(RetrieveClientConnCountResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("MapiClientConnCount"+subTimeSlot.getEndTimeStamp() + wae[0])), omElement.toString());
				return response.get_return();
			}else{
				CiscoWAASDiscoverer.LOGGER.info("Unable to get  MapiClientConnCount Stats for wae :: " + wae[0]);
				return null;
			}
			
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing MapiClientConnCount for wae :: " + wae[0] , e);
		}
		return null;
		
	}

	private MapiRequestTypeStats[] getMapiRequestTypeArray(String[] wae, String deviceType){
		try{
			RetrieveRequestTypeStats rStats = new RetrieveRequestTypeStats();
			rStats.setName(wae);
			rStats.setObjType(deviceType);
			rStats.setTimeframe(timeFrameFilter);
			
			RetrieveRequestTypeStatsResponse response = mapiStatsStub_.retrieveRequestTypeStats(rStats);
			if(response!=null){
				OMElement omElement = response.getOMElement(RetrieveRequestTypeStatsResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("MapiRequestTypeStats"+subTimeSlot.getEndTimeStamp() + wae[0])), omElement.toString());
				return response.get_return();
			}else{
				CiscoWAASDiscoverer.LOGGER.info("Unable to get  MapiRequestTypeStats Stats for wae :: " + wae[0]);
				return null;
			}
			
				
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing MapiRequestTypeStats for wae :: " + wae[0] , e);
		}
		return null;
		
	}

	private MapiSessionCount[] getMapiSessionCountArray(String[] wae, String deviceType){
		try{
			GetSessionCount rStats = new GetSessionCount();
			rStats.setName(wae);
			rStats.setObjType(deviceType);
			rStats.setTimeframe(timeFrameFilter);
			
			
			GetSessionCountResponse response =  mapiStatsStub_.getSessionCount(rStats);
			if(response!=null){
				OMElement omElement = response.getOMElement(GetSessionCountResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("MapiSessionCount"+subTimeSlot.getEndTimeStamp() +wae[0])), omElement.toString());
				return response.get_return();
			}else{
				CiscoWAASDiscoverer.LOGGER.info("Unable to get  MapiSessionCount Stats for wae :: " + wae[0]);
				return null;
			}
			
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing MapiSessionCount for wae :: " + wae[0] , e);
		}
		return null;
		
	}

	// Public methods
	public MapiRequestTypeStats[] retrieveRequestTypeStats(String p_waeName, String p_objectType, TimeFrameFilter p_timeFrameFilter) throws RemoteException, RemoteExceptionException {
		try {
			RetrieveRequestTypeStats retrieveRequestTypeStats = new RetrieveRequestTypeStats();
			retrieveRequestTypeStats.setName(new String[] { p_waeName });
			retrieveRequestTypeStats.setObjType(p_objectType);
			retrieveRequestTypeStats.setTimeframe(p_timeFrameFilter);
			RetrieveRequestTypeStatsResponse response = mapiStatsStub_.retrieveRequestTypeStats(retrieveRequestTypeStats);
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
		mapiStatsStub_._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_GZIP_REQUEST, Boolean.TRUE);
	}



}
