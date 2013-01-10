package com.gkfx;

// all quota
public class OutCommCondition {
	private FxOutCommCondition			m_fx;
	private float						m_stock;
	private CommodityOutCommCondition	m_commodity;
	private	float						m_index;
	
	public OutCommCondition(FxOutCommCondition m_fx, float m_stock,
			CommodityOutCommCondition m_commodity, float m_index) {
		super();
		this.m_fx = m_fx;
		this.m_stock = m_stock;
		this.m_commodity = m_commodity;
		this.m_index = m_index;
	}
	
	public FxOutCommCondition getM_fx() {
		return m_fx;
	}
	public void setM_fx(FxOutCommCondition m_fx) {
		this.m_fx = m_fx;
	}
	public float getM_stock() {
		return m_stock;
	}
	public void setM_stock(float m_stock) {
		this.m_stock = m_stock;
	}
	public CommodityOutCommCondition getM_commodity() {
		return m_commodity;
	}
	public void setM_commodity(CommodityOutCommCondition m_commodity) {
		this.m_commodity = m_commodity;
	}
	public float getM_index() {
		return m_index;
	}
	public void setM_index(float m_index) {
		this.m_index = m_index;
	}
	
	
}
