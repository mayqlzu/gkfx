package com.gkfx;

public class IndexInCommCondition {
	private float	m_low;		// percentage like 0.0035%
	private float 	m_middle;
	private	float	m_high;
	
	public IndexInCommCondition(float m_low, float m_middle, float m_high) {
		super();
		this.m_low = m_low;
		this.m_middle = m_middle;
		this.m_high = m_high;
	}
	
	public float getPercentage(float notionalAmountTraded){
		if(notionalAmountTraded < 250){ // 250 million USD
			return m_low;
		}else if(notionalAmountTraded < 500){ // though seldom IB can reach this
			return m_middle;
		}else{
			return m_high;
		}
	}
}
