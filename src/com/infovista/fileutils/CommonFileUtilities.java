package com.infovista.fileutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.infovista.client.CiscoWAASDiscoverer;

public class CommonFileUtilities {
	public static final String CSV_SEPERATOR = ";";
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static File createIfNotPresent(String localPath) {  
		File dir = new File(localPath);
		if(!dir.exists()) {
			dir.mkdirs();			
		}
		return dir;		
	}
	
	public static List<String[]> readCSVFile(String fileName, String separator) {
		BufferedReader br = null;
		try {
			List<String[]> retVal = new ArrayList<String[]>();
			br = new BufferedReader(new FileReader(fileName));
			String line = null;
			while((line = br.readLine()) != null){
				retVal.add(line.split(separator));
			}
			return retVal;
			
		} catch (FileNotFoundException e) {
			CiscoWAASDiscoverer.LOGGER.info("Error while reading file : " + fileName);
			CiscoWAASDiscoverer.LOGGER.error("Error",e);
		} catch (IOException e) {
			CiscoWAASDiscoverer.LOGGER.info("Error while reading file : " + fileName);
			CiscoWAASDiscoverer.LOGGER.error("Error",e);
		}finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}			
		}
		return null;		
	}
	
	public static String[] getDirListing(String directory) {
		File directoryFile = new File(directory);
		if(directoryFile != null && directoryFile.isDirectory()) {
			try {
				return directoryFile.list();
			} catch (Exception e) {				
			}
		}
		return null;
	}
	
	public static File[] getDirFileListing(String directory){
		File directoryFile = new File(directory);
		if(directoryFile != null && directoryFile.exists() && directoryFile.isDirectory()) {
			try {
				return directoryFile.listFiles();
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static void deleteFile(String fullpath) throws IOException {
		File deletedFile = new File(fullpath);
		if (deletedFile.exists()) {
			if(deletedFile.isDirectory()) {
				FileUtils.deleteDirectory(deletedFile);
			}else if (deletedFile.isFile()) {
				deletedFile.delete();
			}
			
		}
	}

	/*public static String copyLocalFile(String fullpathFilename, File downloadDir) {
		try {
			File toCopyFile = new File(fullpathFilename);
			if(toCopyFile.exists()) {
				FileUtils.copyFileToDirectory(toCopyFile, downloadDir);
				return downloadDir.getAbsolutePath() + File.separator + toCopyFile.getName();
			}else {
			}
		} catch (Exception e) {
		}
	}*/

}
