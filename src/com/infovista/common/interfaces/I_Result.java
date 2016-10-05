package com.infovista.common.interfaces;

import java.util.List;

public interface I_Result{

	public String[] getResultArray();
	
	public <T extends Object> I_Result setProperties(T obj);
}
