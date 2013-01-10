package com.gkfx;

public class CommodityInCommCondition{
	private CommodityGoldInCommCondition	m_gold;
	private CommoditySilverInCommCondition	m_silver;
	private CommodityOilInCommCondition		m_oil;
	
	public CommodityInCommCondition(CommodityGoldInCommCondition m_gold,
			CommoditySilverInCommCondition m_silver,
			CommodityOilInCommCondition m_oil) {
		super();
		this.m_gold = m_gold;
		this.m_silver = m_silver;
		this.m_oil = m_oil;
	}

	public CommodityGoldInCommCondition getM_gold() {
		return m_gold;
	}

	public void setM_gold(CommodityGoldInCommCondition m_gold) {
		this.m_gold = m_gold;
	}

	public CommoditySilverInCommCondition getM_silver() {
		return m_silver;
	}

	public void setM_silver(CommoditySilverInCommCondition m_silver) {
		this.m_silver = m_silver;
	}

	public CommodityOilInCommCondition getM_oil() {
		return m_oil;
	}

	public void setM_oil(CommodityOilInCommCondition m_oil) {
		this.m_oil = m_oil;
	}

	
	
}
