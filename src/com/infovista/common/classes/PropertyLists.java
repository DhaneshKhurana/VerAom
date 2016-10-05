package com.infovista.common.classes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import com.infovista.client.CiscoWAASDiscoverer;
import com.infovista.configs.CommonConfig;

public class PropertyLists {
	
	private static PropertyLists propertyLists;
	
	private List<String> cifsPoperties;
	private List<String> mapiPoperties;
	private List<String> httpPoperties;
	private List<String> trafficPoperties;
	private List<String> videoPoperties;
	private List<String> nfsPoperties;
	private List<String> alarmProperties;

	public final static String LOCAL_RESPONSE="localResp";
	public final static String REMOTE_RESPONSE="remoteResp";
	public final static String AVG_LOCAL_RESP="avgLocalResp";
	public final static String AVG_REMOTE_RESP="avgRemoteResp";
	public final static String AVG_READ_AHEAD="avgReadAhead";
	public final static String AVG_READ_STREAM="avgReadStream";

	public final static String AVG_SYNC_GETBUFFER="avgSyncGetBuffer";
	public final static String CLIENT2K="client2k";
	public final static String CLIENT2K3="client2k3";
	public final static String CLIENT2K7="client2k7";
	public final static String HIGHER_THAN_2KCONN="higherThan2kConnections";
	public final static String LOWER_THAN_2KCONN="lowerThan2kConnections";

	public final static String SECURE_CONN="secureConnections";
	public final static String SESSION_COUNT="sessionCount";
	
	public final static String DISK_CAPACITY="diskCapacity";
	public final static String CACHE_COUNT="cacheCount";
	public final static String CACHE_UTILIZATION="cacheUtilization";
	public final static String FILE_COUNT="fileCount";
	public final static String HIT_RATE="hitRate";
	public final static String REQUEST_COUNT="requestCount";
	public final static String RESOURCE_UTILIZATION="Resourceutilization";
	
	public final static String AUTH_FLAVOR2_COUNT="authflavor2Count";
	public final static String AUTH_FALVOR3_COUNT="authflavor3Count";
	public final static String AUTH_FLAVOR_UNKNOWN="authflavorUnknown";
	public final static String NFS_V2_COUNT="nfsv2Count";
	public final static String NFS_V3_COUNT="nfsv3Count";
	public final static String NFS_V4_COUNT="nfsv4Count";
	public final static String NFS_UNKNOWN_COUNT="nfsUnknownCount";
	
	public final static String REUSED="reused";
	public final static String IDLE="idle";
	public final static String REUSED_PEER="reusedPeer";
	public final static String REUSEDCONN_PER_OPERATION="reusedConn_per_operation";
	public final static String IDLECONN_PER_OPERATION="idleConn_per_operation";
	public final static String FAST_CONNECTION_SETUP_COUNT="fastConnectionSetupCount";
	public final static String MAX_REUSED_PER_OPERATION="maxReusedConn_per_operation";
	public final static String PCT_RTT_SAVED="pct_rtt_saved";
	public final static String TOTAL_HANDOFF="totalHandoff";
	public final static String TOTAL_LAN_CONN="totalLanConnections";
	public final static String MAX_REUSED="maxReused";
	public final static String MAX_REUSED_CONN="maxReusedConnections";
	public final static String PERCENT_CONN_TIME_SAVED="percentConnectionTimeSaved";
	
	
	public final static String ACCELERATED_CONN="acceleratedConnections";
	public final static String INCOMING_BYTES_TOTAL="incomingBytesTotal";
	public final static String OUTGOING_BYTES_TOTAL="outgoingBytesTotal";
	public final static String ERROR_CONN="errorConnections";
	public final static String RECIEVED_CONN="recievedConnections";
	public final static String UNACCELERATED_CONN="unAcceleratedConnections";
	
	public final static String DEVICE_IP="deviceIp";
	public final static String TIMESTAMP="timeStamp";
	
	public final static String FREQUENCY="frequency";
	public final static String COMPRESSED_IN="compressedIn";
	public final static String COMPRESSED_OUT="compressedOut";
	public final static String UNCOMPRESSED_IN="unCompressedIn";
	public final static String UNCOMPRESSED_OUT="unCompressedOut";
	public final static String PASSTHROUGH_OTHER_OUT="passthrough_other_out";
	public final static String PASSTHROUGH_INTERMEDIATE_IN="passthroughintermediatein";
	public final static String PASSTHROUGH_OUT="passthroughotherout";
	public final static String PASSTHROUGH_OVERLOAD_IN="passthroughoverloadin";
	
	public final static String PASSTHROUGH_OVERLOAD_OUT="passthroughoverloadout";
	public final static String PASSTHROUGH_PEER_IN="passthroughpeerin";
	public final static String PASSTHORUGH_PEER_OUT="passthroughpeerout";
	public final static String PASSTHORUGH_POLICY_IN="passthroughpolicyin";
	public final static String PASSTHORUGH_POLICY_OUT="passthroughpolicyout";
	public final static String GET_PASSTHORUGH_POLICY_OUT="getPassthroughpolicyout";
	
	public final static String DEVICE_STATUS="deviceStatus";
	public final static String ALARM_ID="alarmId";
	public final static String ALARM_SEVERITY="alarmSeverity";
	public final static String ALARM_NAME="alarmName";
	public final static String ALARM_DESCRIPTION="alarmdesciprion";
	
	
	//property file constants

	
	
	public final static String MAX_HOUR_IN_REQUEST="maxHoursInRequest";
	public final static String FOLLOW_LASTRUN="followlastRunMechanism";
	public final static String AQUISITON_RATE="aquisiotion_rate";
	public final static String MODE="mode";
	public final static String OUTPUT_FOLDER="output_folder";
	
	public final static String HOST="host";
	public final static String USERNAME="username";
	public final static String PASSWORD="password";
	
	public final static String PORT="port";
	public final static String PROTOCOL="protocol";
	public final static String DATA_MAX_hISTORY="dataMaxHistory";
	public final static String WAAS_URL="waasURL";
	public final static String DEVICE_CONF_URL="deviceConfURL";
	public final static String TRAFFIC_URL="trafficURL";
	
	public final static String CIFS_URL="cifsURL";
	public final static String HTTP_URL="httpURL";
	public final static String MAPI_URL="mapiURL";
	public final static String NFS_URL="nfsURL";
	public final static String VIDEO_URL="videoURL";
	public final static String ALARM_URL="alarmURL";

	public final static String TRAFFIC_Service_Enabled="traffic_enabled";
	public final static String CIFS_Service_Enabled="cifs_enabled";
	public final static String HTTP_Service_Enabled="http_enabled";
	public final static String MAPI_Service_Enabled="mapi_enabled";
	public final static String NFS_Service_Enabled="nfs_enabled";
	public final static String VIDEO_Service_Enabled="video_enabled";
	public final static String ALARM_Service_Enabled="alarm_enabled";


	
	public final static String DEVICE_STATUS_URL="deviceStatusURL";
	public final static String WASS_CONF_DIRECTORY="waasConfDirectory";
	public final static String TRAFFIC_TYPE="trafficType";
	public final static String DIRECTIONS="directions";
	public final static String START_TIME="startTime";
	public final static String END_TIME="endTime";
	
	public final static String WAAS_TIMEOUT="waas_timeout";
	public final static String WAES_IN_REQUEST="waesInOneRequest";
	public final static String TOTAL_WAES="totalWaes";
	public final static String TOTAL_THRREADS="nThreads";
	
	private PropertyLists() {
		init();
	}
	
	
	//GET INSTANCE
	public static PropertyLists getInstance(){
		if(propertyLists==null){
			synchronized(PropertyLists.class){
				if(propertyLists==null){
					propertyLists = new PropertyLists();
				}
			}
			
		}
		return propertyLists;
	}
	
	private void init(){
		initcifsPoperties();
		initnfsPoperties();
		initmapiPoperties();
		inithttpPoperties();
		initvideoPoperties();
		inittrafficPoperties();
		initalarmproperties();
	}

	private void initalarmproperties(){
		alarmProperties = new ArrayList<>();
		alarmProperties.add(ALARM_NAME);
		alarmProperties.add(ALARM_SEVERITY);
		alarmProperties.add(DEVICE_STATUS);
		alarmProperties.add(ALARM_DESCRIPTION);
	}
	
	private void initcifsPoperties(){
		cifsPoperties = new ArrayList<>();
		cifsPoperties.add(DISK_CAPACITY);
		cifsPoperties.add(REQUEST_COUNT);
		cifsPoperties.add(CACHE_COUNT);
		cifsPoperties.add(RESOURCE_UTILIZATION);
		cifsPoperties.add(HIT_RATE);
		cifsPoperties.add(SESSION_COUNT);
	}
	
	private void initnfsPoperties(){
		nfsPoperties = new ArrayList<>();
		nfsPoperties.add(AUTH_FLAVOR2_COUNT);
		nfsPoperties.add(AUTH_FALVOR3_COUNT);
		nfsPoperties.add(AUTH_FLAVOR_UNKNOWN);
		nfsPoperties.add(AVG_LOCAL_RESP);
		nfsPoperties.add(AVG_REMOTE_RESP);
		nfsPoperties.add(LOCAL_RESPONSE);
		nfsPoperties.add(REMOTE_RESPONSE);

		nfsPoperties.add(NFS_V2_COUNT);
		nfsPoperties.add(NFS_V3_COUNT);
		nfsPoperties.add(NFS_V4_COUNT);
		nfsPoperties.add(NFS_UNKNOWN_COUNT);
		nfsPoperties.add(SESSION_COUNT);
		
	}
	
	private void initmapiPoperties(){
		mapiPoperties = new ArrayList<>();
		
		mapiPoperties.add(LOCAL_RESPONSE);
		mapiPoperties.add(REMOTE_RESPONSE);
		mapiPoperties.add(AVG_LOCAL_RESP);
		mapiPoperties.add(AVG_REMOTE_RESP);
		mapiPoperties.add(AVG_READ_AHEAD);
		mapiPoperties.add(AVG_READ_STREAM);
		mapiPoperties.add(CLIENT2K);
		mapiPoperties.add(CLIENT2K3);
		mapiPoperties.add(CLIENT2K7);
		mapiPoperties.add(HIGHER_THAN_2KCONN);
		mapiPoperties.add(LOWER_THAN_2KCONN);
		mapiPoperties.add(SECURE_CONN);
		mapiPoperties.add(SESSION_COUNT);
		
	}
	
	private void inithttpPoperties(){
		httpPoperties = new ArrayList<>();
		
		httpPoperties.add(REUSED_PEER);
		httpPoperties.add(REUSED);
		httpPoperties.add(IDLE);
		httpPoperties.add(MAX_REUSED);
		httpPoperties.add(PCT_RTT_SAVED);
		httpPoperties.add(TOTAL_HANDOFF);
		httpPoperties.add(FAST_CONNECTION_SETUP_COUNT);
		httpPoperties.add(TOTAL_LAN_CONN);
		httpPoperties.add(MAX_REUSED_CONN);
		httpPoperties.add(PERCENT_CONN_TIME_SAVED);
		
	}
	
	private void initvideoPoperties(){
		videoPoperties = new ArrayList<>();
		
		videoPoperties.add(ACCELERATED_CONN);
		videoPoperties.add(INCOMING_BYTES_TOTAL);
		videoPoperties.add(OUTGOING_BYTES_TOTAL);
		videoPoperties.add(ERROR_CONN);
		videoPoperties.add(RECIEVED_CONN);
		videoPoperties.add(UNACCELERATED_CONN);
		
	}
	
	private void inittrafficPoperties(){
		trafficPoperties = new ArrayList<>();
		
		trafficPoperties.add(COMPRESSED_IN);
		trafficPoperties.add(COMPRESSED_OUT);
		trafficPoperties.add(UNCOMPRESSED_IN);
		trafficPoperties.add(UNCOMPRESSED_OUT);
		trafficPoperties.add(PASSTHROUGH_INTERMEDIATE_IN);
		trafficPoperties.add(PASSTHROUGH_OTHER_OUT);
		trafficPoperties.add(PASSTHROUGH_OVERLOAD_IN);
		trafficPoperties.add(PASSTHROUGH_OVERLOAD_OUT);
		trafficPoperties.add(PASSTHROUGH_PEER_IN);
		trafficPoperties.add(PASSTHORUGH_PEER_OUT);
		trafficPoperties.add(PASSTHORUGH_POLICY_IN);
		trafficPoperties.add(PASSTHORUGH_POLICY_OUT);
		trafficPoperties.add(FREQUENCY);
	}
	

	public List<String> getCifsPoperties() {
		return cifsPoperties;
	}

	public List<String> getMapiPoperties() {
		return mapiPoperties;
	}

	public List<String> getHttpPoperties() {
		return httpPoperties;
	}

	public List<String> getTrafficPoperties() {
		return trafficPoperties;
	}

	public List<String> getVideoPoperties() {
		return videoPoperties;
	}

	public List<String> getNfsPoperties() {
		return nfsPoperties;
	}

	public List<String> getAlarmProperties() {
		return alarmProperties;
	}


	public void setAlarmProperties(List<String> alarmProperties) {
		this.alarmProperties = alarmProperties;
	}


	public static String getLocalResponse() {
		return LOCAL_RESPONSE;
	}

	public static String getRemoteResponse() {
		return REMOTE_RESPONSE;
	}

	public static String getAvgLocalResp() {
		return AVG_LOCAL_RESP;
	}

	public static String getAvgRemoteResp() {
		return AVG_REMOTE_RESP;
	}

	public static String getAvgReadAhead() {
		return AVG_READ_AHEAD;
	}

	public static String getAvgReadStream() {
		return AVG_READ_STREAM;
	}

	public static String getAvgSyncGetbuffer() {
		return AVG_SYNC_GETBUFFER;
	}

	public static String getClient2k() {
		return CLIENT2K;
	}

	public static String getClient2k3() {
		return CLIENT2K3;
	}

	public static String getClient2k7() {
		return CLIENT2K7;
	}

	public static String getHigherThan2kconn() {
		return HIGHER_THAN_2KCONN;
	}

	public static String getLowerThan2kconn() {
		return LOWER_THAN_2KCONN;
	}

	public static String getSecureConn() {
		return SECURE_CONN;
	}

	public static String getSessionCount() {
		return SESSION_COUNT;
	}

	public static String getDiskCapacity() {
		return DISK_CAPACITY;
	}

	public static String getCacheCount() {
		return CACHE_COUNT;
	}

	public static String getCacheUtilization() {
		return CACHE_UTILIZATION;
	}

	public static String getFileCount() {
		return FILE_COUNT;
	}

	public static String getHitRate() {
		return HIT_RATE;
	}

	public static String getRequestCount() {
		return REQUEST_COUNT;
	}

	public static String getResourceUtilization() {
		return RESOURCE_UTILIZATION;
	}

	public static String getAuthFlavor2Count() {
		return AUTH_FLAVOR2_COUNT;
	}

	public static String getAuthFalvor3Count() {
		return AUTH_FALVOR3_COUNT;
	}

	public static String getAuthFlavorUnknown() {
		return AUTH_FLAVOR_UNKNOWN;
	}

	public static String getNfsV2Count() {
		return NFS_V2_COUNT;
	}

	public static String getNfsV3Count() {
		return NFS_V3_COUNT;
	}

	public static String getNfsV4Count() {
		return NFS_V4_COUNT;
	}

	public static String getNfsUnknownCount() {
		return NFS_UNKNOWN_COUNT;
	}

	public static String getReused() {
		return REUSED;
	}

	public static String getIdle() {
		return IDLE;
	}

	public static String getReusedPeer() {
		return REUSED_PEER;
	}

	public static String getReusedconnPerOperation() {
		return REUSEDCONN_PER_OPERATION;
	}

	public static String getIdleconnPerOperation() {
		return IDLECONN_PER_OPERATION;
	}

	public static String getFastConnectionSetupCount() {
		return FAST_CONNECTION_SETUP_COUNT;
	}

	public static String getMaxReusedPerOperation() {
		return MAX_REUSED_PER_OPERATION;
	}

	public static String getPctRttSaved() {
		return PCT_RTT_SAVED;
	}

	public static String getTotalHandoff() {
		return TOTAL_HANDOFF;
	}

	public static String getTotalLanConn() {
		return TOTAL_LAN_CONN;
	}

	public static String getMaxReused() {
		return MAX_REUSED;
	}

	public static String getMaxReusedConn() {
		return MAX_REUSED_CONN;
	}

	public static String getPercentConnTimeSaved() {
		return PERCENT_CONN_TIME_SAVED;
	}

	public static String getAcceleratedConn() {
		return ACCELERATED_CONN;
	}

	public static String getIncomingBytesTotal() {
		return INCOMING_BYTES_TOTAL;
	}

	public static String getOutgoingBytesTotal() {
		return OUTGOING_BYTES_TOTAL;
	}

	public static String getErrorConn() {
		return ERROR_CONN;
	}

	public static String getRecievedConn() {
		return RECIEVED_CONN;
	}

	public static String getUnacceleratedConn() {
		return UNACCELERATED_CONN;
	}

	public static String getDeviceIp() {
		return DEVICE_IP;
	}

	public static String getTimestamp() {
		return TIMESTAMP;
	}

	public static String getFrequency() {
		return FREQUENCY;
	}

	public static String getCompressedIn() {
		return COMPRESSED_IN;
	}

	public static String getCompressedOut() {
		return COMPRESSED_OUT;
	}

	public static String getUncompressedIn() {
		return UNCOMPRESSED_IN;
	}

	public static String getUncompressedOut() {
		return UNCOMPRESSED_OUT;
	}

	public static String getPassthroughOtherOut() {
		return PASSTHROUGH_OTHER_OUT;
	}

	public static String getPassthroughIntermediateIn() {
		return PASSTHROUGH_INTERMEDIATE_IN;
	}

	public static String getPassthroughOut() {
		return PASSTHROUGH_OUT;
	}

	public static String getPassthroughOverloadIn() {
		return PASSTHROUGH_OVERLOAD_IN;
	}

	public static String getPassthroughOverloadOut() {
		return PASSTHROUGH_OVERLOAD_OUT;
	}

	public static String getPassthroughPeerIn() {
		return PASSTHROUGH_PEER_IN;
	}

	public static String getPassthorughPeerOut() {
		return PASSTHORUGH_PEER_OUT;
	}

	public static String getPassthorughPolicyIn() {
		return PASSTHORUGH_POLICY_IN;
	}

	public static String getPassthorughPolicyOut() {
		return PASSTHORUGH_POLICY_OUT;
	}

	public static String getGetPassthorughPolicyOut() {
		return GET_PASSTHORUGH_POLICY_OUT;
	}
	
}
