package com.gkfx;

public class FxPIPTable {
	// according to jeffery's doc
	public static float getPIPBySymbol(String symbol){
		ExchangeRateTable table = ExchangeRateTableFactory.getTheExchangeRateTable();
		
		String quoteCurrency = getQuoteCurrency(symbol);
		if(quoteCurrency.equals("usd")){
			return 10;
		}else if(quoteCurrency.equals("eur")
				|| quoteCurrency.equals("gbp")
				|| quoteCurrency.equals("nzd")
				|| quoteCurrency.equals("aud")){
			return 10 * table.getRate(quoteCurrency + "usd");
		}else if(quoteCurrency.equals("jpy")
				|| quoteCurrency.equals("thb")
				|| quoteCurrency.equals("huf")){
			return 1000 / table.getRate("usd" + quoteCurrency);
		}else{
			return 10 / table.getRate("usd" + quoteCurrency);
		}
	}
	
	/*
	 * quota: pei e
	 * quote: bao jia
	 */
	private static String getQuoteCurrency(String pair){
		assert(6 == pair.length());
		return pair.substring(3);
	}
	
}
