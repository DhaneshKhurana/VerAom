package com.infovista.common.classes;

import com.infovista.configs.CommonConfig;

public class CommonParams {
	
	private static CommonParams params;
	private static String[] trafficType;
	private static String[]  directions;
	
	private CommonParams(){
		
	}
	
	public static CommonParams getInstance(){
		if(params==null){
			params = new CommonParams();
		}
		return params;
	}
	
	//RETURING HTTP TIME FRAME FILTER
	public static void setTimeFrameFilterHTTP(com.cisco.waas.wsc.HttpStatsStub.TimeFrameFilter timeFrameFilter, TimeSlot timeSlot){
		timeFrameFilter.setFrequency(CommonConfig.getInstance().getAr());
		timeFrameFilter.setStartTime(timeSlot.getStartTime());
		timeFrameFilter.setEndTime(timeSlot.getEndTime());	
	}
	
	public static void setTimeFrameFilterMAPI(com.cisco.waas.wsc.MapiStatsStub.TimeFrameFilter timeFrameFilter, TimeSlot timeSlot){
		timeFrameFilter.setFrequency(CommonConfig.getInstance().getAr());
			timeFrameFilter.setStartTime(timeSlot.getStartTime());
			timeFrameFilter.setEndTime(timeSlot.getEndTime());	
	}
	
	public static void setTimeFrameFilterNFS(com.cisco.waas.wsc.NfsStatsStub.TimeFrameFilter timeFrameFilter, TimeSlot timeSlot){
		timeFrameFilter.setFrequency(CommonConfig.getInstance().getAr());
		timeFrameFilter.setStartTime(timeSlot.getStartTime());
		timeFrameFilter.setEndTime(timeSlot.getEndTime());	
	}
	
	public static void setTimeFrameFilterVideo(com.cisco.waas.wsc.VideoStatsStub.TimeFrameFilter timeFrameFilter, TimeSlot timeSlot){
		timeFrameFilter.setFrequency(CommonConfig.getInstance().getAr());
		timeFrameFilter.setStartTime(timeSlot.getStartTime());
		timeFrameFilter.setEndTime(timeSlot.getEndTime());	
	}
	
	public static void setTimeFrameFilterCIFS(com.cisco.waas.wsc.CIFSStatsStub.TimeFrameFilter timeFrameFilter, TimeSlot timeSlot){
		timeFrameFilter.setFrequency(CommonConfig.getInstance().getAr());
		timeFrameFilter.setStartTime(timeSlot.getStartTime());
		timeFrameFilter.setEndTime(timeSlot.getEndTime());	
	}
	
	public static void setTimeFrameFilterTraffic(com.cisco.waas.wsc.TrafficStatsStub.TimeFrameFilter timeFrameFilter, TimeSlot timeSlot){
		timeFrameFilter.setFrequency(CommonConfig.getInstance().getAr());
		timeFrameFilter.setStartTime(timeSlot.getStartTime());
		timeFrameFilter.setEndTime(timeSlot.getEndTime());	
	}
	
	
}
