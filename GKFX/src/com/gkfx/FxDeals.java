package com.gkfx;

import java.util.ArrayList;

public class FxDeals {
	private ArrayList<Deal>		m_deals; // fx deals only, no commodity or others
	
	private Condition			m_condition;
	
	public FxDeals() {
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
	
	private boolean shouldComputeInCommByQuota(){
		// use GBPUSD quota as flag
		float quota = m_condition.getM_in().getFx().getGBPUSD().getQuota();
		if(0 != quota)
			return true;
		else
			return false;
	}
	
	private void computeInCommissionForEveryDeal(){
		if(shouldComputeInCommByQuota()){
			computeInCommissionByQuotaForEveryDeal();
		}else{
			computeInCommissionByPointForEveryDeal();
		}
	}
	
	private void computeInCommissionByQuotaForEveryDeal(){
		for(Deal d : m_deals){
			String symbol = d.getSymbol();
			float slot = d.getVol();
			
			/* 	i get m_fxCommodityVolSum from m_condition and pass it back,
			 *	to reminder myself that this quoatPerSlot depends on fxCommodityVolSum 
			 */
			float quotaPerSlot = m_condition.getFxInCommQuotaPerSlotBySymbol(
					m_condition.getFxCommodityVolSum(),
					symbol); 
			float inComm = slot * quotaPerSlot;
			d.setInComm(inComm);
		}
	}
	
	private void computeInCommissionByPointForEveryDeal(){
		for(Deal d : m_deals){
			String symbol = d.getSymbol();
			float slot = d.getVol();
			/* 	i get m_fxCommodityVolSum from m_condition and pass it back,
			 *	to reminder myself that this quoatPerSlot depends on fxCommodityVolSum 
			 */
			float pointPerSlot = m_condition.getFxInCommPointPerSlotBySymbol(
					m_condition.getFxCommodityVolSum(), symbol);
			float PIP = FxPIPTable.getPIPBySymbol(symbol);
			float inComm = slot * pointPerSlot * PIP;
			d.setInComm(inComm);
		}
	}
	
	private void computeMarkupForEveryDeal(){
		for(Deal d : m_deals){
			String symbol = d.getSymbol();
			float slot = d.getVol();
			float pointPerSlot =  m_condition.getFxMarkupPointPerSlotBySymol(symbol);
			float PIP = FxPIPTable.getPIPBySymbol(symbol);
			float markup = slot * pointPerSlot * PIP;
			d.setMarkup(markup);
		}
		
	}
	
	private void computeOutCommissionForEveryDeal(){
		for(Deal d : m_deals){
			String symbol = d.getSymbol();
			float slot = d.getVol();
			float quotaPerSlot = m_condition.getFxOutCommQuotaPerSlotBySymbol(symbol);
			float outComm = slot * quotaPerSlot;
			d.setOutComm(outComm);
		}	
		
	}
}
