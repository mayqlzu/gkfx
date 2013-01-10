package com.gkfx;

import java.util.ArrayList;

public class Customer {
	private String			m_login;
	private String			m_ib;		// IB's id, not name
	private ArrayList<Deal>	m_deals;
	private Condition		m_condition;
	
	private FxDeals			m_fxDeals;
	private CommodityDeals	m_commodityDeals;
	private IndexDeals		m_indexDeals;
	private StockDeals		m_stockDeals;
	
	
	public Customer(String login) {
		this.m_login = login;
		this.m_deals = new ArrayList<Deal>();
	}
	
	public void addDeal(Deal d){
		this.m_deals.add(d);
	}
	
	public ArrayList<Deal> getDeals(){
		return this.m_deals;
	}
	
	public String getLogin(){
		return this.m_login;
	}
	
	public float getFxVol(){
		todo
		do not use recursion on productGroups anymore, solve it at this level directly
	}
	
	public float getCommodityVol(){
		todo
	}
	
	public void setIB(String ibCode){
		this.m_ib = ibCode;
	}
	
	public void setCondition(Condition c) {
		this.m_condition = c;
	}
	
	public void computeRelatedFee(){
		groupDealsIntoProductGroups();
		setConditionForAllProductGroups();
		letProductGroupsComputeTheirFee();
	}
	
	private void groupDealsIntoProductGroups(){
		m_fxDeals			= new FxDeals();
		m_commodityDeals	= new CommodityDeals();
		m_indexDeals		= new IndexDeals();
		m_stockDeals		= new StockDeals();
		
		for(Deal d: m_deals){
			ProductTypes type = d.getRoughProductType();
			if(ProductTypes.FX == type){
				m_fxDeals.addDeal(d);
			}else if(ProductTypes.COMMODITY == type){
				m_commodityDeals.addDeal(d);
			}else if(ProductTypes.INDEX == type){
				m_indexDeals.addDeal(d);
			}else if(ProductTypes.STOCK == type){
				m_stockDeals.addDeal(d);
			}else{
				System.out.println("Customer:groupDealsIntoProductGroups(): invalid type: " + type);
			}
		}
	}
	
	private void setConditionForAllProductGroups(){
		m_fxDeals.setCondition(m_condition);
		m_commodityDeals.setCondition(m_condition);
		m_indexDeals.setCondition(m_condition);
		m_stockDeals.setCondition(m_condition);
	}

	private void letProductGroupsComputeTheirFee(){
		m_fxDeals.computeRelatedFee();
		m_commodityDeals.computeRelatedFee();
		m_indexDeals.computeRelatedFee();
		m_stockDeals.computeRelatedFee();
	}
	
	
}
