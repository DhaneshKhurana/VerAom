package com.infovista.sdk.stub.trafficStats;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.commons.io.FileUtils;

import com.cisco.waas.wsc.TrafficStatsStub.RetrieveAppTrafficStats;
import com.cisco.waas.wsc.TrafficStatsStub.RetrieveAppTrafficStatsResponse;
import com.cisco.waas.wsc.TrafficStatsStub.TimeFrameFilter;
import com.cisco.waas.wsc.TrafficStatsStub.TrafficStats;
import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.common.classes.CommonParams;
import com.infovista.common.classes.IPStringComparator;
import com.infovista.common.classes.TimeSlot;
import com.infovista.common.interfaces.I_Result;
import com.infovista.configs.CommonConfig;
import com.infovista.fileutils.CsvWriter;
import com.infovista.fileutils.FileNameRetriever;
import com.infovista.instances.Keyword;
import com.infovista.statsRetriever.AbstractStatsProcessor;

public class AppTrafficStats extends AbstractStatsProcessor {
	
	private TrafficStub trafficStub;
	private Map<String, Map<String, Map <Long, I_Result>>> resultMap;
	private TimeFrameFilter timeFrameFilter;
	
	
	public AppTrafficStats(String p_configDir,String p_url,String p_userName,String p_password,long p_timeout, boolean isDataCompressionEnabled){
		trafficStub = TrafficStub.getInstance(p_configDir, p_url, p_userName, p_password, p_timeout, isDataCompressionEnabled);
		resultMap = new HashMap<>();
		timeFrameFilter = new TimeFrameFilter();
	}
	
	
	@Override
	protected void initTimeFrame(TimeSlot timeSlot) {
		CommonParams.setTimeFrameFilterTraffic(timeFrameFilter, timeSlot);
	}
	
	
	@Override
	public String getServiceName() {
		return Keyword.APP_TRAFFIC;
	}
	
	@Override
	public void updateTimeResult() {
		timeResult.setServiceName(getServiceName());
		timeResult.setLastTimeStamp(subTimeSlot.getEndTime().getTimeInMillis());
	}
	
	@Override
	public void writeStats() {
		writeTrafficStats();
	}
	
	@Override
	protected void cleanup() {
		resultMap = new HashMap<>();
	}
	
	@Override
	public void retrieveStats(){
		try{
		//	CiscoWAASDiscoverer.LOGGER.info("Free Memory available to the JVM before App Traffic STATS " + Runtime.getRuntime().freeMemory());
		
			List<String[]> waeList = getCapableWaes(Keyword.TRAFFIC);
			int noOfAppsProcessed=waeList.size();
			
			CiscoWAASDiscoverer.LOGGER.debug("Processing App TRAFFIC stats for Monitored_APPS ");
			CiscoWAASDiscoverer.LOGGER.debug("no. of Monitopred apps in App traffic stats :: " +monitoredAppsNames.length);
			
			if(monitoredAppsNames!=null && monitoredAppsNames.length>0){
				for(String[] waeNames : waeList){
					if(waeNames.length>0){
						for(String trafType : CommonConfig.getInstance().getTrafficType()){
							for(String dir : CommonConfig.getInstance().getDirections()){
								retrieveStats(waeNames, trafType, dir);
							}
					}
				}
			}
			noOfAppsProcessed--;
			CiscoWAASDiscoverer.LOGGER.debug("App Traffic stats tasks left ::  " +noOfAppsProcessed );
			}
			
			
			//CiscoWAASDiscoverer.LOGGER.info("Free Memory available to the JVM after traffic Stats " + Runtime.getRuntime().freeMemory());
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.info(" Monitored Application Response not retrieved for traffic stats :: " + e.getMessage());
		}
	}
	
	private void retrieveStats(String[] waeNames, String trafType, String dir){
			requestAppTrafficStats(waeNames, trafType, dir);
	}
	
	
	//retrieve app traffic stats
	private void requestAppTrafficStats(String[] waeNames, String trafType, String dir){
		try{
			TrafficStats[] appTfStatsArray = getAppTrafficStatsArray(waeNames, Keyword.WAE_TYPE, trafType, dir, timeFrameFilter, monitoredAppsNames);	
				
			if(appTfStatsArray!=null && appTfStatsArray.length>0){
				CiscoWAASDiscoverer.LOGGER.debug("NO. of traffic objects retrieved for App traffic stats:: " + appTfStatsArray.length);
				for(TrafficStats tfStat : appTfStatsArray){
					if(tfStat!=null){
						if(wae_Name_Ip_Map.containsKey(tfStat.getDeviceName())){
							if(isFrequencyValid(tfStat.getFrequency())){
								String appName = tfStat.getApplicationname();
								String deviceIP = wae_Name_Ip_Map.get(tfStat.getDeviceName());
								Long timeStamp = tfStat.getEndtime().getTimeInMillis();
								I_Result result = new TrafficStatResult();
								result.setProperties(tfStat);
								
								if(!resultMap.containsKey(appName)){
									Map<String, Map <Long, I_Result>> ipResultMap = new TreeMap<>(new IPStringComparator());
									Map <Long, I_Result> timeMap = new TreeMap<>();
									timeMap.put(timeStamp, result);
									ipResultMap.put(deviceIP, timeMap);
									resultMap.put(appName, ipResultMap);
								}else{
									if(!resultMap.get(appName).containsKey(deviceIP)){
										Map <Long, I_Result> timeMap = new TreeMap<>();
										timeMap.put(timeStamp, result);
										resultMap.get(appName).put(deviceIP, timeMap);
									}else{
										resultMap.get(appName).get(deviceIP).put(timeStamp, result);
									}
								}
							}
						}
				}else{
					CiscoWAASDiscoverer.LOGGER.info("Stat object returned for App Traffic stat is null");
				}
				}
			}else{
			CiscoWAASDiscoverer.LOGGER.info("App TrafficStats array returned is null");
			}
		}catch(Exception ae){
		CiscoWAASDiscoverer.LOGGER.debug("IN traffic stats error occured ", ae);
		}
	}
	
	
	//getting traffic stats with retrieveAppTrafficStats
	private TrafficStats[] getAppTrafficStatsArray(String[] waeNames ,String objectType, String trafficType, String direction, TimeFrameFilter timeFrameFilter,String[] appNames){
		try {
			RetrieveAppTrafficStats retrieveAppTrafficStats=new RetrieveAppTrafficStats();
			retrieveAppTrafficStats.setName(waeNames);
			retrieveAppTrafficStats.setObjType(objectType);
			retrieveAppTrafficStats.setTrafficType(trafficType);
			retrieveAppTrafficStats.setDirection(direction);
			retrieveAppTrafficStats.setTimeframe(timeFrameFilter);
			retrieveAppTrafficStats.setAppNames(appNames);
			if(retrieveAppTrafficStats!=null){
				OMElement omElement = retrieveAppTrafficStats.getOMElement(RetrieveAppTrafficStats.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("TrafficAppStats_Request_"+subTimeSlot.getEndTimeStamp()+ waeNames[0])), omElement.toString());
			}
			
			CiscoWAASDiscoverer.LOGGER.debug("Sending request for retrieveAppTrafficStats with free memory avaialble :: " + Runtime.getRuntime().freeMemory());

			RetrieveAppTrafficStatsResponse response=trafficStub.retrieveAppTrafficStatsResponse(retrieveAppTrafficStats);
			
			
			if(response!=null){
				OMElement omElement = response.getOMElement(RetrieveAppTrafficStatsResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("TrafficAppStats_Response_"+ subTimeSlot.getEndTimeStamp() +waeNames[0])), omElement.toString());
				CiscoWAASDiscoverer.LOGGER.debug("RetrieveAppTrafficStatsResponse returned and free memory left is :: " + Runtime.getRuntime().freeMemory());
				return response.get_return();
			}else{
				CiscoWAASDiscoverer.LOGGER.info("Unable to get App TrafficStats Stats for wae Array ");
				return null;
			}
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing App TrafficStats for wae Array", e);
		}
		return null;
	}
	
	private void writeTrafficStats(){
		for(Entry<String, Map<String, Map<Long, I_Result>>> map : resultMap.entrySet()){
			CsvWriter.checkAndWrite(FileNameRetriever.getApplicationStatsFileName(map.getKey()), getServiceName(),  map.getValue());
		}
	}

}
