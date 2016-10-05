package com.infovista.common.classes;

import java.util.Comparator;

public class IPStringComparator implements Comparator<String>{
	
	public final static String ipAddressSeperator = "\\.";
	
	@Override
	public int compare(String obj1, String obj2) {
		String[] ip1 = obj1.split(ipAddressSeperator);
		String[] ip2 = obj2.split(ipAddressSeperator);
		
		for(int i=0; i<ip1.length;i++){
			if(ip1[i].length()!=ip2[i].length()){
				if( ip1[i].length()>ip2[i].length() )
					return 1;
				else
					return -1;
			}else{
				if(!ip1[i].equals(ip2[i])){
					return ip1[i].compareTo(ip2[i]);
				}
			}
		}
		return 0;
	}

}
