package com.infovista.sdk.stub.cifsStats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cisco.waas.wsc.CIFSStatsStub.CacheCountStats;
import com.cisco.waas.wsc.CIFSStatsStub.CacheUtilizationStats;
import com.cisco.waas.wsc.CIFSStatsStub.DiskCapacityStats;
import com.cisco.waas.wsc.CIFSStatsStub.HitRateStats;
import com.cisco.waas.wsc.CIFSStatsStub.RequestCountStats;
import com.cisco.waas.wsc.CIFSStatsStub.SessionCountStats;
import com.cisco.waas.wsc.HttpStatsStub.HttpConnOptRate;
import com.cisco.waas.wsc.HttpStatsStub.HttpConnStats;
import com.cisco.waas.wsc.HttpStatsStub.HttpMaxConnReuseCount;
import com.cisco.waas.wsc.HttpStatsStub.HttpOptConnCount;
import com.cisco.waas.wsc.HttpStatsStub.HttpTotalConnCount;
import com.infovista.common.classes.PropertyLists;
import com.infovista.common.interfaces.*;

public class CIFSStatResult implements I_Result{

	private Map<String, String> resultMap;
	
	public CIFSStatResult(){
		resultMap = new HashMap<>();
	}
	
	@Override
	public String[] getResultArray() {
		String[] result = new String[PropertyLists.getInstance().getCifsPoperties().size()];
		int i=0;
		for(String property : PropertyLists.getInstance().getCifsPoperties()){
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
		if(obj instanceof DiskCapacityStats){
			return setProperties((DiskCapacityStats)obj);
		}
		else if(obj instanceof SessionCountStats){
			return setProperties((SessionCountStats)obj);
		}
		else if(obj instanceof RequestCountStats){
			return setProperties((RequestCountStats)obj);
		}
		else if(obj instanceof CacheCountStats){
			return setProperties((CacheCountStats)obj);
		}
		else if(obj instanceof CacheUtilizationStats){
			return setProperties((CacheUtilizationStats)obj);
		}
		else if(obj instanceof HitRateStats){
			return setProperties((HitRateStats)obj);
		}
		return this;
	}
	
	public I_Result setProperties(DiskCapacityStats obj) {
		resultMap.put(PropertyLists.DISK_CAPACITY, String.valueOf(obj.getDiskcapacity()));
		return this;
	}
	
	public I_Result setProperties(SessionCountStats obj) {
		resultMap.put(PropertyLists.SESSION_COUNT, String.valueOf(obj.getSessioncount()));

		return this;
	}
	
	public I_Result setProperties(RequestCountStats obj) {
		resultMap.put(PropertyLists.REQUEST_COUNT, String.valueOf(obj.getRequestcount()));

		return this;
	}
	
	public I_Result setProperties(CacheCountStats obj) {
		resultMap.put(PropertyLists.CACHE_COUNT, String.valueOf(obj.getCachecount()));

		return this;
	}
	
	public I_Result setProperties(CacheUtilizationStats obj) {
		resultMap.put(PropertyLists.RESOURCE_UTILIZATION, String.valueOf(obj.getResourceutilization()));
		return this;
	}
	
	public I_Result setProperties(HitRateStats obj) {
		resultMap.put(PropertyLists.HIT_RATE, String.valueOf(obj.getHitrate()));

		return this;
	}
	
/*	private int getDiskCapacity() {
		return diskCapacity;
	}
	private int getCacheCount() {
		return cacheCount;
	}
	private int getCacheUtilization() {
		return cacheUtilization;
	}
	private int getFileCount() {
		return fileCount;
	}
	private int getRequestCount() {
		return requestCount;
	}
	private int getHitRate() {
		return hitRate;
	}
	private int getSessionCount() {
		return sessionCount;
	}*/
	
/*	public void setDiskCapacity(int diskCapacity) {
		this.diskCapacity = diskCapacity;
	}
	public void setCacheCount(int cacheCount) {
		this.cacheCount = cacheCount;
	}
	public void setCacheUtilization(int cacheUtilization) {
		this.cacheUtilization = cacheUtilization;
	}
	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}
	public void setRequestCount(int requestCount) {
		this.requestCount = requestCount;
	}
	public void setHitRate(int hitRate) {
		this.hitRate = hitRate;
	}
	public void setSessionCount(int sessionCount) {
		this.sessionCount = sessionCount;
	}
*/	
}