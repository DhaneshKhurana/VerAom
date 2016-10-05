package com.infovista.fileutils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.sdk.stub.eventNStatusStats.AlarmStatusResult;
import com.infovista.common.classes.TimeStampCache;
import com.infovista.common.interfaces.*;
import com.infovista.configs.CommonConfig;
import com.infovista.instances.Keyword;

public class CsvWriter {

	private String filename;
	private BufferedWriter bufWriter;

	public static final String CSV_SEPERATOR = ";";
	public static final String LIST_SEPARATOR = ",";
	public static final String ENDLINE_CHAR = "\n";

	public CsvWriter(String filename) {
		this.filename = filename;
		initialize();
	}

	private void initialize() {
		try {
			bufWriter = new BufferedWriter(new FileWriter(filename));
		}catch (IOException e) {
			CiscoWAASDiscoverer.LOGGER.info("BufferedWriter could not be initialized for file :: " + filename);
		}

	}
	
	public static boolean checkIfPresent(String localPath) {  
		File file = new File(localPath);
		if(!file.exists()) {
			return false;			
		}
		return true;		
	}
	
	public static void checkAndWrite(String fileName, String stubName,  Map<String, Map<Long, I_Result>> map){
		CsvWriter writer;
		if(!checkIfPresent(fileName)){
			CiscoWAASDiscoverer.LOGGER.debug("no previous stat file could be found for  :: " + stubName);
			writer = new CsvWriter(fileName);
			writer.writeSortedStubMap(map, stubName);	
			writer.closeStream();
		}else{
			CiscoWAASDiscoverer.LOGGER.debug("previous stat file found for  :: " + stubName + " . so data will be merged and written back sorted");
			Map<String, Map<Long, String[]>>fullMap =  loadAndMergeData(fileName, map, stubName);
			writer = new CsvWriter(fileName);
			writer.writeStubMap(fullMap, stubName);
			writer.closeStream();
		}
	}
	
/*	public static void checkAndWriteAlarmMap(String fileName, Map<String, Map<Long, I_Result>> map, String[] colNames){
		CsvWriter writer;
		if(!checkIfPresent(fileName)){
			writer = new CsvWriter(fileName);
			writer.writeAlarmStatusMap(map);
		}else{
			Map<String, Map<Long, String[]>>fullMap =  loadAndMergeData(fileName, map);
			writer = new CsvWriter(fileName);
			writer.writeMap(fullMap);
			
		}
		writer.closeStream();
	}*/
	
	//load and merge data
	private static Map<String, Map<Long, String[]>> loadAndMergeData(String fileName, Map<String, Map<Long, I_Result>> currentMap, String stubname){
		Map<String, Map<Long, String[]>> oldMap = new CsvReader().loadMapWithoutHeader(fileName, stubname);	
		for(Entry<String, Map<Long, I_Result>> ipMap : currentMap.entrySet()){
			if(!oldMap.containsKey(ipMap.getKey())){
				Map<Long, String[]> timeMap = new TreeMap<>();
				for(Entry<Long, I_Result> timeStampMap : ipMap.getValue().entrySet()){
					timeMap.put(timeStampMap.getKey(), timeStampMap.getValue().getResultArray());
				}
				oldMap.put(ipMap.getKey(), timeMap);
			}else{
				for(Entry<Long, I_Result> timeStampMap : ipMap.getValue().entrySet()){
					oldMap.get(ipMap.getKey()).put(timeStampMap.getKey(), timeStampMap.getValue().getResultArray());
				}
			}
		}
		
		//CiscoWAASDiscoverer.LOGGER.debug("after merging printing map :: ");
		//print_StringArray_Map(oldMap);
		return oldMap;
	}
	
	public static void print_StringArray_Map(Map<String, Map<Long, String[]>> resultMap){
		CiscoWAASDiscoverer.LOGGER.debug("printing map");
		for(Entry<String, Map<Long, String[]>> ipmap : resultMap.entrySet()){
			for(Entry<Long, String[]> timeMap : ipmap.getValue().entrySet()){
				CiscoWAASDiscoverer.LOGGER.debug(ipmap.getKey() + ";" + timeMap.getKey() );
				for(String value : timeMap.getValue()){
					CiscoWAASDiscoverer.LOGGER.debug(";" + value);	
				}
			}
		}
	}	
	
	public static void printI_Result_Map(Map<String, Map<Long, I_Result>> resultMap){
		CiscoWAASDiscoverer.LOGGER.debug("printing map");
		for(Entry<String, Map<Long, I_Result>> ipmap : resultMap.entrySet()){
			for(Entry<Long, I_Result> timeMap : ipmap.getValue().entrySet()){
				CiscoWAASDiscoverer.LOGGER.debug(ipmap.getKey() + ";" + timeMap.getKey() );
				for(String value : timeMap.getValue().getResultArray()){
					CiscoWAASDiscoverer.LOGGER.debug(";" + value);	
				}
			}
		}
	}
	
	
	//writing map with stub values as string array
	public void writeStubMap(Map<String, Map<Long, String[]>> map, String stub) {
		writeHeader(stub);
		if (map != null && map.size() > 0) {
			try {
				for(Entry<String, Map<Long, String[]>> waeIP : map.entrySet()){
					for(Entry<Long, String[]> timeMap : waeIP.getValue().entrySet()){
						bufWriter.write(waeIP.getKey());
						bufWriter.write(CSV_SEPERATOR);
						if(stub.equals(Keyword.ALARM)){
							bufWriter.write(String.valueOf(timeMap.getKey()));	
						}else{
							Date date = new Date(timeMap.getKey());
							String dateString = new SimpleDateFormat(CommonFileUtilities.DATE_FORMAT).format(date);
							bufWriter.write(dateString);	
						}
						for(String values : timeMap.getValue()){
							bufWriter.write(CSV_SEPERATOR);
							bufWriter.write(values);
						}
						bufWriter.write(ENDLINE_CHAR);
					}
						
				}
			}catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.info("Error Occured while writing for file :: " + filename + " with error :: " + e.getMessage());
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.info("Map is empty for file :: " + filename);
		}
	}
	

	
	public void writeline(String[] values) {
		if (values != null) {
			try {
				// Write first value
				bufWriter.write(values[0]);
				for (int ii = 1; ii < values.length; ii++) {
					bufWriter.write(CSV_SEPERATOR);
					if (values[ii] != null) {
						bufWriter.write(values[ii]);
					} else {
						bufWriter.write("null");
					}
				}
				bufWriter.write(ENDLINE_CHAR);
			} catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.error("Exception occured while writing in CSVWriter :: ", e);
			}
		}
	}
	
	public void writeHeader(String stub) {
		List<String> values = CommonConfig.getInstance().getHeaderCols(stub);
		if (values != null) {
			try {
				if(stub.equals(Keyword.ALARM)){
					bufWriter.write("#Device-IP;Alarm_Id");
				}else{
					bufWriter.write("#Device-IP;TimeStamp");
				}
				Iterator<String> it = values.iterator();
				while(it.hasNext()) {
					bufWriter.write(CSV_SEPERATOR);
					bufWriter.write(it.next());
				}
				bufWriter.write(ENDLINE_CHAR);
			} catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.error("Exception occured while writing in CSVWriter :: ", e);
			}
		}
	}

	public void writeList(List<String[]> values) {
		if (values != null && values.size() > 0) {
			try {
				// Write first value
				Iterator<String[]> it = values.iterator();
				while (it.hasNext()) {
					String[] vals = it.next();
					bufWriter.write(vals[0]);
					for(int i=1; i<vals.length; i++){
						bufWriter.write(CSV_SEPERATOR);
						bufWriter.write(vals[i] != null ? vals[i] : "null");
					}
					if(it.hasNext()){
						bufWriter.write(ENDLINE_CHAR);
					}
				}
			}catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.info("Error Occured while writing for file :: " + filename + " with error :: " + e.getMessage());
			}finally{
				closeStream();
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.info("String Array List is empty for file :: " + filename);
			closeStream();
		}
	}
	
	public void writeList(List<String[]> values, String[] cols) {
		writeline(cols);
		if (values != null && values.size() > 0) {
			try {
				Iterator<String[]> it = values.iterator();
				while (it.hasNext()) {
					String[] vals = it.next();
					bufWriter.write(vals[0]!=null ? vals[0] :"null");
					for(int i=1; i<vals.length; i++){
						bufWriter.write(CSV_SEPERATOR);
						bufWriter.write(vals[i] != null ? vals[i] : "null");
					}
					bufWriter.write(ENDLINE_CHAR);
				}
			}catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.info("Error Occured while writing for file :: " + filename + " with error :: " , e);
			}finally{
				closeStream();
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.info("String Array List is empty for file :: " + filename);
			closeStream();
		}
	}
	//writing ip-result maps
/*	public void writeResultMap(Map<String,List<I_Result>> map, String[] colNames) {
		writeline(colNames);
		if (map != null && map.size() > 0) {
			try {
				for(Entry<String, List<I_Result>> waeIP : map.entrySet()){
					for(I_Result result : waeIP.getValue()){
						bufWriter.write(waeIP.getKey());
						String[] resultList =  result.getResultArray();
						for(String values : resultList){
							bufWriter.write(CSV_SEPERATOR);
							bufWriter.write(values);	
						}
						bufWriter.write(ENDLINE_CHAR);
					}
						
				}
			}catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.info("Error Occured while writing for file :: " + filename + " with error :: " + e.getMessage());
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.info("Map is empty for file :: " + filename);
		}
	}*/
	
	public void writeSortedStubMap(Map<String, Map<Long, I_Result>> map, String stub) {
		writeHeader(stub);
		if (map != null && map.size() > 0) {
			try {
				for(Entry<String, Map<Long, I_Result>> waeIP : map.entrySet()){
					for(Entry<Long, I_Result> timeMap : waeIP.getValue().entrySet()){
						bufWriter.write(waeIP.getKey());
						bufWriter.write(CSV_SEPERATOR);
						if(stub.equals(Keyword.ALARM)){
							bufWriter.write(String.valueOf(timeMap.getKey()));
						}else{
							Date date = new Date(timeMap.getKey());
							String dateString = new SimpleDateFormat(CommonFileUtilities.DATE_FORMAT).format(date);
							bufWriter.write(dateString);	
						}
						for(String values : timeMap.getValue().getResultArray()){
							bufWriter.write(CSV_SEPERATOR);
							bufWriter.write(values);
						}
						bufWriter.write(ENDLINE_CHAR);
					}
						
				}
			}catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.info("Error Occured while writing for file :: " + filename + " with error :: " + e.getMessage());
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.info("Map is empty for file :: " + filename);
		}
	}
	
	public void writeServiceTimeStampMap(Map<String, Long> map) {
		if (map != null && map.size() > 0) {
			try {
				// Write first value
				for(Entry<String, Long> serviceName : map.entrySet()){
					Date date = new Date(serviceName.getValue());
					String dateString = new SimpleDateFormat(TimeStampCache.DATE_TIME_FORMAT).format(date);
					bufWriter.write(serviceName.getKey());
					bufWriter.write(CSV_SEPERATOR);
					bufWriter.write(dateString);
					bufWriter.write(ENDLINE_CHAR);
				}
			}catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.info("Error Occured while writing for file :: " + filename + " with error :: " + e.getMessage());
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.info("Map is empty for file :: " + filename);
		}
	}
	
	public void writeAlarmStatusMap(Map<String, Map<Long, I_Result>> map, String stub) {
		writeHeader(stub);
		if (map != null && map.size() > 0) {
			try {
				for(Entry<String, Map<Long, I_Result>> waeIP : map.entrySet()){
					for(Entry<Long, I_Result> timeMap : waeIP.getValue().entrySet()){
						for(String values : timeMap.getValue().getResultArray()){
							bufWriter.write(CSV_SEPERATOR);
							bufWriter.write(values);
						}
						bufWriter.write(ENDLINE_CHAR);
					}
						
				}
			}catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.info("Error Occured while writing for file :: " + filename + " with error :: " + e.getMessage());
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.info("Map is empty for file :: " + filename);
		}	
	}
	
/*	public void writeMap(Map<String, TreeMap<Long, ArrayList<String>>> map, String[] cols) {
		writeline(cols);
		if (map != null && map.size() > 0) {
			try {
				// Write first value
				for(Entry<String, TreeMap<Long, ArrayList<String>>> waeIP : map.entrySet()){
					for(Entry<Long, ArrayList<String>> timeMap : waeIP.getValue().entrySet()){
						Date date = new Date(timeMap.getKey());
						String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
						bufWriter.write(waeIP.getKey());
						bufWriter.write(CSV_SEPERATOR);
						bufWriter.write(dateString);
						for(String values : timeMap.getValue()){
							bufWriter.write(CSV_SEPERATOR);
							bufWriter.write(values);
						}
						bufWriter.write(ENDLINE_CHAR);
					}
						
				}
			}catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.info("Error Occured while writing for file :: " + filename + " with error :: " + e.getMessage());
			}finally{
				try {
					bufWriter.close();
				} catch (IOException e) {
					CiscoWAASDiscoverer.LOGGER.info("BufferedWriter could not be closed for file :: " + filename + " due to error :: " + e.getMessage());
				}
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.info("Map is empty for file :: " + filename);
		}
	}
*/	
	public void writeWaeMap(HashMap<String, HashMap<String, ArrayList<String>>> map) {
		if (map != null && map.size() > 0) {
			try {
				// Write first value
				for(Entry<String, HashMap<String, ArrayList<String>>> waeName : map.entrySet()){
					for(Entry<String, ArrayList<String>> waeIP : waeName.getValue().entrySet()){
						bufWriter.write(waeName.getKey());
						bufWriter.write(CSV_SEPERATOR);
						bufWriter.write(waeIP.getKey());
						for(String values : waeIP.getValue()){
							bufWriter.write(CSV_SEPERATOR);
							bufWriter.write(values);
						}
						bufWriter.write(ENDLINE_CHAR);
					}
						
				}
			}catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.info("Error Occured while writing for file :: " + filename + " with error :: " + e.getMessage());
			}finally{
				try {
					bufWriter.close();
				} catch (IOException e) {
					CiscoWAASDiscoverer.LOGGER.info("BufferedWriter could not be closed for file :: " + filename + " due to error :: " + e.getMessage());
				}
			}
		}else{
			CiscoWAASDiscoverer.LOGGER.info("Map is empty for file :: " + filename);
		}
	}
	
	public void closeStream() {
		try {
			bufWriter.close();
		} catch (IOException e) {
			CiscoWAASDiscoverer.LOGGER.debug("Exception caught while closing stream :: " + e);
		}
	}

}
