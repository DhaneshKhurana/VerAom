package com.infovista.instances;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.infovista.client.CiscoWAASDiscoverer;

public class Instance {
	private static final String SEP = ";";
	public static final String SERVICE_TYPE = "SERVICE";
	public static final String WAE_TYPE = "WAE";
	public static final String WAE_GROUP_TYPE = "WAE GROUP";
	public static final String OPTIMIZATION_SERVICE = "SERVICE";
	public static final String APPLICATION_TYPE = "APPLICATION";
	public static final String APPLICATION_GROUP_TYPE = "APPLICATION GROUP";
	public static final String SITE_TYPE = "SITE";
	public static final String CUSTOMER_TYPE = "CUSTOMER";
	public static final String LOCATION_TYPE = "LOCATION";
	public static final String OAM = "OAM";
	String tag;
	String name;
	String type;
	Map<String, String> properties;
	public static final String[] HEADER=new String[]{Keyword.TAG,Keyword.INSTANCE_NAME};
	public static final String[] ORDERED_INSTANCE_TYPES = new String[]{SITE_TYPE,OPTIMIZATION_SERVICE,WAE_GROUP_TYPE,WAE_TYPE,APPLICATION_GROUP_TYPE};
	public Instance(String type) {
		this.type = type;	
		properties = new HashMap<String, String>();
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Map<String, String> getProperties() {
		return properties;
	}
	
	public void addAttribute(String property, String value){
		properties.put(property,value);
	}
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}


	public String getValue(String attribute) {
		return properties.get(attribute);
	}
	
	@Override
	public int hashCode() {
		return tag.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null){
			return false;
		}
		Instance otherInstance = (Instance)obj;
		
		return tag.equalsIgnoreCase(otherInstance.getTag());
	}
	
	public String getHeader(){
		String[] headerVariables = getHeaderVariables();
		String header = StringUtils.join(headerVariables, SEP);
		return "#TYPE;"+header;
		
	}
	
	protected String[] getHeaderVariables(){
		return HEADER;
	}
	
	@Override
	public String toString() {
		String[] headerVariables = getHeaderVariables();
		StringBuilder retVal = new StringBuilder(type);
		for (String variable : headerVariables) {
			String value = getValue(variable);
			if(value==null){
				value=CiscoWAASDiscoverer.EMTPY_STRING;
			}
			retVal.append(SEP).append(value);
		}
		return retVal.toString();
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		addAttribute(Keyword.TAG, tag);
		this.tag = tag;
	}
}
