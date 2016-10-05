package com.infovista.sdk.stub.eventNStatusStats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

import com.cisco.waas.wsc.AlarmStatusStub;
import com.cisco.waas.wsc.AlarmStatusStub.Alarm;
import com.cisco.waas.wsc.AlarmStatusStub.RetrieveAllAlarmsResponse;
import com.cisco.waas.wsc.TrafficStatsStub.RetrieveAppTrafficStatsResponse;
import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.common.classes.*;
import com.infovista.common.interfaces.I_Result;
import com.infovista.configs.CommonConfig;
import com.infovista.fileutils.CsvWriter;
import com.infovista.fileutils.FileNameRetriever;
import com.infovista.instances.Keyword;
import com.infovista.sdk.error.WAASErrorManager;
import com.infovista.statsRetriever.AbstractStatsProcessor;

public class AlarmStub extends AbstractStatsProcessor{
	
	private Map<String, Map<Long, I_Result>> resultMap;
	private AlarmStatusStub alarmStatsStub;
	private final static String colArray[] = {"#Device-IP","Device-Status", "Alarm-Id" ,"Alarm-Name", "Alarm-Severity", "Alarm-Descripition"};
	
	// Constructor
	public AlarmStub(String p_configDir,String p_url,String p_userName,String p_password,long p_timeout){
		resultMap = new TreeMap<>(new IPStringComparator());
		try {
			synchronized (WAASErrorManager.instance().getMonitor()){
				URL repURL=this.getClass().getClassLoader().getResource("com/infovista/sdk/rampart/");
				ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromURIs(null,repURL);
					alarmStatsStub = new AlarmStatusStub(ctx, p_url);
					alarmStatsStub._getServiceClient().getOptions().setTimeOutInMilliSeconds(p_timeout);
					alarmStatsStub._getServiceClient().engageModule("rampart");
					alarmStatsStub._getServiceClient().getOptions().setProperty(
							RampartMessageData.KEY_RAMPART_POLICY,
							loadPolicy());
					alarmStatsStub._getServiceClient().getOptions().setUserName(p_userName);
					alarmStatsStub._getServiceClient().getOptions().setPassword(p_password);
					
					
			}
		} catch (AxisFault exception) {
			CiscoWAASDiscoverer.LOGGER.info("The Alarm Status stub could not be initialized :: " + exception.getMessage());
		} catch (FileNotFoundException exception) {
			CiscoWAASDiscoverer.LOGGER.info("The Alarm Status stub could not be initialized :: " + exception.getMessage());
		} catch (XMLStreamException exception) {
			CiscoWAASDiscoverer.LOGGER.info("The Alarm Status stub could not be initialized :: " + exception.getMessage());
		}	
		
	}
	
	@Override
	protected void initTimeFrame(TimeSlot timeSlot) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void updateTimeResult() {
		timeResult.setServiceName(getServiceName());
		timeResult.setLastTimeStamp(subTimeSlot.getEndTime().getTimeInMillis());
	}
	
	@Override
	public void writeStats() {
		writeAlarmStats();
	}

	@Override
	protected void cleanup() {
		resultMap = new HashMap<>();
	}
	
	@Override
	public String getServiceName() {
		return Keyword.ALARM;
	}
	
	//Constructor
	public AlarmStub(String p_configDir,String p_url,String p_userName,String p_password,long p_timeout, boolean isDataCompressionEnabled){
		this(p_configDir,p_url, p_userName, p_password,p_timeout);
		try {
				if(alarmStatsStub!=null){
					if(isDataCompressionEnabled){
						enableDataCompression();
					}
				}
		}catch (Exception exception) {
			CiscoWAASDiscoverer.LOGGER.info("The traffic stub could not be initialized :: " + exception.getMessage());
		} 
		
	}
	
	@Override
	public void retrieveStats() {
		if(istimeStampValid(getServiceName())){
			requestAlarmStats();
		}else{
			CiscoWAASDiscoverer.LOGGER.info("Stats for timstamp :: " + subTimeSlot.toString() + " has already been calculated for service :: " + getServiceName());
		}
	}
	
	private void requestAlarmStats(){
		Alarm[] alarmStats = getAlarmStatusArray();
		
		if(alarmStats.length>0){
			for(Alarm alarm : alarmStats){
				if(alarm!=null){
					String deviceIP = alarm.getDeviceIpAddress();
					Long alarmID = alarm.getAlarmId();
					I_Result result = new AlarmStatusResult();	
					result.setProperties(alarm);
					
					if(!resultMap.containsKey(deviceIP)){
						Map<Long, I_Result> idMap = new TreeMap<>();
						idMap.put(alarmID, result);
						resultMap.put(deviceIP, idMap);
					}else{
						resultMap.get(deviceIP).put(alarmID, result);
					}
				}else{
					CiscoWAASDiscoverer.LOGGER.info("The Alarm stats object obtained is empty");
				}
			}	
		}else{
			CiscoWAASDiscoverer.LOGGER.info("The Alarm stats array obtained is empty");
		}
	}
	
	//writing to file
	private void writeAlarmStats(){
		CsvWriter.checkAndWrite(FileNameRetriever.getAlarmStatusFileName(), getServiceName(), resultMap);
	}
	//retrieve alarm status array
	private Alarm[] getAlarmStatusArray(){
		try {
			CiscoWAASDiscoverer.LOGGER.debug("Sending request for alarm stats");
			RetrieveAllAlarmsResponse response = alarmStatsStub.retrieveAllAlarms();
			if(response!=null){
				OMElement omElement = response.getOMElement(RetrieveAllAlarmsResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
				FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("AlarmStatus_Response_" + subTimeSlot.getEndTimeStamp())), omElement.toString());
				CiscoWAASDiscoverer.LOGGER.debug("Response for Alarm stats returned and free memory left is :: " + Runtime.getRuntime().freeMemory());
				return response.get_return();
			}else{
				CiscoWAASDiscoverer.LOGGER.info("Unable to get  Alarm Stats ");
				return null;
			}
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Error while retrieveing Alarm Stats", e);
		}
		return null;
	}
		
	//Enable Data Compression	
	public void enableDataCompression() {
		alarmStatsStub._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_GZIP_REQUEST, Boolean.TRUE);
	}
	
	//Load Policy
	private Policy loadPolicy() throws FileNotFoundException, XMLStreamException{
		InputStream is=this.getClass().getClassLoader().getResourceAsStream("com/infovista/sdk/rampart/policy/policyWAAS.xml");
		StAXOMBuilder builder = new StAXOMBuilder(is);
		return PolicyEngine.getPolicy(builder.getDocumentElement());
	}

	
}
