package com.gkfx;

public class FxDirectInCommCondition {

	private float 	m_lowRate;
	private float	m_middleRate;
	private float	m_highRate;

	private	float	m_quota;

	public FxDirectInCommCondition(float m_lowRate, float m_middleRate,
			float m_highRate, float quota) {
		super();
		this.m_lowRate = m_lowRate;
		this.m_middleRate = m_middleRate;
		this.m_highRate = m_highRate;
		this.m_quota = quota;
	}

	public float getRate(float vol){
		if(vol < 500){ // todo: 1.  gkfx may change range ?  2. what if =500, ==1000 ?
			return m_lowRate;
		}else if(vol < 1000){
			return m_middleRate;
		}else
			return m_highRate;
	}

	public float getQuota(){
		return m_quota;
	}
}

