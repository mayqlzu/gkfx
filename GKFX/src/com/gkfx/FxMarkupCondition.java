package com.gkfx;

public class FxMarkupCondition {
	private float	m_GBPUSD;
	private	float	m_direct;
	private	float	m_cross;
	
	public FxMarkupCondition(float m_GBPUSD, float m_direct, float m_cross) {
		super();
		this.m_GBPUSD = m_GBPUSD;
		this.m_direct = m_direct;
		this.m_cross = m_cross;
	}
	
	public float getM_GBPUSD() {
		return m_GBPUSD;
	}
	public void setM_GBPUSD(float m_GBPUSD) {
		this.m_GBPUSD = m_GBPUSD;
	}
	public float getM_direct() {
		return m_direct;
	}
	public void setM_direct(float m_direct) {
		this.m_direct = m_direct;
	}
	public float getM_cross() {
		return m_cross;
	}
	public void setM_cross(float m_cross) {
		this.m_cross = m_cross;
	}
	

}
