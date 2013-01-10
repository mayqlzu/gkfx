package com.gkfx;

import java.util.ArrayList;

public class CommodityDeals {
	private ArrayList<Deal>		m_deals;
	private Condition			m_condition;
	
	public CommodityDeals() {
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
		for(Deal d: m_deals){
			/*
			 *  Deal.vol is from html file directly, 
			 *  slot = vol/10 for all commodity(gold, silver, oil(brent, wti))
			 */
			float slot = d.getVol() / 10;
			/* 1) do NOT distinguish gold, silver and oil here, 
			 * because getInCommQuotaPerSlotBySymbol will return different value
			 * 2) i get m_fxCommodityVolSum from m_condition and pass it back,
			 * to reminder myself that this quotaPerSlot depends on fxCommodityVolSum
			 */
			float quotaPerSlot = m_condition.getCommodityInCommQuotaPerSlotBySymbol(
					m_condition.getFxCommodityVolSum(),
					d.getSymbol());
			float inComm = slot * quotaPerSlot;
			d.setInComm(inComm);
		}
	}
	
	private void computeMarkupForEveryDeal(){
		for(Deal d : m_deals){
			/*
			 *  Deal.vol is from html file directly, 
			 *  slot = vol/10 for all commodity(gold, silver, oil(brent, wti))
			 */
			float slot = d.getVol() / 10;
			float quotaPerSlot = m_condition.getCommodityMarkupQuotaPerSlotBySymbol(d.getSymbol());
			float markup = slot * quotaPerSlot;
			d.setMarkup(markup);
		}
	}
	
	private void computeOutCommissionForEveryDeal(){
		for(Deal d : m_deals){
			/*
			 *  Deal.vol is from html file directly, 
			 *  slot = vol/10 for all commodity(gold, silver, oil(brent, wti))
			 */
			float slot = d.getVol() / 10;
			float quotaPerSlot = m_condition.getCommodityOutCommQuotaPerSlotBySymbol(d.getSymbol());
			float outComm = slot * quotaPerSlot;
			d.setOutComm(outComm);
		}
	}

}
