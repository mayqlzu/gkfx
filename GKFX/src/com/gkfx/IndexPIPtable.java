package com.gkfx;

public class IndexPIPtable {
	private ExRateTable m_rateTable;
	
	public IndexPIPtable(ExRateTable t){
		this.m_rateTable = t;
	}
	
	// according to jeffery's doc
	public float getPIPbySymbol(String symbol){
		if(symbol.equals("uk100")){
			return m_rateTable.getRate("gbpusd");
		}else if(symbol.equals("esx50")
				|| symbol.equals("dax30")
				|| symbol.equals("aex25")
				|| symbol.equals("cac40")){
			return 10 * m_rateTable.getRate("eurusd");
		}
		else if(symbol.equals("sp500")
				|| symbol.equals("ws30")
				|| symbol.equals("nas100")
				|| symbol.equals("russ")){
			return 1;
		}
		else if(symbol.equals("smi20")){
			return 1/m_rateTable.getRate("usdchf");
		}
		else if(symbol.equals("swe30")){
			return 1/m_rateTable.getRate("usdsek");
		}
		else if(symbol.equals("ise30")){
			return 1/m_rateTable.getRate("usdtry");
		}
		else{
			System.out.println("IndexPIPtable: getPIPbySymbol(): invalid symbol " + symbol);
			return 0;
		}
				
	}
	
}
