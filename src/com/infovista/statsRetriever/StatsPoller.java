package com.infovista.statsRetriever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.ParseException;
import org.apache.commons.httpclient.protocol.Protocol;

import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.common.classes.StatStubFactory;
import com.infovista.common.classes.StatsTimeResult;
import com.infovista.common.classes.TimeSlot;
import com.infovista.common.classes.TimeStampCache;
import com.infovista.common.interfaces.I_StatsProcessor;
import com.infovista.configs.CLParser;
import com.infovista.configs.CommonConfig;
import com.infovista.fileutils.CommonFileUtilities;
import com.infovista.fileutils.CsvReader;
import com.infovista.fileutils.FileNameRetriever;
import com.infovista.instances.Keyword;
import com.infovista.sdk.ssl.FakeSSLProtocolSocketFactory;
import com.infovista.sdk.stub.cifsStats.CIFSStub;
import com.infovista.sdk.stub.deviceConf.DeviceStub;
import com.infovista.sdk.stub.deviceStatus.DeviceStatus;
import com.infovista.sdk.stub.httpStats.HttpStub;
import com.infovista.sdk.stub.mapiStats.MapiStub;
import com.infovista.sdk.stub.nfsStats.NfsStub;
import com.infovista.sdk.stub.trafficStats.TrafficStub;
import com.infovista.sdk.stub.videoStats.VideoStub;

public class StatsPoller {
	private final List<ParallelStatsRetriever> statStubs;
	
	//Constructor
/*	public StatsPoller(){
		statStubs = new ArrayList<ParallelStatsRetriever>();
		initialize();
	}*/

	public StatsPoller(){
		statStubs = new ArrayList<ParallelStatsRetriever>();
		initialize();
	}
	
	//Initializing with all the services registered
	private void initialize(){
		List<String> services = CommonConfig.getInstance().getRegisteredServices();
		printRegisteredService(services);
		for(String service : services){
			if(service.equals(CLParser.ALL_SERVICE)){
				List<I_StatsProcessor> stubList = StatStubFactory.getAllInstance(CLParser.ALL_SERVICE);
				for(I_StatsProcessor stub : stubList){
					statStubs.add(new ParallelStatsRetriever(stub));
				}
				break;
			}else if(service.equals(CLParser.TRAFFIC_SERVICE)){
				List<I_StatsProcessor> stubList = StatStubFactory.getAllInstance(CLParser.TRAFFIC_SERVICE);
				for(I_StatsProcessor stub : stubList){
					statStubs.add(new ParallelStatsRetriever(stub));
				}
			}else{
				I_StatsProcessor stub;
				if( (stub =  StatStubFactory.getInstance(service)) != null){
					statStubs.add(new ParallelStatsRetriever(stub));	
				}else{
					CiscoWAASDiscoverer.LOGGER.error("Could not initialize the service :: " + service);
				}
			}
		}
	}
	
	public void printRegisteredService(List<String> services){
		for(String service : services){
			CiscoWAASDiscoverer.LOGGER.debug("Service registered :: " + service);
		}
	}
	
	public static void main(String[] args) throws ParseException {
		CiscoWAASDiscoverer.LOGGER.debug("argumnets entered by user ");
		for(int i=0; i<args.length;){
			CiscoWAASDiscoverer.LOGGER.info(args[i++]);
		}
		//setting it to false , so that wae ip map and monitored apps could be initialized.
		CommonConfig.getInstance().setProcessingTopo(false);
		
		CLParser.createParser(args);
		
		initAndProcessStats();

	}
	
	//initilaize and process stats
	private static void initAndProcessStats(){
		CiscoWAASDiscoverer.LOGGER.debug("JVM Information : ");
		CiscoWAASDiscoverer.LOGGER.debug("Available Processors to JVM :: " + Runtime.getRuntime().availableProcessors());
		CiscoWAASDiscoverer.LOGGER.debug("Maximun memory available to JVM :: " + Runtime.getRuntime().maxMemory());
		CiscoWAASDiscoverer.LOGGER.debug("Heap Size of JVM " + Runtime.getRuntime().totalMemory());
		CiscoWAASDiscoverer.LOGGER.debug("Free memory available to the JVM :: " + Runtime.getRuntime().freeMemory());
		
		CompletionService<StatsTimeResult> completion = new ExecutorCompletionService<StatsTimeResult>(CommonConfig.getInstance().getExService());
		StatsPoller sPoller = new StatsPoller();
		TimeSlot timeSlot = new TimeSlot().getWholeTimeSlot();
		TimeSlot subTimeSlot = null;
		CiscoWAASDiscoverer.LOGGER.debug("Time duration for which stats will be collected :: " + timeSlot.toString());
		while(timeSlot.hasNextSubSlot(subTimeSlot)){
			subTimeSlot = timeSlot.getNextSubTimeSlot(subTimeSlot);
			sPoller.initializeWithTimeSlot(subTimeSlot);
			sPoller.processForTimeSlot(subTimeSlot, completion);
		}
		
		CiscoWAASDiscoverer.LOGGER.debug("All processes for all time-slots completed");
		System.out.println("All processes for all time-slots completed");
		CommonConfig.getInstance().shutdownExecutor();
	}
	
	public void initializeWithTimeSlot(TimeSlot subTimeSlot){
		for(ParallelStatsRetriever statExecutor : statStubs){
			statExecutor.setTimeSlot(subTimeSlot);
		}
	}
	
	//processing stats for timeSlot
	public void processForTimeSlot(TimeSlot subTimeSlot, CompletionService<StatsTimeResult> completion){
		CiscoWAASDiscoverer.LOGGER.debug("Processing for sub time-slot	:: " + subTimeSlot.toString());
		this.execute(completion);	
		CiscoWAASDiscoverer.LOGGER.debug("All processes for time-Slot :: " + subTimeSlot.toString() + " completed" );
		System.out.println("All processes for time-Slot :: " + subTimeSlot.toString() + " completed");
	}

	//Execute Method
	private void execute(CompletionService<StatsTimeResult> completion) {
		try {
			int noOfTasks = statStubs.size();
			CiscoWAASDiscoverer.LOGGER.debug("Submitting Tasks for Stats Processing");
			CiscoWAASDiscoverer.LOGGER.info("Total no. of tasks submitted :: " + noOfTasks);
			List<Future<StatsTimeResult>> statResults = new ArrayList<>();
			
			for (Callable<StatsTimeResult> stub : statStubs) {
				Future<StatsTimeResult> task = completion.submit(stub);
				statResults.add(task);
			}
			for(Future<StatsTimeResult> timeResult : statResults){
				if(timeResult!=null){
					StatsTimeResult result = timeResult.get();
					if(result!=null){
						CiscoWAASDiscoverer.LOGGER.info("Process completed for :: " + result.getServiceName());
						updateTimeStampCache(result);
					}else{
						CiscoWAASDiscoverer.LOGGER.info("Cache can not be updated as result is null");
					}
					CiscoWAASDiscoverer.LOGGER.info("No. of tasks left :: " + --noOfTasks);	
				}else{
					CiscoWAASDiscoverer.LOGGER.info("Future task returned is null");
				}
			}
			updateAndWriteCache();
			
		} catch (Exception e) {
			CiscoWAASDiscoverer.LOGGER.error("Exception occured in main thread :: ",e);
		}
	}
	
	private void updateTimeStampCache(StatsTimeResult timeResult){
		CiscoWAASDiscoverer.LOGGER.info("updating time-stamp cache for :: " + timeResult.getServiceName());

		TimeStampCache.getInstance().updateCache(timeResult);
		TimeStampCache.getInstance().writeCache();
		
		CiscoWAASDiscoverer.LOGGER.info("cache updated with values :: ");
		TimeStampCache.getInstance().printServiceTimeStampMap();
	}
	
	private void updateAndWriteCache(){
		TimeStampCache.getInstance().writeCache();
	}
}
