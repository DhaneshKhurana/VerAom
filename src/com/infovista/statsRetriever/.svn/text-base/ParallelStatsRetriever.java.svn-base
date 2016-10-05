package com.infovista.statsRetriever;

import java.util.Calendar;
import java.util.concurrent.Callable;

import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.common.classes.StatsTimeResult;
import com.infovista.common.classes.TimeSlot;
import com.infovista.common.classes.TimeStampCache;
import com.infovista.common.interfaces.I_StatsProcessor;
import com.infovista.instances.Keyword;
import com.infovista.sdk.stub.eventNStatusStats.AlarmStub;

public class ParallelStatsRetriever implements Callable<StatsTimeResult>{
	private final I_StatsProcessor statStub;
	private volatile TimeSlot subTimeSlot;

	public ParallelStatsRetriever(I_StatsProcessor statStub){
		this.statStub = statStub;
		subTimeSlot = new TimeSlot();
	}
	
	public ParallelStatsRetriever(I_StatsProcessor statStub, TimeSlot subTimeSlot){
		this.statStub = statStub;
		this.subTimeSlot = subTimeSlot;
	}
	
	@Override
	public StatsTimeResult call() {
		if(statStub.getServiceName().equals(Keyword.ALARM)){
			if(subTimeSlot.isLastTimeSlot()){
				return statStub.retrieveStatsForTimeSlot(subTimeSlot);
			}else{
				return null;
			}
		}else{
			return statStub.retrieveStatsForTimeSlot(subTimeSlot);
		}
	}
	
	public void setTimeSlot(TimeSlot subTimeSlot){
		this.subTimeSlot = new TimeSlot(subTimeSlot.getStartTime(), subTimeSlot.getEndTime());
	}
}
