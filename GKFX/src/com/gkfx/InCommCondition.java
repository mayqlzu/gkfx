package com.gkfx;

public class InCommCondition{
	private FxInCommCondition			m_fx;
	private CommodityInCommCondition	m_commodity;
	private StockInCommCondition		m_stock;
	private IndexInCommCondition		m_index;
	
	public InCommCondition(
			FxInCommCondition fx, CommodityInCommCondition commdity, 
			StockInCommCondition stock, IndexInCommCondition index) {
		this.m_fx		= fx;
		this.m_commodity = commdity;
		this.m_stock	= stock;
		this.m_index	= index;
	}

	public FxInCommCondition getFx() {
		return m_fx;
	}


	public CommodityInCommCondition getCommodity() {
		return m_commodity;
	}

	public StockInCommCondition getStock() {
		return m_stock;
	}


	public IndexInCommCondition getIndex() {
		return m_index;
	}
	
}

	
	






