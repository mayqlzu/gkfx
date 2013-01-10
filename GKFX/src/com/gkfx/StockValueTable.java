package com.gkfx;

import java.util.HashMap;

public class StockValueTable {
	HashMap<String, Object> m_map;
	
	public StockValueTable(){
		m_map = new HashMap<String, Object>();
		
		/*
		 *  todo: read file to init the map
		 *  attention: more than items in jeffery's doc
		 *	valPerSlot = exRate * settlementPrice;
		 */
		
	}
	
	public float getValuePerSlot(String symbol){
		return (Float)m_map.get(symbol);
	}

}
