package com.gkfx;

// we need one ExchangeRate table only
public class ExchangeRateTableFactory {
	private static ExchangeRateTable m_table;
	
	public static ExchangeRateTable getTheExchangeRateTable(){
		return m_table;
	}

}
