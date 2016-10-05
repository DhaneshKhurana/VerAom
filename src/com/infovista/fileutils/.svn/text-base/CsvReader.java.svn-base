package com.infovista.fileutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.common.classes.IPStringComparator;
import com.infovista.common.classes.TimeStampCache;
import com.infovista.common.interfaces.I_Result;
import com.infovista.instances.Keyword;
import com.infovista.sdk.stub.trafficStats.TrafficStatResult;
import com.infovista.statsRetriever.StatsPoller;

public class CsvReader {
	
	
	public static File createIfNotPresent(String localPath) {  
		File dir = new File(localPath);
		if(!dir.exists()) {
			dir.mkdirs();	
			CiscoWAASDiscoverer.LOGGER.debug(" directory created :: " + localPath);
		}
		return dir;		
	}
	
	public static boolean checkIfPresent(String localPath) {  
		File file = new File(localPath);
		if(!file.exists()) {
			return false;			
		}
		return true;		
	}
	
	public static Calendar getDateFromString(String timestampString){
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat(TimeStampCache.DATE_TIME_FORMAT);
			formatter.setLenient(false);
			cal.setTime(formatter.parse(timestampString));
			return cal;
			} catch (ParseException e) {
				CiscoWAASDiscoverer.LOGGER.error("Please specify Start and End time correctly", e);
		}
		return null;
	}
	
	public Map<String, Map<Long, String[]>> loadMapWithoutHeader(String fileName, String stub) {
		BufferedReader br = null;
		try {
			Map<String, Map<Long, String[]>> retMap = new TreeMap<String, Map<Long,String[]>>(new IPStringComparator());
			br = new BufferedReader(new FileReader(fileName));
			String line = null;
			//checking if file is empty
			if((line = br.readLine()) != null && !line.isEmpty()){
				//ignoring and printing 1st line having headers
				CiscoWAASDiscoverer.LOGGER.info("loading map for file " + fileName + " with header :: " + line);	
				while((line = br.readLine()) != null){
					if( (line.trim()).length()>0){
						String[] values = line.split(CommonFileUtilities.CSV_SEPERATOR);
						Long timeStamp;
						if(stub.equals(Keyword.ALARM)){
							timeStamp = Long.parseLong(values[1]);
						}else{
							timeStamp = TimeStampCache.getTimeStampFromString(values[1]);	
						}
						if(!retMap.containsKey(values[0])){
							Map<Long, String[]> timeMap = new TreeMap<>();
							timeMap.put(timeStamp, Arrays.copyOfRange(values, 2, values.length));
							retMap.put(values[0], timeMap);
						}else{
							// if the value exist overwrite it, else create new one 
							retMap.get(values[0]).put(timeStamp, Arrays.copyOfRange(values, 2, values.length));
						}	
					}
				}	
			}else{
				CiscoWAASDiscoverer.LOGGER.info(fileName + " found empty while loading");
			}
			return retMap;
			
		}catch (FileNotFoundException e) {
			CiscoWAASDiscoverer.LOGGER.error("File could not be found :: " + fileName);
		}catch (IOException e) {
			CiscoWAASDiscoverer.LOGGER.error("IO Exception occured while reading file :: " + fileName + " with stack trace :: ", e);
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Exception occured while reading file :: " + fileName + " with stack trace :: ", e);
		}finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					CiscoWAASDiscoverer.LOGGER.info("Bufferedreader could not be closed for file :: " + fileName + " with stack trace :: " , e);
				}
			}			
		}
		//if some error occurs due to some defect in file, simply return an empty map, which will merge with current map and write back current response obtained atleast
		return new HashMap<>();
	}
	
/*	public Map<String, Map<Long, String[]>> loadMapWithoutHeaderAlarm(String fileName) {
		BufferedReader br = null;
		try {
			Map<String, Map<Long, String[]>> retMap = new TreeMap<String, Map<Long,String[]>>(new IPStringComparator());
			br = new BufferedReader(new FileReader(fileName));
			String line = null;
			//ignoring and printing 1st line having headers
			CiscoWAASDiscoverer.LOGGER.info("loading map for file " + fileName + " with header :: " + br.readLine());
			while((line = br.readLine()) != null){
				String[] values = line.split(CommonFileUtilities.CSV_SEPERATOR);
				Long timeStamp = Long.parseLong((values[1]));
				if(!retMap.containsKey(values[0])){
					Map<Long, String[]> timeMap = new TreeMap<>();
					timeMap.put(timeStamp, Arrays.copyOfRange(values, 2, values.length));
					retMap.put(values[0], timeMap);
				}else{
					if(!retMap.get(values[0]).containsKey(timeStamp)){
						retMap.get(values[0]).put(timeStamp, Arrays.copyOfRange(values, 2, values.length));
					}
				}
			}
			return retMap;
			
		}catch (FileNotFoundException e) {
			CiscoWAASDiscoverer.LOGGER.info("File could not be found :: " + fileName);
		}catch (IOException e) {
			CiscoWAASDiscoverer.LOGGER.info("IO Exception occured while reading file :: " + fileName + " with stack trace :: ", e);
		}finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					CiscoWAASDiscoverer.LOGGER.info("Bufferedreader could not be closed for file :: " + fileName + " with stack trace :: " , e);
				}
			}			
		}
		return null;
	}*/
	
	
	
	public static List<String[]> readListOfStringArrays(String fileName, String separator) {
		BufferedReader br = null;
		try {
			List<String[]> retVal = new ArrayList<String[]>();
			br = new BufferedReader(new FileReader(fileName));
			String line = null;
			while((line = br.readLine()) != null){
				if((line.trim()).length()>0){
					retVal.add(line.split(separator));	
				}
			}
			return retVal;
			
		}catch (FileNotFoundException e) {
			CiscoWAASDiscoverer.LOGGER.info("File could not be found :: " + fileName);
		}catch (IOException e) {
			CiscoWAASDiscoverer.LOGGER.info("IO Exception occured while reading file :: " + fileName + " with stack trace :: " + e.getMessage());
		}finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					CiscoWAASDiscoverer.LOGGER.info("Bufferedreader could not be closed for file :: " + fileName + " with stack trace :: " + e.getMessage());
				}
			}			
		}
		return null;
	}
	
	public static void readWaesCapabilityAndIPMap(String fileName, String separator, Map<String,HashMap<String, ArrayList<String>>> retVal, Map<String, String> wae_Name_Ip_map) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line = null;
			while((line = br.readLine()) != null){
				if((line.trim()).length()>0){
					String[] vals = line.split(separator);
					ArrayList<String> capabilities = new ArrayList<String>();
					for(int i=2;i<vals.length;i++){
						capabilities.add(vals[i]);
					}
					HashMap<String, ArrayList<String>> ipMap = new HashMap<String, ArrayList<String>>();
					
					wae_Name_Ip_map.put(vals[0], vals[1]);
					
					ipMap.put(vals[1], capabilities);
					retVal.put(vals[0], ipMap);	
				}
			}
			
		}catch (FileNotFoundException e) {
			CiscoWAASDiscoverer.LOGGER.error("File could not be found :: " + fileName);
		}catch (Exception e) {
			CiscoWAASDiscoverer.LOGGER.error("Exception occured while reading file :: " + fileName + " with stack trace :: " + e.getMessage());
		}finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					CiscoWAASDiscoverer.LOGGER.info("Bufferedreader could not be closed for file :: " + fileName + " with stack trace :: " + e.getMessage());
				}
			}			
		}
	}
	
	
	
	/*public static List<String> readWaeListFromTopo(String fileName, String separator) {
		BufferedReader br = null;
		try {
			List<String> retVal = new ArrayList<String>();
			br = new BufferedReader(new FileReader(fileName));
			String line = null;
			boolean flag = false;
			
			while((line = br.readLine())!=null){
				if(line.equals("#WAE")){
					flag = true;
					br.readLine();
				}
				if(flag){
					if(!line.startsWith("#") && line!=""){
						retVal.add(line.split(separator)[1]);
					}else{
						return retVal;
					}
					return retVal;
				}
			}
		}catch (FileNotFoundException e) {
			
		}catch (IOException e) {
			
		}finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}			
		}
		return null;
	}*/
	
	public static void populateStubMapFromFile(String file, Map<String, Map<Long, I_Result>> resultMap){
		if(checkIfPresent(file)){
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				String line = null;
				//ignoring and printing 1st line having headers
				CiscoWAASDiscoverer.LOGGER.info("loading map for file " + file + " with header :: " + br.readLine());
				while((line = br.readLine()) != null){
					String[] values = line.split(CommonFileUtilities.CSV_SEPERATOR);
					TrafficStatResult result = new TrafficStatResult();
					result.setCompressedIn(Long.parseLong(values[2]));
					result.setCompressedOut(Long.parseLong(values[3]));
					result.setUnCompressedIn(Long.parseLong(values[4]));
					result.setUnCompressedOut(Long.parseLong(values[5]));
					result.setPassthroughintermediatein(Long.parseLong(values[6]));
					result.setPassthroughotherout(Long.parseLong(values[7]));
					result.setPassthroughoverloadin(Long.parseLong(values[8]));
					result.setPassthroughoverloadout(Long.parseLong(values[9]));
					result.setPassthroughpeerin(Long.parseLong(values[10]));
					result.setPassthroughpeerout(Long.parseLong(values[11]));
					result.setPassthroughpolicyin(Long.parseLong(values[12]));
					result.setPassthroughpolicyout(Long.parseLong(values[13]));
					result.setFrequency(values[14]);
					if(!resultMap.containsKey(values[0])){
						Map<Long, I_Result> timeMap = new TreeMap<>();
						timeMap.put(TimeStampCache.getTimeStampFromString(values[1]), result);
						resultMap.put(values[0], timeMap);
					}else{
						if(!resultMap.get(values[0]).containsKey(TimeStampCache.getTimeStampFromString(values[1]))){
							resultMap.get(values[0]).put(TimeStampCache.getTimeStampFromString(values[1]), result);
						}
					}
				}
			}catch(Exception e){
				CiscoWAASDiscoverer.LOGGER.debug("");
			}
		}
	}
	

}

