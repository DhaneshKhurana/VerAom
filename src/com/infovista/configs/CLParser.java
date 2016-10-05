package com.infovista.configs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.statsRetriever.AbstractStatsProcessor;

public class CLParser {
	
	public final static String HOST= "h";
	public final static String LOGIN= "u";
	public final static String PASSWORD= "pwd";
	public final static String AR= "ar";
	public final static String TRAFFIC_TYPE= "traf";
	public final static String DIRECTION= "dir";
	public final static String HELP = "help";
	public final static String MODE = "m";
	public final static String TOTALWAES = "w";
	public final static String WAESPERREQUEST = "wpr";
	public final static String START_TIME="st";
	public final static String END_TIME="et";
	public final static String ALIGN_TIME="at";
	public final static String SAME_TIME="sameTime";
	public final static String NO_TIME="noTime";
	public final static String PORT= "port";
	public final static String PROTOCOL= "proto";
	public final static String DATAMAXHISTORY = "dmh";
	public final static String maxHoursInRequest = "mhir";
	public final static String OUTPUT_STAT_FOLDER="ocsv";
	
	public final static String TRAFFIC_FUNCTION="retApp";
	
	public final static String ALARM_SERVICE = "alarm";
	public final static String TRAFFIC_SERVICE = "traffic";
	public final static String CIFS_SERVICE = "cifs";
	public final static String NFS_SERVICE = "nfs";
	public final static String HTTP_SERVICE = "http";
	public final static String MAPI_SERVICE = "mapi";
	public final static String VIDEO_SERVICE = "video";
	public final static String ALL_SERVICE = "all";
	
	public static final String DATE_TIME_FORMAT = "yyyy/MM/dd-HH:mm";
	
	public static void createParser(String[] args){
		CommandLineParser parser = new BasicParser();
		Options options = new Options();

		options.addOption("help", false, "Know Your options");
		
		options.addOption(HOST, true, "[mandatory] Host Name/Address");
		options.addOption(LOGIN, true, "[mandatory] Login Id/ User Name");
		options.addOption(PASSWORD, true, "[mandatory] Password");
		
		options.addOption(AR, true, "[optional] Aquisiotion Rate for stats. Valid values : last5min, lasthour, lastday, lastweek, lastmonth, lastyear");
		options.addOption(MODE, true, "[optional] Monotenant or MultiTenant");
		options.addOption(START_TIME, true, "[optional] Specify the start-time as yyyy/MM/dd-HH:mm");
		options.addOption(END_TIME, true, "[optional] Specify the end-time as yyyy/MM/dd-HH:mm");
		options.addOption(ALIGN_TIME, false, "[optional] Provide this option to align time to top of the hour");
		options.addOption(TRAFFIC_TYPE, true, "[optional] Traffic Type : Passthrogh or Optimized");
		options.addOption(DIRECTION, true, "[optional] Direction : inbound or outbound or biderctional");
		options.addOption(PORT, true, "[optional] specify port number");
		options.addOption(PROTOCOL, true, "[optional] specify protocol i.e. http or https");
		options.addOption(DATAMAXHISTORY, true, "[optional] specify data max hiostory in hours");
		options.addOption(maxHoursInRequest, true, "[optional] specify max hours to be set in a soap request");
		options.addOption(OUTPUT_STAT_FOLDER, true, "[optional] specify the name of the output folder to store output files");
		
		options.addOption(TRAFFIC_SERVICE, false, "[optional] enables traffic services");
		options.addOption(CIFS_SERVICE, false, "[optional] enables cifs services");
		options.addOption(NFS_SERVICE, false, "[optional] enables nfs services");
		options.addOption(HTTP_SERVICE, false, "[optional] enables http services");
		options.addOption(MAPI_SERVICE, false, "[optional] enables mapi services");
		options.addOption(VIDEO_SERVICE, false, "[optional] enables video services");
		options.addOption(ALARM_SERVICE, false, "[optional] enables video services");
		
		options.addOption(TRAFFIC_FUNCTION, false, "[optional] if specified retrieveAppTrafficStats function will be used else retrievetrafficStats function will be used");
		
		options.addOption(NO_TIME, false, "[optional] enables mapi services");
		options.addOption(SAME_TIME, false, "[optional] enables video services");
		
		options.addOption(TOTALWAES, true, "[optional] total number of waes");
		options.addOption(WAESPERREQUEST, true, "[optional] number of waes in a request(i.e. waes per request)");
		
		parseArguments(parser, options, args);
		
		update();
	}
	
	//updating fields after parsing arguments
	private static void update(){
		if(!CommonConfig.getInstance().isProcessingTopo()){
			AbstractStatsProcessor.initializeStaticFields();
			CommonConfig.getInstance().updateTimeSlot();
			CommonConfig.getInstance().printAllProperties();
		}
	}
	
	private static void parseArguments(CommandLineParser parser , Options options, String[] args){
		try{
			CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption(HOST)) {
				CommonConfig.getInstance().setHost(cmd.getOptionValue(HOST));
				CiscoWAASDiscoverer.LOGGER.debug("Host ::" + cmd.getOptionValue(HOST));
			} else {
				CiscoWAASDiscoverer.LOGGER.error("Please provide Host Address");
				printHelp(options);
				System.exit(0);
			}
			if (cmd.hasOption(LOGIN)) {
					CommonConfig.getInstance().setLogin(cmd.getOptionValue(LOGIN));
					CiscoWAASDiscoverer.LOGGER.debug("password :: " + cmd.getOptionValue(LOGIN));
			} else {
				CiscoWAASDiscoverer.LOGGER.error("Please Provide User-name");
				printHelp(options);
				System.exit(0);
			}
			if (cmd.hasOption(PASSWORD)) {
					CommonConfig.getInstance().setPassword(cmd.getOptionValue(PASSWORD));
					CiscoWAASDiscoverer.LOGGER.debug("UserName :: " + cmd.getOptionValue(PASSWORD));
			} else {
				CiscoWAASDiscoverer.LOGGER.error("Please provide password");
				printHelp(options);
				System.exit(0);
			}
			if (cmd.hasOption(MODE)) {
				CommonConfig.getInstance().setMode(cmd.getOptionValue(MODE));
				CiscoWAASDiscoverer.LOGGER.info("Mode :: " + cmd.getOptionValue(MODE));
			}
			if (cmd.hasOption(AR)) {
					CommonConfig.getInstance().setAr(cmd.getOptionValue(AR));
					CiscoWAASDiscoverer.LOGGER.debug("AR :: " + cmd.getOptionValue(AR));
			}
			if (cmd.hasOption(PORT)) {
				CommonConfig.getInstance().setPort(cmd.getOptionValue(PORT));
				CiscoWAASDiscoverer.LOGGER.debug("PORT :: " + cmd.getOptionValue(PORT));
			}
			if (cmd.hasOption(PROTOCOL)) {
				CommonConfig.getInstance().setProtocol(cmd.getOptionValue(PROTOCOL));
				CiscoWAASDiscoverer.LOGGER.debug("PROTOCOL :: " + cmd.getOptionValue(PROTOCOL));
			}
			if (cmd.hasOption(TRAFFIC_FUNCTION)) {
				CommonConfig.getInstance().setRetAppTrafficStats(true);
				CiscoWAASDiscoverer.LOGGER.debug("retrieveTrafficStats function will be used as specified");
			}
			if(cmd.hasOption(maxHoursInRequest)){
				CommonConfig.getInstance().setMaxHoursInRequest(Integer.parseInt(cmd.getOptionValue(maxHoursInRequest)));
				CiscoWAASDiscoverer.LOGGER.debug("max hours in a request changed to :: " + cmd.getOptionValue(maxHoursInRequest));
			}
			if(cmd.hasOption(START_TIME) || cmd.hasOption(END_TIME)){
				CommonConfig.getInstance().setFollowlastRunMechanism(false);
				CiscoWAASDiscoverer.LOGGER.debug("Last run mechanism has been changed to false");
				if(cmd.hasOption(START_TIME) && cmd.hasOption(END_TIME)){
					Calendar startDate = getDateFromString(cmd.getOptionValue(START_TIME));
					Calendar endDate = getDateFromString(cmd.getOptionValue(END_TIME));
					if(startDate!=null && endDate!=null){
						startDate.set(Calendar.MINUTE, 0);
						startDate.set(Calendar.SECOND, 0);
						startDate.set(Calendar.MILLISECOND, 0);
						endDate.set(Calendar.MINUTE, 0);
						endDate.set(Calendar.SECOND, 0);
						endDate.set(Calendar.MILLISECOND, 0);
						CommonConfig.getInstance().setStartTime(startDate);	
						CommonConfig.getInstance().setEndTime(endDate);
						CiscoWAASDiscoverer.LOGGER.debug("Start Time :: " + printDate(startDate));
						CiscoWAASDiscoverer.LOGGER.debug("End Time :: " + printDate(endDate));	
					}else{
						CiscoWAASDiscoverer.LOGGER.error("Please specify Start-time and End-time correctly");
						printHelp(options);
						System.exit(0);
					}
				}else{
					if (cmd.hasOption(START_TIME)) {
						Calendar startDate = getDateFromString(cmd.getOptionValue(START_TIME));
						Calendar endDate = Calendar.getInstance();
						endDate.add(Calendar.HOUR_OF_DAY, -1);
						if(startDate!=null){
							startDate.set(Calendar.MINUTE, 0);
							startDate.set(Calendar.SECOND, 0);
							startDate.set(Calendar.MILLISECOND, 0);
							endDate.set(Calendar.MINUTE, 0);
							endDate.set(Calendar.SECOND, 0);
							endDate.set(Calendar.MILLISECOND, 0);
							
							CommonConfig.getInstance().setStartTime(startDate);	
							CommonConfig.getInstance().setEndTime(endDate);
							CiscoWAASDiscoverer.LOGGER.debug("Start Time :: " + printDate(startDate));
							CiscoWAASDiscoverer.LOGGER.debug("End Time :: " + printDate(endDate));
						}else{
							CiscoWAASDiscoverer.LOGGER.error("Incorrect value specified for Start-Time");
							printHelp(options);
							System.exit(0);
						}
					}else{
						Calendar endDate = getDateFromString(cmd.getOptionValue(END_TIME));
						if(endDate!= null){
							Calendar startDate = getDateFromString(cmd.getOptionValue(END_TIME));
							startDate.add(Calendar.HOUR_OF_DAY, -CommonConfig.getInstance().getStatHistory());
							startDate.set(Calendar.MINUTE, 0);
							startDate.set(Calendar.SECOND, 0);
							startDate.set(Calendar.MILLISECOND, 0);
							endDate.set(Calendar.MINUTE, 0);
							endDate.set(Calendar.SECOND, 0);
							endDate.set(Calendar.MILLISECOND, 0);
							
							CommonConfig.getInstance().setStartTime(startDate);
							CommonConfig.getInstance().setEndTime(endDate);
							CiscoWAASDiscoverer.LOGGER.debug("Start Time :: " + printDate(startDate));
							CiscoWAASDiscoverer.LOGGER.debug("End Time :: " + printDate(endDate));
							
						}else{
							CiscoWAASDiscoverer.LOGGER.error("Incorrect value specified for End-Time");
							printHelp(options);
							System.exit(0);
						}
						
					}
				}
			}else{
				if(cmd.hasOption(DATAMAXHISTORY)){
					CommonConfig.getInstance().setDataMaxHistory(Integer.parseInt(cmd.getOptionValue(DATAMAXHISTORY)));
					CiscoWAASDiscoverer.LOGGER.debug("Data max history has been chanegd to :: " + cmd.getOptionValue(DATAMAXHISTORY));
				}
			}
			
			
			
			/*else{
				if(cmd.hasOption(SAME_TIME)){
					if(cmd.hasOption(ALIGN_TIME)){
						Calendar endTime = Calendar.getInstance();
						Calendar startTime = Calendar.getInstance();
						startTime.set(Calendar.MINUTE, 0);
						startTime.set(Calendar.SECOND, 0);
						startTime.set(Calendar.MILLISECOND, 0);
						endTime.set(Calendar.MINUTE, 0);
						endTime.set(Calendar.SECOND, 0);
						endTime.set(Calendar.MILLISECOND, 0);
						CommonConfig.getInstance().setStartTime(startTime);
						CommonConfig.getInstance().setEndTime(endTime);
						CiscoWAASDiscoverer.LOGGER.debug("Start Time :: " + printDate(startTime));
						CiscoWAASDiscoverer.LOGGER.debug("End Time :: " + printDate(endTime));
					}else{
						Calendar endTime = Calendar.getInstance();
						Calendar startTime = Calendar.getInstance();
						CommonConfig.getInstance().setStartTime(startTime);
						CommonConfig.getInstance().setEndTime(endTime);
						CiscoWAASDiscoverer.LOGGER.debug("Start Time :: " + printDate(startTime));
						CiscoWAASDiscoverer.LOGGER.debug("End Time :: " + printDate(endTime));	
					}
				}
				else if(cmd.hasOption(NO_TIME)){
					CommonConfig.getInstance().setStartTime(null);
					CommonConfig.getInstance().setEndTime(null);
					CiscoWAASDiscoverer.LOGGER.debug("Start Time :: " + printDate(CommonConfig.getInstance().getStartTime()));
					CiscoWAASDiscoverer.LOGGER.debug("End Time :: " + printDate(CommonConfig.getInstance().getEndTime()));
				}
				else if(cmd.hasOption(ALIGN_TIME)){
					Calendar endTime = CommonConfig.getInstance().getEndTime();
					Calendar startTime = CommonConfig.getInstance().getStartTime();
					startTime.set(Calendar.MINUTE, 0);
					startTime.set(Calendar.SECOND, 0);
					startTime.set(Calendar.MILLISECOND, 0);
					endTime.set(Calendar.MINUTE, 0);
					endTime.set(Calendar.SECOND, 0);
					endTime.set(Calendar.MILLISECOND, 0);
					CiscoWAASDiscoverer.LOGGER.debug("Start Time :: " + printDate(startTime));
					CiscoWAASDiscoverer.LOGGER.debug("End Time :: " + printDate(endTime));
				}else{
					CiscoWAASDiscoverer.LOGGER.debug("Start Time :: " + printDate(CommonConfig.getInstance().getStartTime()));
					CiscoWAASDiscoverer.LOGGER.debug("End Time :: " + printDate(CommonConfig.getInstance().getEndTime()));
				}
			}*/
			if(cmd.hasOption(ALARM_SERVICE) || cmd.hasOption(TRAFFIC_SERVICE) || cmd.hasOption(HTTP_SERVICE) || cmd.hasOption(MAPI_SERVICE) || cmd.hasOption(NFS_SERVICE) || cmd.hasOption(CIFS_SERVICE) || cmd.hasOption(VIDEO_SERVICE) ){
				CommonConfig.getInstance().setAll_services(false);
				CommonConfig.getInstance().clearServices();
			}/*else{
				CommonConfig.getInstance().registerService(ALL_SERVICE);
				CiscoWAASDiscoverer.LOGGER.debug("ALL services enabled");
			}*/
			if(cmd.hasOption(ALARM_SERVICE)){
				CommonConfig.getInstance().registerService(ALARM_SERVICE);
				CommonConfig.getInstance().setAlarm_services(true);
				CiscoWAASDiscoverer.LOGGER.debug("ALARM services enabled");
			}
			if(cmd.hasOption(TRAFFIC_SERVICE)){
				CommonConfig.getInstance().registerService(TRAFFIC_SERVICE);
				CommonConfig.getInstance().setTraffic_services(true);
				CiscoWAASDiscoverer.LOGGER.debug("Traffic services enabled");
			}
			if(cmd.hasOption(HTTP_SERVICE)){
				CommonConfig.getInstance().registerService(HTTP_SERVICE);
				CommonConfig.getInstance().setHttp_services(true);
				CiscoWAASDiscoverer.LOGGER.debug("HTTP services enabled");
			}
			if(cmd.hasOption(MAPI_SERVICE)){
				CommonConfig.getInstance().registerService(MAPI_SERVICE);
				CommonConfig.getInstance().setMapi_services(true);
				CiscoWAASDiscoverer.LOGGER.debug("MAPI services enabled");
			}
			if(cmd.hasOption(CIFS_SERVICE)){
				CommonConfig.getInstance().registerService(CIFS_SERVICE);
				CommonConfig.getInstance().setCifs_services(true);
				CiscoWAASDiscoverer.LOGGER.debug("CIFS services enabled");
			}
			if(cmd.hasOption(NFS_SERVICE)){
				CommonConfig.getInstance().registerService(NFS_SERVICE);
				CommonConfig.getInstance().setNfs_services(true);
				CiscoWAASDiscoverer.LOGGER.debug("NFS services enabled");
			}
			if(cmd.hasOption(VIDEO_SERVICE)){
				CommonConfig.getInstance().registerService(VIDEO_SERVICE);
				CommonConfig.getInstance().setVideo_services(true);
				CiscoWAASDiscoverer.LOGGER.debug("VIDEO services enabled");
			}
			if(cmd.hasOption(OUTPUT_STAT_FOLDER)){
				CommonConfig.getInstance().setOutput_folder(cmd.getOptionValue(OUTPUT_STAT_FOLDER));
				CiscoWAASDiscoverer.LOGGER.debug("Output folder changed to :: " + cmd.getOptionValue(OUTPUT_STAT_FOLDER));
			}
			if(cmd.hasOption(TOTALWAES)){
				try{
					int num = Integer.parseInt(cmd.getOptionValue(TOTALWAES));	
					CommonConfig.getInstance().setTotalWaes(num);
					CiscoWAASDiscoverer.LOGGER.debug("total No. of waes :: " + num);
				}catch(Exception e){
					CiscoWAASDiscoverer.LOGGER.error("Inavlid value specified for NUMBER OF WAES :: ", e);
				}
				
			}
			if(cmd.hasOption(WAESPERREQUEST)){
				try{
					int num = Integer.parseInt(cmd.getOptionValue(WAESPERREQUEST));	
					CommonConfig.getInstance().setWaesInOneRequest(num);
					CiscoWAASDiscoverer.LOGGER.debug("Waes per request :: " + num);
				}catch(Exception e){
					CiscoWAASDiscoverer.LOGGER.error("Inavlid value specified for WAES per request :: ", e);
				}
				
			}
			if(cmd.hasOption(TRAFFIC_TYPE)){
				String type = cmd.getOptionValue(TRAFFIC_TYPE);
				CommonConfig.getInstance().setTrafficType(new String[]{type});
				CiscoWAASDiscoverer.LOGGER.info("Traffic type changed to  :: " +type);
			}
			if(cmd.hasOption(DIRECTION)){
				String dir = cmd.getOptionValue(DIRECTION);
				CommonConfig.getInstance().setDirections(new String[]{dir});
				CiscoWAASDiscoverer.LOGGER.info("Direction type set is changed to  :: " +dir);
			}
			if (cmd.hasOption(HELP)) {
				printHelp(options);
				System.exit(0);
			} 
		}catch(Exception e){
			CiscoWAASDiscoverer.LOGGER.error("Exception occured while parsing arguments. \nPlease specify options correctyl. \nExiting the application. \nException Details :: ", e);
			printHelp(options);
			System.exit(0);
		}
	}
	
	public static Calendar getDateFromString(String timestampString){
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);
			formatter.setLenient(false);
			cal.setTime(formatter.parse(timestampString));
			return cal;
			} catch (ParseException e) {
				CiscoWAASDiscoverer.LOGGER.error("Please specify Start and End time correctly", e);
		}
		return null;
	}
	
	public static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Command Line Options \n", options);
	}
	
	public static String printDate(Calendar cal){
		if(cal!=null)
			return (cal.get(Calendar.YEAR)+"/" +(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DATE)+"/" + ", " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) +":"+ cal.get(Calendar.SECOND) + ":" +cal.get(Calendar.MILLISECOND) );
		else
			return "null";
	}
	
	
}
