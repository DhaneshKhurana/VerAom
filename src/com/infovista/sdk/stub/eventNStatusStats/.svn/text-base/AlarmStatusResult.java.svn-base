package com.infovista.sdk.stub.eventNStatusStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cisco.waas.wsc.AlarmStatusStub.Alarm;
import com.cisco.waas.wsc.HttpStatsStub.HttpConnOptRate;
import com.cisco.waas.wsc.HttpStatsStub.HttpConnStats;
import com.cisco.waas.wsc.HttpStatsStub.HttpMaxConnReuseCount;
import com.cisco.waas.wsc.HttpStatsStub.HttpOptConnCount;
import com.cisco.waas.wsc.HttpStatsStub.HttpTotalConnCount;
import com.infovista.common.classes.PropertyLists;
import com.infovista.common.interfaces.*;

public class AlarmStatusResult implements I_Result{
	
	private String deviceIp;
	private String deviceStatus;
	private long alarmId;
	private String alarmSeverity;
	private String alarmName;
	private String alarmdesciprion;
	private Map<String, String> resultMap;
	
	public AlarmStatusResult(){
		resultMap = new HashMap<>();
	}
	
	@Override
	public String[] getResultArray() {
		String[] result = new String[PropertyLists.getInstance().getAlarmProperties().size()];
		int i=0;
		for(String property : PropertyLists.getInstance().getAlarmProperties()){
			if(resultMap.containsKey(property)){
				result[i++] = resultMap.get(property);	
			}else{
				result[i++] = "null";
			}
		}
		return result;
	}
	
	@Override
	public <T extends Object> I_Result setProperties(T obj) {
		if(obj instanceof Alarm){
			return setProperties((Alarm)obj);
		}
		return this;
	}
	
	public I_Result setProperties(Alarm obj) {
		resultMap.put(PropertyLists.DEVICE_IP, String.valueOf(obj.getDeviceIpAddress()));
		resultMap.put(PropertyLists.ALARM_ID, String.valueOf(obj.getAlarmId()));
		resultMap.put(PropertyLists.ALARM_NAME, String.valueOf(obj.getAlarmName()));
		resultMap.put(PropertyLists.ALARM_SEVERITY, String.valueOf(obj.getSeverity()));
		resultMap.put(PropertyLists.ALARM_DESCRIPTION, String.valueOf(obj.getDescription()));
		return this;
	}
	
	private String getDeviceIp() {
		return deviceIp;
	}
	private String getDeviceStatus() {
		return deviceStatus;
	}
	private long getAlarmId() {
		return alarmId;
	}
	private String getAlarmSeverity() {
		return alarmSeverity;
	}
	private String getAlarmName() {
		return alarmName;
	}
	private String getAlarmdesciprion() {
		return alarmdesciprion;
	}
	
	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}
	public void setDeviceStatus(String deviceStatus) {
		this.deviceStatus = deviceStatus;
	}
	public void setAlarmId(long alarmId) {
		this.alarmId = alarmId;
	}
	public void setAlarmSeverity(String alarmSeverity) {
		this.alarmSeverity = alarmSeverity;
	}
	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}
	public void setAlarmdesciprion(String alarmdesciprion) {
		this.alarmdesciprion = alarmdesciprion;
	}


	
}
