package com.infovista.fileutils;

public class FileNameRetriever {
	
	public final static String File_Comment="#";
	public final static String FILE_SEPERATOR = "/";
	public final static String CSV_EXTENSION = ".csv";
	public final static String XML_EXTENSION = ".xml";
	public final static String PROPERTIES_EXTENSION = ".properties";
	
	public final static String XML_RESPONSES_FOLDER = "Xml_Responses";
	public final static String OUTPUT_TOPO = "Output_Topology";
	private volatile static String OUTPUT_STATS = "Output_Stats";
	public final static String TIMESTAMP_CACHE = "TimeStamp_Cache";
	public final static String PROPERTYFILE_FOLDER = "config";

	public final static String WAEIPFILE = "IPList.csv";
	public final static String WAESTATFILE = "WAEStats.csv";
	public final static String MONITORED_FILE = "MonitoredAPP_";
	public final static String WAE_NAME_MAPPING = "WaeNamesDiscovered.csv";
	public final static String WAE_Application_MAPPING = "WaeNameApplicationMapping.csv";
	public final static String WAE_NAME_LIST= "waeNameList.csv";
	public final static String CIFSFILENAME = "Wae_CIFS.csv";
	public final static String HTTTPFILENAME = "Wae_HTTP.csv";
	public final static String MAPIFILENAME = "Wae_MAPI.csv";
	public final static String NFSFILENAME = "Wae_NFS.csv";
	public final static String VIDEOFILENAME = "Wae_Videos.csv";
	public final static String TRAFFICFILENAME = "Wae_Traffic.csv";
	public final static String Monitored_Apps = "Monitored_apps.csv";
	public final static String TopoFileName = "topology.csv";
	public final static String WAE_CAPABILITY_MAP ="WAE_Capability_MAP.csv";
	public final static String WAE_GROUP_LIST ="WAE_Group_List.csv";
	public final static String SERVICE_TIMESTAMP_CACHE ="Service_TimeStamps.csv";
	
	public final static String POLLER_PRPOERTIES = "verizon_poller.properties";

	public final static String ALARM_STATUS = "Alarm_Status.csv";
	
	static{
		CsvReader.createIfNotPresent(XML_RESPONSES_FOLDER);
		CsvReader.createIfNotPresent(OUTPUT_TOPO);
		CsvReader.createIfNotPresent(OUTPUT_STATS);
		CsvReader.createIfNotPresent(TIMESTAMP_CACHE);
		CsvReader.createIfNotPresent(PROPERTYFILE_FOLDER);
	}
	
	public static void setOutputFolder(String output){
		OUTPUT_STATS = output;
		CsvReader.createIfNotPresent(output);
	}
	
	public static String getOutputTopoFolderName(){
		return (OUTPUT_TOPO);
	}

	public static String getVerizonPollerPrpopertyFile(){
		return (PROPERTYFILE_FOLDER + FILE_SEPERATOR + POLLER_PRPOERTIES);
	}
	
	public static String getTimeStampCacheFolder(){
		return (TIMESTAMP_CACHE);
	}
	
	public static String getServiceTimeStampsFileName(){
		return (TIMESTAMP_CACHE+FILE_SEPERATOR+SERVICE_TIMESTAMP_CACHE);
	}
	
	public static String getOutputStatsFolderName(){
		return (OUTPUT_STATS);
	}
	
	public static String getWaeCapabilityMapFileName(){
		return (OUTPUT_TOPO+FILE_SEPERATOR+WAE_CAPABILITY_MAP);
	}
	
	public static String getWaeGroupFileName(){
		return (OUTPUT_TOPO+FILE_SEPERATOR+WAE_GROUP_LIST);
	}
	
	public static String getWaeIPFileName(){
		return (OUTPUT_TOPO+FILE_SEPERATOR+WAEIPFILE);
	}
	
	public static String getWaeStatFileName(){
		return (OUTPUT_STATS+FILE_SEPERATOR+WAESTATFILE);
	}
	
	public static String getApplicationStatsFileName(String applicationName){
		applicationName=applicationName.replaceAll("[\\s]+" , "_");
		return (OUTPUT_STATS+FILE_SEPERATOR+MONITORED_FILE+applicationName+CSV_EXTENSION);
	}
	
	public static String getWaeDiscoveredFileName(){
		return (OUTPUT_TOPO+FILE_SEPERATOR+WAE_NAME_MAPPING);
	}
	
	public static String getWaeAppMappingFileName(){
		return (OUTPUT_TOPO+FILE_SEPERATOR+WAE_Application_MAPPING);
	}
	
	public static String getWaeListFileName(){
		return (OUTPUT_TOPO+FILE_SEPERATOR+WAE_NAME_LIST);
	}
	public static String getCIFSFileName(){
		return (OUTPUT_STATS+FILE_SEPERATOR+CIFSFILENAME);
	}
	public static String getHTTPFileName(){
		return (OUTPUT_STATS+FILE_SEPERATOR+HTTTPFILENAME);
	}
	public static String getMAPIFileName(){
		return (OUTPUT_STATS+FILE_SEPERATOR+MAPIFILENAME);
	}
	public static String getNFSFileName(){
		return (OUTPUT_STATS+FILE_SEPERATOR+NFSFILENAME);
	}
	public static String getVIDEOSFileName(){
		return (OUTPUT_STATS+FILE_SEPERATOR+VIDEOFILENAME);
	}
	public static String getTRAFFICFileName(){
		return (OUTPUT_STATS+FILE_SEPERATOR+TRAFFICFILENAME);
	}
	public static String getAlarmStatusFileName(){
		return (OUTPUT_STATS+FILE_SEPERATOR+ALARM_STATUS);
	}
	public static String getMonitoredAppsFileName(){
		return(OUTPUT_TOPO+FILE_SEPERATOR+Monitored_Apps);
	}
	public static String getTopoFileName(){
		return(OUTPUT_TOPO+FILE_SEPERATOR+TopoFileName);
	}
	
	public static String getXMLFolder(){
		return XML_RESPONSES_FOLDER;
	}
	
	public static String getxmlFileName(String fileName){
		fileName=fileName.replaceAll("[\\s]+", "_");
		return(XML_RESPONSES_FOLDER+FILE_SEPERATOR+fileName+XML_EXTENSION);
	}
}
