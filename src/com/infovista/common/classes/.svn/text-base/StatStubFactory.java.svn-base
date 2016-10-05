package com.infovista.common.classes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.common.interfaces.I_StatsProcessor;
import com.infovista.configs.CLParser;
import com.infovista.configs.CommonConfig;
import com.infovista.instances.Keyword;
import com.infovista.sdk.ssl.FakeSSLProtocolSocketFactory;
import com.infovista.sdk.stub.cifsStats.CIFSStub;
import com.infovista.sdk.stub.eventNStatusStats.AlarmStub;
import com.infovista.sdk.stub.httpStats.HttpStub;
import com.infovista.sdk.stub.mapiStats.MapiStub;
import com.infovista.sdk.stub.nfsStats.NfsStub;
import com.infovista.sdk.stub.trafficStats.AllTrafficStats;
import com.infovista.sdk.stub.trafficStats.AppTrafficStats;
import com.infovista.sdk.stub.trafficStats.TrafficStub;
import com.infovista.sdk.stub.videoStats.VideoStub;
import com.infovista.statsRetriever.AbstractStatsProcessor;

public class StatStubFactory {
	private static String login;
	private static String password;
	private static String waasConfDirectory_;
	private static int waasTimeout;
	static boolean isDataCompressionEnabled = false;
	
	static{
		initialize();
		
	}
	
	private static void initialize(){
		login = CommonConfig.getInstance().getLogin();
		password = CommonConfig.getInstance().getPassword();
		waasConfDirectory_ = CommonConfig.getInstance().getWaasConfDirectory_();
		waasTimeout = CommonConfig.getInstance().getWaasTimeout();
		
		isDataCompressionEnabled = checkDataCompression();
	}
	
	private static boolean checkDataCompression(){
		if (AbstractStatsProcessor.checkDeviceStub()){
			return AbstractStatsProcessor.isSOAPCompressionSupported();
		}
		return false;
	}
	
	public static I_StatsProcessor getInstance(String service){
		try{
			String protocol = CommonConfig.getInstance().getProtocol();
			if(protocol.equals(Keyword.PROTO_HTTPS)){
				Protocol.unregisterProtocol(protocol);
				Protocol.registerProtocol(protocol , new Protocol(protocol, new FakeSSLProtocolSocketFactory(), 443));
			}
			
			if(service.equals(CLParser.CIFS_SERVICE)){
				CiscoWAASDiscoverer.LOGGER.error("Pinging url :: " +  CommonConfig.getInstance().getCifsURL() + " to initialize cifs Stub");
				return(new CIFSStub(waasConfDirectory_, CommonConfig.getInstance().getCifsURL(), login, password, waasTimeout, isDataCompressionEnabled));
			}
			else if(service.equals(CLParser.HTTP_SERVICE)){
				CiscoWAASDiscoverer.LOGGER.error("Pinging url :: " +  CommonConfig.getInstance().getHttpURL() + " to initialize http Stub");
				return(new HttpStub(waasConfDirectory_, CommonConfig.getInstance().getHttpURL(), login, password, waasTimeout, isDataCompressionEnabled));
			}
			else if(service.equals(CLParser.MAPI_SERVICE)){
				CiscoWAASDiscoverer.LOGGER.error("Pinging url :: " +  CommonConfig.getInstance().getMapiURL() + " to initialize mapi Stub");
				return(new MapiStub(waasConfDirectory_, CommonConfig.getInstance().getMapiURL(), login, password, waasTimeout,isDataCompressionEnabled));
			}
			else if(service.equals(CLParser.NFS_SERVICE)){
				CiscoWAASDiscoverer.LOGGER.error("Pinging url :: " +  CommonConfig.getInstance().getNfsURL() + " to initialize nfs Stub");
				return(new NfsStub(waasConfDirectory_,CommonConfig.getInstance().getNfsURL(), login, password, waasTimeout, isDataCompressionEnabled));
			}
			else if(service.equals(CLParser.VIDEO_SERVICE)){
				CiscoWAASDiscoverer.LOGGER.error("Pinging url :: " +  CommonConfig.getInstance().getVideoURL() + " to initialize video Stub");
				return(new VideoStub(waasConfDirectory_, CommonConfig.getInstance().getVideoURL(), login, password, waasTimeout, isDataCompressionEnabled));
			}
			else if(service.equals(CLParser.ALARM_SERVICE)){
				CiscoWAASDiscoverer.LOGGER.error("Pinging url :: " +  CommonConfig.getInstance().getAlarmURL() + " to initialize alarm Stub");
				return(new AlarmStub(waasConfDirectory_, CommonConfig.getInstance().getAlarmURL(), login, password, waasTimeout, isDataCompressionEnabled));
			}
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Exception occured while initializing stub ", e);
		}
		return null;
	}
	
		
	public static List<I_StatsProcessor> getAllInstance(String service){
		List<I_StatsProcessor> stubList = new ArrayList<I_StatsProcessor>();
		try{
			String protocol = CommonConfig.getInstance().getProtocol();
			if(protocol.equals(Keyword.PROTO_HTTPS)){
				Protocol.unregisterProtocol(protocol);
				Protocol.registerProtocol(protocol , new Protocol(protocol, new FakeSSLProtocolSocketFactory(), 443));
			}
			
			if(service.equals(CLParser.ALL_SERVICE)){
				CiscoWAASDiscoverer.LOGGER.error("Pinging urls :: " +  CommonConfig.getInstance().getNfsURL() + " " +CommonConfig.getInstance().getTrafficURL() + " "  + CommonConfig.getInstance().getCifsURL() + " " +CommonConfig.getInstance().getHttpURL() + " " +  CommonConfig.getInstance().getMapiURL() + " "  +   CommonConfig.getInstance().getAlarmURL() + CommonConfig.getInstance().getVideoURL() +  " to initialize all Stub");
				stubList.add(new AppTrafficStats(waasConfDirectory_, CommonConfig.getInstance().getTrafficURL(), login, password, waasTimeout, isDataCompressionEnabled));
				stubList.add(new AllTrafficStats(waasConfDirectory_, CommonConfig.getInstance().getTrafficURL(), login, password, waasTimeout, isDataCompressionEnabled));
				stubList.add(new CIFSStub(waasConfDirectory_, CommonConfig.getInstance().getCifsURL(), login, password, waasTimeout, isDataCompressionEnabled));
				stubList.add(new HttpStub(waasConfDirectory_, CommonConfig.getInstance().getHttpURL(), login, password, waasTimeout, isDataCompressionEnabled));
				stubList.add(new MapiStub(waasConfDirectory_, CommonConfig.getInstance().getMapiURL(), login, password, waasTimeout,isDataCompressionEnabled));
				stubList.add(new NfsStub(waasConfDirectory_,CommonConfig.getInstance().getNfsURL(), login, password, waasTimeout, isDataCompressionEnabled));
				stubList.add(new VideoStub(waasConfDirectory_, CommonConfig.getInstance().getVideoURL(), login, password, waasTimeout, isDataCompressionEnabled));
				stubList.add(new AlarmStub(waasConfDirectory_, CommonConfig.getInstance().getAlarmURL(), login, password, waasTimeout, isDataCompressionEnabled));
			}else if(service.equals(CLParser.TRAFFIC_SERVICE)){
				stubList.add(new AppTrafficStats(waasConfDirectory_, CommonConfig.getInstance().getTrafficURL(), login, password, waasTimeout, isDataCompressionEnabled));
				stubList.add(new AllTrafficStats(waasConfDirectory_, CommonConfig.getInstance().getTrafficURL(), login, password, waasTimeout, isDataCompressionEnabled));
			}
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Error occured while initilizing stubs :: ", e);
			System.exit(0);
		}
		return stubList;
	}
}
