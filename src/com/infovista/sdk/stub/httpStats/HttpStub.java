package com.infovista.sdk.stub.httpStats;

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

import com.cisco.waas.wsc.HttpStatsStub;
import com.cisco.waas.wsc.DeviceConfStub.Device;
import com.cisco.waas.wsc.HttpStatsStub.GetConnOptRate;
import com.cisco.waas.wsc.HttpStatsStub.GetConnOptRateResponse;
import com.cisco.waas.wsc.HttpStatsStub.GetMaxConnReuseCount;
import com.cisco.waas.wsc.HttpStatsStub.GetMaxConnReuseCountResponse;
import com.cisco.waas.wsc.HttpStatsStub.GetOptConnCount;
import com.cisco.waas.wsc.HttpStatsStub.GetOptConnCountResponse;
import com.cisco.waas.wsc.HttpStatsStub.GetTotalConnCount;
import com.cisco.waas.wsc.HttpStatsStub.GetTotalConnCountResponse;
import com.cisco.waas.wsc.HttpStatsStub.HttpConnOptRate;
import com.cisco.waas.wsc.HttpStatsStub.HttpConnStats;
import com.cisco.waas.wsc.HttpStatsStub.HttpMaxConnReuseCount;
import com.cisco.waas.wsc.HttpStatsStub.HttpOptConnCount;
import com.cisco.waas.wsc.HttpStatsStub.HttpTotalConnCount;
import com.cisco.waas.wsc.HttpStatsStub.RetrieveStats;
import com.cisco.waas.wsc.HttpStatsStub.RetrieveStatsResponse;
import com.cisco.waas.wsc.HttpStatsStub.TimeFrameFilter;
import com.cisco.waas.wsc.MapiStatsStub.MapiResponseStats;
import com.cisco.waas.wsc.NfsStatsStub.NfsReqTypeStats;
import com.cisco.waas.wsc.NfsStatsStub.RetrieveRequestTypeStats;
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

public class HttpStub extends AbstractStatsProcessor{

	private Map<String, Map<Long, I_Result>> resultMap;
	private HttpStatsStub httpStatsStub_;
	private String cols[] = {"#Tag", "TimeStamp", "Reused-Peer", "Reused", "Idle", "Max-reused", "Pct-Rtt-Saved", "Total-Handoff", "FastConnection-Setup", "Total-Lan-Connection", "Max-Reuse-Count", "Percent-Connection-Time-Saved"};
	private TimeFrameFilter timeFrameFilter;
	private CsvWriter writer;
	
	// Constructor
	public HttpStub(String p_configDir,String p_url,String p_userName,String p_password,long p_timeout){
		resultMap = new TreeMap<>(new IPStringComparator());
		timeFrameFilter = new TimeFrameFilter();
		try {
				synchronized (WAASErrorManager.instance().getMonitor()){
					URL repURL=this.getClass().getClassLoader().getResource("com/infovista/sdk/rampart/");
					ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromURIs(null,repURL);
					httpStatsStub_ = new HttpStatsStub(ctx, p_url);
					httpStatsStub_._getServiceClient().getOptions().setTimeOutInMilliSeconds(p_timeout);
					httpStatsStub_._getServiceClient().engageModule("rampart");
					httpStatsStub_._getServiceClient().getOptions().setProperty(
							RampartMessageData.KEY_RAMPART_POLICY,
							loadPolicy());
					httpStatsStub_._getServiceClient().getOptions().setUserName(p_userName);
					httpStatsStub_._getServiceClient().getOptions().setPassword(p_password);
				}
			} catch (AxisFault e) {
				CiscoWAASDiscoverer.LOGGER.info("The HTTP Stub could not be initilaized :: " + e.getMessage()) ;
			} catch (FileNotFoundException e) {
				CiscoWAASDiscoverer.LOGGER.info("The HTTP Stub could not be initilaized :: " + e.getMessage()) ;
			} catch (XMLStreamException e) {
				CiscoWAASDiscoverer.LOGGER.info("The HTTP Stub could not be initilaized :: " + e.getMessage()) ;
			}	
		
	}
	// Constructor
	public HttpStub(String p_configDir,String p_url,String p_userName,String p_password,long p_timeout, boolean isDataCompressionEnabled){
		this(p_configDir, p_url, p_userName, p_password, p_timeout);
		if(httpStatsStub_!=null){
			if(isDataCompressionEnabled){
				enableDataCompression();
			}
			
		}
	}

	@Override
	protected void initTimeFrame(TimeSlot timeSlot) {
		CommonParams.setTimeFrameFilterHTTP(timeFrameFilter, timeSlot);
		
	}

	@Override
	public String getServiceName() {
		return Keyword.HTTP;
	}
	
	@Override
	public void updateTimeResult() {
		timeResult.setServiceName(getServiceName());
		timeResult.setLastTimeStamp(subTimeSlot.getEndTime().getTimeInMillis());
	}
	
	@Override
	public void writeStats() {
		writeHTTPStats();	
	}

	@Override
	protected void cleanup() {
		resultMap = new HashMap<>();
	}
	
	@Override
	public void retrieveStats(){
		CiscoWAASDiscoverer.LOGGER.debug("Free Memory available to the JVM before HTTP " + Runtime.getRuntime().freeMemory());
		List<String[]> waeList = getCapableWaes(Keyword.HTTP);
		int noOfArraysProcessed = waeList.size();
		CiscoWAASDiscoverer.LOGGER.info("Total HTTP tasks submitted :: " + noOfArraysProcessed);
		CiscoWAASDiscoverer.LOGGER.debug("No. of properties to be calculated for HTTP service :: " + cols.length);
		for(String[] waeNameArray : waeList){
			if(waeNameArray.length>0){
				
				retrieveStats(waeNameArray);
			
				
				
			}
		}

		CiscoWAASDiscoverer.LOGGER.info("Free Memory available to the JVM after Http " + Runtime.getRuntime().freeMemory());
	}
		
	private void retrieveStats(String[] waeNameArray){
		if(istimeStampValid(getServiceName())){
			requestHttpConnStats(waeNameArray);
			//requestHttpOptConnCount(waeNameArray);
			requestHttpTotalConnCount(waeNameArray);
			requestHttpMaxConnReuseCount(waeNameArray);
			requestHttpConnOptRate(waeNameArray);
		}else{
			CiscoWAASDiscoverer.LOGGER.info("Stats for timstamp :: " + subTimeSlot.toString() + " has already been calculated for service :: " + getServiceName());
		}
	}
	
	private void requestHttpConnStats(String[] waeNameArray){
		HttpConnStats[] statArray = getConnStatArray(waeNameArray, Keyword.WAE_TYPE);
		
		if(statArray!=null){
			CiscoWAASDiscoverer.LOGGER.debug("NO. of responses Obtained for HttpConnStats waes  " + statArray.length);
			for(HttpConnStats stats : statArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(stats.getDeviceName())){
							Long timeStamp = stats.getEndtime().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(stats.getDeviceName());
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new HttpStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new HttpStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(stats);
								}
							}
						}
						
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("The Object IN RESPONSEArray Returned is NULL for HttpConnStats  :: ");
				}
			}
		}
	}
	
	private void requestHttpTotalConnCount(String[] waeNameArray){
		HttpTotalConnCount[] statArray = getTotalConnCountArray(waeNameArray, Keyword.WAE_TYPE);
		
		if(statArray!=null){
			CiscoWAASDiscoverer.LOGGER.debug("NO. of responses Obtained for HttpTotalConnCount waes  " + statArray.length);
			for(HttpTotalConnCount stats : statArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(stats.getDeviceName())){
							Long timeStamp = stats.getEndtime().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(stats.getDeviceName());
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new HttpStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new HttpStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(stats);
								}
							}
						}
						
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("The Object IN RESPONSEArray Returned is NULL for HttpTotalConnCount  :: ");
				}
			}
		}
	}
	
	private void requestHttpOptConnCount(String[] waeNameArray){
		HttpOptConnCount[] statArray = getConCountArray(waeNameArray, Keyword.WAE_TYPE);
		
		if(statArray!=null){
			CiscoWAASDiscoverer.LOGGER.debug("NO. of responses Obtained for HttpOptConnCount waes  " + statArray.length);
			for(HttpOptConnCount stats : statArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(stats.getDeviceName())){
							Long timeStamp = stats.getEndtime().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(stats.getDeviceName());
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new HttpStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new HttpStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(stats);
								}
							}
						}
						
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("The Object IN RESPONSEArray Returned is NULL for HttpOptConnCount  :: ");
				}
			}
		}
	}
	
	private void requestHttpMaxConnReuseCount(String[] waeNameArray){
		HttpMaxConnReuseCount[] statArray = getMaxConnReuseCount(waeNameArray, Keyword.WAE_TYPE);
		
		if(statArray!=null){
			CiscoWAASDiscoverer.LOGGER.debug("NO. of responses Obtained for HttpOptConnCount waes  " + statArray.length);
			for(HttpMaxConnReuseCount stats : statArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(stats.getDeviceName())){
							Long timeStamp = stats.getEndtime().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(stats.getDeviceName());
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new HttpStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new HttpStatResult().setProperties(stats));
								}else{
									resultMap.get(waeIP).get(timeStamp).setProperties(stats);
								}
							}
						}
						
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("The Object IN RESPONSEArray Returned is NULL for HttpOptConnCount  :: ");
				}
			}
		}

	}
	
	private void requestHttpConnOptRate(String[] waeNameArray){
		HttpConnOptRate[] statArray = getConnOptRate(waeNameArray, Keyword.WAE_TYPE);
		
		if(statArray!=null){
			CiscoWAASDiscoverer.LOGGER.debug("NO. of responses Obtained for Nfs waes  " + statArray.length);
			for(HttpConnOptRate stats : statArray){
				if(stats!=null){
					if(isFrequencyValid(stats.getFrequency())){
						if(wae_Name_Ip_Map.containsKey(stats.getDeviceName())){
							Long timeStamp = stats.getEndtime().getTimeInMillis();
							String waeIP  = wae_Name_Ip_Map.get(stats.getDeviceName());
							
							if(!resultMap.containsKey(waeIP)){
								Map<Long, I_Result> localMap = new TreeMap<>();
								localMap.put(timeStamp, new HttpStatResult().setProperties(stats));
								resultMap.put(waeIP, localMap);
							}else{	
								if(!resultMap.get(waeIP).containsKey(timeStamp)){
									resultMap.get(waeIP).put(timeStamp, new HttpStatResult().setProperties(stats));
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
	
		//WRITING AVERAGE THROUGHPUT
	private void writeHTTPStats(){
		CsvWriter.checkAndWrite(FileNameRetriever.getHTTPFileName() , getServiceName(), resultMap);
	}
	
	public HttpConnStats[] retrieveStats(String p_waeName,String p_objectType,
			TimeFrameFilter p_timeFrameFilter) throws RemoteException, RemoteExceptionException{
		try {
			RetrieveStats retrieveStats=new RetrieveStats();
			retrieveStats.setName(new String[] {p_waeName});
			retrieveStats.setObjType(p_objectType);
			retrieveStats.setTimeframe(p_timeFrameFilter);
			RetrieveStatsResponse response=httpStatsStub_.retrieveStats(retrieveStats);
			return response.get_return();
		} catch (RemoteException e){
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}
		
		
		//GETTING CLIENT AVG THRUPUT ARRAY
		private HttpConnStats[] getConnStatArray(String[] waeName,String deviceType) {
			try{
				
				CiscoWAASDiscoverer.LOGGER.debug("Sending HttpConnStats request for HTTP stats");

				RetrieveStats gConnStats = new RetrieveStats();
				gConnStats.setName(waeName);
				gConnStats.setObjType(deviceType);
				gConnStats.setTimeframe(timeFrameFilter);
				
				if(gConnStats!=null){
					OMElement omElement = gConnStats.getOMElement(RetrieveStats.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("HttpConnStats_Request_"+subTimeSlot.getEndTimeStamp()+"_"+waeName[0])), omElement.toString());
				}
				
				RetrieveStatsResponse response = httpStatsStub_.retrieveStats(gConnStats);
				if(response!=null){
					OMElement omElement = response.getOMElement(RetrieveStatsResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("HttpConnStats_Response_"+subTimeSlot.getEndTimeStamp()+"_"+waeName[0])), omElement.toString());
					CiscoWAASDiscoverer.LOGGER.debug("HttpConnStats response retrieved for HTTP stats");
					return response.get_return();
				}else{
					CiscoWAASDiscoverer.LOGGER.info("Unable to get HTTP RetrieveStats Stats for wae" );
					return null;
				}
				
			}catch(Exception e){
				CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing HttpConnStats" , e);
			}
			return null;
		}
		
		//GETTING CLIENT AVG THRUPUT ARRAY
		private HttpOptConnCount[] getConCountArray(String[] waeName,String deviceType){
			try{
				CiscoWAASDiscoverer.LOGGER.debug("Sending HttpOptConnCount request for HTTP stats");

				
				GetOptConnCount gConnCount = new GetOptConnCount();
				gConnCount.setName(waeName);
				gConnCount.setObjType(deviceType);
				gConnCount.setTimeframe(timeFrameFilter);
				
				if(gConnCount!=null){
					OMElement omElement = gConnCount.getOMElement(GetOptConnCount.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("HttpOptConnCount_Request_"+subTimeSlot.getEndTimeStamp()+ waeName[0])), omElement.toString());
				}
				
				
				GetOptConnCountResponse response = httpStatsStub_.getOptConnCount(gConnCount);
				if(response!=null){
					CiscoWAASDiscoverer.LOGGER.debug("HttpOptConnCount response retrieved for HTTP stats");
					OMElement omElement = response.getOMElement(GetOptConnCountResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("HttpOptConnCount_Response_"+subTimeSlot.getEndTimeStamp()+"_"+waeName[0])), omElement.toString());
					return response.get_return();
				}else{
					CiscoWAASDiscoverer.LOGGER.info("Unable to get  HttpOptConnCount Stats for wae :: " + waeName[0]);
					return null;
				}
			}catch(Exception e){
				CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing HttpOptConnCount for wae :: " + waeName[0] , e);
			}
			return null;
			
			
		}
		
		//GETTING CLIENT AVG THRUPUT ARRAY
		private HttpTotalConnCount[] getTotalConnCountArray(String[] waeName,String deviceType){
			try{
				CiscoWAASDiscoverer.LOGGER.debug("Sending HttpTotalConnCount request for HTTP stats");

				
				GetTotalConnCount gTot = new GetTotalConnCount();
				gTot.setName(waeName);
				gTot.setObjType(deviceType);
				gTot.setTimeframe(timeFrameFilter);
				
				if(gTot!=null){
					OMElement omElement = gTot.getOMElement(GetTotalConnCount.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("HttpTotalConnCount_Request_"+subTimeSlot.getEndTimeStamp()+ "_"+waeName[0])), omElement.toString());
				}
				
				GetTotalConnCountResponse response =  httpStatsStub_.getTotalConnCount(gTot);
				if(response!=null){
					CiscoWAASDiscoverer.LOGGER.debug("HttpTotalConnCount response retrieved for HTTP stats");
					OMElement omElement = response.getOMElement(GetTotalConnCountResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("HttpTotalConnCount_Response_"+subTimeSlot.getEndTimeStamp()+"_"+waeName[0])), omElement.toString());
					return response.get_return();
				}else{
					CiscoWAASDiscoverer.LOGGER.info("Unable to get  HttpTotalConnCount Stats for wae :: " + waeName[0]);
					return null;
				}
			}catch(Exception e){
				CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing HttpTotalConnCount for wae :: " + waeName[0] , e);
			}
			return null;
			
		}
		
		//GETTING CLIENT AVG THRUPUT ARRAY
		private HttpMaxConnReuseCount[] getMaxConnReuseCount(String[] waeName,String deviceType){
			try{
				CiscoWAASDiscoverer.LOGGER.debug("Sending HttpMaxConnReuseCount request for HTTP stats");

				
				GetMaxConnReuseCount gTot = new GetMaxConnReuseCount();
				gTot.setName(waeName);
				gTot.setObjType(deviceType);
				gTot.setTimeframe(timeFrameFilter);
				
				if(gTot!=null){
					OMElement omElement = gTot.getOMElement(GetMaxConnReuseCount.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("HttpMaxConnReuseCount_Request_"+subTimeSlot.getEndTimeStamp()+ "_"+waeName[0])), omElement.toString());
				}
				
				GetMaxConnReuseCountResponse response =   httpStatsStub_.getMaxConnReuseCount(gTot);
				if(response!=null){
					CiscoWAASDiscoverer.LOGGER.debug("HttpMaxConnReuseCount response retrieved for HTTP stats");
					OMElement omElement = response.getOMElement(GetMaxConnReuseCountResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("HttpMaxConnReuseCount_Response_"+subTimeSlot.getEndTimeStamp()+ "_"+waeName[0])), omElement.toString());
					return response.get_return();
				}else{
					CiscoWAASDiscoverer.LOGGER.info("Unable to get  HttpMaxConnReuseCount Stats for wae :: " + waeName[0]);
					return null;
				}
			}catch(Exception e){
				CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing HttpMaxConnReuseCount for wae :: " + waeName[0] , e);
			}
			return null;
			
		}
		
		private HttpConnOptRate[] getConnOptRate(String[] waeName,String deviceType){
			try{
				CiscoWAASDiscoverer.LOGGER.debug("Sending HttpConnOptRate request for HTTP stats");
				GetConnOptRate gTot = new GetConnOptRate();
				gTot.setName(waeName);
				gTot.setObjType(deviceType);
				gTot.setTimeframe(timeFrameFilter);
				
				if(gTot!=null){
					OMElement omElement = gTot.getOMElement(GetMaxConnReuseCount.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("HttpConnOptRate_Request_"+subTimeSlot.getEndTimeStamp()+ "_"+waeName[0])), omElement.toString());
				}
				
				GetConnOptRateResponse response =   httpStatsStub_.getConnOptRate(gTot);
				if(response!=null){
					
					CiscoWAASDiscoverer.LOGGER.debug("HttpConnOptRate response retrieved for HTTP stats");
					OMElement omElement = response.getOMElement(GetConnOptRateResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("HttpConnOptRate_Response_"+subTimeSlot.getEndTimeStamp()+ "_"+waeName[0])), omElement.toString());
					return response.get_return();
				}else{
					CiscoWAASDiscoverer.LOGGER.info("Unable to get  HttpConnOptRate Stats for wae :: " + waeName[0]);
					return null;
				}
			}catch(Exception e){
				CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing HttpConnOptRate for wae :: " + waeName[0] , e);
			}
			return null;
			
		}

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
		httpStatsStub_._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_GZIP_REQUEST, Boolean.TRUE);
	}
	
	public HttpStatsStub getHttpStatStub(){
		return httpStatsStub_;
	}
}
