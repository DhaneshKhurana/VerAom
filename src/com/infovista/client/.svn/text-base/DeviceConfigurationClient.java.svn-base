package com.infovista.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.cisco.waas.wsc.DeviceConfStub.Device;
import com.cisco.waas.wsc.DeviceConfStub.DeviceGroup;
import com.cisco.waas.wsc.DeviceConfStub.GetAPIVersionResponse;
import com.cisco.waas.wsc.DeviceConfStub.GetDeviceGroupsResponse;
import com.cisco.waas.wsc.DeviceConfStub.GetWAEsInGroupResponse;
import com.cisco.waas.wsc.DeviceConfStub.GetWANInfoResponse;
import com.cisco.waas.wsc.DeviceStatusStub.GetMonitoredAOsByWaeIdsResponse;
import com.cisco.waas.wsc.RemoteExceptionException;
import com.cisco.waas.wsc.TrafficStatsStub.GetMonitoredApplicationsResponse;
import com.infovista.sdk.ssl.FakeSSLProtocolSocketFactory;
import com.infovista.sdk.stub.deviceConf.DeviceStub;
import com.infovista.sdk.stub.deviceStatus.DeviceStatus;
import com.infovista.sdk.stub.trafficStats.TrafficStub;

public class DeviceConfigurationClient {
	
	public static void main(String[] args) {
		try {
			if (args.length < 3) {
				CiscoWAASDiscoverer.LOGGER.info("Usage : ");
				CiscoWAASDiscoverer.LOGGER.info("DeviceConfigurationClient <ip/hostname> <user> <password>");
				return;
			}
			Protocol.unregisterProtocol("https");
			Protocol.registerProtocol("https", new Protocol("https", new FakeSSLProtocolSocketFactory(), 443));
			int p_timeout = 10000;
			String p_password = args[2];
			String p_userName = args[1];
			String p_hostname = args[0];
			String waasURL="https://"+p_hostname+":8443/ws";
			DeviceStub stub = new DeviceStub(null, waasURL+"/DeviceConf", p_userName, p_password, p_timeout);
			boolean isDataCompressionEnabled = false;
			if(stub!=null){
				isDataCompressionEnabled = stub.isSOAPCompressionSupported();
			}
			
			GetDeviceGroupsResponse deviceGroups = stub.getDeviceGroupsResponse();
			DeviceGroup[] groups = null;
			try {
				if (deviceGroups != null) {
					groups = deviceGroups.get_return();
					OMElement omElement = deviceGroups.getOMElement(GetDeviceGroupsResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File("output","getDeviceGroups.xml"), omElement.toString());
				} else {
					CiscoWAASDiscoverer.LOGGER.info("Unable to get Device Groups");
				}
			} catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.info("Error receiving Device Groups");
				CiscoWAASDiscoverer.LOGGER.error("Error",e);
			}
			
			Device[] devices = null;
			try {
				GetWANInfoResponse wanInfo = stub.getWANInfoResponse();
				if (wanInfo != null) {
					CiscoWAASDiscoverer.LOGGER.info("Received WAN Info");
					OMElement omElement = wanInfo.getOMElement(GetWANInfoResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
					devices = wanInfo.get_return();
					FileUtils.writeStringToFile(new File("output","getWANInfo.xml"), omElement.toString());
				} else {
					CiscoWAASDiscoverer.LOGGER.info("Unable to get WAN Info");
				}
			} catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.info("Error receiving WAN Info");
				CiscoWAASDiscoverer.LOGGER.error("Error",e);
			}

			if(groups != null){
				try {
					for (DeviceGroup deviceGroup : groups) {
						long id = deviceGroup.getId();
						GetWAEsInGroupResponse wanInfo = stub.getWAEsInGroupResponse(id);
						if (wanInfo != null) {
							CiscoWAASDiscoverer.LOGGER.info("Received WAEs in Group");
							OMElement omElement = wanInfo.getOMElement(GetWAEsInGroupResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
							String fileName = "getWAEsInGroup_"+String.valueOf(id) +"_Response.xml";
							FileUtils.writeStringToFile(new File("output",fileName), omElement.toString());
						} else {
							CiscoWAASDiscoverer.LOGGER.info("Unable to get WAE in Group");
						}	
					}
					
				} catch (Exception e) {
					CiscoWAASDiscoverer.LOGGER.info("Error receiving WAE in group");
					CiscoWAASDiscoverer.LOGGER.error("Error",e);
				}
			}
			
			try {
				GetAPIVersionResponse apiVersion = stub.getAPIVersionResponse();
				if (apiVersion != null) {
					CiscoWAASDiscoverer.LOGGER.info("Received API Version");
					OMElement omElement = apiVersion.getOMElement(GetAPIVersionResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
					FileUtils.writeStringToFile(new File("output","getAPIVersion.xml"), omElement.toString());
				} else {
					CiscoWAASDiscoverer.LOGGER.info("Unable to get API Version");
				}
			} catch (Exception e) {
				CiscoWAASDiscoverer.LOGGER.info("Error receiving WAN Info");
				CiscoWAASDiscoverer.LOGGER.error("Error",e);
			}

			try {
				TrafficStub trafficStub = TrafficStub.getInstance(null, waasURL+"/TrafficStats", p_userName, p_password, p_timeout, isDataCompressionEnabled);

				try {
					GetMonitoredApplicationsResponse monitoredApplications = trafficStub.getMonitoredApplicationsResponse();
					if (monitoredApplications != null) {
						CiscoWAASDiscoverer.LOGGER.info("Received Monitored Applications info");
						OMElement omElement = monitoredApplications.getOMElement(GetMonitoredApplicationsResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
						FileUtils.writeStringToFile(new File("output","getMonitoredApplications.xml"), omElement.toString());
					} else {
						CiscoWAASDiscoverer.LOGGER.info("Unable to get Monitored Applications");
					}
				} catch (IOException e1) {
					CiscoWAASDiscoverer.LOGGER.info("Error receiving Monitored Applications");
					e1.printStackTrace();
				}
			} catch (Exception e2) {
				CiscoWAASDiscoverer.LOGGER.info("Error creating Traffic Stats stub");
				e2.printStackTrace();
			}
			
			if(devices != null){
				try {
					DeviceStatus statusStub = new DeviceStatus(null, waasURL+"/DeviceStatus", p_userName, p_password, p_timeout, isDataCompressionEnabled);
					List<Long> deviceIDList = new ArrayList<Long>();
					for (Device device : devices) {
						if(device.getType()!=null && device.getType().equalsIgnoreCase("wae")){
							deviceIDList.add(device.getId());
						}
					}
					long[] ids=new long[deviceIDList.size()];
					for (int i=0;i<deviceIDList.size();i++){
						ids[i]=deviceIDList.get(i);
					}
					try {
						GetMonitoredAOsByWaeIdsResponse monitoredApplications = statusStub.getMonitoredAOsByWaeIdsResponse(ids);
						if (monitoredApplications != null) {
							CiscoWAASDiscoverer.LOGGER.info("Received Monitored Applications info");
							OMElement omElement = monitoredApplications.getOMElement(GetMonitoredAOsByWaeIdsResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
							FileUtils.writeStringToFile(new File("output","getMonitoredAOsByWaeIds.xml"), omElement.toString());
						} else {
							CiscoWAASDiscoverer.LOGGER.info("Unable to get MonitoredAOs by WAE Ids");
						}
					} catch (IOException e1) {
						CiscoWAASDiscoverer.LOGGER.info("Error receiving MonitoredAOs by WAE Ids");
						e1.printStackTrace();
					} catch (RemoteExceptionException e) {
						CiscoWAASDiscoverer.LOGGER.info("Error receiving MonitoredAOs by WAE Ids");
						CiscoWAASDiscoverer.LOGGER.error("Error",e);
					}
				} catch (Exception e) {
					CiscoWAASDiscoverer.LOGGER.info("Error creating DeviceStatus stub");
					CiscoWAASDiscoverer.LOGGER.error("Error",e);
				}
			}
			

		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			CiscoWAASDiscoverer.LOGGER.error("Error",e);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			CiscoWAASDiscoverer.LOGGER.error("Error",e);
		} catch (RemoteExceptionException e) {
			// TODO Auto-generated catch block
			CiscoWAASDiscoverer.LOGGER.error("Error",e);
		}
	}

}
