package com.infovista.configs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sound.midi.SysexMessage;

import org.apache.commons.httpclient.protocol.Protocol;

import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.instances.Keyword;
import com.infovista.sdk.ssl.FakeSSLProtocolSocketFactory;
import com.infovista.sdk.stub.cifsStats.CIFSStub;
import com.infovista.sdk.stub.httpStats.HttpStub;
import com.infovista.sdk.stub.mapiStats.MapiStub;
import com.infovista.sdk.stub.nfsStats.NfsStub;
import com.infovista.sdk.stub.trafficStats.TrafficStub;
import com.infovista.sdk.stub.videoStats.VideoStub;
import com.infovista.statsRetriever.AbstractStatsProcessor;
import com.infovista.statsRetriever.ParallelStatsRetriever;
import com.infovista.common.classes.*;
import com.infovista.fileutils.FileNameRetriever;
import com.infovista.statsRetriever.StatsPoller;
import com.sun.xml.bind.v2.runtime.RuntimeUtil.ToStringAdapter;

public class CommonConfig {
	
		private static CommonConfig config;
		private PropertyLists propertyLists;
		
		private ExecutorService exService;
		
		private String host;
		private String login;
		private String password;
		private String protocol;
		private String port;
		private String ar;
		private String STAT_TYPE;
		private String Mode;
		private String timeStamp;
		
		private boolean processingTopo;
		
		private int waasTimeout;
		private int nThreads;
		private int statHistory;
		private int waesInOneRequest;
		private int totalWaes;
		private int dataMaxHistory; // in hours
		private int maxHoursInRequest;
		
		private Calendar startTime;
		private Calendar endTime;
		
		private boolean alarm_services = false;
		private boolean mapi_services = false;
		private boolean cifs_services = false;
		private boolean nfs_services = false;
		private boolean video_services = false;
		private boolean traffic_services = false;
		private boolean http_services = false;
		private boolean all_services = true;
		
		private boolean followlastRunMechanism;
		
		private List<String> registeredServices;
		private List<String> registeredMapiRequests;
		
		private String waasConfDirectory_;
		private String waasURL;
		private String deviceConfURL;
		private String trafficURL;
		private String cifsURL;
		private String httpURL;
		private String mapiURL;
		private String nfsURL;
		private String deviceStatusURL;
		private String videoURL;
		private String alarmURL;
		
		private String output_folder;
		
		private boolean retAppTrafficStats;
		
		
		private String[] trafficType;
		private String[]  directions;
		
		
		//CONSTRUCTOR
		private CommonConfig(){
			init();
		}
		
		//GET INSTANCE
		public static CommonConfig getInstance(){
			if(config==null){
				synchronized(CommonConfig.class){
					if(config==null){
						config = new CommonConfig();
					}
				}
				
			}
			return config;
		}
		//INITILIZATION
		private void init(){
			propertyLists = PropertyLists.getInstance();
			
			inializeFieldsFromPropertyFiles();
			
			initAllServices();
			
			initThreadPool();
			
			initOutputFolder();
			
		}
		
		private void initOutputFolder() {
			FileNameRetriever.setOutputFolder(output_folder);
		}

		private void initThreadPool() {
			CiscoWAASDiscoverer.LOGGER.debug("no. of thread are set to :: " + nThreads);
			System.out.println("no. of thread are set to :: " + nThreads);
			exService = Executors.newFixedThreadPool(nThreads);
		}

		private void initAllServices(){
			registeredServices = new ArrayList<String>();
			if(cifs_services)
				registerService(CLParser.CIFS_SERVICE);
			if(nfs_services)
				registerService(CLParser.NFS_SERVICE);
			if(mapi_services)
				registerService(CLParser.MAPI_SERVICE);
			if(http_services)
				registerService(CLParser.HTTP_SERVICE);
			if(traffic_services)
				registerService(CLParser.TRAFFIC_SERVICE);
			if(video_services)
				registerService(CLParser.VIDEO_SERVICE);
			if(alarm_services)
				registerService(CLParser.ALARM_SERVICE);
		}
		
		
		private void inializeFieldsFromPropertyFiles(){
			try {
				FileInputStream fis = new FileInputStream(FileNameRetriever.getVerizonPollerPrpopertyFile());
				Properties props = new Properties();
				props.load(fis);
				
				loadProperties(props);
				
			} catch (FileNotFoundException e) {
				CiscoWAASDiscoverer.LOGGER.error("Could not load property file", e);
			} catch (IOException e) {
				CiscoWAASDiscoverer.LOGGER.error("Could not load property file", e);
			}
		}
	
		private void loadProperties(Properties props){
			waasTimeout = Integer.parseInt(props.getProperty(PropertyLists.WAAS_TIMEOUT));
			waesInOneRequest = Integer.parseInt(props.getProperty(PropertyLists.WAES_IN_REQUEST));
			totalWaes = Integer.parseInt(props.getProperty(PropertyLists.TOTAL_WAES));
			nThreads = Integer.parseInt(props.getProperty(PropertyLists.TOTAL_THRREADS));;
			dataMaxHistory = Integer.parseInt(props.getProperty(PropertyLists.DATA_MAX_hISTORY)); //in hours
			maxHoursInRequest = Integer.parseInt(props.getProperty(PropertyLists.MAX_HOUR_IN_REQUEST));
			
			host = props.getProperty(PropertyLists.HOST).trim();
			login = props.getProperty(PropertyLists.USERNAME).trim();
			password = props.getProperty(PropertyLists.PASSWORD).trim();
			
			followlastRunMechanism = Boolean.parseBoolean(props.getProperty(PropertyLists.FOLLOW_LASTRUN).trim());
			
			protocol = props.getProperty(PropertyLists.PROTOCOL).trim();
			port = props.getProperty(PropertyLists.PORT).trim();
			ar = props.getProperty(PropertyLists.AQUISITON_RATE).trim();		//Aquisition Rate
			Mode = props.getProperty(PropertyLists.MODE).trim();
			
			output_folder =props.getProperty(PropertyLists.OUTPUT_FOLDER).trim();
			waasURL=props.getProperty(PropertyLists.WAAS_URL).trim();
			deviceConfURL = props.getProperty(PropertyLists.DEVICE_CONF_URL).trim();
			trafficURL= props.getProperty(PropertyLists.TRAFFIC_URL).trim();
			cifsURL= props.getProperty(PropertyLists.CIFS_URL).trim();
			httpURL= props.getProperty(PropertyLists.HTTP_URL).trim();
			mapiURL= props.getProperty(PropertyLists.MAPI_URL).trim();
			nfsURL = props.getProperty(PropertyLists.NFS_URL).trim();
			videoURL = props.getProperty(PropertyLists.VIDEO_URL).trim();
			alarmURL = props.getProperty(PropertyLists.ALARM_URL).trim();
			deviceStatusURL= props.getProperty(PropertyLists.DEVICE_CONF_URL).trim();
			
			if(props.getProperty(PropertyLists.WASS_CONF_DIRECTORY).equals("null") || props.getProperty(PropertyLists.WASS_CONF_DIRECTORY).isEmpty()){
				waasConfDirectory_= null;
			}else{
				waasConfDirectory_= props.getProperty(PropertyLists.WASS_CONF_DIRECTORY).trim();
			}
			
			trafficType = new String[]{props.getProperty(PropertyLists.TRAFFIC_TYPE)};
			directions = new String[]{props.getProperty(PropertyLists.DIRECTIONS)};
		
			if(!props.getProperty(PropertyLists.START_TIME).isEmpty()){
				startTime= CLParser.getDateFromString(props.getProperty(PropertyLists.START_TIME));
				followlastRunMechanism = false;
			}else{
				startTime = Calendar.getInstance();
			}
			
			if(!props.getProperty(PropertyLists.END_TIME).isEmpty()){
				endTime = CLParser.getDateFromString(props.getProperty(PropertyLists.END_TIME));
				followlastRunMechanism = false;
			}else{
				endTime = Calendar.getInstance();
			}
			
			mapi_services = Boolean.parseBoolean(props.getProperty(PropertyLists.MAPI_Service_Enabled).trim());
			cifs_services = Boolean.parseBoolean(props.getProperty(PropertyLists.CIFS_Service_Enabled).trim());
			nfs_services = Boolean.parseBoolean(props.getProperty(PropertyLists.NFS_Service_Enabled).trim());
			video_services = Boolean.parseBoolean(props.getProperty(PropertyLists.VIDEO_Service_Enabled).trim());
			traffic_services = Boolean.parseBoolean(props.getProperty(PropertyLists.TRAFFIC_Service_Enabled).trim());
			http_services = Boolean.parseBoolean(props.getProperty(PropertyLists.HTTP_Service_Enabled).trim());
			alarm_services = Boolean.parseBoolean(props.getProperty(PropertyLists.ALARM_Service_Enabled).trim());
			
			
			
			
			CiscoWAASDiscoverer.LOGGER.info("Properties set in property file :: ");
			for(String key : props.stringPropertyNames()){
				CiscoWAASDiscoverer.LOGGER.info(key + " ----> " + props.getProperty(key));	
			}
			CiscoWAASDiscoverer.LOGGER.info("----------");
		}
		
		public void update(){
			updateTimeSlot();
		}
		
		public void updateTimeSlot(){
			if(followlastRunMechanism){
				startTime.add(Calendar.HOUR_OF_DAY, -1* (dataMaxHistory+1));
				
				startTime.set(Calendar.MINUTE, 0);
				startTime.set(Calendar.SECOND, 0);
				startTime.set(Calendar.MILLISECOND, 0);
				
				endTime.add(Calendar.HOUR_OF_DAY, -1);
				endTime.set(Calendar.MILLISECOND, 0);
				endTime.set(Calendar.SECOND, 0);
				endTime.set(Calendar.MINUTE, 0);
				
				CiscoWAASDiscoverer.LOGGER.debug("last run mechanism will be follwed with time duration as :: from time :: " + CLParser.printDate(startTime) + " to time :: " + CLParser.printDate(endTime));
			}else{
				CiscoWAASDiscoverer.LOGGER.debug("last run mechanism will NOT be follwed.\nManual from time :: " + CLParser.printDate(startTime) + " to time :: " + CLParser.printDate(endTime));
			}
		}

		public List<String> getRegisteredRequests(String stub){
			if(stub.equals("MAPI")){
				return registeredMapiRequests;
			}
			return null;
		}
		
		public void shutdownExecutor(){
			try{
				exService.shutdown();
				
				if(!exService.isShutdown()){
					CiscoWAASDiscoverer.LOGGER.debug("Shutting down services now");
					exService.shutdownNow();
				}
				
				/*if (!exService.awaitTermination(30, TimeUnit.MINUTES)) {
						CiscoWAASDiscoverer.LOGGER.debug("Sevices weren't shut-down, so forcing it to exit");
					    System.exit(0);
				}*/
			}catch(Exception e){
				CiscoWAASDiscoverer.LOGGER.error("Could not shut-down executor, due to error :: ", e);
				System.exit(0);
			}
		}
		
		public void printAllProperties(){
			CiscoWAASDiscoverer.LOGGER.debug("Finally all the properties set are as follows :: ");
			CiscoWAASDiscoverer.LOGGER.debug(getHost());
			CiscoWAASDiscoverer.LOGGER.debug(getLogin());
			CiscoWAASDiscoverer.LOGGER.debug(getPassword());
			CiscoWAASDiscoverer.LOGGER.debug(getPort());
			CiscoWAASDiscoverer.LOGGER.debug(getProtocol());
			CiscoWAASDiscoverer.LOGGER.debug(getWaasTimeout());
			CiscoWAASDiscoverer.LOGGER.debug(getWaasConfDirectory_()==null?"null":getWaasConfDirectory_());
			CiscoWAASDiscoverer.LOGGER.debug(getWaasURL());
			CiscoWAASDiscoverer.LOGGER.debug(getDeviceConfURL());
			CiscoWAASDiscoverer.LOGGER.debug(getDeviceStatusURL());
			CiscoWAASDiscoverer.LOGGER.debug(getCifsURL());
			CiscoWAASDiscoverer.LOGGER.debug(getHttpURL());
			CiscoWAASDiscoverer.LOGGER.debug(getMapiURL());
			CiscoWAASDiscoverer.LOGGER.debug(getNfsURL());
			CiscoWAASDiscoverer.LOGGER.debug(getVideoURL());
			CiscoWAASDiscoverer.LOGGER.debug(getTrafficURL());
			CiscoWAASDiscoverer.LOGGER.debug(getAlarmURL());
			CiscoWAASDiscoverer.LOGGER.debug(CLParser.printDate(startTime));
			CiscoWAASDiscoverer.LOGGER.debug(CLParser.printDate(endTime));
			CiscoWAASDiscoverer.LOGGER.debug(getAr());
			CiscoWAASDiscoverer.LOGGER.debug(getOutput_folder());
			CiscoWAASDiscoverer.LOGGER.debug("following last run mechanism::" + isFollowlastRunMechanism());
			CiscoWAASDiscoverer.LOGGER.debug(waesInOneRequest);
			CiscoWAASDiscoverer.LOGGER.debug(getTotalWaes());
			
		}
		
		public void registerService(String service){
			if(service.equals(CLParser.ALL_SERVICE)){
				registeredServices.add(CLParser.TRAFFIC_SERVICE);
				registeredServices.add(CLParser.CIFS_SERVICE);
				registeredServices.add(CLParser.MAPI_SERVICE);
				registeredServices.add(CLParser.NFS_SERVICE);
				registeredServices.add(CLParser.HTTP_SERVICE);
				registeredServices.add(CLParser.VIDEO_SERVICE);
				registeredServices.add(CLParser.ALARM_SERVICE);
			}else{
				registeredServices.add(service);	
			}
		}
		
		public List<String> getHeaderCols(String stub){
			if(stub.equals(Keyword.CIFS)){
				return propertyLists.getCifsPoperties();
			}
			else if(stub.equals(Keyword.MAPI)){
				return propertyLists.getMapiPoperties();
			}
			else if(stub.equals(Keyword.NFS)){
				return propertyLists.getNfsPoperties();
			}
			else if(stub.equals(Keyword.HTTP)){
				return propertyLists.getHttpPoperties();
			}
			else if(stub.equals(Keyword.VIDEO)){
				return propertyLists.getVideoPoperties();
			}
			else if(stub.equals(Keyword.ALL_TRAFFIC) || stub.equals(Keyword.APP_TRAFFIC)){
				return propertyLists.getTrafficPoperties();
			}
			else if(stub.equals(Keyword.ALARM)){
				return propertyLists.getAlarmProperties();
			}
			else{
				return new ArrayList<>();
			}
		}
		

		public int getTotalWaes() {
			return totalWaes;
		}

		public void setTotalWaes(int totalWaes) {
			this.totalWaes = totalWaes;
		}
		
		public int getWaesInOneRequest() {
			return waesInOneRequest>totalWaes?totalWaes:waesInOneRequest;
		}

		public void setWaesInOneRequest(int waes) {
			this.waesInOneRequest = waes;
		}

		public Calendar getStartTime() {
			return startTime;
		}

		public void setStartTime(Calendar startTime) {
			this.startTime = startTime;
		}

		public Calendar getEndTime() {
			return endTime;
		}

		public void setEndTime(Calendar endTime) {
			this.endTime = endTime;
		}

		public boolean isTraffic_services() {
			return traffic_services;
		}

		public void setTraffic_services(boolean traffic_services) {
			this.traffic_services = traffic_services;
		}

		public boolean isHttp_services() {
			return http_services;
		}

		public void setHttp_services(boolean http_services) {
			this.http_services = http_services;
		}

		public boolean isMapi_services() {
			return mapi_services;
		}

		public void setMapi_services(boolean mapi_services) {
			this.mapi_services = mapi_services;
		}

		public boolean isCifs_services() {
			return cifs_services;
		}

		public void setCifs_services(boolean cifs_services) {
			this.cifs_services = cifs_services;
		}

		public boolean isNfs_services() {
			return nfs_services;
		}

		public void setNfs_services(boolean nfs_services) {
			this.nfs_services = nfs_services;
		}

		public boolean isVideo_services() {
			return video_services;
		}

		public void setVideo_services(boolean video_services) {
			this.video_services = video_services;
		}

		public boolean isAll_services() {
			return all_services;
		}

		public void setAll_services(boolean all_services) {
			this.all_services = all_services;
		}

		public int getStatHistory() {
			return statHistory;
		}

		public void setStatHistory(int statHistory) {
			this.statHistory = statHistory;
		}

		public String getWaasURL(){
			return protocol+"://" + host + ":" +port + "/" + waasURL;
		}
		
		
		public String getMode() {
			return Mode;
		}

		public void setMode(String mode) {
			Mode = mode;
		}

		
		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public String getLogin() {
			return login;
		}

		public void setLogin(String login) {
			this.login = login;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getProtocol() {
			return protocol;
		}

		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}

		public String getPort() {
			return port;
		}

		public void setPort(String port) {
			this.port = port;
		}

		public String getAr() {
			return ar;
		}

		public void setAr(String ar) {
			this.ar = ar;
		}

		public String getSTAT_TYPE() {
			return STAT_TYPE;
		}

		public void setSTAT_TYPE(String sTAT_TYPE) {
			STAT_TYPE = sTAT_TYPE;
		}

		public ExecutorService getExService() {
			return exService;
		}

		public String getWaasConfDirectory_() {
			return waasConfDirectory_;
		}

		public void setWaasConfDirectory_(String waasConfDirectory_) {
			this.waasConfDirectory_ = waasConfDirectory_;
		}

		public String getDeviceConfURL() {
			return getWaasURL() + "/" + deviceConfURL;
		}

		public void setDeviceConfURL(String deviceConfURL) {
			this.deviceConfURL = deviceConfURL;
		}

		public String getTrafficURL() {
			return getWaasURL() + "/" +  trafficURL;
		}

		public void setTrafficURL(String trafficURL) {
			this.trafficURL = trafficURL;
		}

		public String getCifsURL() {
			return getWaasURL() + "/" +  cifsURL;
		}

		public void setCifsURL(String cifsURL) {
			this.cifsURL = cifsURL;
		}

		public String getHttpURL() {
			return  getWaasURL() + "/" + httpURL;
		}

		public void setHttpURL(String httpURL) {
			this.httpURL = httpURL;
		}

		public String getMapiURL() {
			return getWaasURL() + "/" +  mapiURL;
		}

		public void setMapiURL(String mapiURL) {
			this.mapiURL = mapiURL;
		}

		public String getDeviceStatusURL() {
			return getWaasURL() + "/" +  deviceStatusURL;
		}

		public void setDeviceStatusURL(String deviceStatusURL) {
			this.deviceStatusURL = deviceStatusURL;
		}
		
		public String getNfsURL() {
			return getWaasURL() + "/" + nfsURL;
		}

		public void setNfsURL(String nfsURL) {
			this.nfsURL = nfsURL;
		}

		public String getVideoURL() {
			return getWaasURL() + "/" + videoURL;
		}
		
		public void setVideoURL(String videoURL) {
			this.videoURL =  videoURL;
		}
		
		public String getAlarmURL() {
			return getWaasURL() + "/" +alarmURL;
		}
		
		public void setAlarmURL(String alarmURL) {
			this.alarmURL = alarmURL;
		}
		
		public int getWaasTimeout() {
			return waasTimeout;
		}

		public void setWaasTimeout(int waasTimeout) {
			this.waasTimeout = waasTimeout;
		}

		public String[] getTrafficType() {
			return trafficType;
		}

		public String[] getDirections() {
			return directions;
		}

		public void setTrafficType(String[] trafficType) {
			this.trafficType = trafficType;
		}

		public void setDirections(String[] directions) {
			this.directions = directions;
		}

		public List<String> getRegisteredServices() {
			return registeredServices;
		}

		public String getTimeStamp() {
			return timeStamp;
		}

		public boolean isRetAppTrafficStats() {
			return retAppTrafficStats;
		}

		public void setRetAppTrafficStats(boolean retAppTrafficStats) {
			this.retAppTrafficStats = retAppTrafficStats;
		}

		public boolean isFollowlastRunMechanism() {
			return followlastRunMechanism;
		}

		public void setFollowlastRunMechanism(boolean followlastRunMechanism) {
			this.followlastRunMechanism = followlastRunMechanism;
		}

		public int getDataMaxHistory() {
			return dataMaxHistory;
		}

		public void setDataMaxHistory(int dataMaxHistory) {
			this.dataMaxHistory = dataMaxHistory;
		}

		public int getMaxHoursInRequest() {
			return maxHoursInRequest;
		}

		public void setMaxHoursInRequest(int maxHoursInRequest) {
			this.maxHoursInRequest = maxHoursInRequest;
		}

		public String getOutput_folder() {
			return output_folder;
		}

		public void setOutput_folder(String output_folder) {
			this.output_folder = output_folder;
			FileNameRetriever.setOutputFolder(output_folder);
		}
		
		public void clearServices(){
			registeredServices = new ArrayList<>();
		}

		public boolean isAlarm_services() {
			return alarm_services;
		}

		public void setAlarm_services(boolean alarm_services) {
			this.alarm_services = alarm_services;
		}

		public boolean isProcessingTopo() {
			return processingTopo;
		}

		public void setProcessingTopo(boolean processingTopo) {
			this.processingTopo = processingTopo;
		}
}



