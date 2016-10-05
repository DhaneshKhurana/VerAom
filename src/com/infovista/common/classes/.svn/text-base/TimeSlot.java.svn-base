package com.infovista.common.classes;

import java.util.Calendar;

import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.configs.CLParser;
import com.infovista.configs.CommonConfig;

public class TimeSlot {
	
	private Calendar startTime;
	private Calendar endTime;
	
	public TimeSlot(){
		startTime = null;
		endTime = null;
	}
	
	public TimeSlot(Calendar startTime, Calendar endTime){
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public boolean hasNextSubSlot(TimeSlot subTimeSlot){
		if(subTimeSlot!=null){
			return subTimeSlot.getEndTime().before(this.getEndTime());
		}	
		return true;
	}
	
	public TimeSlot getNextSubTimeSlot(TimeSlot subTimeSlot){
		if(subTimeSlot!=null){
			CiscoWAASDiscoverer.LOGGER.debug("IN getNextSubTimeSlot : sub time slot recieved is :: " + subTimeSlot.toString());
			subTimeSlot.setStartTime(subTimeSlot.getEndTime());
			subTimeSlot.getEndTime().add(Calendar.HOUR_OF_DAY, CommonConfig.getInstance().getMaxHoursInRequest());
			if(subTimeSlot.getEndTime().after(this.getEndTime())){
				subTimeSlot.setEndTime(this.getEndTime());
			}
			CiscoWAASDiscoverer.LOGGER.debug("IN getNextSubTimeSlot : sub time slot retruning is :: " + subTimeSlot.toString());
			return subTimeSlot;
		}else{
			CiscoWAASDiscoverer.LOGGER.debug("IN getNextSubTimeSlot : sub time slot recieved is :: null");
			subTimeSlot = new TimeSlot();
			subTimeSlot.setStartTime(this.getStartTime());
			subTimeSlot.setEndTime(this.getStartTime());
			subTimeSlot.getEndTime().add(Calendar.HOUR_OF_DAY, CommonConfig.getInstance().getMaxHoursInRequest());
			if(subTimeSlot.getEndTime().after(this.getEndTime())){
				subTimeSlot.setEndTime(this.getEndTime());
			}
			CiscoWAASDiscoverer.LOGGER.debug("IN TimeSlot : sub time slot retruning is :: " + subTimeSlot.toString());
			return subTimeSlot;
		}
	}
	
	public TimeSlot getWholeTimeSlot(){
		startTime = CommonConfig.getInstance().getStartTime();
		endTime = CommonConfig.getInstance().getEndTime();
		return this;
	}

	public Calendar getStartTime() {
		return startTime;
	}
	
	public Long getStartTimeStamp() {
		return startTime.getTimeInMillis();
	}


	public void setStartTime(Calendar startTime) {
		this.startTime = (Calendar) startTime.clone();
	}

	public Calendar getEndTime() {
		return endTime;
	}
	
	public Long getEndTimeStamp() {
		return endTime.getTimeInMillis();
	}

	public void setEndTime(Calendar endTime) {
		this.endTime = (Calendar) endTime.clone();
	}
	
	public boolean isLastTimeSlot(){
		if(endTime.equals(CommonConfig.getInstance().getEndTime())){
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return ("Start-Time :: " + CLParser.printDate(startTime) + " end-Time :: " + CLParser.printDate(endTime));
	}

}
