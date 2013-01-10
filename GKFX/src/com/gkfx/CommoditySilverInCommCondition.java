package com.gkfx;

public class CommoditySilverInCommCondition {
	private float	m_low;
	private float	m_middle;
	private float	m_high;

	public CommoditySilverInCommCondition(float m_low, float m_middle,
			float m_high) {
		super();
		this.m_low = m_low;
		this.m_middle = m_middle;
		this.m_high = m_high;
	}

	public float getQuotaPerSlot(float slot){
		if(slot < 500){
			return m_low;
		}else if(slot < 1000){
			return m_middle;
		}else{
			return m_high;
		}


	}
}
