package com.infovista.instances;

public class Site extends Instance {
	public static final String[] HEADER=new String[]{Keyword.NAME};
	public Site() {
		super(Instance.SITE_TYPE);
	}
	protected String[] getHeaderVariables(){
		return HEADER;
	}
}
