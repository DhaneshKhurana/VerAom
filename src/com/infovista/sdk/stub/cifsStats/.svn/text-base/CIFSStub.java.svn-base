package com.infovista.sdk.stub.cifsStats;

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

import com.cisco.waas.wsc.CIFSStatsStub;
import com.cisco.waas.wsc.CIFSStatsStub.CIFSTrafficStats;
import com.cisco.waas.wsc.CIFSStatsStub.CacheCountStats;
import com.cisco.waas.wsc.CIFSStatsStub.CacheUtilizationStats;
import com.cisco.waas.wsc.CIFSStatsStub.ClientAvgThroughputStats;
import com.cisco.waas.wsc.CIFSStatsStub.CoreCountStats;
import com.cisco.waas.wsc.CIFSStatsStub.DiskCapacityStats;
import com.cisco.waas.wsc.CIFSStatsStub.EdgeCountStats;
import com.cisco.waas.wsc.CIFSStatsStub.FileCountStats;
import com.cisco.waas.wsc.CIFSStatsStub.GetCIFSClientAvgThroughput;
import com.cisco.waas.wsc.CIFSStatsStub.GetCIFSCoreCount;
import com.cisco.waas.wsc.CIFSStatsStub.GetCIFSCoreEdgeTraffic;
import com.cisco.waas.wsc.CIFSStatsStub.GetCIFSEdgeCoreTraffic;
import com.cisco.waas.wsc.CIFSStatsStub.GetCIFSEdgeCount;
import com.cisco.waas.wsc.CIFSStatsStub.GetDiskCapacity;
import com.cisco.waas.wsc.CIFSStatsStub.GetDiskCapacityResponse;
import com.cisco.waas.wsc.CIFSStatsStub.GetOpenFileCount;
import com.cisco.waas.wsc.CIFSStatsStub.GetOptCIFSSessionCount;
import com.cisco.waas.wsc.CIFSStatsStub.GetOptCIFSSessionCountResponse;
import com.cisco.waas.wsc.CIFSStatsStub.GetRequestCount;
import com.cisco.waas.wsc.CIFSStatsStub.GetRequestCountResponse;
import com.cisco.waas.wsc.CIFSStatsStub.HitRateStats;
import com.cisco.waas.wsc.CIFSStatsStub.RequestCountStats;
import com.cisco.waas.wsc.CIFSStatsStub.RetrieveCacheObjectCount;
import com.cisco.waas.wsc.CIFSStatsStub.RetrieveCacheObjectCountResponse;
import com.cisco.waas.wsc.CIFSStatsStub.RetrieveCacheUtilization;
import com.cisco.waas.wsc.CIFSStatsStub.RetrieveCacheUtilizationResponse;
import com.cisco.waas.wsc.CIFSStatsStub.RetrieveRequestHitRate;
import com.cisco.waas.wsc.CIFSStatsStub.RetrieveRequestHitRateResponse;
import com.cisco.waas.wsc.CIFSStatsStub.SessionCountStats;
import com.cisco.waas.wsc.CIFSStatsStub.TimeFrameFilter;
import com.cisco.waas.wsc.RemoteExceptionException;
import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.common.classes.CommonParams;
import com.infovista.common.classes.IPStringComparator;
import com.infovista.common.classes.TimeSlot;
import com.infovista.common.interfaces.I_Result;
import com.infovista.configs.CommonConfig;
import com.infovista.fileutils.CsvWriter;
import com.infovista.fileutils.FileNameRetriever;
import com.infovista.instances.Keyword;
import com.infovista.sdk.error.WAASErrorManager;
import com.infovista.statsRetriever.AbstractStatsProcessor;
import com.infovista.statsRetriever.StatsPoller;
public class CIFSStub extends AbstractStatsProcessor{
	
	private Map<String, Map<Long, I_Result>> resultMap;
	private CIFSStatsStub cifsStatsStub_;
	private String cols[] = { "#Tag", "TimeStamp", " Disk-Capacity", "Request_Count", "Cache-Count", "Resource-Utilization",  "Hit-Rate", "Session-Count" };
	private boolean isMultitenant;   
	private TimeFrameFilter timeFrameFilter;

	// Constructor
	public CIFSStub(String p_configDir, String p_url, String p_userName, String p_password, long p_timeout) {
		resultMap = new TreeMap<>(new IPStringComparator());
		timeFrameFilter = new TimeFrameFilter();
		try {
			synchronized (WAASErrorManager.instance().getMonitor()) {
				URL repURL = this.getClass().getClassLoader().getResource("com/infovista/sdk/rampart/");
				ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromURIs(null, repURL);
				cifsStatsStub_ = new CIFSStatsStub(ctx, p_url);
				cifsStatsStub_._getServiceClient().getOptions().setTimeOutInMilliSeconds(p_timeout);
				cifsStatsStub_._getServiceClient().engageModule("rampart");
				cifsStatsStub_._getServiceClient().getOptions().setProperty(RampartMessageData.KEY_RAMPART_POLICY, loadPolicy());
				cifsStatsStub_._getServiceClient().getOptions().setUserName(p_userName);
				cifsStatsStub_._getServiceClient().getOptions().setPassword(p_password);

			}
		} catch (AxisFault exception) {
			CiscoWAASDiscoverer.LOGGER.info("The CIFS Stub could not be initialized :: " + exception.getMessage());
		} catch (FileNotFoundException exception) {
			CiscoWAASDiscoverer.LOGGER.info("The CIFS Stub could not be initialized :: " + exception.getMessage());
		} catch (XMLStreamException exception) {
			CiscoWAASDiscoverer.LOGGER.info("The CIFS Stub could not be initialized :: " + exception.getMessage());
		}

	}
	
	// Constructor
	public CIFSStub(String p_configDir, String p_url, String p_userName, String p_password, long p_timeout, TimeSlot subTimeSlot) {
		super(subTimeSlot);
		timeFrameFilter = new TimeFrameFilter();
		try {
			synchronized (WAASErrorManager.instance().getMonitor()) {
				URL repURL = this.getClass().getClassLoader().getResource("com/infovista/sdk/rampart/");
				ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromURIs(null, repURL);
				cifsStatsStub_ = new CIFSStatsStub(ctx, p_url);
				cifsStatsStub_._getServiceClient().getOptions().setTimeOutInMilliSeconds(p_timeout);
				cifsStatsStub_._getServiceClient().engageModule("rampart");
				cifsStatsStub_._getServiceClient().getOptions().setProperty(RampartMessageData.KEY_RAMPART_POLICY, loadPolicy());
				cifsStatsStub_._getServiceClient().getOptions().setUserName(p_userName);
				cifsStatsStub_._getServiceClient().getOptions().setPassword(p_password);

			}
		} catch (AxisFault exception) {
			CiscoWAASDiscoverer.LOGGER.info("The CIFS Stub could not be initialized :: " + exception.getMessage());
		} catch (FileNotFoundException exception) {
			CiscoWAASDiscoverer.LOGGER.info("The CIFS Stub could not be initialized :: " + exception.getMessage());
		} catch (XMLStreamException exception) {
			CiscoWAASDiscoverer.LOGGER.info("The CIFS Stub could not be initialized :: " + exception.getMessage());
		}

	}
	
	// Constructor
	public CIFSStub(String p_configDir, String p_url, String p_userName, String p_password, long p_timeout, boolean isDataCompressionEnabled) {
		this(p_configDir, p_url, p_userName, p_password, p_timeout);
		if(cifsStatsStub_!=null){
			if(isDataCompressionEnabled){
				enableDataCompression();
			}
			
		}
	}
	
	// Constructor
	public CIFSStub(String p_configDir, String p_url, String p_userName, String p_password, long p_timeout, boolean isDataCompressionEnabled, TimeSlot subTimeSlot) {
		this(p_configDir, p_url, p_userName, p_password, p_timeout, subTimeSlot);
		if(cifsStatsStub_!=null){
			if(isDataCompressionEnabled){
				enableDataCompression();
			}
			
		}
	}
	
	@Override
	public void updateTimeResult() {
		timeResult.setServiceName(getServiceName());
		timeResult.setLastTimeStamp(subTimeSlot.getEndTime().getTimeInMillis());
	}
	
	@Override
	public void writeStats() {
		writeCIFSStats();
	}
	
	@Override
	protected void cleanup() {
		resultMap = new HashMap<>();
	}
	
	@Override
	protected void initTimeFrame(TimeSlot timeSlot) {
		CommonParams.setTimeFrameFilterCIFS(timeFrameFilter, timeSlot);
	}
	
	
	
	// CALL method
	@Override
	public void retrieveStats() {
		CiscoWAASDiscoverer.LOGGER.debug("Processing CIFS stats");
		CiscoWAASDiscoverer.LOGGER.info("Free Memory available to the JVM before CIFS " + Runtime.getRuntime().freeMemory());

		if (!isMultitenant) {
			List<String> waeList = getCIFSCapableWaes();
			int noOfWaeProcessed=waeList.size();
			if (waeList.size()>0) {
				for(String device : waeList) {
					noOfWaeProcessed--;
					for (String trafType : CommonConfig.getInstance().getTrafficType()) {
						for (String dir : CommonConfig.getInstance().getDirections()) {
							
							retrieveStats(device, trafType,dir);
							
							
						}
					}
					
					CiscoWAASDiscoverer.LOGGER.info("CIFS STATS LEFT for :: " + noOfWaeProcessed + " waes");
				}


			}else{
				CiscoWAASDiscoverer.LOGGER.info("NO CIFS capable WAE found!");
			}
		} else {
			CiscoWAASDiscoverer.LOGGER.info("In CIFS process :: Aom is Multitenant");
		}
		CiscoWAASDiscoverer.LOGGER.info("Free Memory available to the JVM after CIFS " + Runtime.getRuntime().freeMemory());
	}
	
	private void retrieveStats(String device, String trafType, String dir){
		if(istimeStampValid(getServiceName())){
			requestDiskCapacityStats(device, trafType, dir);
			requestSessionCountStats(device, trafType, dir);
			requestRequestCountStats(device, trafType, dir);
			requestCacheCountStats(device, trafType, dir);
			requestCacheUtilizationStats(device, trafType, dir);
			requestHitRateStats(device, trafType, dir);
		}else{
			CiscoWAASDiscoverer.LOGGER.info("Stats for timstamp :: " + subTimeSlot.toString() + " has already been calculated for service :: " + getServiceName());
		}
	}

	private void requestDiskCapacityStats(String device, String trafType, String dir){
		DiskCapacityStats[] statArray = getDiskCapacityArray(device, Keyword.WAE_TYPE, trafType, dir);
		
		if(statArray!=null){
			CiscoWAASDiscoverer.LOGGER.debug("NO. of responses Obtained for CIFS for wae :: " + device +  " :: " + statArray.length);
			for(DiskCapacityStats stats : statArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(device)){
							Long timeStamp = stats.getTimestamp().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(device);
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new CIFSStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new CIFSStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(stats);
								}
							}
						}
						
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("The Object IN RESPONSEArray Returned is NULL for wae  :: " + device);
				}
			}
		}
	}
	


	
	private void requestSessionCountStats(String device, String trafType, String dir){
		SessionCountStats[] statArray = getSessionCountArray(device, Keyword.WAE_TYPE, trafType, dir);
		if(statArray!=null){
			CiscoWAASDiscoverer.LOGGER.debug("NO. of responses Obtained for CIFS for wae :: " + device +  " :: " + statArray.length);
			for(SessionCountStats stats : statArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(device)){
							Long timeStamp = stats.getTimestamp().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(device);
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new CIFSStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new CIFSStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(stats);
								}
							}
						}
						
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("The Object IN RESPONSEArray Returned is NULL for wae  :: " + device);
				}
			}
		}
		
		
	}
	
	private void requestRequestCountStats(String device, String trafType, String dir){
		RequestCountStats[] statArray = getRequestCountArray(device, Keyword.WAE_TYPE, trafType, dir);
		if(statArray!=null){
			CiscoWAASDiscoverer.LOGGER.debug("NO. of responses Obtained for CIFS for wae :: " + device +  " :: " + statArray.length);
			for(RequestCountStats stats : statArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(device)){
							Long timeStamp = stats.getTimestamp().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(device);
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new CIFSStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new CIFSStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(stats);
								}
							}
						}
						
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("The Object IN RESPONSEArray Returned is NULL for wae  :: " + device);
				}
			}
		}
		
	}
	
	private void requestCacheCountStats(String device, String trafType, String dir){
		CacheCountStats[] statArray = getCacheCountArray(device, Keyword.WAE_TYPE, trafType, dir);
		if(statArray!=null){
			CiscoWAASDiscoverer.LOGGER.debug("NO. of responses Obtained for CIFS for wae :: " + device +  " :: " + statArray.length);
			for(CacheCountStats stats : statArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(device)){
							Long timeStamp = stats.getTimestamp().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(device);
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new CIFSStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new CIFSStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(stats);
								}
							}
						}
						
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("The Object IN RESPONSEArray Returned is NULL for wae  :: " + device);
				}
			}
		}

	}
	
	private void requestCacheUtilizationStats(String device, String trafType, String dir){
		CacheUtilizationStats[] statArray = getCacheUtilizationArray(device, Keyword.WAE_TYPE, trafType, dir);
		if(statArray!=null){
			CiscoWAASDiscoverer.LOGGER.debug("NO. of responses Obtained for CIFS for wae :: " + device +  " :: " + statArray.length);
			for(CacheUtilizationStats stats : statArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(device)){
							Long timeStamp = stats.getTimestamp().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(device);
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new CIFSStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new CIFSStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(stats);
								}
							}
						}
						
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("The Object IN RESPONSEArray Returned is NULL for wae  :: " + device);
				}
			}
		}

	}
	
	private void requestHitRateStats(String device, String trafType, String dir){
		HitRateStats[] statArray = getHitRateArray(device, Keyword.WAE_TYPE, trafType, dir);
		if(statArray!=null){
			CiscoWAASDiscoverer.LOGGER.debug("NO. of responses Obtained for CIFS for wae :: " + device +  " :: " + statArray.length);
			for(HitRateStats stats : statArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(device)){
							Long timeStamp = stats.getTimestamp().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(device);
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new CIFSStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new CIFSStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(stats);
								}
							}
						}
						
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("The Object IN RESPONSEArray Returned is NULL for wae  :: " + device);
				}
			}
		}

	}
	
	// WRITING AVERAGE THROUGHPUT
	private void writeCIFSStats() {
		CsvWriter.checkAndWrite(FileNameRetriever.getCIFSFileName() , getServiceName(),  resultMap);
	}

	// Public methods
	public HitRateStats[] retrieveRequestHitRate(String p_waeName, String p_objectType, String p_trafficType, String p_direction, TimeFrameFilter p_timeFrameFilter) throws RemoteException,
			RemoteExceptionException {
		try {
			RetrieveRequestHitRate retrieveRequestHitRate = new RetrieveRequestHitRate();
			retrieveRequestHitRate.setName(p_waeName);
			retrieveRequestHitRate.setObjType(p_objectType);
			retrieveRequestHitRate.setTrafficType(p_trafficType);
			retrieveRequestHitRate.setDirection(p_direction);
			retrieveRequestHitRate.setTimeframe(p_timeFrameFilter);
			RetrieveRequestHitRateResponse response = cifsStatsStub_.retrieveRequestHitRate(retrieveRequestHitRate);
			return response.get_return();
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	// GETTING CLIENT AVG THRUPUT ARRAY
	private ClientAvgThroughputStats[] getClientAvgThrougput(String waeName, String deviceType, String trafType, String direction) throws RemoteException, RemoteExceptionException {
		GetCIFSClientAvgThroughput gCifsAvgTput = new GetCIFSClientAvgThroughput();
		gCifsAvgTput.setName(waeName);
		gCifsAvgTput.setObjType(deviceType);
		gCifsAvgTput.setTrafficType(trafType);
		gCifsAvgTput.setDirection(direction);
		gCifsAvgTput.setTimeframe(timeFrameFilter);
		return cifsStatsStub_.getCIFSClientAvgThroughput(gCifsAvgTput).get_return();

	}

	// GETTING CLIENT AVG THRUPUT ARRAY
	private CoreCountStats[] getCoreCountArray(String waeName, String deviceType, String trafType, String direction) throws RemoteException, RemoteExceptionException {
		GetCIFSCoreCount gCoreCount = new GetCIFSCoreCount();
		gCoreCount.setName(waeName);
		gCoreCount.setObjType(deviceType);
		gCoreCount.setTrafficType(trafType);
		gCoreCount.setDirection(direction);
		gCoreCount.setTimeframe(timeFrameFilter);
		return cifsStatsStub_.getCIFSCoreCount(gCoreCount).get_return();

	}

	// GETTING CLIENT AVG THRUPUT ARRAY
	private CIFSTrafficStats[] getCIFSTrafficStatArray(String waeName, String deviceType, String trafType, String direction) throws RemoteException, RemoteExceptionException {
		GetCIFSCoreEdgeTraffic gCoreEdgetraffic = new GetCIFSCoreEdgeTraffic();
		gCoreEdgetraffic.setName(waeName);
		gCoreEdgetraffic.setObjType(deviceType);
		gCoreEdgetraffic.setTrafficType(trafType);
		gCoreEdgetraffic.setDirection(direction);
		gCoreEdgetraffic.setTimeframe(timeFrameFilter);
		return cifsStatsStub_.getCIFSCoreEdgeTraffic(gCoreEdgetraffic).get_return();
	}

	// GETTING CLIENT AVG THRUPUT ARRAY
	private CIFSTrafficStats[] getCIFSCoreTrafficStatArray(String waeName, String deviceType, String trafType, String direction) throws RemoteException, RemoteExceptionException {
		GetCIFSEdgeCoreTraffic gEdgeCoretraffic = new GetCIFSEdgeCoreTraffic();
		gEdgeCoretraffic.setName(waeName);
		gEdgeCoretraffic.setObjType(deviceType);
		gEdgeCoretraffic.setTrafficType(trafType);
		gEdgeCoretraffic.setDirection(direction);
		gEdgeCoretraffic.setTimeframe(timeFrameFilter);
		return cifsStatsStub_.getCIFSEdgeCoreTraffic(gEdgeCoretraffic).get_return();
	}

	// GETTING CLIENT AVG THRUPUT ARRAY
	private EdgeCountStats[] getEdgeCountArray(String waeName, String deviceType, String trafType, String direction) throws RemoteException, RemoteExceptionException {
		GetCIFSEdgeCount gEdgeCountStats = new GetCIFSEdgeCount();
		gEdgeCountStats.setName(waeName);
		gEdgeCountStats.setObjType(deviceType);
		gEdgeCountStats.setTrafficType(trafType);
		gEdgeCountStats.setDirection(direction);
		gEdgeCountStats.setTimeframe(timeFrameFilter);
		return cifsStatsStub_.getCIFSEdgeCount(gEdgeCountStats).get_return();
	}

	
	private DiskCapacityStats[] getDiskCapacityArray(String waeName, String deviceType, String trafType, String direction){
		
		
		try {
			GetDiskCapacity gDiskCapacity = new GetDiskCapacity();
			gDiskCapacity.setName(waeName);
			gDiskCapacity.setObjType(deviceType);
			gDiskCapacity.setTrafficType(trafType);
			gDiskCapacity.setDirection(direction);
			gDiskCapacity.setTimeframe(timeFrameFilter);
			
			//writing xml file
		
			GetDiskCapacityResponse response= cifsStatsStub_.getDiskCapacity(gDiskCapacity);
			if (response != null) {
				if(CiscoWAASDiscoverer.LOGGER.isDebugEnabled()){
					OMElement omElement = response.getOMElement(GetDiskCapacityResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("GetDiskCapacity_" + subTimeSlot.getEndTimeStamp() + waeName)), omElement.toString());
				}
				return response.get_return();
			} else {
				CiscoWAASDiscoverer.LOGGER.debug("Unable to get Disk Capacity response for wae " + waeName + " for timeFrame :: " + timeFrameFilter.getEndTime().toString());
				return null;
			}
		} catch (Exception e) {
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing Disk Capacity for wae :: " + waeName , e);
		}
		return null;
	}

	// GETTING CLIENT AVG THRUPUT ARRAY
	private FileCountStats[] getFileCountArray(String waeName, String deviceType, String trafType, String direction) throws RemoteException, RemoteExceptionException {
		GetOpenFileCount gfileCount = new GetOpenFileCount();
		gfileCount.setName(waeName);
		gfileCount.setObjType(deviceType);
		gfileCount.setTrafficType(trafType);
		gfileCount.setDirection(direction);
		gfileCount.setTimeframe(timeFrameFilter);
		return cifsStatsStub_.getOpenFileCount(gfileCount).get_return();
	}

	// GETTING CLIENT AVG THRUPUT ARRAY
	private SessionCountStats[] getSessionCountArray(String waeName, String deviceType, String trafType, String direction){
		try {
			GetOptCIFSSessionCount gSessionCount = new GetOptCIFSSessionCount();
			gSessionCount.setName(waeName);
			gSessionCount.setObjType(deviceType);
			gSessionCount.setTrafficType(trafType);
			gSessionCount.setDirection(direction);
			gSessionCount.setTimeframe(timeFrameFilter);
		
			GetOptCIFSSessionCountResponse response = cifsStatsStub_.getOptCIFSSessionCount(gSessionCount);
			if (response != null) {
				if(CiscoWAASDiscoverer.LOGGER.isDebugEnabled()){
					OMElement omElement = response.getOMElement(GetOptCIFSSessionCountResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("GetOptCIFSSessionCount_" +subTimeSlot.getEndTimeStamp()+ waeName)), omElement.toString());
				}
				
				return response.get_return();
			} else {
				CiscoWAASDiscoverer.LOGGER.info("Unable to get Session Count Stats for wae :: " + waeName);
				return null;
			}
		} catch (Exception e) {
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing SessionCount stats for :: " + waeName,e);
		}
		
		
		
		return null;
	}

	// GETTING CLIENT AVG THRUPUT ARRAY
	private RequestCountStats[] getRequestCountArray(String waeName, String deviceType, String trafType, String direction){
		try {
			GetRequestCount gRequestCount = new GetRequestCount();
			gRequestCount.setName(waeName);
			gRequestCount.setObjType(deviceType);
			gRequestCount.setTrafficType(trafType);
			gRequestCount.setDirection(direction);
			gRequestCount.setTimeframe(timeFrameFilter);
			
			GetRequestCountResponse response =  cifsStatsStub_.getRequestCount(gRequestCount);
			if (response != null) {
				if(CiscoWAASDiscoverer.LOGGER.isDebugEnabled()){
					OMElement omElement = response.getOMElement(GetRequestCountResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("GetRequestCount_" + subTimeSlot.getEndTimeStamp()+waeName)), omElement.toString());
				}
				
				return response.get_return();
			} else {
				CiscoWAASDiscoverer.LOGGER.info("Unable to get Request Count Stats for wae :: " + waeName);
				return null;
			}
		} catch (Exception e) {
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing REquest Count stats for :: " + waeName,e);
		}
		
		
		
		return null;
	}

	// GETTING CLIENT AVG THRUPUT ARRAY
	private CacheCountStats[] getCacheCountArray(String waeName, String deviceType, String trafType, String direction){
		try{
			RetrieveCacheObjectCount cCacheCount = new RetrieveCacheObjectCount();
			cCacheCount.setName(waeName);
			cCacheCount.setObjType(deviceType);
			cCacheCount.setTrafficType(trafType);
			cCacheCount.setDirection(direction);
			cCacheCount.setTimeframe(timeFrameFilter);
			
			RetrieveCacheObjectCountResponse response =  cifsStatsStub_.retrieveCacheObjectCount(cCacheCount);
			if (response != null) {
				if(CiscoWAASDiscoverer.LOGGER.isDebugEnabled()){
					OMElement omElement = response.getOMElement(RetrieveCacheObjectCountResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("RetrieveCacheObjectCount_"+ subTimeSlot.getEndTimeStamp()+ waeName)), omElement.toString());
				}
				return response.get_return();
			} else {
				CiscoWAASDiscoverer.LOGGER.info("Unable to get Request Count Stats for wae :: " + waeName);
				return null;
			}
		} catch (Exception e) {
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing REquest Count stats for :: " + waeName,e);
		}
		
		
		
		return null;
	}

	// GETTING CLIENT AVG THRUPUT ARRAY
	private CacheUtilizationStats[] getCacheUtilizationArray(String waeName, String deviceType, String trafType, String direction){
		try {
			RetrieveCacheUtilization cCacheUtilization = new RetrieveCacheUtilization();
			cCacheUtilization.setName(waeName);
			cCacheUtilization.setObjType(deviceType);
			cCacheUtilization.setTrafficType(trafType);
			cCacheUtilization.setDirection(direction);
			cCacheUtilization.setTimeframe(timeFrameFilter);
			
			RetrieveCacheUtilizationResponse response = cifsStatsStub_.retrieveCacheUtilization(cCacheUtilization);
			if(response!=null){
				if(CiscoWAASDiscoverer.LOGGER.isDebugEnabled()){
					OMElement omElement = response.getOMElement(RetrieveCacheUtilizationResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("RetrieveCacheUtilization_"+subTimeSlot.getEndTimeStamp() +waeName)), omElement.toString());
				}
				return response.get_return();
			}else{
				CiscoWAASDiscoverer.LOGGER.info("Unable to get RetrieveCacheUtilization Stats for wae :: " + waeName);
				return null;
			}
			} catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing RetrieveCacheUtilization stats for :: " + waeName,e);
			}
		
		
		return null;
	}

	// GETTING CLIENT AVG THRUPUT ARRAY
	private HitRateStats[] getHitRateArray(String waeName, String deviceType, String trafType, String direction){
		try{
			RetrieveRequestHitRate rHitrate = new RetrieveRequestHitRate();
			rHitrate.setName(waeName);
			rHitrate.setObjType(deviceType);
			rHitrate.setTrafficType(trafType);
			rHitrate.setDirection(direction);
			rHitrate.setTimeframe(timeFrameFilter);
			
			return cifsStatsStub_.retrieveRequestHitRate(rHitrate).get_return();
			
		}  catch (Exception e) {
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing RetrieveRequestHitRate stats for :: " + waeName,e);
		}
		
		
		
		return null;
	}

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
		cifsStatsStub_._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_GZIP_REQUEST, Boolean.TRUE);
	}

	public CIFSStatsStub getCIFSStatStub() {
		return cifsStatsStub_;
	}


	@Override
	public String getServiceName() {
		return Keyword.CIFS;
	}
}
