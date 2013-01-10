package com.gkfx;

// every IB has such a Condtion object
public class Condition {
	private InCommCondition		m_in;
	private OutCommCondition	m_out;
	private MarkupCondition		m_markup;
	
	private float				m_fxCommodityVolSum;	// maybe uncomfortable to place these 2 here :)
	private	float				m_indexNotionalAmountTraded;
	
	public Condition(InCommCondition m_in, OutCommCondition m_out,
			MarkupCondition m_markup) {
		super();
		this.m_in = m_in;
		this.m_out = m_out;
		this.m_markup = m_markup;
	}

	public InCommCondition getM_in() {
		return m_in;
	}

	public void setM_in(InCommCondition m_in) {
		this.m_in = m_in;
	}

	public OutCommCondition getM_out() {
		return m_out;
	}

	public void setM_out(OutCommCondition m_out) {
		this.m_out = m_out;
	}

	public MarkupCondition getM_markup() {
		return m_markup;
	}

	public void setM_markup(MarkupCondition m_markup) {
		this.m_markup = m_markup;
	}

	public void setFxCommodityVolSum(float sum) {
		this.m_fxCommodityVolSum = sum;
	}
	
	public float getFxCommodityVolSum() {
		return this.m_fxCommodityVolSum;
	}
	
	public void setIndexNotionalAmountTraded(float a){
		this.m_indexNotionalAmountTraded = a;
	}

	public float getIndexNotionalAmountTraded(){
		return this.m_indexNotionalAmountTraded;
	}
	

	public float getStockInCommPercentage() {
		todo
	}

	public float getStockOutCommQuotaPerSlotBySymbol() {
		todo
	}

	public float getIndexOutCommQuotaPerSlot() {
		todo
	}

	public float getFxInCommQuotaPerSlotBySymbol(float fxCommodityVolSum, String symbol) {
		todo
	}

	public float getFxInCommPointPerSlotBySymbol(float fxCommodityVolSum, String symbol) {
		todo
	}

	public float getFxMarkupPointPerSlotBySymol(String symbol) {
		todo
	}

	public float getFxOutCommQuotaPerSlotBySymbol(String symbol) {
		todo
	}

	public float getIndexInCommPercentage(float notionalAmountTraded) {
		todo
	}

	public float getCommodityInCommQuotaPerSlotBySymbol(float fxCommodityVolSum, String symbol) {
		todo
	}

	public float getCommodityMarkupQuotaPerSlotBySymbol(String symbol) {
		todo
	}

	public float getCommodityOutCommQuotaPerSlotBySymbol(String symbol) {
		todo
	}
	
}
