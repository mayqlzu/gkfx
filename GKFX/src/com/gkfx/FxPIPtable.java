package com.gkfx;

public class FxPIPtable {
	private ExRateTable m_rateTable;
	
	public FxPIPtable(ExRateTable t){
		this.m_rateTable = t;
	}
	
	// according to jeffery's doc
	public float getPIPbySymbol(String symbol){
		String quoteCurrency = Tools.getQuoteCurrency(symbol);
		if(quoteCurrency.equals("usd")){
			return 10;
		}else if(quoteCurrency.equals("eur")
				|| quoteCurrency.equals("gbp")
				|| quoteCurrency.equals("nzd")
				|| quoteCurrency.equals("aud")){
			return 10 * m_rateTable.getRate(quoteCurrency + "usd");
		}else if(quoteCurrency.equals("jpy")
				|| quoteCurrency.equals("thb")
				|| quoteCurrency.equals("huf")){
			return 1000 / m_rateTable.getRate("usd" + quoteCurrency);
		}else{
			return 10 / m_rateTable.getRate("usd" + quoteCurrency);
		}
	}
	
}
