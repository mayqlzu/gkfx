package com.gkfx;

import java.util.ArrayList;
import java.util.HashMap;

public class IB {
	private String 				m_id;
	private String				m_name;
	private ArrayList<Customer>	m_customers;
	private Condition			m_condition;

	
	public IB(String id){
		this.m_id	= id;
		this.m_customers = new ArrayList<Customer>();
	}
	
	public void addCustomer(Customer c) {
		this.m_customers.add(c);
	}
	
	private float countFxCommodityVolSum(){
		int sum = 0;
		for(Customer c: m_customers){
			sum += c.getFxVol() + c.getCommodityVol();	// if not ready, Customer will count
		}
		return sum;
	}

	private float countIndexNotionalAmountTraded() {
		todo
		sum = 0;
		for every index product {
			val = slot * valPerSlot;
			sum += val;
		}
	}
	
	public void computeIncome(){
		// these 2 param must be evaluated at this level, and used in deeper level
		float sum = countFxCommodityVolSum(); //fxVol + (goldVol + silverVol + oilVol)/10
		float amount = countIndexNotionalAmountTraded();// 股指本金交易量
		m_condition.setFxCommodityVolSum(sum);
		m_condition.setIndexNotionalAmountTraded(amount);
		
		setConditionForMyCustomers();
		letMyCustomersComputeTheirFee(); // their related fee
	}

	private void setConditionForMyCustomers(){
		for(Customer c: m_customers){
			c.setCondition(m_condition);
		}
	}
	
	private void letMyCustomersComputeTheirFee(){
		for(Customer c: m_customers){
			c.computeRelatedFee();
		}
	}

	public String getID() {
		return this.m_id;
	}

	public void setCondition(Condition c) {
		this.m_condition = c;
		
	}
	
}
