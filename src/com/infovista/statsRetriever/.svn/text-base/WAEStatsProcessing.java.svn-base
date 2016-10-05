package com.infovista.statsRetriever;

import java.rmi.RemoteException;
import java.util.*;
import java.lang.Exception;

import com.cisco.waas.wsc.RemoteExceptionException;
import com.cisco.waas.wsc.DeviceConfStub.*;
import com.infovista.fileutils.CsvWriter;
import com.infovista.fileutils.FileNameRetriever;
import com.infovista.sdk.error.WAASPermissionDeniedError;
import com.infovista.sdk.error.WAASTimeoutError;
import com.infovista.sdk.stub.deviceConf.DeviceStub;

public class WAEStatsProcessing {

	private final static int DEFAULT_COLS_MONO=7;
	private final static String colArrayMono[] = {"#", "Id", "Name", "Model", "Location", "IPAddress", "MacAddress", "Status"};
	private final static String colArrayMulti[] = {"#", "DeviceGroupName", "Id", "Name", "Model", "Location", "IPAddress", "MacAddress", "Status"};
	private static Device[] deviceArray;
	private static DeviceGroup[] deviceGroupArray;
	
	/*public static void processWAEs(DeviceStub dStub, boolean isMultitenant) throws RemoteException, RemoteExceptionException{
		CsvWriter csvWriter = new CsvWriter(FileNameRetriever.getWaeStatFile());
		if (!isMultitenant) {
			GetWANInfoResponse wanInfo = dStub.getWANInfoResponse();
			if (wanInfo == null)
				exitWithMessage("WAN Info not received from the server.");
			
			deviceArray = wanInfo.get_return();
			
			if (deviceArray.length == 0)
				exitWithMessage("Empty WAN Info received from the server.");
			
			csvWriter.writeline(colArrayMono);
			for (Device device : deviceArray) {
				String[] waeValues = new String[DEFAULT_COLS_MONO];
				if (device.getType().equalsIgnoreCase("wae")) {
					waeValues[0]=String.valueOf(device.getId());
					waeValues[1]=(device.getName());
					waeValues[2]=(device.getModel());
					waeValues[3]=(device.getLocation());
					waeValues[4]=(device.getIpAddress());
					waeValues[5]=(device.getMacAddress());
					waeValues[6]=(device.getStatus());
					csvWriter.writeline(waeValues);
				}
			}
		} else{
			GetDeviceGroupsResponse deviceGroupsResponse = dStub.getDeviceGroupsResponse();
			if (deviceGroupsResponse == null)
				exitWithMessage("DeviceGroup info not received from the server");
			
			deviceGroupArray = deviceGroupsResponse.get_return();
			
			Hashtable<String, String> managedWAEs = new Hashtable<String, String>();
			Hashtable<DeviceGroup, LinkedList<Device>> waeListPerDeviceGroup = new Hashtable<DeviceGroup, LinkedList<Device>>();
			
			if (deviceGroupArray == null)
				exitWithMessage("Empty device groups received from the server");
			
			for (int i = 0; i < deviceGroupArray.length; i++) {
				DeviceGroup deviceGroup = deviceGroupArray[i];
				LinkedList<Device> deviceList = new LinkedList<Device>();
				waeListPerDeviceGroup.put(deviceGroup, deviceList);
				try {
					GetWAEsInGroupResponse devicesResponse = dStub.getWAEsInGroupResponse(deviceGroup.getId());
					if (devicesResponse == null) 
						exitWithMessage("Response not received for WAEsInGroup");
					
					Device[] devices = devicesResponse.get_return();
					if (devices != null) {
						for (int j = 0; j < devices.length; j++) {
							Device device = devices[j];
							deviceList.add(device);
							if (!managedWAEs.containsKey(device.getName())) {
								managedWAEs.put(device.getName(), deviceGroup.getName());
							}
						}
					}
				} catch (WAASPermissionDeniedError e) {
					CiscoWAASDiscoverer.LOGGER.info("Not enough rights to retrieve WAEs on deviceGroup " + deviceGroup.getName());
				} catch (WAASTimeoutError e) {
					CiscoWAASDiscoverer.LOGGER.info("Timeout when retrieving WAEs on deviceGroup " + deviceGroup.getName());
				} catch (Exception e) {
					CiscoWAASDiscoverer.LOGGER.info("Error when retrieving WAEs on deviceGroup " + deviceGroup.getName());
				}
			}
			csvWriter.writeline(colArrayMulti);
			for (DeviceGroup devicesgrp : waeListPerDeviceGroup.keySet()) {
				String[] waeValues = new String[DEFAULT_COLS_MONO+1];
				waeValues[0]=devicesgrp.getName();
				for (Device device : waeListPerDeviceGroup.get(devicesgrp)) {
					waeValues[1]=String.valueOf(device.getId());
					waeValues[2]=(device.getName());
					waeValues[3]=(device.getModel());
					waeValues[4]=(device.getLocation());
					waeValues[5]=(device.getIpAddress());
					waeValues[6]=(device.getMacAddress());
					waeValues[7]=(device.getStatus());
					csvWriter.writeline(waeValues);
				}
			}
		}
	}
	
	
	private static void exitWithMessage(String message) {
		CiscoWAASDiscoverer.LOGGER.info(message);
		System.exit(1);
	}
	
	public static Device[] getDeviceArray(){
		return deviceArray;
	}
	
	public static DeviceGroup[] getDeviceGroupArray(){
		return deviceGroupArray;
	}*/
}
