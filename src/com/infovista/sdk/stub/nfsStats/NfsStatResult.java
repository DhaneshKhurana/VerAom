package com.infovista.sdk.stub.nfsStats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cisco.waas.wsc.HttpStatsStub.HttpConnOptRate;
import com.cisco.waas.wsc.HttpStatsStub.HttpConnStats;
import com.cisco.waas.wsc.HttpStatsStub.HttpMaxConnReuseCount;
import com.cisco.waas.wsc.HttpStatsStub.HttpOptConnCount;
import com.cisco.waas.wsc.HttpStatsStub.HttpTotalConnCount;
import com.cisco.waas.wsc.NfsStatsStub.NfsReqTypeStats;
import com.cisco.waas.wsc.NfsStatsStub.NfsRespTypeStats;
import com.cisco.waas.wsc.NfsStatsStub.NfsSessionCount;
import com.cisco.waas.wsc.NfsStatsStub.NfsTypeStats;
import com.infovista.common.classes.PropertyLists;
import com.infovista.common.interfaces.*;

public class NfsStatResult implements I_Result {
	
	private long authflavor2Count;
	private long authflavor3Count;
	private long authflavorUnknown;
	private long avgLocalResp;
	private long avgRemoteResp;
	private long localResp;
	private long remoteResp;
	private long nfsv2Count;
	private long nfsv3Count;
	private long nfsv4Count;
	private long nfsUnknownCount;
	private int sessionCount;
	private Map<String, String> resultMap= new HashMap<>();
	
	
	@Override
	public String[] getResultArray(){
		String[] result = new String[PropertyLists.getInstance().getNfsPoperties().size()];
		int i=0;
		for(String property : PropertyLists.getInstance().getNfsPoperties()){
			if(resultMap.containsKey(property)){
				result[i++] = resultMap.get(property);	
			}else{
				result[i++] = "null";
			}
		}
		return result;	
	}

	@Override
	public <T  extends Object> I_Result setProperties(T obj) {
		if(obj instanceof NfsReqTypeStats){
			return setProperties((NfsReqTypeStats)obj);
		}
		else if(obj instanceof NfsRespTypeStats){
			return setProperties((NfsRespTypeStats)obj);
		}
		else if(obj instanceof NfsTypeStats){
			return setProperties((NfsTypeStats)obj);
		}
		else if(obj instanceof NfsSessionCount){
			return setProperties((NfsSessionCount)obj);
		}
		return this;
	}
	
	public I_Result setProperties(NfsReqTypeStats obj) {
		resultMap.put(PropertyLists.AUTH_FLAVOR2_COUNT, String.valueOf(obj.getAuthflavor2Count()));
		resultMap.put(PropertyLists.AUTH_FALVOR3_COUNT, String.valueOf(obj.getAuthflavor3Count()));
		resultMap.put(PropertyLists.AUTH_FLAVOR_UNKNOWN, String.valueOf(obj.getAuthflavorUnknown()));
		
		return this;
	}
	
	public I_Result setProperties(NfsRespTypeStats obj) {
		resultMap.put(PropertyLists.AVG_LOCAL_RESP, String.valueOf(obj.getAvgLocalResp()));		
		resultMap.put(PropertyLists.AVG_REMOTE_RESP, String.valueOf(obj.getAvgRemoteResp()));		
		resultMap.put(PropertyLists.LOCAL_RESPONSE, String.valueOf(obj.getLocalResp()));		
		resultMap.put(PropertyLists.REMOTE_RESPONSE, String.valueOf(obj.getRemoteResp()));		
		return this;
	}
	
	public I_Result setProperties(NfsTypeStats obj) {
		resultMap.put(PropertyLists.NFS_V2_COUNT, String.valueOf(obj.getNfsv2Count()));		
		resultMap.put(PropertyLists.NFS_V3_COUNT, String.valueOf(obj.getNfsv3Count()));		
		resultMap.put(PropertyLists.NFS_V4_COUNT, String.valueOf(obj.getNfsv4Count()));		
		resultMap.put(PropertyLists.NFS_UNKNOWN_COUNT, String.valueOf(obj.getNfsvUnknownCount()));		
		return this;
	}
	
	public I_Result setProperties(NfsSessionCount obj) {
		resultMap.put(PropertyLists.SESSION_COUNT, String.valueOf(obj.getSessioncount()));		
		return this;
	}
	

	public long getAuthflavor2Count() {
		return authflavor2Count;
	}



	public void setAuthflavor2Count(long authflavor2Count) {
		this.authflavor2Count = authflavor2Count;
	}



	public long getAuthflavor3Count() {
		return authflavor3Count;
	}



	public void setAuthflavor3Count(long authflavor3Count) {
		this.authflavor3Count = authflavor3Count;
	}



	public long getAuthflavorUnknown() {
		return authflavorUnknown;
	}



	public void setAuthflavorUnknown(long authflavorUnknown) {
		this.authflavorUnknown = authflavorUnknown;
	}



	public long getAvgLocalResp() {
		return avgLocalResp;
	}



	public void setAvgLocalResp(long avgLocalResp) {
		this.avgLocalResp = avgLocalResp;
	}



	public long getAvgRemoteResp() {
		return avgRemoteResp;
	}



	public void setAvgRemoteResp(long avgRemoteResp) {
		this.avgRemoteResp = avgRemoteResp;
	}



	public long getLocalResp() {
		return localResp;
	}



	public void setLocalResp(long localResp) {
		this.localResp = localResp;
	}



	public long getRemoteResp() {
		return remoteResp;
	}



	public void setRemoteResp(long remoteResp) {
		this.remoteResp = remoteResp;
	}



	public long getNfsv2Count() {
		return nfsv2Count;
	}



	public void setNfsv2Count(long nfsv2Count) {
		this.nfsv2Count = nfsv2Count;
	}



	public long getNfsv3Count() {
		return nfsv3Count;
	}



	public void setNfsv3Count(long nfsv3Count) {
		this.nfsv3Count = nfsv3Count;
	}



	public long getNfsv4Count() {
		return nfsv4Count;
	}



	public void setNfsv4Count(long nfsv4Count) {
		this.nfsv4Count = nfsv4Count;
	}



	public long getNfsUnknownCount() {
		return nfsUnknownCount;
	}



	public void setNfsUnknownCount(long nfsUnknownCount) {
		this.nfsUnknownCount = nfsUnknownCount;
	}



	public int getSessionCount() {
		return sessionCount;
	}



	public void setSessionCount(int sessionCount) {
		this.sessionCount = sessionCount;
	}




	
}
