package com.infovista.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.stream.XMLStreamException;

import org.apache.axis2.AxisFault;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.cisco.waas.wsc.CIFSStatsStub.HitRateStats;
import com.cisco.waas.wsc.DeviceConfStub.Device;
import com.cisco.waas.wsc.DeviceConfStub.DeviceGroup;
import com.cisco.waas.wsc.DeviceStatusStub.MonitoredAO;
import com.cisco.waas.wsc.HttpStatsStub.HttpConnStats;
import com.cisco.waas.wsc.MapiStatsStub.MapiRequestTypeStats;
import com.cisco.waas.wsc.NfsStatsStub.NfsRespTypeStats;
import com.cisco.waas.wsc.TrafficStatsStub.MonitoredApps;
import com.cisco.waas.wsc.VideoStatsStub.VideoStats;
import com.infovista.fileutils.CsvWriter;
import com.infovista.fileutils.FileNameRetriever;
import com.infovista.instances.ApplicationGroup;
import com.infovista.instances.Instance;
import com.infovista.instances.Keyword;
import com.infovista.instances.OAM;
import com.infovista.instances.OptimizationService;
import com.infovista.instances.Site;
import com.infovista.instances.WAE;
import com.infovista.instances.WAEGroup;
import com.infovista.sdk.error.WAASAuthenticationError;
import com.infovista.sdk.error.WAASPermissionDeniedError;
import com.infovista.sdk.error.WAASTimeoutError;
import com.infovista.sdk.error.WAASTransportError;
import com.infovista.sdk.stub.cifsStats.CIFSStub;
import com.infovista.sdk.stub.deviceConf.DeviceStub;
import com.infovista.sdk.stub.deviceStatus.DeviceStatus;
import com.infovista.sdk.stub.httpStats.HttpStub;
import com.infovista.sdk.stub.mapiStats.MapiStub;
import com.infovista.sdk.stub.nfsStats.NfsStub;
import com.infovista.sdk.stub.trafficStats.TrafficStub;
import com.infovista.sdk.stub.videoStats.VideoStub;

public class CiscoWAASDiscoverer {
	private static final String MULTI_VALUE_SEPARATOR = ",";
	public static final String EMTPY_STRING = "";
	private static final String CSV_SEP = ";";
	private static final String END_LINE = "\n";
	int waasTTL = 7;
	Map<String, Set<Instance>> typeInstanceMap = new HashMap<String, Set<Instance>>();
	private List<String> ipList = new ArrayList<String>();
	private String cmName;
	public static final Logger LOGGER = Logger.getLogger("VDLinkLogger");
	
	private static HashMap<String, HashMap<String, ArrayList<String>>> wae_list_map;
	private static List<String[]> waeGroups_;
	private static List<String[]> monitoredApps_;
	
	static {
		monitoredApps_ = new ArrayList<String[]>();
		wae_list_map = new HashMap<String, HashMap<String, ArrayList<String>>>();
		waeGroups_ = new ArrayList<String[]>();
	}

	private void discoverWAAS(String host, String p_userName, String p_password, String mode) {
		boolean passwordFound = false;
		try {
			if (mode.indexOf("MULTI") != -1)
				passwordFound = retrieveWAASInfo(host, p_userName, p_password, true);
			else
				passwordFound = retrieveWAASInfo(host, p_userName, p_password, false);
		} catch (WAASTransportError e) {
			// Stack message as an error only if we are in integration mode.
			// In this mode, the host should answer to WAAS as it is declared in the CSV file.
			// Otherwise, log the message only at the debug level.
			CiscoWAASDiscoverer.LOGGER.info("Http transport error while doing WAAS request: " + e.getTransportErrorMessage());
			CiscoWAASDiscoverer.LOGGER.info("WAAS web services probably not well configured on your device");
			CiscoWAASDiscoverer.LOGGER.info("Http transport error while doing WAAS request: " + e.getTransportErrorMessage());
		} catch (WAASTimeoutError e) {
			// Stack message as an error only if we are in integration mode.
			// In this mode, the host should answer to WAAS as it is declared in the CSV file.
			// Otherwise, log the message only at the debug level.
			CiscoWAASDiscoverer.LOGGER.info("Timeout during waas connection establishment");
			CiscoWAASDiscoverer.LOGGER.info("Check your waas configuration or increase the waas discovery timeout.");
			CiscoWAASDiscoverer.LOGGER.info("Timeout during waas connection establishment");
		} catch (AxisFault e) {
			CiscoWAASDiscoverer.LOGGER.info("Error while configuring WAAS stub");
			CiscoWAASDiscoverer.LOGGER.info("The waas discovery will be stopped for other devices.");
			CiscoWAASDiscoverer.LOGGER.info("Error while configuring WAAS stub");

		} catch (FileNotFoundException e) {
			CiscoWAASDiscoverer.LOGGER.info("Error while loading WAAS policy file");
			CiscoWAASDiscoverer.LOGGER.info("The waas discovery will be stopped for other devices.");
			CiscoWAASDiscoverer.LOGGER.info("Error while loading WAAS policy file");

		} catch (XMLStreamException e) {
			CiscoWAASDiscoverer.LOGGER.info("Error while parsing WAAS policy file");
			CiscoWAASDiscoverer.LOGGER.info("The waas discovery will be stopped for other devices.");
			CiscoWAASDiscoverer.LOGGER.info("Error while parsing WAAS policy file");

		}
	}

	public boolean retrieveWAASInfo(String p_host, String p_login, String p_password, boolean p_multitenant) throws WAASTransportError, WAASTimeoutError, AxisFault, FileNotFoundException,
			XMLStreamException {
		String waasURL = "https://" + p_host + ":8443/ws";
		String waasConfDirectory_ = null;
		long waasTimeout_ = 2000;
		DeviceStub deviceStub = new DeviceStub(waasConfDirectory_, waasURL + "/DeviceConf", p_login, p_password, waasTimeout_);
		
		boolean isDataCompressionEnabled = false;
		if(deviceStub!=null){
			isDataCompressionEnabled = deviceStub.isSOAPCompressionSupported();
		}
		
		
		TrafficStub trafficStub = TrafficStub.getInstance(waasConfDirectory_, waasURL + "/TrafficStats", p_login, p_password, waasTimeout_, isDataCompressionEnabled);
		CIFSStub cifsStub = new CIFSStub(waasConfDirectory_, waasURL + "/CIFSStats", p_login, p_password, waasTimeout_, isDataCompressionEnabled);
		HttpStub httpStub = new HttpStub(waasConfDirectory_, waasURL + "/HttpStats", p_login, p_password, waasTimeout_, isDataCompressionEnabled);
		MapiStub mapiStub = new MapiStub(waasConfDirectory_, waasURL + "/MapiStats", p_login, p_password, waasTimeout_, isDataCompressionEnabled);
		NfsStub nfsStub = new NfsStub(waasConfDirectory_, waasURL + "/NfsStats", p_login, p_password, waasTimeout_, isDataCompressionEnabled);
		VideoStub videoStub = new VideoStub(waasConfDirectory_, waasURL + "/VideoStats", p_login, p_password, waasTimeout_, isDataCompressionEnabled);
		DeviceStatus deviceStatusStub = new DeviceStatus(waasConfDirectory_, waasURL + "/DeviceStatus", p_login, p_password, waasTimeout_, isDataCompressionEnabled);
		if (deviceStub.isSOAPCompressionSupported()) {
			trafficStub.enableDataCompression();
			cifsStub.enableDataCompression();
			httpStub.enableDataCompression();
			mapiStub.enableDataCompression();
			nfsStub.enableDataCompression();
			videoStub.enableDataCompression();
			deviceStatusStub.enableDataCompression();
		}

		try {

			monotenantDiscovery(p_host, deviceStub, trafficStub, cifsStub, deviceStatusStub, httpStub, mapiStub, nfsStub, videoStub, p_login, p_password);
			// updating monitored applications
			updateMonitoredApps();
			//update wae_Map_List
			updateWaeCapabilityMap();
			//update wae group list
			updateWaeGroupList();
			
			
			return true;
		} catch (WAASPermissionDeniedError e) {
			CiscoWAASDiscoverer.LOGGER.info("Not enough rights for the WAAS credentials: " + p_login);
			return false;
		} catch (WAASAuthenticationError e) {
			CiscoWAASDiscoverer.LOGGER.info("Wrong WAAS credentials: " + p_login);
			return false;
		} catch (WAASTimeoutError e) {
			throw e;
		} catch (WAASTransportError e) {
			throw e;
		}
	}

	/*
	 * private Device[] removeWAEFromName(Device[] devices, String name) { for (int i = 0; i < devices.length; i++) { Device device = devices[i]; if (device.getName().equals(name)) {
	 * System.arraycopy(devices, i + 1, devices, i, devices.length - 1 - i); return Arrays.copyOf(devices, devices.length - 1); } } return devices; }
	 */

	private void removeWAEFromName(Hashtable<DeviceGroup, LinkedList<Device>> waeListPerDeviceGroup, String name) {
		Enumeration<DeviceGroup> deviceGroupEnum = waeListPerDeviceGroup.keys();
		while (deviceGroupEnum.hasMoreElements()) {
			DeviceGroup deviceGroup = deviceGroupEnum.nextElement();
			// Browse the WAE List
			LinkedList<Device> devices = waeListPerDeviceGroup.get(deviceGroup);
			Iterator<Device> deviceIt = devices.iterator();
			while (deviceIt.hasNext()) {
				Device device = deviceIt.next();
				if (device.getName().equals(name)) {
					devices.remove(device);
					return;
				}
			}
		}
	}

	/**
	 * The mono-tenant discovery
	 * 
	 * @throws WAASAuthenticationError
	 * @throws WAASTransportError
	 */
	public void monotenantDiscovery(String p_host, DeviceStub p_deviceStub, TrafficStub p_trafficStub, CIFSStub p_cifsStub, DeviceStatus p_deviceStatusStub, HttpStub p_httpStub, MapiStub p_mapiStub,
			NfsStub p_nfsStub, VideoStub p_videoStub, String p_login, String p_password) throws WAASPermissionDeniedError, WAASAuthenticationError, WAASTransportError, WAASTimeoutError {
		try {
			// Check for Central manager and WAE
			Device[] devices = p_deviceStub.getWANInfo();
			
			DeviceGroup[] waeGroups = p_deviceStub.getDeviceGroupsResponse().get_return();
			for(DeviceGroup dgroup : waeGroups){
				String[] grp = new String[3];
				grp[0] = dgroup.getName();
				grp[1] = dgroup.getGroupType().toLowerCase();
				waeGroups_.add(grp);
			}
			
			boolean is5minCapable = false;
			if (devices == null) {
				CiscoWAASDiscoverer.LOGGER.info("Unable to retrieve WAAS content informations from the getWANInfo method");
				throw new WAASPermissionDeniedError("Unable to retrieve WAAS content informations from the getWANInfo method");
			}
			try {
				String API = p_deviceStub.getAPIVersion();
				// No exception ?
				// it is 5min compatible
				is5minCapable = true;
			} catch (Exception e) {
				LOGGER.info("Api version is not 5min capable");
			}
			// Check for central manager
			Device centralManager = null;
			boolean CMfound = false;
			for (int i = 0; i < devices.length && !CMfound; i++) {
				Device device = devices[i];
				if (device.getType().equalsIgnoreCase("cm")) {
					centralManager = device;
					CMfound = true;
				}
			}
			if (centralManager == null) {
				CiscoWAASDiscoverer.LOGGER.info("Unable to retrieve centralManager content informations from the getWANInfo method");
				throw new WAASPermissionDeniedError("Unable to retrieve centralManager content informations from the getWANInfo method");
			}
			LinkedList<String> locationList = new LinkedList<String>();
			LinkedList<String> applicationGroupList = new LinkedList<String>();
			try {
				MonitoredApps[] monitoredApps = p_trafficStub.getMonitoredApplications();
				if (monitoredApps != null) {
					for (int i = 0; i < monitoredApps.length; i++) {
						MonitoredApps monitoredApp = monitoredApps[i];
						if (monitoredApp.getMonitored() && !applicationGroupList.contains(monitoredApp.getApplicationName())) {
							String appName = monitoredApp.getApplicationName();
							applicationGroupList.add(appName);
							monitoredApps_.add(new String[] { appName, String.valueOf(monitoredApp.getMonitored()) });
						}

					}
				}
				if (! ( applicationGroupList.contains("Other") || applicationGroupList.contains("Other Traffic") ) ){
					applicationGroupList.add("Other Traffic");
					monitoredApps_.add(new String[]{"Other Traffic", "true"});
				}
					
			} catch (WAASPermissionDeniedError e) {
				CiscoWAASDiscoverer.LOGGER.info("Not enough rights to retrieve monitored applications");
				throw e;
			} catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.info("Unable to retrieve monitored applications");
			}
			int nbOfDevicesFound = 0;
			Instance waeGroup = new WAEGroup();
			waeGroup.addAttribute(Keyword.DISCOVERYIP, p_host);
			waeGroup.addAttribute(Keyword.NAME, centralManager.getName());
			waeGroup.addAttribute(Keyword.IP, centralManager.getIpAddress());
			waeGroup.setTag("WAE_GP_" + centralManager.getIpAddress());
			waeGroup.addAttribute(Keyword.INSTANCE_NAME, "WAE GP " + centralManager.getName());
			addInstance(Instance.WAE_GROUP_TYPE, waeGroup);
			//waeGroups_.add(new String[]{centralManager.getName(), Keyword.WAE_GROUP_TYPE});
			
			if (devices != null) {
				CiscoWAASDiscoverer.LOGGER.info("WAAS web services detected with login " + p_login);
				// Set the capabilities in order to create the host as the main Device Group (monotenant)
				// p_host.node.addCapability("WAAS-Monotenant");
				// Check for layer7 capability
				// First browse discovered WAEs in order to check their layer7 capability
				Vector<Long> idsToCheck = new Vector<Long>();
				for (int i = 0; i < devices.length; i++) {
					Device device = devices[i];
					if (device.getType().equalsIgnoreCase("WAE")) {
						idsToCheck.add(device.getId());
						
						ArrayList<String> capabilities = new ArrayList<String>();
						HashMap<String, ArrayList<String>> ipMap = new HashMap<String, ArrayList<String>>();
						ipMap.put(device.getIpAddress(), capabilities);
						wae_list_map.put(device.getName(), ipMap);
					}
				}
				long[] ids = new long[idsToCheck.size()];
				for (int i = 0; i < idsToCheck.size(); i++) {
					ids[i] = idsToCheck.get(i);
				}
				Hashtable<String, Vector<String>> layer7CapabilityByHost = new Hashtable<String, Vector<String>>();
				// Hashtable<String,String> layer7CapabilityToCheck=new Hashtable<String, String>();
				HashSet<String> layer7capaWithNotEnoughRights = new HashSet<String>();
				try {
					MonitoredAO[] monitoredAOs = p_deviceStatusStub.getMonitoredAOsByWaeIds(ids);
					int numberOfRequests = monitoredAOs.length;
					for (int i = 0; i < monitoredAOs.length; i++) {
						/*
						 * MonitoredAO monitoredAO=monitoredAOs[i]; String waeToTest=monitoredAO.getDeviceName(); if (!layer7CapabilityByHost.containsKey(waeToTest))
						 * layer7CapabilityByHost.put(waeToTest, new Vector<String>()); Vector<String> layer7capabilities=layer7CapabilityByHost.get(waeToTest); if
						 * (monitoredAO.getCifsEnabled()&&!layer7capabilities.contains("WAAS_CIFS")){ layer7capabilities.add("WAAS_CIFS"); if (!layer7CapabilityToCheck.containsKey("WAAS_CIFS"))
						 * layer7CapabilityToCheck.put("WAAS_CIFS", waeName); } if (monitoredAO.getHttpEnabled()&&!layer7capabilities.contains("WAAS_HTTP")){ layer7capabilities.add("WAAS_HTTP"); if
						 * (!layer7CapabilityToCheck.containsKey("WAAS_HTTP")) layer7CapabilityToCheck.put("WAAS_HTTP", waeName); } if
						 * (monitoredAO.getMapiEnabled()&&!layer7capabilities.contains("WAAS_MAPI")){ layer7capabilities.add("WAAS_MAPI"); if (!layer7CapabilityToCheck.containsKey("WAAS_MAPI"))
						 * layer7CapabilityToCheck.put("WAAS_MAPI", waeName); } if (monitoredAO.getNfsEnabled()&&!layer7capabilities.contains("WAAS_NFS")){ layer7capabilities.add("WAAS_NFS"); if
						 * (!layer7CapabilityToCheck.containsKey("WAAS_NFS")) layer7CapabilityToCheck.put("WAAS_NFS", waeName); } if
						 * (monitoredAO.getSslEnabled()&&!layer7capabilities.contains("WAAS_SSL")){ layer7capabilities.add("WAAS_SSL"); if (!layer7CapabilityToCheck.containsKey("WAAS_SSL"))
						 * layer7CapabilityToCheck.put("WAAS_SSL", waeName); } if (monitoredAO.getVideoEnabled()&&!layer7capabilities.contains("WAAS_VideoStreaming")){
						 * layer7capabilities.add("WAAS_VideoStreaming"); if (!layer7CapabilityToCheck.containsKey("WAAS_VideoStreaming")) layer7CapabilityToCheck.put("WAAS_VideoStreaming", waeName);
						 * }
						 */
						numberOfRequests--;
						LOGGER.info("Number of topology requests remaining : " + numberOfRequests);
						MonitoredAO monitoredAO = monitoredAOs[i];
						
						String waeToTest = monitoredAO.getDeviceName();
						String Ip  = p_deviceStub.getWAEByName(waeToTest).getIpAddress();
						
						if (!layer7CapabilityByHost.containsKey(waeToTest))
							layer7CapabilityByHost.put(waeToTest, new Vector<String>());
						Vector<String> layer7capabilities = layer7CapabilityByHost.get(waeToTest);

						if (!layer7capaWithNotEnoughRights.contains("WAAS_NFS")) {
							try {
								com.cisco.waas.wsc.NfsStatsStub.TimeFrameFilter timeFrameFilter = new com.cisco.waas.wsc.NfsStatsStub.TimeFrameFilter();
								timeFrameFilter.setFrequency("custom");
								Calendar before = Calendar.getInstance();
								before.add(Calendar.DAY_OF_MONTH, -1 * waasTTL);
								timeFrameFilter.setStartTime(before);
								before = Calendar.getInstance();
								timeFrameFilter.setEndTime(before);
								NfsRespTypeStats[] stats = p_nfsStub.retrieveHistoricalStats(waeToTest, "wae", timeFrameFilter);
								if (stats.length > 0){
									layer7capabilities.add("WAAS_NFS");
									
									wae_list_map.get(waeToTest).get(Ip).add(Keyword.NFS);
								}

							} catch (WAASPermissionDeniedError e) {
								CiscoWAASDiscoverer.LOGGER.info("Not enough rights to report on NFS capability with user " + p_deviceStub.getUserName());
								layer7capaWithNotEnoughRights.add("WAAS_NFS");
							} catch (Exception e) {
								if (e.getMessage() != null && e.getMessage().indexOf("No Statistics available for the duration") < 0) {
									CiscoWAASDiscoverer.LOGGER.info("Problem when trying to report on NFS capability on WAE:" + waeToTest + ". Discarding WAE.");
									// devices = removeWAEFromName(devices, waeToTest);
								} else {
									CiscoWAASDiscoverer.LOGGER.info("No statistic available for NFS capability on WAE:" + waeToTest + "." + e.getMessage());
								}
							}
						}

						if (!layer7capaWithNotEnoughRights.contains("WAAS_CIFS")) {
							try {
								com.cisco.waas.wsc.CIFSStatsStub.TimeFrameFilter timeFrameFilter = new com.cisco.waas.wsc.CIFSStatsStub.TimeFrameFilter();
								timeFrameFilter.setFrequency("custom");
								Calendar before = Calendar.getInstance();
								before.add(Calendar.DAY_OF_MONTH, -1 * waasTTL);
								timeFrameFilter.setStartTime(before);
								before = Calendar.getInstance();
								timeFrameFilter.setEndTime(before);
								HitRateStats[] stats = p_cifsStub.retrieveRequestHitRate(waeToTest, "wae", "optimized", "bidirectional", timeFrameFilter);
								if (stats.length > 0){
									layer7capabilities.add("WAAS_CIFS");
									wae_list_map.get(waeToTest).get(Ip).add(Keyword.CIFS);
								}
									
							} catch (WAASPermissionDeniedError e) {
								CiscoWAASDiscoverer.LOGGER.info("Not enough rights to report on CIFS capability with user " + p_deviceStub.getUserName(), e);
								layer7capaWithNotEnoughRights.add("WAAS_CIFS");
							} catch (Exception e) {
								if (e.getMessage() != null && e.getMessage().indexOf("No Statistics available for the duration") < 0) {
									CiscoWAASDiscoverer.LOGGER.debug("Problem when trying to report on CIFS capability on WAE:" + waeToTest + ". Discarding WAE.", e);
									// devices = removeWAEFromName(devices, waeToTest);
								} else {
									CiscoWAASDiscoverer.LOGGER.info("No statistic available for CIFS capability on WAE:" + waeToTest + "." + e.getMessage(), e);
								}
							}
						}

						if (!layer7capaWithNotEnoughRights.contains("WAAS_HTTP")) {
							try {
								com.cisco.waas.wsc.HttpStatsStub.TimeFrameFilter timeFrameFilter = new com.cisco.waas.wsc.HttpStatsStub.TimeFrameFilter();
								timeFrameFilter.setFrequency("custom");
								Calendar before = Calendar.getInstance();
								before.add(Calendar.DAY_OF_MONTH, -1 * waasTTL);
								timeFrameFilter.setStartTime(before);
								before = Calendar.getInstance();
								timeFrameFilter.setEndTime(before);
								HttpConnStats[] stats = p_httpStub.retrieveStats(waeToTest, "wae", timeFrameFilter);
								if (stats.length > 0){
									layer7capabilities.add("WAAS_HTTP");
									wae_list_map.get(waeToTest).get(Ip).add(Keyword.HTTP);
								}
							} catch (WAASPermissionDeniedError e) {
								CiscoWAASDiscoverer.LOGGER.info("Not enough rights to report on HTTP capability with user " + p_deviceStub.getUserName());
								layer7capaWithNotEnoughRights.add("WAAS_HTTP");
							} catch (Exception e) {
								if (e.getMessage() != null && e.getMessage().indexOf("No Statistics available for the duration") < 0) {
									CiscoWAASDiscoverer.LOGGER.info("Problem when trying to report on HTTP capability on WAE:" + waeToTest + ". Discarding WAE.");
									// devices = removeWAEFromName(devices, waeToTest);
								} else {
									CiscoWAASDiscoverer.LOGGER.info("No statistic available for HTTP capability on WAE:" + waeToTest + "." + e.getMessage());
								}
							}
						}

						if (!layer7capaWithNotEnoughRights.contains("WAAS_MAPI")) {
							try {
								com.cisco.waas.wsc.MapiStatsStub.TimeFrameFilter timeFrameFilter = new com.cisco.waas.wsc.MapiStatsStub.TimeFrameFilter();
								timeFrameFilter.setFrequency("custom");
								Calendar before = Calendar.getInstance();
								before.add(Calendar.DAY_OF_MONTH, -1 * waasTTL);
								timeFrameFilter.setStartTime(before);
								before = Calendar.getInstance();
								timeFrameFilter.setEndTime(before);
								MapiRequestTypeStats[] stats = p_mapiStub.retrieveRequestTypeStats(waeToTest, "wae", timeFrameFilter);
								if (stats.length > 0){
									layer7capabilities.add("WAAS_MAPI");
									wae_list_map.get(waeToTest).get(Ip).add(Keyword.MAPI);
								}
							} catch (WAASPermissionDeniedError e) {
								CiscoWAASDiscoverer.LOGGER.info("Not enough rights to report on MAPI capability with user " + p_deviceStub.getUserName());
								layer7capaWithNotEnoughRights.add("WAAS_MAPI");
							} catch (Exception e) {
								if (e.getMessage() != null && e.getMessage().indexOf("No Statistics available for the duration") < 0) {
									CiscoWAASDiscoverer.LOGGER.info("Problem when trying to report on MAPI capability on WAE:" + waeToTest + ". Discarding WAE.");
									// devices = removeWAEFromName(devices, waeToTest);
								} else {
									CiscoWAASDiscoverer.LOGGER.info("No statistic available for MAPI capability on WAE:" + waeToTest + "." + e.getMessage());
								}
							}
						}

						if (!layer7capaWithNotEnoughRights.contains("WAAS_VideoStreaming")) {
							try {
								com.cisco.waas.wsc.VideoStatsStub.TimeFrameFilter timeFrameFilter = new com.cisco.waas.wsc.VideoStatsStub.TimeFrameFilter();
								timeFrameFilter.setFrequency("custom");
								Calendar before = Calendar.getInstance();
								before.add(Calendar.DAY_OF_MONTH, -1 * waasTTL);
								timeFrameFilter.setStartTime(before);
								before = Calendar.getInstance();
								timeFrameFilter.setEndTime(before);
								VideoStats[] stats = p_videoStub.retrieveHistoricalStats(waeToTest, "wae", timeFrameFilter);
								if (stats.length > 0){
									layer7capabilities.add("WAAS_VideoStreaming");
									wae_list_map.get(waeToTest).get(Ip).add(Keyword.VIDEO);
								}
							} catch (WAASPermissionDeniedError e) {
								CiscoWAASDiscoverer.LOGGER.info("Not enough rights to report on Video Streaming capability with user " + p_deviceStub.getUserName());
								layer7capaWithNotEnoughRights.add("WAAS_VideoStreaming");
							} catch (Exception e) {
								if (e.getMessage() != null && e.getMessage().indexOf("No Statistics available for the duration") < 0) {
									CiscoWAASDiscoverer.LOGGER.info("Problem when trying to report on Video Streaming capability on WAE:" + waeToTest + ". Discarding WAE.");
									// devices = removeWAEFromName(devices, waeToTest);
								} else {
									CiscoWAASDiscoverer.LOGGER.info("No statistic available for Video Streaming capability on WAE:" + waeToTest + "." + e.getMessage());
								}
							}
						}

					}
				} catch (WAASPermissionDeniedError e) {
					CiscoWAASDiscoverer.LOGGER.info("Not enough rights to detect layer 7 capabilities with user " + p_deviceStub.getUserName() + ", discarding WAEs");
				} catch (WAASTimeoutError e) {
					CiscoWAASDiscoverer.LOGGER.info("Timeout when retrieving layer 7 capabilities on central manager " + centralManager.getHostName() + ", discarding WAEs");
				} catch (Exception e) {
					CiscoWAASDiscoverer.LOGGER.info("Error when retrieving layer 7 capabilities on central manager " + centralManager.getHostName() + ", discarding WAEs");
				}

				// Create the associated service
				Instance service = new OptimizationService();
				service.addAttribute(Keyword.DISCOVERYIP, p_host);
				if (centralManager != null)
					service.addAttribute(Keyword.CM_NAME, centralManager.getName());
				service.setTag("SERVICE_" + centralManager.getIpAddress());
				service.addAttribute(Keyword.INSTANCE_NAME, "Service " + centralManager.getName());
				service.addAttribute(Keyword.NAME, centralManager.getName());
				waeGroup.addAttribute(Keyword.SERVICEID, service.getTag());
				waeGroup.addAttribute(Keyword.MEMBER_OF_GROUP, service.getTag());
				addInstance(Instance.OPTIMIZATION_SERVICE, service);
				/*
				 * service.getCapabilities().addCapability("WAAS-Service"); service.setMustBeCreated(true); if (is5minCapable) service.getCapabilities().addCapability("WAAS_5min");
				 * p_host.node.putAdditionalProperties(service);
				 */
				// Set the service ID on the host
				/*
				 * AtiIPDAdditionalProperties serviceID = new AtiIPDAdditionalProperties(p_host.address(), p_host.address()); serviceID.addLinkToProxy(Keyword.SERVICE_ID.getKeyword(), p_host.address()
				 * + ":Service", AtiIPDAdditionalProperties.generateRelationCode(false, true, false)); serviceID.setMustBeCreated(false); p_host.node.putAdditionalProperties(serviceID);
				 */
				// Manage the WAE list
				for (int i = 0; i < devices.length; i++) {
					Device device = devices[i];
					if (device.getType().equalsIgnoreCase("WAE")) {
						// Create WAE devices
						nbOfDevicesFound++;
						Instance site = new Site();
						site.addAttribute(Keyword.DISCOVERYIP, p_host);
						site.addAttribute(Keyword.NAME, device.getLocation());
						site.setTag("SITE_" + device.getLocation());
						site.addAttribute(Keyword.INSTANCE_NAME, device.getLocation());
						addInstance(Instance.SITE_TYPE, site);

						Instance wae = new WAE();
						wae.addAttribute(Keyword.IP, device.getIpAddress());
						wae.addAttribute(Keyword.SOAPUSER, p_login);
						wae.addAttribute(Keyword.SOAPURL, "https://" + p_host + ":8443");
						wae.addAttribute(Keyword.NAME, device.getName());
						wae.addAttribute(Keyword.SERVICEID, service.getTag());
						wae.addAttribute(Keyword.WAE_GROUP_ID, waeGroup.getTag());
						wae.addAttribute(Keyword.MEMBER_OF_GROUP, waeGroup.getTag());
						wae.addAttribute(Keyword.LOCATION, device.getLocation());
						wae.setTag(device.getIpAddress());
						wae.addAttribute(Keyword.INSTANCE_NAME, "WAE " + device.getName());
						Vector<String> capabilities = layer7CapabilityByHost.get(device.getName());
						StringBuilder capablityString = new StringBuilder("WAE");
						if (capabilities != null) {
							for (String value : capabilities) {
								capablityString.append(MULTI_VALUE_SEPARATOR).append(value);
							}
						}

						wae.addAttribute(Keyword.CAPABILITIES, capablityString.toString());
						/*
						 * wae.getCapabilities().addCapability("WAE"); if (is5minCapable) wae.getCapabilities().addCapability("WAAS_5min");
						 */

						/*
						 * wae.addLinkToBasic(Keyword.DEVICEGROUP_ID.getKeyword(), p_host.address()).setEmitOnNodeOnly(true); wae.addLinkToProxy(Keyword.SERVICE_ID.getKeyword(), p_host.address() +
						 * ":Service", AtiIPDAdditionalProperties.generateRelationCode(false, true, false)); wae.addLinkToProxy(Keyword.LOCATION_ID.getKeyword(), "Site:" + device.getLocation(),
						 * AtiIPDAdditionalProperties.generateRelationCode(false, true, false));
						 *//*
							 * Vector<String> info = p_host.getIntegrationInfo(); if (info != null) { Iterator<String> infoIt = info.iterator(); String infoString = ""; while (infoIt.hasNext())
							 * infoString = "," + infoIt.next(); if (infoString.length() > 1) wae.addAttribute(Keyword.INTEGRATIONINFO, infoString.substring(1)); }
							 */

						/*
						 * String integrationInfoId = AtiIPDWAASProcessor.getWAEIntegrationCapa(p_host); String integrationCapa = Integration.getDiscoInfo().get(integrationInfoId); if (integrationCapa
						 * != null ) wae.addAttribute(Keyword.INTEGRATIONINFO, integrationCapa);
						 */
						// Stack locations in order to create them later
						addInstance(Instance.WAE_TYPE, wae);
						if (!locationList.contains(device.getLocation()))
							locationList.add(device.getLocation());
						// Manage the applications list
						try {
							int configuredApplications = 0;
							Iterator<String> appIt = applicationGroupList.iterator();
							while (appIt.hasNext()) {
								String applicationName = appIt.next();

								// Create application group
								Instance applicationGroup = new ApplicationGroup();
								applicationGroup.addAttribute(Keyword.DISCOVERYIP, p_host);
								applicationGroup.addAttribute(Keyword.NAME, applicationName);
								applicationGroup.addAttribute(Keyword.CM_IP, p_host);
								applicationGroup.addAttribute(Keyword.WAE_GROUP_ID, waeGroup.getTag());
								applicationGroup.setTag("APP_GP_" + centralManager.getIpAddress() + "_" + applicationName);
								applicationGroup.addAttribute(Keyword.INSTANCE_NAME, "APP GP " + applicationName + " " + centralManager.getName());
								if (centralManager != null)
									applicationGroup.addAttribute(Keyword.CM_NAME, centralManager.getName());
								// Set device group ID
								applicationGroup.addAttribute(Keyword.DEVICEGROUP_ID, p_host);
								applicationGroup.addAttribute(Keyword.SERVICEID, service.getTag());
								// Set service ID
								/*
								 * applicationGroup.addLinkToProxy(Keyword.SERVICE_ID.getKeyword(), p_host.address() + ":Service", AtiIPDAdditionalProperties.generateRelationCode(false, true, false))
								 * .setEmitOnNodeOnly(true); applicationGroup.setMustBeCreated(true); if (is5minCapable) applicationGroup.getCapabilities().addCapability("WAAS_5min");
								 */
								addInstance(Instance.APPLICATION_GROUP_TYPE, applicationGroup);

								Instance application = new OAM();
								application.addAttribute(Keyword.NAME, applicationName);
								application.setTag("APP_" + device.getIpAddress() + "_" + applicationName);
								application.addAttribute(Keyword.INSTANCE_NAME, "APP " + applicationName + " " + wae.getValue(Keyword.INSTANCE_NAME));
								application.addAttribute(Keyword.APPLICATION_MEASUREMENT_NAME, applicationName);
								application.addAttribute(Keyword.APPLICATION_GROUP, applicationGroup.getTag());
								application.addAttribute(Keyword.SERVICEID, service.getTag());
								application.addAttribute(Keyword.WAEIP, device.getIpAddress());
								/*
								 * application.addLinkToBasic(Keyword.DEVICEGROUP_ID.getKeyword(), p_host.address()).setEmitOnNodeOnly(true);
								 * application.addLinkToProxy(Keyword.SERVICE_ID.getKeyword(), p_host.address() + ":Service", AtiIPDAdditionalProperties.generateRelationCode(false, true, false));
								 * application.addLinkToProxy(Keyword.APPLICATIONGROUP_ID.getKeyword(), "ApplicationGroup:" + applicationName);
								 * application.addLinkToProxy(Keyword.LOCATION_ID.getKeyword(), "Site:" + device.getLocation()); if (is5minCapable)
								 * application.getCapabilities().addCapability("WAAS_5min");
								 */
								addInstance(Instance.OAM, application);
								configuredApplications++;
							}
							if (configuredApplications == 0)
								CiscoWAASDiscoverer.LOGGER.info("Warning: no applications configured on WAE " + device.getName());
						} catch (Exception e) {
							CiscoWAASDiscoverer.LOGGER.info("Error when retrieving application on WAE " + device.getName());
						}
						/*
						 * // Check for layer7 capabilities if (layer7CapabilityByHost.containsKey(device.getName())) { Vector<String> layer7capabilities =
						 * layer7CapabilityByHost.get(device.getName()); String layer7capaS = null; int nbOfLayer7Detected = 0; for (int j = 0; j < layer7capabilities.size(); j++) { if
						 * (!layer7capaWithNotEnoughRights.contains(layer7capabilities.get(j))) { nbOfLayer7Detected++; wae.getCapabilities().addCapability(layer7capabilities.get(j)); if (layer7capaS
						 * == null) layer7capaS = layer7capabilities.get(j); else layer7capaS = layer7capaS + "," + layer7capabilities.get(j); } } if (nbOfLayer7Detected > 0)
						 * CiscoWAASDiscoverer.LOGGER.info(layer7capaS + " capabilities detected on WAE " + device.getName()); }
						 */
						// Reinject wae devices to the discovery engine (mainly in order to do some snmp polling)
						ipList.add(device.getIpAddress());
					}
				}
				if (centralManager != null)
					cmName = centralManager.getName();
			}

			

		} catch (WAASPermissionDeniedError e) {
			throw e;
		} catch (WAASAuthenticationError e) {
			throw e;
		} catch (WAASTransportError e) {
			throw e;
		} catch (WAASTimeoutError e) {
			throw e;
		} catch (Exception e) {
			CiscoWAASDiscoverer.LOGGER.info("Error when retrieving CM and WAEs");
		}
		generateTopoFile();

	}

	private void updateMonitoredApps() {
		CiscoWAASDiscoverer.LOGGER.info("Updating Monitored apps");
		CsvWriter writer = new CsvWriter(FileNameRetriever.getMonitoredAppsFileName());
		writer.writeList(monitoredApps_);
	}
	
	private void updateWaeCapabilityMap() {
		CiscoWAASDiscoverer.LOGGER.info("Updating Wae_Capability_Map_List apps");
		CsvWriter writer = new CsvWriter(FileNameRetriever.getWaeCapabilityMapFileName());
		writer.writeWaeMap(wae_list_map);
	}
	
	private void updateWaeGroupList() {
		CiscoWAASDiscoverer.LOGGER.info("Updating WaeGroup apps");
		CsvWriter writer = new CsvWriter(FileNameRetriever.getWaeGroupFileName());
		writer.writeList(waeGroups_);
	}
	
	private void generateTopoFile() {
		// Generate WAEGroup
		try {
			Set<Instance> instanceSet;
			List<String> outputLines = new ArrayList<String>();
			for (String instanceType : Instance.ORDERED_INSTANCE_TYPES) {
				try {
					instanceSet = typeInstanceMap.get(instanceType);

					if (instanceSet != null) {
						addOutputLines(instanceSet, outputLines);
					}

				} catch (Exception e5) {
					CiscoWAASDiscoverer.LOGGER.info("Error writing " + instanceType + " Topo");
				}
			}
			CiscoWAASDiscoverer.LOGGER.info("Creating file  : topology.csv");
			FileUtils.writeLines(new File(FileNameRetriever.getOutputTopoFolderName(), "topology.csv"), outputLines);
			
			
			instanceSet = typeInstanceMap.get(Instance.OAM);
			if(instanceSet != null){
				outputLines.clear();
				addOutputLines(instanceSet, outputLines);
			}
			CiscoWAASDiscoverer.LOGGER.info("Creating file : topology_OAM.csv");
			FileUtils.writeLines(new File(FileNameRetriever.getOutputTopoFolderName(),"topology_OAM.csv"),outputLines);
			
		} catch (IOException e) {
			CiscoWAASDiscoverer.LOGGER.info("Error writing topology.csv");
		}

	}

	private void addOutputLines(Set<Instance> instanceSet, List<String> outputLines) {
		boolean headerWritten = false;
		for (Instance instance : instanceSet) {
			if (!headerWritten) {
				outputLines.add(instance.getHeader());
				headerWritten = true;
			}
			outputLines.add(instance.toString());
		}
		outputLines.add(EMTPY_STRING);
	}

	private void addInstance(String serviceType, Instance service) {
		Set<Instance> set = typeInstanceMap.get(serviceType);
		if (set == null) {
			set = new HashSet<Instance>();
			typeInstanceMap.put(serviceType, set);
		}
		set.add(service);
	}

	public static void main(String[] args) {
		String p_host = "localhost";
		String CMP_IP = "192.168.1.36";
		Instance site = new Site();
		site.addAttribute(Keyword.DISCOVERYIP, p_host);
		String location = "location";
		site.addAttribute(Keyword.NAME, location);
		site.setTag("SITE_" + location);
		site.addAttribute(Keyword.INSTANCE_NAME, location);

		CiscoWAASDiscoverer.LOGGER.info(site.getHeader());
		CiscoWAASDiscoverer.LOGGER.info(site.toString());

		Instance service = new OptimizationService();
		service.addAttribute(Keyword.DISCOVERYIP, p_host);
		String CM_NAME = "cm_name";
		service.addAttribute(Keyword.CM_NAME, CM_NAME);
		service.setTag("SERVICE_" + CMP_IP);
		service.addAttribute(Keyword.INSTANCE_NAME, "Service " + CM_NAME);
		service.addAttribute(Keyword.NAME, CM_NAME);
		CiscoWAASDiscoverer.LOGGER.info(service.getHeader());
		CiscoWAASDiscoverer.LOGGER.info(service.toString());

		Instance waeGroup = new WAEGroup();

		waeGroup.addAttribute(Keyword.DISCOVERYIP, p_host);
		waeGroup.addAttribute(Keyword.NAME, "abc");
		waeGroup.addAttribute(Keyword.IP, CMP_IP);
		waeGroup.addAttribute(Keyword.SERVICEID, CMP_IP);
		waeGroup.setTag("WAE_GP_" + CMP_IP);
		waeGroup.addAttribute(Keyword.INSTANCE_NAME, "WAE GP " + "abc");
		CiscoWAASDiscoverer.LOGGER.info(waeGroup.getHeader());
		CiscoWAASDiscoverer.LOGGER.info(waeGroup.toString());

		Instance wae = new WAE();
		wae.addAttribute(Keyword.IP, CMP_IP);
		wae.addAttribute(Keyword.SOAPUSER, "abc");
		wae.addAttribute(Keyword.SOAPURL, "https://" + p_host + ":8443");
		wae.addAttribute(Keyword.NAME, CM_NAME);
		wae.addAttribute(Keyword.SERVICEID, service.getTag());
		wae.addAttribute(Keyword.WAE_GROUP_ID, waeGroup.getTag());
		wae.addAttribute(Keyword.MEMBER_OF_GROUP, waeGroup.getTag());
		wae.addAttribute(Keyword.LOCATION, location);
		wae.setTag(CMP_IP);
		wae.addAttribute(Keyword.INSTANCE_NAME, "WAE " + CM_NAME);
		CiscoWAASDiscoverer.LOGGER.info(wae.getHeader());
		CiscoWAASDiscoverer.LOGGER.info(wae.toString());

		// Create application group
		Instance applicationGroup = new ApplicationGroup();
		applicationGroup.addAttribute(Keyword.DISCOVERYIP, p_host);
		String applicationName = "APP 1";
		applicationGroup.addAttribute(Keyword.NAME, applicationName);
		applicationGroup.addAttribute(Keyword.CM_IP, p_host);
		applicationGroup.addAttribute(Keyword.WAE_GROUP_ID, waeGroup.getTag());
		applicationGroup.setTag("APP_GP_" + CMP_IP + "_" + applicationName);
		applicationGroup.addAttribute(Keyword.INSTANCE_NAME, "APP GP " + applicationName + " " + CM_NAME);
		applicationGroup.addAttribute(Keyword.CM_NAME, CM_NAME);
		// Set device group ID
		applicationGroup.addAttribute(Keyword.DEVICEGROUP_ID, p_host);
		applicationGroup.addAttribute(Keyword.SERVICEID, service.getTag());

		CiscoWAASDiscoverer.LOGGER.info(applicationGroup.getHeader());
		CiscoWAASDiscoverer.LOGGER.info(applicationGroup.toString());

		// Set service ID
		/*
		 * applicationGroup.addLinkToProxy(Keyword.SERVICE_ID.getKeyword(), p_host.address() + ":Service", AtiIPDAdditionalProperties.generateRelationCode(false, true, false))
		 * .setEmitOnNodeOnly(true); applicationGroup.setMustBeCreated(true); if (is5minCapable) applicationGroup.getCapabilities().addCapability("WAAS_5min");
		 */

		Instance application = new OAM();
		application.addAttribute(Keyword.NAME, applicationName);
		application.setTag("APP_" + CMP_IP + "_" + applicationName);
		application.addAttribute(Keyword.INSTANCE_NAME, "APP " + applicationName + " " + wae.getValue(Keyword.INSTANCE_NAME));
		application.addAttribute(Keyword.APPLICATION_MEASUREMENT_NAME, applicationName);
		application.addAttribute(Keyword.APPLICATION_GROUP, applicationGroup.getTag());
		application.addAttribute(Keyword.SERVICEID, service.getTag());
		CiscoWAASDiscoverer.LOGGER.info(application.getHeader());
		CiscoWAASDiscoverer.LOGGER.info(application.toString());

	}

}