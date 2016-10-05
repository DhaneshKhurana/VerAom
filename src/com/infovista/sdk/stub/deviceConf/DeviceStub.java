package com.infovista.sdk.stub.deviceConf;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.RampartMessageData;

import com.cisco.waas.wsc.DeviceConfStub;
import com.cisco.waas.wsc.DeviceConfStub.Device;
import com.cisco.waas.wsc.DeviceConfStub.DeviceGroup;
import com.cisco.waas.wsc.DeviceConfStub.GetAPIVersionResponse;
import com.cisco.waas.wsc.DeviceConfStub.GetCM;
import com.cisco.waas.wsc.DeviceConfStub.GetCMByName;
import com.cisco.waas.wsc.DeviceConfStub.GetCMByNameResponse;
import com.cisco.waas.wsc.DeviceConfStub.GetCMResponse;
import com.cisco.waas.wsc.DeviceConfStub.GetDeviceGroupsResponse;
import com.cisco.waas.wsc.DeviceConfStub.GetWAE;
import com.cisco.waas.wsc.DeviceConfStub.GetWAEByName;
import com.cisco.waas.wsc.DeviceConfStub.GetWAEByNameResponse;
import com.cisco.waas.wsc.DeviceConfStub.GetWAEResponse;
import com.cisco.waas.wsc.DeviceConfStub.GetWAEs;
import com.cisco.waas.wsc.DeviceConfStub.GetWAEsInGroup;
import com.cisco.waas.wsc.DeviceConfStub.GetWAEsInGroupByName;
import com.cisco.waas.wsc.DeviceConfStub.GetWAEsInGroupByNameResponse;
import com.cisco.waas.wsc.DeviceConfStub.GetWAEsInGroupResponse;
import com.cisco.waas.wsc.DeviceConfStub.GetWAEsResponse;
import com.cisco.waas.wsc.DeviceConfStub.GetWANInfoResponse;
import com.cisco.waas.wsc.RemoteExceptionException;
import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.sdk.error.WAASErrorManager;

public class DeviceStub {

	// ///////////////////////////////////////////////////////////////////////////////
	// Attributes
	//
	private DeviceConfStub deviceConfStub_;
	private String userName_;

	// ///////////////////////////////////////////////////////////////////////////////
	// Constructor
	//
	public DeviceStub(String p_configDir, String p_url, String p_userName, String p_password, long p_timeout) {
		try {
			CiscoWAASDiscoverer.LOGGER.debug("initializing device Stub ");
			
			synchronized (WAASErrorManager.instance().getMonitor()) {
				URL repURL = this.getClass().getClassLoader().getResource("com/infovista/sdk/rampart/");
				ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromURIs(null, repURL);
				deviceConfStub_ = new DeviceConfStub(ctx, p_url);
				deviceConfStub_._getServiceClient().getOptions().setTimeOutInMilliSeconds(p_timeout);

				deviceConfStub_._getServiceClient().engageModule("rampart");
				deviceConfStub_._getServiceClient().getOptions().setProperty(RampartMessageData.KEY_RAMPART_POLICY, loadPolicy());
				deviceConfStub_._getServiceClient().getOptions().setUserName(p_userName);
				deviceConfStub_._getServiceClient().getOptions().setPassword(p_password);
				userName_ = p_userName;
			}
		} catch (AxisFault e) {
			CiscoWAASDiscoverer.LOGGER.info("The Device Stub could not be initialized " + e.getMessage());
		} catch (FileNotFoundException e) {
			CiscoWAASDiscoverer.LOGGER.info("The Device Stub could not be initialized " + e.getMessage());
		} catch (XMLStreamException e) {
			CiscoWAASDiscoverer.LOGGER.info("The Device Stub could not be initialized " + e.getMessage());
		}
	//	CiscoWAASDiscoverer.LOGGER.debug("After initializing device Stub " + this.userName_ + " :: "+ this.deviceConfStub_.toString() + " :: " + this.deviceConfStub_ );
	}

	// /////////////////////////////////////////////////////////////////////////////////
	// Public methods
	//

	public String getUserName() {
		return userName_;
	}

	/**
	 * Get informations on Central Manager and WAEs
	 * 
	 * @return Array of device corresponding to the central manager and all WAEs defined on this central manager.
	 * @throws RemoteException
	 * @throws RemoteExceptionException
	 */
	public GetWANInfoResponse getWANInfoResponse() throws RemoteException, RemoteExceptionException {
		try {
			GetWANInfoResponse response = deviceConfStub_.getWANInfo();
			return response;
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	/**
	 * Get informations on Central Manager and WAEs
	 * 
	 * @return Array of device corresponding to the central manager and all WAEs defined on this central manager.
	 * @throws RemoteException
	 * @throws RemoteExceptionException
	 */
	public Device[] getWANInfo() throws RemoteException, RemoteExceptionException {
		try {
			GetWANInfoResponse response = deviceConfStub_.getWANInfo();
			return response.get_return();
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	/**
	 * Get device groups
	 * 
	 * @return Array of device group or null if no device group are configured.
	 * @throws RemoteException
	 * @throws RemoteExceptionException
	 */
	public GetDeviceGroupsResponse getDeviceGroupsResponse() throws RemoteException, RemoteExceptionException {
		try {
			GetDeviceGroupsResponse response = deviceConfStub_.getDeviceGroups();
			return response;
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	/**
	 * Get device groups
	 * 
	 * @return Array of device group or null if no device group are configured.
	 * @throws RemoteException
	 * @throws RemoteExceptionException
	 */
	public DeviceGroup[] getDeviceGroups() throws RemoteException, RemoteExceptionException {
		try {
			GetDeviceGroupsResponse response = deviceConfStub_.getDeviceGroups();
			return response.get_return();
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	/**
	 * Get a WAE based on its ID
	 * 
	 * @param p_id
	 *            the WAE ID
	 * @return the WAE with this ID or null if not able to find the corresponding WAE
	 * @throws RemoteException
	 * @throws RemoteExceptionException
	 */
	public Device getWAE(long p_id) throws RemoteException, RemoteExceptionException {
		try {
			GetWAE getWAE = new GetWAE();
			getWAE.setId(p_id);
			GetWAEResponse response = deviceConfStub_.getWAE(getWAE);
			return response.get_return();
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	/**
	 * Get a WAE based on its name
	 * 
	 * @param p_name
	 *            the WAE name
	 * @return the corresponding or null if not able to find it
	 * @throws RemoteException
	 * @throws RemoteExceptionException
	 */
	public Device getWAEByName(String p_name) throws RemoteException, RemoteExceptionException {
		try {
			GetWAEByName getWAE = new GetWAEByName();
			getWAE.setName(p_name);
			GetWAEByNameResponse response = deviceConfStub_.getWAEByName(getWAE);
			return response.get_return();
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	/**
	 * Get a central manager based on its ID
	 * 
	 * @param p_id
	 *            the central manager ID
	 * @return the corresponding Central Manager or null if not able to find it
	 * @throws RemoteException
	 * @throws RemoteExceptionException
	 */
	public Device getCM(long p_id) throws RemoteException, RemoteExceptionException {
		try {
			GetCM getCM = new GetCM();
			getCM.setId(p_id);
			GetCMResponse response = deviceConfStub_.getCM(getCM);
			return response.get_return();
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	/**
	 * Get a central manager based on its name
	 * 
	 * @param p_name
	 *            the central manager name
	 * @return the corresponding central manager or null if not able to find it
	 * @throws RemoteException
	 * @throws RemoteExceptionException
	 */
	public Device getCMByName(String p_name) throws RemoteException, RemoteExceptionException {
		try {
			GetCMByName getCM = new GetCMByName();
			getCM.setName(p_name);
			GetCMByNameResponse response = deviceConfStub_.getCMByName(getCM);
			return response.get_return();
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	/**
	 * Get a WAE list based on an ID list
	 * 
	 * @param p_ids
	 *            the ID list
	 * @return the corresponding WAEs or null if not able to find them
	 * @throws RemoteException
	 * @throws RemoteExceptionException
	 */
	public Device[] getWAEs(long[] p_ids) throws RemoteException, RemoteExceptionException {
		try {
			GetWAEs getWAEs = new GetWAEs();
			getWAEs.setIds(p_ids);
			GetWAEsResponse response = deviceConfStub_.getWAEs(getWAEs);
			return response.get_return();
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	/**
	 * Get a WAE list belonging to one device group
	 * 
	 * @param p_id
	 *            the device group ID
	 * @return the corresponding WAEs or null if not able to find them
	 * @throws RemoteException
	 * @throws RemoteExceptionException
	 */
	public GetWAEsInGroupResponse getWAEsInGroupResponse(long p_id) throws RemoteException, RemoteExceptionException {
		try {
			GetWAEsInGroup getWAEsInGroup = new GetWAEsInGroup();
			getWAEsInGroup.setDeviceGroupId(p_id);
			GetWAEsInGroupResponse response = deviceConfStub_.getWAEsInGroup(getWAEsInGroup);
			return response;
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	/**
	 * Get a WAE list belonging to one device group
	 * 
	 * @param p_id
	 *            the device group ID
	 * @return the corresponding WAEs or null if not able to find them
	 * @throws RemoteException
	 * @throws RemoteExceptionException
	 */
	public Device[] getWAEsInGroup(long p_id) throws RemoteException, RemoteExceptionException {
		try {
			GetWAEsInGroup getWAEsInGroup = new GetWAEsInGroup();
			getWAEsInGroup.setDeviceGroupId(p_id);
			GetWAEsInGroupResponse response = deviceConfStub_.getWAEsInGroup(getWAEsInGroup);
			return response.get_return();
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	/**
	 * Get a WAE list belonging to one device group
	 * 
	 * @param p_name
	 *            the device group name
	 * @return the corresponding WAEs or null if not able to find them
	 * @throws RemoteException
	 * @throws RemoteExceptionException
	 */
	public Device[] getWAEsInGroupByName(String p_name) throws RemoteException, RemoteExceptionException {
		try {
			GetWAEsInGroupByName getWAEsInGroupByName = new GetWAEsInGroupByName();
			getWAEsInGroupByName.setName(p_name);
			GetWAEsInGroupByNameResponse response = deviceConfStub_.getWAEsInGroupByName(getWAEsInGroupByName);
			return response.get_return();
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	/**
	 * Get a API Version belonging to the central manager
	 * 
	 * @return theAPI Version
	 * @throws RemoteException
	 * @throws RemoteExceptionException
	 */
	public GetAPIVersionResponse getAPIVersionResponse() throws RemoteException, RemoteExceptionException {
		try {
			DeviceConfStub.GetAPIVersionResponse version = deviceConfStub_.getAPIVersion();
			return version;
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	/**
	 * Get a API Version belonging to the central manager
	 * 
	 * @return theAPI Version
	 * @throws RemoteException
	 * @throws RemoteExceptionException
	 */
	public String getAPIVersion() throws RemoteException, RemoteExceptionException {
		try {
			DeviceConfStub.GetAPIVersionResponse version = deviceConfStub_.getAPIVersion();
			return version.get_return();
		} catch (RemoteException e) {
			throw WAASErrorManager.instance().getWAASError(e);
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////
	// Private methods
	//

	/**
	 * Load a WAAS security policy
	 * 
	 * @param xmlPath
	 *            the path to the WAAS security policy configuration file
	 * @return the corresponding policy
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	private Policy loadPolicy() throws FileNotFoundException, XMLStreamException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("com/infovista/sdk/rampart/policy/policyWAAS.xml");
		StAXOMBuilder builder = new StAXOMBuilder(is);
		return PolicyEngine.getPolicy(builder.getDocumentElement());
	}

	/**
	 * Check wether SOAP message compression supported or not.
	 * 
	 * @return true if compression supported else false.
	 */
	public boolean isSOAPCompressionSupported() {
		deviceConfStub_._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_GZIP_REQUEST, Boolean.TRUE);
		try {
			GetWANInfoResponse response = deviceConfStub_.getWANInfo();
		} catch (Exception e) {
			deviceConfStub_._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.MC_GZIP_REQUEST, Boolean.FALSE);
			return false;
		}
		return true;
	}

}
