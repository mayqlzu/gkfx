package com.gkfx;

import java.util.HashMap;

public class ExRateTable {
	// can not use float
	private HashMap<String, Float> m_map; // use lower case as key
	
	public ExRateTable(HashMap<String, Float> m){
		m_map = m;
	}
	
	public boolean containsKey(String key){
		return m_map.containsKey(key);
	}
	
	public HashMap<String, Float> getTheMap(){
		return m_map;
	}
	
	public float getRate(String symbol){
		return (Float) m_map.get(symbol.toLowerCase()); // accept both AAABBB and aaabbb
	}

}
