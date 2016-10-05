package com.infovista.sdk.stub.trafficStats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cisco.waas.wsc.TrafficStatsStub.TrafficStats;
import com.infovista.common.classes.PropertyLists;
import com.infovista.common.interfaces.*;
import com.infovista.statsRetriever.AbstractStatsProcessor;

public class TrafficStatResult implements I_Result{
	
	private String deviceIp;
	private long timeStamp;
	private String frequency;
	private long compressedIn;
	private long compressedOut;
	private long unCompressedIn;
	private long unCompressedOut;
	private long passthroughintermediatein;
	private long passthroughotherout;
	private long passthroughoverloadin;
	private long passthroughoverloadout;
	private long passthroughpeerin;
	private long passthroughpeerout;
	private long passthroughpolicyin;
	private long passthroughpolicyout;
	private String getPassthroughpolicyout;
	private Map<String, String> resultMap;
	
	public TrafficStatResult(){
		resultMap = new HashMap<>();
	}
	
	@Override
	public String[] getResultArray() {
		String[] result = new String[PropertyLists.getInstance().getTrafficPoperties().size()];
		int i=0;
		for(String property : PropertyLists.getInstance().getTrafficPoperties()){
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
		if(obj instanceof TrafficStats){
			return setProperties((TrafficStats)obj);
		}
		return this;
	}
	
	public I_Result setProperties(TrafficStats obj) {
		resultMap.put(PropertyLists.DEVICE_IP, AbstractStatsProcessor.getIPFromName(obj.getDeviceName()));
		resultMap.put(PropertyLists.TIMESTAMP, String.valueOf(obj.getEndtime().getTimeInMillis()));
		resultMap.put(PropertyLists.COMPRESSED_IN, String.valueOf(obj.getCompressedin()));
		resultMap.put(PropertyLists.COMPRESSED_OUT, String.valueOf(obj.getCompressedout()));
		resultMap.put(PropertyLists.UNCOMPRESSED_IN, String.valueOf(obj.getUncompressedin()));
		resultMap.put(PropertyLists.UNCOMPRESSED_OUT, String.valueOf(obj.getUncompressedout()));
		resultMap.put(PropertyLists.PASSTHROUGH_INTERMEDIATE_IN, String.valueOf(obj.getPassthroughintermediatein()));
		resultMap.put(PropertyLists.PASSTHROUGH_OTHER_OUT, String.valueOf(obj.getPassthroughotherout()));
		resultMap.put(PropertyLists.PASSTHROUGH_OVERLOAD_IN, String.valueOf(obj.getPassthroughoverloadin()));
		resultMap.put(PropertyLists.PASSTHROUGH_OVERLOAD_OUT, String.valueOf(obj.getPassthroughoverloadout()));
		resultMap.put(PropertyLists.PASSTHROUGH_PEER_IN, String.valueOf(obj.getPassthroughpeerin()));
		resultMap.put(PropertyLists.PASSTHORUGH_PEER_OUT, String.valueOf(obj.getPassthroughpeerout()));
		resultMap.put(PropertyLists.PASSTHORUGH_POLICY_IN, String.valueOf(obj.getPassthroughpolicyin()));
		resultMap.put(PropertyLists.PASSTHORUGH_POLICY_OUT, String.valueOf(obj.getPassthroughpolicyout()));
		resultMap.put(PropertyLists.FREQUENCY, obj.getFrequency());
		return this;
	}


	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
		resultMap.put(PropertyLists.DEVICE_IP, deviceIp);

	}


	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
		resultMap.put(PropertyLists.TIMESTAMP, String.valueOf(timeStamp));
	}


	public void setCompressedIn(long compressedIn) {
		this.compressedIn = compressedIn;
		resultMap.put(PropertyLists.COMPRESSED_IN, String.valueOf(compressedIn));
	}


	public void setCompressedOut(long compressedOut) {
		this.compressedOut = compressedOut;
		resultMap.put(PropertyLists.COMPRESSED_OUT, String.valueOf(compressedOut));
	}


	public void setUnCompressedIn(long unCompressedIn) {
		this.unCompressedIn = unCompressedIn;
		resultMap.put(PropertyLists.UNCOMPRESSED_IN, String.valueOf(unCompressedIn));
	}


	public void setUnCompressedOut(long unCompressedOut) {
		this.unCompressedOut = unCompressedOut;
		resultMap.put(PropertyLists.UNCOMPRESSED_OUT, String.valueOf(unCompressedOut));
	}


	public void setPassthroughintermediatein(long passthroughintermediatein) {
		this.passthroughintermediatein = passthroughintermediatein;
		resultMap.put(PropertyLists.PASSTHROUGH_INTERMEDIATE_IN, String.valueOf(passthroughintermediatein));
	}


	public void setPassthroughotherout(long passthroughotherout) {
		this.passthroughotherout = passthroughotherout;
		resultMap.put(PropertyLists.PASSTHROUGH_OUT, String.valueOf(passthroughotherout));
	}


	public void setPassthroughoverloadin(long passthroughoverloadin) {
		this.passthroughoverloadin = passthroughoverloadin;
		resultMap.put(PropertyLists.PASSTHROUGH_OVERLOAD_IN, String.valueOf(passthroughoverloadin));
	}


	public void setPassthroughoverloadout(long passthroughoverloadout) {
		this.passthroughoverloadout = passthroughoverloadout;
		resultMap.put(PropertyLists.PASSTHROUGH_OVERLOAD_OUT, String.valueOf(passthroughoverloadout));

	}


	public void setPassthroughpeerin(long passthroughpeerin) {
		this.passthroughpeerin = passthroughpeerin;
		resultMap.put(PropertyLists.PASSTHROUGH_PEER_IN, String.valueOf(passthroughpeerin));

	}


	public void setPassthroughpeerout(long passthroughpeerout) {
		this.passthroughpeerout = passthroughpeerout;
		resultMap.put(PropertyLists.PASSTHORUGH_PEER_OUT, String.valueOf(passthroughpeerout));

	}


	public void setPassthroughpolicyin(long passthroughpolicyin) {
		this.passthroughpolicyin = passthroughpolicyin;
		resultMap.put(PropertyLists.PASSTHORUGH_POLICY_IN, String.valueOf(passthroughpolicyin));

	}


	public void setPassthroughpolicyout(long passthroughpolicyout) {
		this.passthroughpolicyout = passthroughpolicyout;
		resultMap.put(PropertyLists.PASSTHORUGH_POLICY_OUT, String.valueOf(passthroughpolicyout));

	}


	public void setGetPassthroughpolicyout(String getPassthroughpolicyout) {
		this.getPassthroughpolicyout = getPassthroughpolicyout;
		resultMap.put(PropertyLists.PASSTHORUGH_POLICY_OUT, String.valueOf(getPassthroughpolicyout));

	}


	private String getDeviceIp() {
		return deviceIp;
	}


	private long getTimeStamp() {
		return timeStamp;
	}


	private long getCompressedIn() {
		return compressedIn;
	}


	private long getCompressedOut() {
		return compressedOut;
	}


	private long getUnCompressedIn() {
		return unCompressedIn;
	}


	private long getUnCompressedOut() {
		return unCompressedOut;
	}


	private long getPassthroughintermediatein() {
		return passthroughintermediatein;
	}


	private long getPassthroughotherout() {
		return passthroughotherout;
	}


	private long getPassthroughoverloadin() {
		return passthroughoverloadin;
	}


	private long getPassthroughoverloadout() {
		return passthroughoverloadout;
	}


	private long getPassthroughpeerin() {
		return passthroughpeerin;
	}


	private long getPassthroughpeerout() {
		return passthroughpeerout;
	}


	private long getPassthroughpolicyin() {
		return passthroughpolicyin;
	}


	private long getPassthroughpolicyout() {
		return passthroughpolicyout;
	}


	private String getGetPassthroughpolicyout() {
		return getPassthroughpolicyout;
	}


	public String getFrequency() {
		return frequency;
	}


	public void setFrequency(String frequency) {
		this.frequency = frequency;
		resultMap.put(PropertyLists.FREQUENCY, frequency);

	}


}
