package com.gkfx;

//IB, Group, Client, ProductType, ProductSymbol, slot, rebate, markup, commission, totalFee
public class Line {
	private Object[] m_cells;
	
	public Line(Object[] cells){
		m_cells = cells;
	}
	
	public Object[] getArray(){
		return m_cells;
	}
}
