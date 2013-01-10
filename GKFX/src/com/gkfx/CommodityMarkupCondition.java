package com.gkfx;

public class CommodityMarkupCondition {
	private float	m_gold;
	private float	m_silver;
	private float	m_oil;
	
	public CommodityMarkupCondition(float m_gold, float m_silver, float m_oil) {
		super();
		this.m_gold = m_gold;
		this.m_silver = m_silver;
		this.m_oil = m_oil;
	}
	public float getM_gold() {
		return m_gold;
	}
	public void setM_gold(float m_gold) {
		this.m_gold = m_gold;
	}
	public float getM_silver() {
		return m_silver;
	}
	public void setM_silver(float m_silver) {
		this.m_silver = m_silver;
	}
	public float getM_oil() {
		return m_oil;
	}
	public void setM_oil(float m_oil) {
		this.m_oil = m_oil;
	}

}
