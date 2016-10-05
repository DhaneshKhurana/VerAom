package com.infovista.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.cisco.waas.wsc.DeviceConfStub.Device;
import com.cisco.waas.wsc.DeviceConfStub.DeviceGroup;
import com.cisco.waas.wsc.DeviceConfStub.GetDeviceGroupsResponse;
import com.cisco.waas.wsc.DeviceConfStub.GetWAEsInGroupResponse;
import com.cisco.waas.wsc.DeviceConfStub.GetWANInfoResponse;
import com.cisco.waas.wsc.HttpStatsStub.RetrieveStatsResponse;
import com.cisco.waas.wsc.RemoteExceptionException;
import com.infovista.configs.CLParser;
import com.infovista.configs.CommonConfig;
import com.infovista.fileutils.CsvWriter;
import com.infovista.fileutils.FileNameRetriever;
import com.infovista.sdk.error.WAASPermissionDeniedError;
import com.infovista.sdk.error.WAASTimeoutError;
import com.infovista.sdk.ssl.FakeSSLProtocolSocketFactory;
import com.infovista.sdk.stub.deviceConf.DeviceStub;
import com.infovista.statsRetriever.StatsPoller;
import com.infovista.statsRetriever.WAEStatsProcessing;;

public class WAEDiscoverer {
	private static final String END_LINE = "\n";
	

	public static void main(String[] args) {
		try {
			//setting it to trur to skip initiliazining wae capablities map and monitored apps. as Their files will be created when discovery prcoess finishes.
			CommonConfig.getInstance().setProcessingTopo(true);
			CLParser.createParser(args);
			//CiscoWAASDiscoverer.LOGGER.info("After parsing arguments in discovery");
			String p_password = CommonConfig.getInstance().getPassword();
			String p_userName = CommonConfig.getInstance().getLogin();
			String p_hostname = CommonConfig.getInstance().getHost();
			String mode = CommonConfig.getInstance().getMode(); 
					
			CiscoWAASDiscoverer.LOGGER.info("username ===> " + p_userName);
			CiscoWAASDiscoverer.LOGGER.info("password ===> " + p_password);
			CiscoWAASDiscoverer.LOGGER.info("host ===> " + p_hostname);
			CiscoWAASDiscoverer.LOGGER.info("Mode ===> " + mode);
			CiscoWAASDiscoverer.LOGGER.info("AR ===> " + CommonConfig.getInstance().getAr());
			if (args.length == 4) {
				mode = args[3];
			}
			boolean multitenant = true;
			if( mode.trim().indexOf("mono")>-1){
				multitenant = false;
			}
			
			try {
				WAEDiscoverer discoverer = new WAEDiscoverer();
				List<String> waeIPList = discoverer.discover(p_hostname, p_userName, p_password, mode.trim());
				
				if (waeIPList.size() == 0) {
					exitWithMessage("No WAE device retrieved from the server");
				}
				writeToFile(waeIPList, "IPList.txt");
				
			} catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.error("Error",e);
			}
			
			CiscoWAASDiscoverer waasDiscoverer = new CiscoWAASDiscoverer();
			waasDiscoverer.retrieveWAASInfo(p_hostname, p_userName, p_password, false);
			
			
			
			
		} catch (Exception exception) {
			CiscoWAASDiscoverer.LOGGER.info("Error while getting response from the server");
			exception.printStackTrace();
		}

		
	}

	private List<String> discover(String p_hostname, String p_userName, String p_password, String mode) throws AxisFault, FileNotFoundException, XMLStreamException, RemoteException, RemoteExceptionException, WAASPermissionDeniedError,
			WAASTimeoutError {
		Protocol.unregisterProtocol("https");
		Protocol.registerProtocol("https", new Protocol("https", new FakeSSLProtocolSocketFactory(), 443));
		int p_timeout = 10000;

		String waasURL = "https://" + p_hostname + ":8443/ws";
		DeviceStub stub = new DeviceStub(null, waasURL + "/DeviceConf", p_userName, p_password, p_timeout);
		List<String> waeIPList = new ArrayList<String>();
		if (mode.equalsIgnoreCase("mono")) {
			GetWANInfoResponse wanInfo = stub.getWANInfoResponse();
			if (wanInfo == null) {
				exitWithMessage("WAN Info not received from the server.");
			}else{
				try {
					OMElement omElement = wanInfo.getOMElement(GetWANInfoResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("GetWANInfoResponse_")), omElement.toString());
				} catch (IOException e) {
					CiscoWAASDiscoverer.LOGGER.debug("error while retrieving device ", e);
				}
			}
			
			Device[] devices = wanInfo.get_return();
			if (devices == null) {
				exitWithMessage("Empty WAN Info received from the server.");
			}
			for (Device device : devices) {
				if (device.getType().equalsIgnoreCase("wae")) {
					waeIPList.add(device.getIpAddress());
				}
			}
			
			GetDeviceGroupsResponse deviceGroupsResponse = stub.getDeviceGroupsResponse();
			if(deviceGroupsResponse!=null){
				try {
					OMElement omElement = deviceGroupsResponse.getOMElement(GetDeviceGroupsResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("GetDeviceGroupsResponse_")), omElement.toString());
				} catch (IOException e) {
					CiscoWAASDiscoverer.LOGGER.debug("error while retrieving device-Groups ", e);
				}
			}
			DeviceGroup[] deviceGroups = deviceGroupsResponse.get_return();
			for (int i = 0; i < deviceGroups.length; i++) {
				DeviceGroup deviceGroup = deviceGroups[i];
				// Retrieve the WAE list for this device group
				try {
					GetWAEsInGroupResponse devicesResponse = stub.getWAEsInGroupResponse(deviceGroup.getId());
					if (devicesResponse != null) {
						OMElement omElement = deviceGroupsResponse.getOMElement(GetDeviceGroupsResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
						FileUtils.writeStringToFile(new File(FileNameRetriever.getxmlFileName("GetWAEsInGroupResponse_" + deviceGroups[i].getName())), omElement.toString());
					}
				} catch (IOException e) {
					CiscoWAASDiscoverer.LOGGER.debug("error while retrieving waes from device-Groups ", e);
				}
			}
			
		} else if (mode.equalsIgnoreCase("multi")) {
			GetDeviceGroupsResponse deviceGroupsResponse = stub.getDeviceGroupsResponse();
			if (deviceGroupsResponse == null) {
				exitWithMessage("DeviceGroup info not received from the server");
			}
			DeviceGroup[] deviceGroups = deviceGroupsResponse.get_return();
			Hashtable<String, LinkedList<String>> notManagedWAE = new Hashtable<String, LinkedList<String>>();
			Hashtable<String, String> managedWAEs = new Hashtable<String, String>();
			Hashtable<DeviceGroup, LinkedList<Device>> waeListPerDeviceGroup = new Hashtable<DeviceGroup, LinkedList<Device>>();
			if (deviceGroups == null) {
				exitWithMessage("Empty device groups received from the server");
			}
			for (int i = 0; i < deviceGroups.length; i++) {
				DeviceGroup deviceGroup = deviceGroups[i];
				LinkedList<Device> deviceList = new LinkedList<Device>();
				waeListPerDeviceGroup.put(deviceGroup, deviceList);
				// Retrieve the WAE list for this device group
				try {
					GetWAEsInGroupResponse devicesResponse = stub.getWAEsInGroupResponse(deviceGroup.getId());
					if (devicesResponse == null) {
						exitWithMessage("Response not received for WAEsInGroup");
					}
					Device[] devices = devicesResponse.get_return();
					if (devices != null) {
						for (int j = 0; j < devices.length; j++) {
							Device device = devices[j];
							deviceList.add(device);
							if (!managedWAEs.containsKey(device.getName())) {
								managedWAEs.put(device.getName(), deviceGroup.getName());
							} else {
								String originalDeviceGroupName = managedWAEs.get(device.getName());
								if (!deviceGroup.getName().equalsIgnoreCase(originalDeviceGroupName)) {
									// This WAE doesn't have to be managed because it belongs to several device groups
									if (notManagedWAE.containsKey(device.getName())) {
										LinkedList<String> originalDeviceGroupList = notManagedWAE.get(device.getName());
										if (!originalDeviceGroupList.contains(deviceGroup.getName()))
											originalDeviceGroupList.add(deviceGroup.getName());
									} else {
										LinkedList<String> deviceGroupList = new LinkedList<String>();
										deviceGroupList.add(originalDeviceGroupName);
										deviceGroupList.add(deviceGroup.getName());
										notManagedWAE.put(device.getName(), deviceGroupList);
									}
								}
							}
						}
					}

				} catch (WAASPermissionDeniedError e) {
					CiscoWAASDiscoverer.LOGGER.info("Not enough rights to retrieve WAEs on deviceGroup " + deviceGroup.getName());
					throw e;
				} catch (WAASTimeoutError e) {
					CiscoWAASDiscoverer.LOGGER.info("Timeout when retrieving WAEs on deviceGroup " + deviceGroup.getName());
					throw e;
				} catch (Exception e) {
					CiscoWAASDiscoverer.LOGGER.info("Error when retrieving WAEs on deviceGroup " + deviceGroup.getName());
					CiscoWAASDiscoverer.LOGGER.error("Error",e);
				}
			}
			// Stack on progress messages all waes that will not be managed because they belong to several device groups
			Enumeration<String> notManagedWAEEnum = notManagedWAE.keys();
			while (notManagedWAEEnum.hasMoreElements()) {
				String notManagedWAEName = notManagedWAEEnum.nextElement();
				String deviceGroupList = "";
				Iterator<String> deviceGroupIt = notManagedWAE.get(notManagedWAEName).iterator();
				while (deviceGroupIt.hasNext()) {
					deviceGroupList += deviceGroupIt.next();
					if (deviceGroupIt.hasNext())
						deviceGroupList += ",";
				}
				CiscoWAASDiscoverer.LOGGER.info("WAE " + notManagedWAEName + " belongs to several deviceGroups (" + deviceGroupList + ")");
				CiscoWAASDiscoverer.LOGGER.info("WAE " + notManagedWAEName + " will not be managed!");
			}

			for (LinkedList<Device> devices : waeListPerDeviceGroup.values()) {
				for (Device device : devices) {
					waeIPList.add(device.getIpAddress());
				}
			}
		}
		return waeIPList;
	}

	public static void writeToFile(List<String> waeIPList, String fileName) throws IOException {
		File file = new File(FileNameRetriever.getOutputTopoFolderName(), fileName);
		BufferedWriter writer = null;
		try {
			CiscoWAASDiscoverer.LOGGER.info("Writing file : " + fileName);
			writer = new BufferedWriter(new FileWriter(file));
			for (String ip : waeIPList) {
				writer.write(ip + END_LINE);
			}

		} catch (IOException e) {
			CiscoWAASDiscoverer.LOGGER.info("Error while writing the file : " + fileName);
			CiscoWAASDiscoverer.LOGGER.error("Error",e);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	private static void exitWithMessage(String message) {
		CiscoWAASDiscoverer.LOGGER.info(message);
		System.exit(1);
	}
	
}
