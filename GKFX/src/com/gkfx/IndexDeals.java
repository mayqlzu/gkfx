package com.gkfx;

import java.util.ArrayList;

public class IndexDeals {
	private ArrayList<Deal>		m_deals;
	private Condition			m_condition;
	
	public IndexDeals() {
		m_deals = new ArrayList<Deal>();
	}
	
	public void addDeal(Deal d){
		m_deals.add(d);
	}
	
	public void setCondition(Condition c) {
		this.m_condition = c;
		
	}

	public void computeRelatedFee() {
		computeInCommissionForEveryDeal();
		computeMarkupForEveryDeal();
		computeOutCommissionForEveryDeal();
	}
	
	private void computeInCommissionForEveryDeal(){
		float notionalAmountTraded = m_condition.getIndexNotionalAmountTraded();
		// same percentage for all index deals
		float percent = m_condition.getIndexInCommPercentage(notionalAmountTraded) ;
		
		for(Deal d: m_deals){
			float slot = d.getVol();
			float valuePerSlot = 
					IndexValueTableFactory.getTheIndexValueTable().getValuePerSlot(d.getSymbol());
			float inComm = slot * valuePerSlot * percent;
			d.setInComm(inComm);
		}
	}
	
	private void computeMarkupForEveryDeal(){
		for(Deal d: m_deals){
			d.setMarkup(0); // no markup for index deals
		}
	}
	
	private void computeOutCommissionForEveryDeal(){
		float quotaPerSlot = m_condition.getIndexOutCommQuotaPerSlot(); // same quota for all index deals of one IB
		for(Deal d: m_deals){
			float slot = d.getVol();
			float outComm = slot * quotaPerSlot;
			d.setOutComm(outComm);
		}
	}
}
