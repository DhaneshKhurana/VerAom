package com.infovista.sdk.stub.httpStats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cisco.waas.wsc.HttpStatsStub.HttpConnOptRate;
import com.cisco.waas.wsc.HttpStatsStub.HttpConnStats;
import com.cisco.waas.wsc.HttpStatsStub.HttpMaxConnReuseCount;
import com.cisco.waas.wsc.HttpStatsStub.HttpOptConnCount;
import com.cisco.waas.wsc.HttpStatsStub.HttpTotalConnCount;
import com.infovista.common.classes.PropertyLists;
import com.infovista.common.interfaces.*;

public class HttpStatResult implements I_Result{
	
	private long reusedPeer;
	private long reusedConn_per_operation;
	private long idleConn_per_operation;
	private long fastConnectionSetupCount;
	private long maxReusedConn_per_operation;
	private double pct_rtt_saved;
	private long totalHandoff;
	private long totalLanConnections;
	private long maxReusedConnections;
	private long percentConnectionTimeSaved;
	private Map<String, String> resultMap = new HashMap<>();
	

	@Override
	public String[] getResultArray() {
		String[] result = new String[PropertyLists.getInstance().getHttpPoperties().size()];
		int i=0;
		for(String property : PropertyLists.getInstance().getHttpPoperties()){
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
		if(obj instanceof HttpConnStats){
			return setProperties((HttpConnStats)obj);
		}
		else if(obj instanceof HttpOptConnCount){
			return setProperties((HttpOptConnCount)obj);
		}
		else if(obj instanceof HttpTotalConnCount){
			return setProperties((HttpTotalConnCount)obj);
		}
		else if(obj instanceof HttpMaxConnReuseCount){
			return setProperties((HttpMaxConnReuseCount)obj);
		}
		else if(obj instanceof HttpConnOptRate){
			return setProperties((HttpConnOptRate)obj);
		}
		return this;
	}

	public I_Result setProperties(HttpConnStats obj) {
		resultMap.put(PropertyLists.REUSED_PEER, String.valueOf(obj.getReused_peer()));
		resultMap.put(PropertyLists.REUSED, String.valueOf(obj.getReused()));
		resultMap.put(PropertyLists.IDLE, String.valueOf(obj.getIdle()));
		resultMap.put(PropertyLists.MAX_REUSED, String.valueOf(obj.getMax_reused()));
		resultMap.put(PropertyLists.PCT_RTT_SAVED, String.valueOf(obj.getPct_rtt_saved()));
		resultMap.put(PropertyLists.TOTAL_HANDOFF, String.valueOf(obj.getTotal_handoff()));
		return this;
	}
	
	public I_Result setProperties(HttpOptConnCount obj) {
		resultMap.put(PropertyLists.FAST_CONNECTION_SETUP_COUNT, String.valueOf(obj.getFastConnectionSetupsCount()));
		return null;
	}
	
	public I_Result setProperties(HttpTotalConnCount obj) {
		resultMap.put(PropertyLists.TOTAL_LAN_CONN, String.valueOf(obj.getTotalLanConnectionCount()));
		return this;
	}
	
	public I_Result setProperties(HttpMaxConnReuseCount obj) {
		resultMap.put(PropertyLists.MAX_REUSED_CONN, String.valueOf(obj.getMaxReuseCount()));
		return this;
	}
	
	public I_Result setProperties(HttpConnOptRate obj) {
		resultMap.put(PropertyLists.PERCENT_CONN_TIME_SAVED, String.valueOf(obj.getPercentConnectionTimeSaved()));
		return this;
	}
	
	public long getReusedPeer() {
		return reusedPeer;
	}

	public void setReusedPeer(long reusedPeer) {
		this.reusedPeer = reusedPeer;
	}

	public long getReusedConn_per_operation() {
		return reusedConn_per_operation;
	}

	public void setReusedConn_per_operation(long reusedConn_per_operation) {
		this.reusedConn_per_operation = reusedConn_per_operation;
	}

	public long getIdleConn_per_operation() {
		return idleConn_per_operation;
	}

	public void setIdleConn_per_operation(long idleConn_per_operation) {
		this.idleConn_per_operation = idleConn_per_operation;
	}

	public long getFastConnectionSetupCount() {
		return fastConnectionSetupCount;
	}

	public void setFastConnectionSetupCount(long fastConnectionSetupCount) {
		this.fastConnectionSetupCount = fastConnectionSetupCount;
	}

	public long getMaxReusedConn_per_operation() {
		return maxReusedConn_per_operation;
	}

	public void setMaxReusedConn_per_operation(long maxReusedConn_per_operation) {
		this.maxReusedConn_per_operation = maxReusedConn_per_operation;
	}

	public double getPct_rtt_saved() {
		return pct_rtt_saved;
	}

	public void setPct_rtt_saved(double pct_rtt_saved) {
		this.pct_rtt_saved = pct_rtt_saved;
	}

	public long getTotalHandoff() {
		return totalHandoff;
	}

	public void setTotalHandoff(long totalHandoff) {
		this.totalHandoff = totalHandoff;
	}

	public long getTotalLanConnections() {
		return totalLanConnections;
	}

	public void setTotalLanConnections(long totalLanConnections) {
		this.totalLanConnections = totalLanConnections;
	}

	public long getMaxReusedConnections() {
		return maxReusedConnections;
	}

	public void setMaxReusedConnections(long maxReusedConnections) {
		this.maxReusedConnections = maxReusedConnections;
	}

	public long getPercentConnectionTimeSaved() {
		return percentConnectionTimeSaved;
	}

	public void setPercentConnectionTimeSaved(long percentConnectionTimeSaved) {
		this.percentConnectionTimeSaved = percentConnectionTimeSaved;
	}


}
