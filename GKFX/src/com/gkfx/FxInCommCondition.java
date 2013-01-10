package com.gkfx;

public class FxInCommCondition {
	private FxGBPUSDInCommCondition		m_GBPUSD;
	private FxDirectInCommCondition		m_direct;
	private FxCrossInCommCondition		m_cross;
	
	public FxInCommCondition(FxGBPUSDInCommCondition m_GBPUSD,
			FxDirectInCommCondition m_straight, FxCrossInCommCondition m_cross) {
		super();
		this.m_GBPUSD = m_GBPUSD;
		this.m_direct = m_straight;
		this.m_cross = m_cross;
	}

	public FxGBPUSDInCommCondition getGBPUSD() {
		return m_GBPUSD;
	}

	public void setGBPUSD(FxGBPUSDInCommCondition m_GBPUSD) {
		this.m_GBPUSD = m_GBPUSD;
	}

	public FxDirectInCommCondition getDirect() {
		return m_direct;
	}

	public void setDirect(FxDirectInCommCondition m_direct) {
		this.m_direct = m_direct;
	}

	public FxCrossInCommCondition getCross() {
		return m_cross;
	}

	public void setCross(FxCrossInCommCondition m_cross) {
		this.m_cross = m_cross;
	}
	
	

	
}