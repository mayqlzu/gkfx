package com.gkfx;

import java.util.HashMap;

public class IndexValueTable {
	private HashMap<String, Object> m_map;
	private HashMap<String, Object> m_settlementPrices;
	
	public IndexValueTable(){
		m_map = new HashMap<String, Object>();
		m_settlementPrices = new HashMap<String, Object>();
		// todo: read file to init m_settlementPrices
		
		ExchangeRateTable exRateTable = ExchangeRateTableFactory.getTheExchangeRateTable();
		
		// be careful, check jeffery's doc
		float valPerSlot;
		String symbol;
		
		symbol = "uk100";
		valPerSlot = 100 * exRateTable.getRate("gbpusd") * (Float)m_settlementPrices.get(symbol);
		m_map.put(symbol, valPerSlot);
		
		symbol = "esx50";
		valPerSlot = 100 * exRateTable.getRate("eurusd") * (Float)m_settlementPrices.get(symbol);
		m_map.put(symbol, valPerSlot);
		
		symbol = "dax30";
		valPerSlot = 100 * exRateTable.getRate("eurusd") * (Float)m_settlementPrices.get(symbol);
		m_map.put(symbol, valPerSlot);
		
		symbol = "sp500";
		valPerSlot = 500 * exRateTable.getRate("eurusd") * (Float)m_settlementPrices.get(symbol);
		m_map.put(symbol, valPerSlot);
		
		symbol = "ws30";
		valPerSlot = 100 * (Float)m_settlementPrices.get(symbol);
		m_map.put(symbol, valPerSlot);
		
		symbol = "nas100";
		valPerSlot = 200 * (Float)m_settlementPrices.get(symbol);
		m_map.put(symbol, valPerSlot);
		
		symbol = "russ";
		valPerSlot = 500 * (Float)m_settlementPrices.get(symbol);
		m_map.put(symbol, valPerSlot);
		
		symbol = "aex25";
		valPerSlot = 250 * exRateTable.getRate("eurusd") * (Float)m_settlementPrices.get(symbol);
		m_map.put(symbol, valPerSlot);
		
		symbol = "cac40";
		valPerSlot = 100 * exRateTable.getRate("eurusd") * (Float)m_settlementPrices.get(symbol);
		m_map.put(symbol, valPerSlot);
		
		symbol = "smi20";
		valPerSlot = 100 / exRateTable.getRate("usdchf") * (Float)m_settlementPrices.get(symbol);
		m_map.put(symbol, valPerSlot);
		
		symbol = "swe30";
		valPerSlot = 100 / exRateTable.getRate("usdsek") * (Float)m_settlementPrices.get(symbol);
		m_map.put(symbol, valPerSlot);
	}
	
	public float getValuePerSlot(String symbol){
		return (Float)this.m_map.get(symbol);
	}

}
