package com.gkfx;

public class Summary {
	float m_slotSum;
	float m_rebateSum;
	float m_markupSum;
	float m_commSum;
	float m_totalFee;
	
	
	public Summary() {
		super();
		this.m_slotSum		= 0;
		this.m_rebateSum	= 0;
		this.m_markupSum	= 0;
		this.m_commSum		= 0;
		
		this.m_totalFee		= 0;
	}
	
	public Summary(float slotSum, float rebate, float markupSum,
			float comm) {
		super();
		this.m_slotSum		= slotSum;
		this.m_rebateSum	= rebate;
		this.m_markupSum	= markupSum;
		this.m_commSum		= comm;
		
		this.m_totalFee		= this.m_rebateSum + this.m_markupSum + this.m_commSum;
	}
	
	public void add(Summary another){
		this.m_slotSum		+= another.m_slotSum;
		this.m_rebateSum	+= another.m_rebateSum;
		this.m_markupSum	+= another.m_markupSum;
		this.m_commSum		+= another.m_commSum;
		this.m_totalFee		+= another.m_totalFee;
	}
	
	public float getSlotSum(){
		return this.m_slotSum;
	}
	
	public float getRebateSum(){
		return this.m_rebateSum;
	}
	
	public float getMarkupSum(){
		return this.m_markupSum;
	}
	
	public float getCommSum(){
		return this.m_commSum;
	}
	
	public float getTotalFee(){
		return this.m_totalFee;
	}

}
