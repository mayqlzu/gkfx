package com.gkfx;

import java.util.ArrayList;
import java.util.Date;


public class Deal {
	private	String 	m_id;			//	0
	private String 	m_login;			
	private String	m_openTime;		// save time as string is ok, we only care time diff, easy to resolve String
	private String 	m_actionType;	// buy, sell or else
	private String	m_symbol;			

	private float	m_volume;		//	5
	private float 	m_openPrice;
	private float	m_SL;
	private float	m_TP;
	private String	m_closeTime;

	private float	m_closePrice;	// 10
	private float	m_agent;
	private float	m_commission;
	private float	m_taxes;
	private float	m_swap;

	private float	m_profit;		// 15
	private int		m_points;
	private String	m_comment;
	
	private String	m_ib;
	private String 	m_group;
	private PRODUCT_TYPE	m_roughType; // fx, comm, index or stock


	public Deal(String id, String login, String symbol, 
			float volume, String openTime, String closeTime,
			float closePrice) {
		this.m_id			= id;
		this.m_login		= login;
		this.m_symbol 		= symbol;
		this.m_volume 		= volume;
		this.m_openTime		= openTime;
		this.m_closeTime 	= closeTime;
		this.m_closePrice 	= closePrice;
	}

	public static Deal makeADeal(ArrayList<String> aDealRecord){
		String[] array = (String[])aDealRecord.toArray(new String[0]);
		String vol = array[5];
		//System.out.println("debug, check vol = "+ vol);
		return new Deal(
				array[0], 					// deal id
				array[1],					// login
				array[4],					// symbol
				Float.parseFloat(array[5]),	// volume
				array[2],					// open time
				array[9],					// close time
				Float.parseFloat(array[10])	// close price
				);
	}
	

	private String lastTwoCharsOfSymbol(){
		assert(m_symbol.length() >= 2);
		return  m_symbol.substring(m_symbol.length() - 2);
	}

	private boolean symbolEndWith2Digits() {
		String last2Chars = lastTwoCharsOfSymbol();
		char[] chars = last2Chars.toCharArray();
		if(Character.isDigit(chars[0]) && Character.isDigit(chars[1])){
			return true;
		}else{
			return false;
		}
	}

	private PRODUCT_TYPE parseRoughType(){
		if(	m_symbol.equals("gold")
			|| m_symbol.equals("silver")
			|| m_symbol.equals("brent")
			|| m_symbol.equals("wti")
			){
			return PRODUCT_TYPE.COMMODITY;
		}
		else if(m_symbol.equals("russ") || symbolEndWith2Digits()){
			return PRODUCT_TYPE.INDEX;
		}else{
			String lastTwoChars = lastTwoCharsOfSymbol();
			if(		   lastTwoChars.equals("us")
					|| lastTwoChars.equals("uk")
					|| lastTwoChars.equals("de")
					|| lastTwoChars.equals("fr")
					|| lastTwoChars.equals("nl")
					|| lastTwoChars.equals("it")
					|| lastTwoChars.equals("ch")
					){
				 return PRODUCT_TYPE.STOCK;
			}else{
				// STOCK
				 return PRODUCT_TYPE.FX;
			}
		}
	}

	// private, call getSlot() all the time
	private float getVol(){
		return this.m_volume;
	}
	
	public float getSlot(){
		if(isCommodity())
			return m_volume / 10;
		else
			return m_volume;
	}
	
	public void setVol(float v){
		this.m_volume = v;
	}
	
	public String getIB(){
		return m_ib;
	}
	
	public void setIB(String ib){
		m_ib = ib;
	}
	
	public String getLogin(){
		return m_login;
	}
	
	public void setLogin(String login){
		this.m_login = login;
	}

	public String getSymbol() {
		return m_symbol;
	}

	public void setSymbol(String sym){
		this.m_symbol = sym;
	}

	/* true if closeTime - openTime < xMinutes
	 * 
	 * time string format: "2012.11.07 04:04"
	 * index:  			    0123456789
	 */
	public boolean openTimeCloseTimeTooClose(int xMinutes){
		assert(null != this.m_openTime && null != this.m_closeTime);
		
		int yearOpen	=	Integer.parseInt(this.m_openTime.substring(0, 4));	// year
		int yearClose	=	Integer.parseInt(this.m_closeTime.substring(0, 4));	// year
	
		int monthOpen	=	Integer.parseInt(this.m_openTime.substring(5, 7));	// month
		int monthClose	=	Integer.parseInt(this.m_closeTime.substring(5, 7));	// month
		
		int dayOpen		=	Integer.parseInt(this.m_openTime.substring(8, 10));	// day
		int dayClose	=	Integer.parseInt(this.m_closeTime.substring(8, 10));	// day
		
		int hourOpen	=	Integer.parseInt(this.m_openTime.substring(11, 13));	//hour
		int hourClose	=	Integer.parseInt(this.m_closeTime.substring(11, 13));	//hour
		
		int minOpen		=	Integer.parseInt(this.m_openTime.substring(14, 16));	//minute
		int minClose	=	Integer.parseInt(this.m_closeTime.substring(14, 16));	//minute
		
		if(	yearOpen == yearClose
				&& monthOpen == monthClose
				&& dayOpen == dayClose
				&& hourOpen == hourClose
				&& minClose - minOpen <= xMinutes)
			return true;
		else
			return false;
	}

	public PRODUCT_TYPE getRoughType() {
		if(null == m_roughType){
			m_roughType = parseRoughType(); // save for the next query;
		}
		return this.m_roughType;
	}
	
	public boolean isFx(){
		return PRODUCT_TYPE.FX == getRoughType();
	}
	
	public boolean isCommodity(){
		return PRODUCT_TYPE.COMMODITY == getRoughType();
	}

	public boolean isIndex(){
		return PRODUCT_TYPE.INDEX == getRoughType();
	}
	
	public boolean isStock(){
		return PRODUCT_TYPE.STOCK == getRoughType();
	}
	
	public boolean isGold(){
		return m_symbol.equals(SYMBOLS.GOLD);
	}
	public boolean isSilver(){
		return m_symbol.equals(SYMBOLS.SILVER);
	}
	
	public boolean isOil(){
		return m_symbol.equals(SYMBOLS.BRENT) || m_symbol.equals(SYMBOLS.WTI);
	}
	
	public float getClosePrice() {
		return this.m_closePrice;
	}

	public void setGroup(String group) {
		this.m_group = group;
	}
	
	public String getGroup() {
		return this.m_group;
	}
	
}
