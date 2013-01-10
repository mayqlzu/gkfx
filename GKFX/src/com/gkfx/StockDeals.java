package com.gkfx;

import java.util.ArrayList;

public class StockDeals {
	private ArrayList<Deal>		m_deals;
	private Condition			m_condition;
	
	public StockDeals() {
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
		computeOutCommForEveryDeal();
	}
	
	private void computeInCommissionForEveryDeal(){
		float percent = m_condition.getStockInCommPercentage();
		for(Deal d :m_deals){
			float slot = d.getVol();
			float valuePerSlot = 
					StockValueTableFactory.getTheStockValueTable().getValuePerSlot(d.getSymbol());
			float inComm = slot * valuePerSlot * percent;
			d.setInComm(inComm);
		}
	}
	
	private void computeMarkupForEveryDeal(){
		for(Deal d :m_deals){
			d.setMarkup(0); // no markup for stock deals
		}
	}
	
	private void computeOutCommForEveryDeal(){
		float quotaPerSlot = m_condition.getStockOutCommQuotaPerSlotBySymbol();
		for(Deal d :m_deals){
			float slot = d.getVol();
			float outComm = slot * quotaPerSlot;
			d.setOutComm(outComm);
		}
	}
	
}
