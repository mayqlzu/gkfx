package com.gkfx;

public class IndexValueTable {
	private ExRateTable 			m_exRateTable;
	
	public IndexValueTable(ExRateTable exRateTable){
		m_exRateTable = exRateTable;
	}
	
	public float getValuePerSlot(String symbol, float closePrice){

		// be careful, check jeffery's doc
		float valPerSlot;
		
		if(symbol.equals("uk100")){
			valPerSlot = m_exRateTable.getRate("gbpusd") * closePrice;
		}
		else if(symbol.equals("esx50")){
			valPerSlot = m_exRateTable.getRate("eurusd") * closePrice;
		}
		else if(symbol.equals("dax30")){
			valPerSlot = m_exRateTable.getRate("eurusd") * closePrice;
		}
		else if(symbol.equals("sp500")){
			valPerSlot = m_exRateTable.getRate("eurusd") * closePrice;
		}
		else if(symbol.equals("ws30")){
			valPerSlot = closePrice;
		}
		else if(symbol.equals("nas100")){
			valPerSlot = closePrice;
		}
		else if(symbol.equals("russ")){
			valPerSlot = closePrice;
		}
		else if(symbol.equals("aex25")){
			valPerSlot = m_exRateTable.getRate("eurusd") * closePrice;
		}
		else if(symbol.equals("cac40")){
			valPerSlot = m_exRateTable.getRate("eurusd") * closePrice;
		}
		else if(symbol.equals("smi20")){
			valPerSlot = 1 / m_exRateTable.getRate("usdchf") * closePrice;
		}
		else if(symbol.equals("swe30")){
			valPerSlot = 1 / m_exRateTable.getRate("usdsek") * closePrice;
		}
		else if(symbol.equals("ise30")){
			valPerSlot = 1 / m_exRateTable.getRate("usdtry") * closePrice;
		}
		else{
			valPerSlot = 0;
			System.out.println("IndexValueTable:getValPerSlot(): invliad symbol: " + symbol);
		}
		
		return valPerSlot;
	}

}
