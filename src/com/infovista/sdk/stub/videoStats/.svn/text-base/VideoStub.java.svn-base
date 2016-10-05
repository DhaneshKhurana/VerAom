package com.infovista.sdk.stub.videoStats;

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

import com.cisco.waas.wsc.RemoteExceptionException;
import com.cisco.waas.wsc.VideoStatsStub;
import com.cisco.waas.wsc.NfsStatsStub.NfsReqTypeStats;
import com.cisco.waas.wsc.VideoStatsStub.RetrieveHistoricalStats;
import com.cisco.waas.wsc.VideoStatsStub.RetrieveHistoricalStatsResponse;
import com.cisco.waas.wsc.VideoStatsStub.TimeFrameFilter;
import com.cisco.waas.wsc.VideoStatsStub.VideoStats;
import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.common.classes.*;
import com.infovista.common.interfaces.I_Result;
import com.infovista.configs.CommonConfig;
import com.infovista.fileutils.CsvWriter;
import com.infovista.fileutils.FileNameRetriever;
import com.infovista.instances.Keyword;
import com.infovista.sdk.error.WAASErrorManager;
import com.infovista.sdk.stub.nfsStats.NfsStatResult;
import com.infovista.statsRetriever.AbstractStatsProcessor;
import com.infovista.statsRetriever.StatsPoller;

public class VideoStub extends AbstractStatsProcessor{

	private VideoStatsStub videoStatsStub_;
	private Map<String, Map<Long, I_Result>> resultMap;
	private boolean isMultitenant;
	private String[] cols = {"#Tag", "TimeStamp", "Accelerated-Connections", "Incoming-Bytes-Total", "Outgoing-Bytes-Total", "Error-Connection", "Recieved-Connections", "Unaccelerated-Connections"};
	private TimeFrameFilter timeFrameFilter;

	// Constructor
	public VideoStub(String p_configDir, String p_url, String p_userName, String p_password, long p_timeout) {
		resultMap = new TreeMap<>(new IPStringComparator());
		timeFrameFilter = new TimeFrameFilter();
		try {
			synchronized (WAASErrorManager.instance().getMonitor()) {
				URL repURL = this.getClass().getClassLoader().getResource("com/infovista/sdk/rampart/");
				ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromURIs(null, repURL);
				videoStatsStub_ = new VideoStatsStub(ctx, p_url);
				videoStatsStub_._getServiceClient().getOptions().setTimeOutInMilliSeconds(p_timeout);
				videoStatsStub_._getServiceClient().engageModule("rampart");
				videoStatsStub_._getServiceClient().getOptions().setProperty(RampartMessageData.KEY_RAMPART_POLICY, loadPolicy());
				videoStatsStub_._getServiceClient().getOptions().setUserName(p_userName);
				videoStatsStub_._getServiceClient().getOptions().setPassword(p_password);
			}
		} catch (AxisFault e) {
			CiscoWAASDiscoverer.LOGGER.info("The Video Stub could not be initialized :: " + e.getMessage());
		} catch (FileNotFoundException e) {
			CiscoWAASDiscoverer.LOGGER.info("The Video Stub could not be initialized :: " + e.getMessage());
		} catch (XMLStreamException e) {
			CiscoWAASDiscoverer.LOGGER.info("The Video Stub could not be initialized :: " + e.getMessage());
		}

	}

	// Constructor
	public VideoStub(String p_configDir,String p_url,String p_userName,String p_password,long p_timeout, boolean isDataCompressionEnabled){
		this(p_configDir, p_url, p_userName, p_password, p_timeout);
		if(videoStatsStub_!=null){
			if(isDataCompressionEnabled){
				enableDataCompression();
			}
			
		}
	}
	
	// Constructor
	public VideoStub(String p_configDir, String p_url, String p_userName, String p_password, long p_timeout, TimeSlot subTimeSlot) {
		super(subTimeSlot);
		timeFrameFilter = new TimeFrameFilter();
		try {
			synchronized (WAASErrorManager.instance().getMonitor()) {
				URL repURL = this.getClass().getClassLoader().getResource("com/infovista/sdk/rampart/");
				ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromURIs(null, repURL);
				videoStatsStub_ = new VideoStatsStub(ctx, p_url);
				videoStatsStub_._getServiceClient().getOptions().setTimeOutInMilliSeconds(p_timeout);
				videoStatsStub_._getServiceClient().engageModule("rampart");
				videoStatsStub_._getServiceClient().getOptions().setProperty(RampartMessageData.KEY_RAMPART_POLICY, loadPolicy());
				videoStatsStub_._getServiceClient().getOptions().setUserName(p_userName);
				videoStatsStub_._getServiceClient().getOptions().setPassword(p_password);
			}
		} catch (AxisFault e) {
			CiscoWAASDiscoverer.LOGGER.info("The Video Stub could not be initialized :: " + e.getMessage());
		} catch (FileNotFoundException e) {
			CiscoWAASDiscoverer.LOGGER.info("The Video Stub could not be initialized :: " + e.getMessage());
		} catch (XMLStreamException e) {
			CiscoWAASDiscoverer.LOGGER.info("The Video Stub could not be initialized :: " + e.getMessage());
		}

	}

	// Constructor
	public VideoStub(String p_configDir,String p_url,String p_userName,String p_password,long p_timeout, boolean isDataCompressionEnabled, TimeSlot subTimeSlot){
		this(p_configDir, p_url, p_userName, p_password, p_timeout, subTimeSlot);
		if(videoStatsStub_!=null){
			if(isDataCompressionEnabled){
				enableDataCompression();
			}
			
		}
	}

	@Override
	protected void initTimeFrame(TimeSlot timeSlot) {
		CommonParams.setTimeFrameFilterVideo(timeFrameFilter, timeSlot);
	}

	@Override
	public void updateTimeResult() {
		timeResult.setServiceName(getServiceName());
		timeResult.setLastTimeStamp(subTimeSlot.getEndTime().getTimeInMillis());
	}
	
	@Override
	public void writeStats() {
		writeVideoStats();
	}
	
	@Override
	protected void cleanup() {
		resultMap = new HashMap<>();
	}

	@Override
	public String getServiceName() {
		return Keyword.VIDEO;
	}
	
	
	@Override
	public void retrieveStats() {
		CiscoWAASDiscoverer.LOGGER.info("Free Memory available to the JVM before Video " + Runtime.getRuntime().freeMemory());
		CiscoWAASDiscoverer.LOGGER.debug("Processing VIDEO stats for Waes");
		CiscoWAASDiscoverer.LOGGER.debug("No. of properties to be collected for Video-Streaming :: " + cols.length);
		int noOfWaeProcessed=0;
		List<String[]> statList = new ArrayList<String[]>();
		if (!isMultitenant) {
			List<String[]> waeList  = getCapableWaes(Keyword.VIDEO);
			if(waeList.size()>0){
				for(String[] waeNameArray: waeList){
					if(waeNameArray.length>0){
						retrieveStats(waeNameArray);
					}else{
						CiscoWAASDiscoverer.LOGGER.info("Stats object returned for Video stat was null");
					}
				}
			}
		}
		CiscoWAASDiscoverer.LOGGER.info("Free Memory available to the JVM after Videostats " + Runtime.getRuntime().freeMemory());
	}
				
	private void retrieveStats(String[] waeNames){			
		if(istimeStampValid(getServiceName())){
			requestVideoStats(waeNames);
		}else{
			CiscoWAASDiscoverer.LOGGER.info("Stats for timstamp :: " + subTimeSlot.toString() + " has already been calculated for service :: " + getServiceName());
		}
	}
	
	private void requestVideoStats(String[] waeNames){
		VideoStats[] statArray = getStatArray(waeNames, Keyword.WAE_TYPE);

		if(statArray!=null){
			CiscoWAASDiscoverer.LOGGER.debug("NO. of responses Obtained for Nfs waes  " + statArray.length);
			for(VideoStats stats : statArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(stats.getDeviceName())){
							Long timeStamp = stats.getTimestamp().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(stats.getDeviceName());
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new VideoStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new VideoStatResult().setProperties(stats));
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

	// WRITING AVERAGE THROUGHPUT
	private void writeVideoStats()  {
		CsvWriter.checkAndWrite(FileNameRetriever.getVIDEOSFileName() , getServiceName(), resultMap);
	}

	private VideoStats[] getStatArray(String[] wae, String deviceType){
		try{
			RetrieveHistoricalStats vStats = new RetrieveHistoricalStats();
			vStats.setName(wae);
			vStats.setObjType(deviceType);
			vStats.setTimeframe(timeFrameFilter);
			
			RetrieveHistoricalStatsResponse response =videoStatsStub_.retrieveHistoricalStats(vStats);
			if(response!=null){
				OMElement omElement = response.getOMElement(RetrieveHistoricalStatsResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("VideoStats_"+wae[0] + subTimeSlot.getEndTimeStamp())), omElement.toString());
				return response.get_return();
			}else{
				CiscoWAASDiscoverer.LOGGER.info("Unable to get  VideoStats Stats for wae :: " + wae[0]);
				return null;
			}
			
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing VideoStats for wae :: " + wae[0] , e);
		}
		return null;
		
	}

	// Public methods
	public VideoStats[] retrieveHistoricalStats(String p_waeName, String p_objectType, TimeFrameFilter p_timeFrameFilter) throws RemoteException, RemoteExceptionException {
		try {
			RetrieveHistoricalStats retrieveHistoricalStats = new RetrieveHistoricalStats();
			retrieveHistoricalStats.setName(new String[] { p_waeName });
			retrieveHistoricalStats.setObjType(p_objectType);
			retrieveHistoricalStats.setTimeframe(p_timeFrameFilter);
			RetrieveHistoricalStatsResponse response = videoStatsStub_.retrieveHistoricalStats(retrieveHistoricalStats);
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
		videoStatsStub_._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_GZIP_REQUEST, Boolean.TRUE);
	}
}
