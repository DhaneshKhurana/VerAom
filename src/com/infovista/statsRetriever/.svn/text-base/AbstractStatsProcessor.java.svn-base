package com.infovista.statsRetriever;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.common.classes.StatsTimeResult;
import com.infovista.common.classes.TimeSlot;
import com.infovista.common.classes.TimeStampCache;
import com.infovista.common.interfaces.I_StatsProcessor;
import com.infovista.configs.CommonConfig;
import com.infovista.fileutils.CommonFileUtilities;
import com.infovista.fileutils.CsvReader;
import com.infovista.fileutils.FileNameRetriever;
import com.infovista.instances.Keyword;
import com.infovista.sdk.ssl.FakeSSLProtocolSocketFactory;
import com.infovista.sdk.stub.deviceConf.DeviceStub;

public abstract class AbstractStatsProcessor implements I_StatsProcessor{

	protected final static Map<String, String> wae_Name_Ip_Map;
	private final static Map<String, HashMap<String, ArrayList<String>>> wae_capability_Map;
	protected final static String[] monitoredAppsNames;
	
	private static DeviceStub deviceStub;
	
	private static String aquisionRate;
	private static boolean isMultitenant;
	
	protected volatile TimeSlot subTimeSlot;
	protected StatsTimeResult timeResult;
	
	static{
		final Map<String, String> waeNameIPmap = new HashMap<String, String> ();
		final Map<String, HashMap<String, ArrayList<String>>> waeCapabilityMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
		// now load both name-ip map and capability map from cache
		loadWaeCapabilityAndIpMap(waeNameIPmap, waeCapabilityMap);
		
		wae_Name_Ip_Map = Collections.unmodifiableMap(waeNameIPmap);
		wae_capability_Map = Collections.unmodifiableMap(waeCapabilityMap);
		
		CiscoWAASDiscoverer.LOGGER.debug("wae name ip map :: "  + wae_Name_Ip_Map.keySet().size() + " :: "  + wae_capability_Map.keySet().size());
		//load monitored app from topology cache
		monitoredAppsNames = loadMonitoredAppsList();
	}
	
	public AbstractStatsProcessor(){
		subTimeSlot = new TimeSlot();
		timeResult = new StatsTimeResult();
	}
	
	public AbstractStatsProcessor(TimeSlot subTimeSlot){
		this.subTimeSlot = subTimeSlot;
	}
	
	
	
	// A function that will be implemented by all the stub sub-classes
	protected abstract void initTimeFrame(TimeSlot timeSlot);
	public abstract void updateTimeResult();
	public abstract void writeStats();
	public abstract void retrieveStats();
	protected abstract void cleanup();
	
	public StatsTimeResult retrieveStatsForTimeSlot(TimeSlot subTimeSlot){
		CiscoWAASDiscoverer.LOGGER.debug("sub time-slot value changed for service :: " + getServiceName() + " from :: " + this.subTimeSlot.toString() + " to :: " + subTimeSlot.toString());
		this.subTimeSlot = subTimeSlot;
		if(istimeStampValid(getServiceName())){
			initTimeFrame(subTimeSlot);
			retrieveStats();
			writeStats();
			updateTimeResult();
			cleanup();
			return timeResult;	
		}else{
			CiscoWAASDiscoverer.LOGGER.debug("Data has been collected for timeStamp :: " + subTimeSlot.toString() + " for service " + getServiceName() + " . So exiting");
			return null;
		}
		
	}

	private static void loadWaeCapabilityAndIpMap(Map<String, String> wae_Name_Ip_Map, 	Map<String, HashMap<String, ArrayList<String>>> wae_capability_Map) {
		CsvReader.readWaesCapabilityAndIPMap(FileNameRetriever.getWaeCapabilityMapFileName(), CommonFileUtilities.CSV_SEPERATOR, wae_capability_Map, wae_Name_Ip_Map);
		
		if(wae_capability_Map==null){
			CiscoWAASDiscoverer.LOGGER.info("No WAE FOUND IN TOPO FILE");
		}else{
			CiscoWAASDiscoverer.LOGGER.debug("Wae Capability Map Initialized with " + wae_capability_Map.keySet().size() + " waes");
			CiscoWAASDiscoverer.LOGGER.debug("Wae Name-Ip Map Initialized with " + wae_Name_Ip_Map.keySet().size() + " waes");

		}
	}
	
	private static String[] loadMonitoredAppsList() {
		List<String[]> monApps = CsvReader.readListOfStringArrays(FileNameRetriever.getMonitoredAppsFileName(), CommonFileUtilities.CSV_SEPERATOR);
		if(monApps!=null){
			String[] monitoredAppsNames;
			if(monApps.size()==0){
				monitoredAppsNames = new String[0];
				CiscoWAASDiscoverer.LOGGER.info("No MONITORED APP FOUND IN TOPO FILE");
			}else{
				monitoredAppsNames = new String[monApps.size()];
				int i=0;
				for(String[] appArray : monApps){
					monitoredAppsNames[i++]=  appArray[0];
				}
				
				CiscoWAASDiscoverer.LOGGER.info("No. of MONITORED_APPs Found :: "+ monApps.size());
			}
			return monitoredAppsNames;
		}
		return new String[0];
	}
	
	
	public Map<String, String> getWaeNamesIPMap() {
		return wae_Name_Ip_Map;
	}
	
	public Map<String, HashMap<String, ArrayList<String>>> getWaeCapabilityMap() {
		return wae_capability_Map;
	}
	
	public String[] getMonitoredAppsNames() {
		return monitoredAppsNames;
	}

	public boolean isModeMultitenant() {
		return isMultitenant;
	}
	
	protected boolean istimeStampValid(String serviceName){
		if(CommonConfig.getInstance().isFollowlastRunMechanism()){
			Long latestTimeStamp = TimeStampCache.getInstance().getLatestTimeStamp(serviceName);
			if(latestTimeStamp!=null){
				if(subTimeSlot.getEndTime().getTimeInMillis()>latestTimeStamp){
					CiscoWAASDiscoverer.LOGGER.debug("TimeStamp is valid for service :: " + serviceName + " becoz last-timstamp- stored :: " + latestTimeStamp + " current timestamp :: " + subTimeSlot.getEndTimeStamp());
					return true;
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("TimeStamp is NOT valid for service :: " + serviceName + " becoz last-timstamp- stored :: " + latestTimeStamp + " current timestamp :: " + subTimeSlot.getEndTimeStamp());
					return false;
				}
			}
			return true;
		}
		return true;
	}
	
	protected List<String[]> getCapableWaes (String capability) {
		List<String[]> waeList = new ArrayList<String[]>();
		List<String> waeNames = new ArrayList<String>();
		if(!capability.equals(Keyword.TRAFFIC)){
			CiscoWAASDiscoverer.LOGGER.debug("collecting waes for " + capability);
			for(Entry<String, HashMap<String, ArrayList<String>>> waeName: wae_capability_Map.entrySet()){
				for(Entry<String, ArrayList<String>> waeIP : waeName.getValue().entrySet()){
					if(waeIP.getValue().contains(capability)){
						waeNames.add(waeName.getKey());
					}
				}
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.debug("collecting waes for traffic stats");
			for(String wae: wae_capability_Map.keySet()){
				waeNames.add(wae);
			}
		}
		
		int waeSubArraySize = waeNames.size();
		int totalWaes = waeNames.size();
		if(CommonConfig.getInstance().getTotalWaes()!=0){
			totalWaes = CommonConfig.getInstance().getTotalWaes();
		}
		if(CommonConfig.getInstance().getWaesInOneRequest()!=0){
			CommonConfig.getInstance().getWaesInOneRequest();
		}
		if(totalWaes>=waeSubArraySize){
			if(waeNames.size()>0){
				for(int i=0;i<totalWaes;){
					if((i+waeSubArraySize)<=totalWaes){
						String[] waes = new String[waeSubArraySize];
						for(int j=0;j<waeSubArraySize;j++){
							waes[j]=waeNames.get(i++);
						}
						waeList.add(waes);
					}else{
						int len = totalWaes-i;
						String[] waes = new String[len];
						for(int j=0;j<len;j++){
							waes[j]=waeNames.get(i++);
						}
						waeList.add(waes);
					}
					
					
				}
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.debug("total no. of waes is less than no. of waes in a request so exiting");
			System.exit(0);
		}
		capability = capability!=null?capability:"Traffic";
		CiscoWAASDiscoverer.LOGGER.debug("no. of waes in an array  for app :: " + capability + " are :: " + waeSubArraySize);
		CiscoWAASDiscoverer.LOGGER.debug("no. of arrays in wae list for app :: "+ capability +"   is :: " +  waeList.size());
		return waeList;
	}
	
	protected List<String[]> getCapableWaes (String capability, int slotSize) {
		List<String[]> waeList = new ArrayList<String[]>();
		List<String> waeNames = new ArrayList<String>();
		
		if(capability!=null && capability!=""){
			CiscoWAASDiscoverer.LOGGER.debug("collecting waes for " + capability);
			for(Entry<String, HashMap<String, ArrayList<String>>> waeName: wae_capability_Map.entrySet()){
				for(Entry<String, ArrayList<String>> waeIP : waeName.getValue().entrySet()){
					if(waeIP.getValue().contains(capability)){
						waeNames.add(waeName.getKey());
					}
				}
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.debug("collecting waes for traffic stats");
		
			for(String wae: wae_capability_Map.keySet()){
				waeNames.add(wae);
			}
		}
		if(waeNames.size()>0){
			for(int i=0;i<waeNames.size();){
				if((i+slotSize)<=waeNames.size()){
					String[] waes = new String[slotSize];
					for(int j=0;j<slotSize;j++){
						waes[j]=waeNames.get(i++);
					}
					waeList.add(waes);
				}else{
					int len = waeNames.size()-i;
					String[] waes = new String[len];
					for(int j=0;j<len;j++){
						waes[j]=waeNames.get(i++);
					}
					waeList.add(waes);
				}
				
				
			}
		}
	return waeList;
	}
	
	
	protected List<String> getCIFSCapableWaes () {
		List<String> waeList = new ArrayList<String>();
		for(Entry<String, HashMap<String, ArrayList<String>>> waeName: wae_capability_Map.entrySet()){
			for(Entry<String, ArrayList<String>> waeIP : waeName.getValue().entrySet()){
				if(waeIP.getValue().contains(Keyword.CIFS)){
					waeList.add(waeName.getKey());
				}
			}
		}
		
		return waeList;
	}
	
	protected boolean isFrequencyValid(String frequency){
		if(aquisionRate.equals(Keyword.AR_CUSTOM)){
			if(frequency.equals(Keyword.FREQUENCY_HOUR)){
				return true;
			}else{
				//CiscoWAASDiscoverer.LOGGER.debug( frequency + " value filtered out ");
				return false;
			}
		}
		else if(aquisionRate.equals(Keyword.AR_DAY)){
				if(frequency.equals(Keyword.FREQUENCY_HOUR)){
					return true;
				}else{
					//CiscoWAASDiscoverer.LOGGER.debug(frequency + " value filtered out ");
					return false;
				}
			}
		else if(aquisionRate.equals(Keyword.AR_HOUR)){
			if(frequency.equals(Keyword.FREQUENCY_MIN)){
				return true;
			}else{
				//CiscoWAASDiscoverer.LOGGER.debug( frequency + " value filtered out ");
				return false;
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.debug("Frequency filtering for AR :: " + aquisionRate + " is not defined");
		}
		return false;
	}
	
	public static void initializeStaticFields(){
		aquisionRate = CommonConfig.getInstance().getAr();
		isMultitenant = CommonConfig.getInstance().getMode().equalsIgnoreCase("mono")?false:true;
		
		initDeviceStub();
	
	}
	
	private static void initDeviceStub(){
		try{
			String protocol = CommonConfig.getInstance().getProtocol();
			if(protocol.equals(Keyword.PROTO_HTTPS)){
				Protocol.unregisterProtocol(protocol);
				Protocol.registerProtocol(protocol , new Protocol(protocol, new FakeSSLProtocolSocketFactory(), 443));
			}
			
			String waasConfDirectory_ = CommonConfig.getInstance().getWaasConfDirectory_();
			CiscoWAASDiscoverer.LOGGER.error("Pinging url :: " +  CommonConfig.getInstance().getDeviceConfURL() + " to initialize Device Stub");
			
			deviceStub = new DeviceStub(waasConfDirectory_, CommonConfig.getInstance().getDeviceConfURL() , CommonConfig.getInstance().getLogin() , CommonConfig.getInstance().getPassword() , CommonConfig.getInstance().getWaasTimeout());
			
			if (deviceStub == null){
				CiscoWAASDiscoverer.LOGGER.info("Device Conf service could not be initialized. So exiting");
				System.exit(1);
			}
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Exception occured while initializing stubs in constructor ", e);
		}
	}
	
	public static String getIPFromName(String deviceName){
		if(wae_Name_Ip_Map.containsKey(deviceName)){
			return wae_Name_Ip_Map.get(deviceName);
		}
		return "null";
	}
	
	public static boolean checkDeviceStub(){
		if(deviceStub!=null)
			return true;
		else
			return false;
	}
	
	public static boolean isSOAPCompressionSupported(){
		if(deviceStub!=null && deviceStub.isSOAPCompressionSupported())
			return true;
		else
			return false;
	}
	
}
