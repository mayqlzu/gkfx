package com.gkfx;

public class IBParam {
	private float m_fxGoldSlotSum;
	private float m_silverOilIndexValueSumMillion; // 1 means 1 million USD
	
	public IBParam(float m_fxGoldSlotSum, float m_silverOilIndexValueSumMillion) {
		super();
		this.m_fxGoldSlotSum = m_fxGoldSlotSum;
		this.m_silverOilIndexValueSumMillion = m_silverOilIndexValueSumMillion;
	}
	
	public float getM_fxGoldSlotSum() {
		return m_fxGoldSlotSum;
	}
	
	public float getM_silverOilIndexValueSumMillion() {
		return m_silverOilIndexValueSumMillion;
	}
	

}
