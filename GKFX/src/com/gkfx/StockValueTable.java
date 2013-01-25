package com.gkfx;


public class StockValueTable {
	private ExRateTable		m_exRateTable;
	
	public StockValueTable(ExRateTable t){
		m_exRateTable = t;
	}
	
	public float getValuePerSlot(String symbol, float closePrice){
		String market = Tools.lastTwoCharsOf(symbol);
		float val;
		if(market.equals("us")){
			val = closePrice;
		}
		else if(market.equals("uk")){
			val = m_exRateTable.getRate("gbpusd") * closePrice;
		}
		else if(market.equals("de")
				|| market.equals("fr")
				|| market.equals("nl")
				|| market.equals("it")
				){
			val = m_exRateTable.getRate("eurusd") * closePrice;
		}
		else if(market.equals("ch")){
			val = 1 / m_exRateTable.getRate("usdchf") * closePrice;
		}else{
			System.out.println("StockValueTable: invalid market" + market);
			val = 0;
		}
		
		return val;
	}

}
