package com.infovista.sdk.stub.videoStats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cisco.waas.wsc.CIFSStatsStub.DiskCapacityStats;
import com.cisco.waas.wsc.HttpStatsStub.HttpConnOptRate;
import com.cisco.waas.wsc.HttpStatsStub.HttpConnStats;
import com.cisco.waas.wsc.HttpStatsStub.HttpMaxConnReuseCount;
import com.cisco.waas.wsc.HttpStatsStub.HttpOptConnCount;
import com.cisco.waas.wsc.HttpStatsStub.HttpTotalConnCount;
import com.cisco.waas.wsc.VideoStatsStub.VideoStats;
import com.infovista.common.classes.PropertyLists;
import com.infovista.common.interfaces.*;
import com.infovista.statsRetriever.AbstractStatsProcessor;

public class VideoStatResult implements I_Result{

	private long acceleratedConnections;
	private long incomingBytesTotal;
	private long outgoingBytesTotal;
	private long errorConnections;
	private long recievedConnections;
	private long unAcceleratedConnections;
	private Map<String, String> resultMap = new HashMap<>();
	
	@Override
	public String[] getResultArray() {
		String[] result = new String[PropertyLists.getInstance().getVideoPoperties().size()];
		int i=0;
		for(String property : PropertyLists.getInstance().getVideoPoperties()){
			if(resultMap.containsKey(property)){
				result[i++] = resultMap.get(property);	
			}else{
				result[i++] = "null";
			}
		}
		return result;
	}
	
	@Override
	public <T> I_Result setProperties(T obj) {
		if(obj instanceof VideoStats){
			return setProperties((VideoStats)obj);
		}
		return this;
	}
	
	public I_Result setProperties(VideoStats obj) {
		resultMap.put(PropertyLists.DISK_CAPACITY, AbstractStatsProcessor.getIPFromName(obj.getDeviceName()));
		resultMap.put(PropertyLists.DISK_CAPACITY, String.valueOf(obj.getTimestamp().getTimeInMillis()));
		resultMap.put(PropertyLists.DISK_CAPACITY, String.valueOf(obj.getAcceleratedconnections()));
		resultMap.put(PropertyLists.DISK_CAPACITY, String.valueOf(obj.getIncomingbytesttotal()));
		resultMap.put(PropertyLists.DISK_CAPACITY, String.valueOf(obj.getOutgoingbytestotal()));
		resultMap.put(PropertyLists.DISK_CAPACITY, String.valueOf(obj.getErrorconnections()));
		resultMap.put(PropertyLists.DISK_CAPACITY, String.valueOf(obj.getUnacceleratedconnections()));

		return this;
	}

	public long getAcceleratedConnections() {
		return acceleratedConnections;
	}

	public void setAcceleratedConnections(long acceleratedConnections) {
		this.acceleratedConnections = acceleratedConnections;
	}

	public long getIncomingBytesTotal() {
		return incomingBytesTotal;
	}

	public void setIncomingBytesTotal(long incomingBytesTotal) {
		this.incomingBytesTotal = incomingBytesTotal;
	}

	public long getOutgoingBytesTotal() {
		return outgoingBytesTotal;
	}

	public void setOutgoingBytesTotal(long outgoingBytesTotal) {
		this.outgoingBytesTotal = outgoingBytesTotal;
	}

	public long getErrorConnections() {
		return errorConnections;
	}

	public void setErrorConnections(long errorConnections) {
		this.errorConnections = errorConnections;
	}

	public long getRecievedConnections() {
		return recievedConnections;
	}

	public void setRecievedConnections(long recievedConnections) {
		this.recievedConnections = recievedConnections;
	}

	public long getUnAcceleratedConnections() {
		return unAcceleratedConnections;
	}

	public void setUnAcceleratedConnections(long unAcceleratedConnections) {
		this.unAcceleratedConnections = unAcceleratedConnections;
	}

}
