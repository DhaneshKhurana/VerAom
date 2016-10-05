package com.infovista.instances;

public class WAE extends Instance {
	public static final String[] HEADER=new String[]{Keyword.TAG,Keyword.NAME,Keyword.IP,Keyword.SOAPURL,Keyword.SOAPUSER,Keyword.SOAPPASSWORD,Keyword.LOCATION,Keyword.SERVICEID,Keyword.WAE_GROUP_ID,Keyword.CAPABILITIES,Keyword.CUSTOMER};
	public WAE() {
		super(Instance.WAE_TYPE);
	}
	protected String[] getHeaderVariables(){
		return HEADER;
	}
}
