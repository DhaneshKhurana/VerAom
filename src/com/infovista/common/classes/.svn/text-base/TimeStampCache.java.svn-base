package com.infovista.common.classes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.fileutils.CsvReader;
import com.infovista.fileutils.CsvWriter;
import com.infovista.fileutils.FileNameRetriever;


public class TimeStampCache {

	private volatile Map<String, Long> serviceTimeStampMap;

	private static TimeStampCache timeStampCache;
	
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	//private constructor
	private TimeStampCache(){
		init();
	}
	
	//initilaization method
	private void init(){
		serviceTimeStampMap = loadTimeStampMap();
		CiscoWAASDiscoverer.LOGGER.debug("TimeStamp Cache updated with values :: ");
		printServiceTimeStampMap();
	}
	
	//getting singeltion instance
	public static TimeStampCache getInstance(){
		if(timeStampCache==null){
			synchronized(TimeStampCache.class){
				if(timeStampCache==null){
					timeStampCache = new TimeStampCache();
				}
			}
			
		}
		return timeStampCache;
	}
	
	
	private Map<String, Long> loadTimeStampMap(){
		if(CsvReader.checkIfPresent(FileNameRetriever.getServiceTimeStampsFileName())){
			List<String[]> list = CsvReader.readListOfStringArrays(FileNameRetriever.getServiceTimeStampsFileName(), CsvWriter.CSV_SEPERATOR);
			final Map<String, Long> timeStampMap= new HashMap<>();
			if(list!=null && list.size()>0){
				for(String[] data : list){
					timeStampMap.put(data[0], getTimeStampFromString(data[1]));
					CiscoWAASDiscoverer.LOGGER.debug("putting value in timstampCache :: " + data[0] + " :: " + getTimeStampFromString(data[1]));
				}	
			}
			CiscoWAASDiscoverer.LOGGER.debug("servicetimstampcache loaded from memory with size :: " + timeStampMap.size());
			
			return timeStampMap;
		}else{
			CiscoWAASDiscoverer.LOGGER.debug("Service Time-stamp cache is missing");
			return new HashMap<>();
		}
	}
	
	public static Long getTimeStampFromString(String timestampString){
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);
			formatter.setLenient(false);
			cal.setTime(formatter.parse(timestampString));
			return cal.getTimeInMillis();
			} catch (ParseException e) {
				CiscoWAASDiscoverer.LOGGER.error("Date could not be parsed correctly", e);
		}
		return null;
	}
	
	
	public void writeCache(){
		CsvWriter writer = new CsvWriter(FileNameRetriever.getServiceTimeStampsFileName());
		writer.writeServiceTimeStampMap(serviceTimeStampMap);
		writer.closeStream();
	}
	
	public void put(String serviceName, Calendar endTime){
		serviceTimeStampMap.put(serviceName, endTime.getTimeInMillis());
	}
	
	public Long getLatestTimeStamp(String service){
		if(serviceTimeStampMap.containsKey(service)){
			return serviceTimeStampMap.get(service);
		}
		CiscoWAASDiscoverer.LOGGER.debug(service + " not found in timstamp cache");
		return null;
	}
	
	public void updateCache(StatsTimeResult timeResult){
		if(timeResult!=null){
			String service = timeResult.getServiceName();
			Long timeStamp = timeResult.getLastTimeStamp();
			if(serviceTimeStampMap.containsKey(service)){
				if(serviceTimeStampMap.get(service)<timeStamp){
					CiscoWAASDiscoverer.LOGGER.debug("The timeStamp for service :: " + service + " changed from :: " +serviceTimeStampMap.get(service) + " to :: " + timeStamp);
					serviceTimeStampMap.put(service, timeStamp);	
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("The timeStamp for service :: " + service + " id old . So it won't be overwritten from :: " +serviceTimeStampMap.get(service) + " to :: " + timeStamp);	
				}
			}else{
				serviceTimeStampMap.put(service, timeStamp);
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.debug("Time result found null while updating cache");
		}
	}
	
	public void printServiceTimeStampMap(){
		CiscoWAASDiscoverer.LOGGER.debug("Service \t Timestamp");
		for(Entry<String, Long> map : serviceTimeStampMap.entrySet()){
			CiscoWAASDiscoverer.LOGGER.debug(map.getKey() + " \t " + map.getValue());
		}
	}
	
	
}
