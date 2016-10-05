package com.infovista.instances;

public class OptimizationService extends Instance {
	public OptimizationService() {
		super(Instance.OPTIMIZATION_SERVICE);
	}

	public static final String[] HEADER=new String[]{Keyword.TAG,Keyword.INSTANCE_NAME,Keyword.CUSTOMER,Keyword.NAME};
	protected String[] getHeaderVariables(){
		return HEADER;
	}
}
