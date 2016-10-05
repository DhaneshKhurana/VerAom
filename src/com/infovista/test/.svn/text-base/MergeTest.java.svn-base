package com.infovista.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.infovista.common.classes.IPStringComparator;
import com.infovista.common.interfaces.I_Result;
import com.infovista.fileutils.CsvReader;
import com.infovista.fileutils.CsvWriter;
import com.infovista.instances.Keyword;

public class MergeTest {
	
	Map<String, Map<Long, I_Result>> resultMap = new TreeMap<>(new IPStringComparator());
	
	
	public static void main(String[] args){
		MergeTest test = new MergeTest();
		test.populateMapFromFile("Output_stats/MonitoredAPP_CIFS.csv");
		CsvWriter.checkAndWrite("Output_stats/MonitoredAPP_Citrix.csv", Keyword.TRAFFIC, test.resultMap);
		
		String[] st1= new String[]{"abc", "cde", "efg"};
		String[] st2= new String[]{"abc", "cde", "efg"};
		String[] st3= new String[]{"abc", "cde", "efg"};
		String[] st4= new String[]{"abc", "cde", "efg"};
		String[] st5= new String[]{"abc", "cde", "efg"};
		String[] st6= new String[]{"abc", "cde", "efg"};
		
		List<String[]> ls = new ArrayList<>();
		ls.add(st1);
		ls.add(st2);
		ls.add(st3);
		ls.add(st4);
		ls.add(st5);
		ls.add(st6);
		
		new CsvWriter("log/try.csv").writeList(ls);
	}
	
	public void populateMapFromFile(String filename){
		CsvReader.populateStubMapFromFile(filename, resultMap);
		System.out.println("writing populated map");
		for(Entry<String, Map<Long, I_Result>> ipmap : resultMap.entrySet()){
			for(Entry<Long, I_Result> timeMap : ipmap.getValue().entrySet()){
				System.out.print(ipmap.getKey() + ";" + timeMap.getKey() );
				for(String value : timeMap.getValue().getResultArray()){
					System.out.print(";" + value);	
				}
				System.out.println();
			}
		}
	}
}
