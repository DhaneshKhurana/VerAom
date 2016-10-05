package com.infovista.sdk.stub.mapiStats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cisco.waas.wsc.MapiStatsStub.MapiClientConnCount;
import com.cisco.waas.wsc.MapiStatsStub.MapiDataReadStats;
import com.cisco.waas.wsc.MapiStatsStub.MapiRequestTypeStats;
import com.cisco.waas.wsc.MapiStatsStub.MapiResponseStats;
import com.cisco.waas.wsc.MapiStatsStub.MapiSessionCount;
import com.infovista.common.classes.PropertyLists;
import com.infovista.common.interfaces.*;
import com.infovista.configs.CommonConfig;

public class MapiStatResult implements I_Result {

	private Map<String, String> resultMap;
	
	public MapiStatResult(){
		resultMap = new HashMap<>();
	}
	
	@Override
	public String[] getResultArray() {
		String[] result = new String[PropertyLists.getInstance().getMapiPoperties().size()];
		int i=0;
		for(String property : PropertyLists.getInstance().getMapiPoperties()){
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
		if(obj instanceof MapiResponseStats){
			return setProperties((MapiResponseStats)obj);
		}
		else if(obj instanceof MapiDataReadStats){
			return setProperties((MapiDataReadStats)obj);
		}
		else if(obj instanceof MapiClientConnCount){
			return setProperties((MapiClientConnCount)obj);
		}
		else if (obj instanceof MapiRequestTypeStats){
			return setProperties((MapiRequestTypeStats)obj);
		}
		else if(obj instanceof MapiSessionCount){
			return setProperties((MapiSessionCount)obj);
		}
		
		return this;
	}
	
	public I_Result setProperties (MapiResponseStats stats){
		resultMap.put(PropertyLists.LOCAL_RESPONSE, String.valueOf(stats.getLocalResp()));
		resultMap.put(PropertyLists.REMOTE_RESPONSE, String.valueOf(stats.getRemoteResp()));
		resultMap.put(PropertyLists.AVG_LOCAL_RESP, String.valueOf(stats.getAvgLocalResp()));
		resultMap.put(PropertyLists.AVG_REMOTE_RESP, String.valueOf(stats.getAvgRemoteResp()));
		return this;
		
	}
	
	public I_Result setProperties (MapiDataReadStats stats){
		resultMap.put(PropertyLists.AVG_READ_AHEAD, String.valueOf(stats.getAvgReadAhead()));
		resultMap.put(PropertyLists.AVG_READ_STREAM, String.valueOf(stats.getAvgReadStream()));
		
		
		return this;
	}
	
	public I_Result setProperties (MapiClientConnCount stats){
		resultMap.put(PropertyLists.CLIENT2K, String.valueOf(stats.getClient2K()));
		resultMap.put(PropertyLists.CLIENT2K3, String.valueOf(stats.getClient2K3()));
		resultMap.put(PropertyLists.CLIENT2K7, String.valueOf(stats.getClient2K7()));

		
		return this;
	}
	
	public I_Result setProperties(MapiRequestTypeStats stats){
		resultMap.put(PropertyLists.HIGHER_THAN_2KCONN, String.valueOf(stats.getHigherthan2K7Connections()));
		resultMap.put(PropertyLists.LOWER_THAN_2KCONN, String.valueOf(stats.getLowerthan2KConnections()));
		resultMap.put(PropertyLists.SECURE_CONN, String.valueOf(stats.getSecuredConnections()));

		
		return this;
	}
	
	public I_Result setProperties (MapiSessionCount stats){
		resultMap.put(PropertyLists.SESSION_COUNT, String.valueOf(stats.getSessioncount()));
		return this;
	}
	

	/*public long getAverageLocalResp() {
		return avgLocalResp;
	}

	public void setAvgLocalResp(long avgLocalResp) {
		this.avgLocalResp = avgLocalResp;
	}

	public long getAverageReadAhead() {
		return avgReadAhead;
	}

	public void setAvgReadAhead(long avgReadAhead) {
		this.avgReadAhead = avgReadAhead;
	}

	public long getAverageReadStream() {
		return avgReadStream;
	}

	public void setAvgReadStream(long avgReadStream) {
		this.avgReadStream = avgReadStream;
	}

	public long getAverageRemoteResp() {
		return avgRemoteResp;
	}

	public void setAvgRemoteResp(long avgRemoteResp) {
		this.avgRemoteResp = avgRemoteResp;
	}

	public long getAverageSyncGetBuffer() {
		return avgSyncGetBuffer;
	}

	public void setAvgSyncGetBuffer(long avgSyncGetBuffer) {
		this.avgSyncGetBuffer = avgSyncGetBuffer;
	}

	public long getClient2kConn() {
		return client2k;
	}

	public void setClient2k(long client2k) {
		this.client2k = client2k;
	}

	public long getClient2k3Conn() {
		return client2k3;
	}

	public void setClient2k3(long client2k3) {
		this.client2k3 = client2k3;
	}

	public long getClient2k7Conn() {
		return client2k7;
	}

	public void setClient2k7(long client2k7) {
		this.client2k7 = client2k7;
	}

	public long getHigherThan2kConn() {
		return higherThan2kConnections;
	}

	public void setHigherThan2kConn(long higherThan2kConnections) {
		this.higherThan2kConnections = higherThan2kConnections;
	}

	public long getLowerThan2kConn() {
		return lowerThan2kConnections;
	}

	public void setLowerThan2kConnections(long lowerThan2kConnections) {
		this.lowerThan2kConnections = lowerThan2kConnections;
	}

	public long getLocalResponse() {
		return localResp;
	}

	public void setLocalResp(long localResp) {
		this.localResp = localResp;
	}

	public long getRemoteResponse() {
		return remoteResp;
	}

	public void setRemoteResp(long remoteResp) {
		this.remoteResp = remoteResp;
	}

	public long getSecuredConnections() {
		return secureConnections;
	}

	public void setSecureConnections(long secureConnections) {
		this.secureConnections = secureConnections;
	}

	public long getSessionsCount() {
		return sessionCount;
	}

	public void setSessionsCount(long sessionCount) {
		this.sessionCount = sessionCount;
	}*/



}
