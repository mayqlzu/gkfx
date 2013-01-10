package com.gkfx;

import java.util.HashMap;

public class ExchangeRateTable {
	// i want to use float in the place of Object, but HashMap does not support
	HashMap<String, Object> m_map;
	
	private ExchangeRateTable(){ // other class can NOT new me
		m_map = new HashMap<String, Object>();
		
		/*
		 * todo:
		 * read from file 
		 */
	}
	
	public float getRate(String symbol){
		return (Float) m_map.get(symbol);
	}

}
