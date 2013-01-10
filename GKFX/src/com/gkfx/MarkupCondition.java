package com.gkfx;

public class MarkupCondition {
	private FxMarkupCondition			m_fx;
	private	float						m_stock;
	private CommodityMarkupCondition	m_commodity;
	private float						m_index;
	
	public MarkupCondition(FxMarkupCondition fx, 
			CommodityMarkupCondition commodity) {
		super();
		this.m_fx = fx;
		this.m_commodity = commodity;
		this.m_index = 0;	// default
		this.m_stock = 0; 	// default
	}
	public FxMarkupCondition getFx() {
		return m_fx;
	}
	public void setFx(FxMarkupCondition fx) {
		this.m_fx = fx;
	}
	public float getStock() {
		return m_stock;
	}
	public void setStock(float stock) {
		this.m_stock = stock;
	}
	public CommodityMarkupCondition getCommodity() {
		return m_commodity;
	}
	public void setCommodity(CommodityMarkupCondition commodity) {
		this.m_commodity = commodity;
	}
	public float getIndex() {
		return m_index;
	}
	public void setIndex(float index) {
		this.m_index = index;
	}
	
	
	
}
