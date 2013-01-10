package com.gkfx;

import java.util.ArrayList;
import java.util.Date;


public class Deal {
	private	String 	m_id;			//	0
	private String 	m_login;			
	private String	m_openTime;		// save time as string is ok, we only care time diff, easy to resolve String
	private String 	m_actionType;		// buy, sell or else
	private String	m_symbol;			
	private String	m_productType;	// fx, stock, commodity or index
	private String	m_currency;		// usd, jpy or else

	private float	m_volume;			//	5
	private float 	m_openPrice;
	private float	m_SL;
	private float	m_TP;
	private String	m_closeTime;

	private float	m_closePrice;		// 10
	private float	m_agent;
	private float	m_commission;
	private float	m_taxes;
	private float	m_swap;

	private float	m_profit;			// 15
	private int		m_points;
	private String	m_comment;
	
	private float 	m_PIP;
	
	private float	m_inComm;
	private float	m_outComm;
	private float	m_markup;


	public Deal(String id, String login, String symbol, float volume, String openTime, String closeTime) {
		this.m_id			= id;
		this.m_login		= login;
		this.m_symbol 	= symbol;
		this.m_volume 	= volume;
		this.m_openTime	= openTime;
		this.m_closeTime 	= closeTime;
		
		this.setProductType();
		this.setCurrency();
	}

	public static Deal makeADeal(ArrayList<String> aDealRecord){
		String[] array = (String[])aDealRecord.toArray();
		return new Deal(
				array[0], 					// deal id
				array[1],					// login
				array[4],					// symbol
				Float.parseFloat(array[5]),	// volume
				array[2],					// open time
				array[9]					// close time
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

	// set productType by symbol
	private void setProductType(){
		if(	m_symbol.equals("gold") 
				||	m_symbol.equals("silver") 
				||	m_symbol.equals("oil?")) {	// todo: what is the symbol for oil? how many types?
			m_productType = "commodity";
		}	else if(m_symbol.equals("russ") || symbolEndWith2Digits()){
			m_productType = "index";
		}else{
			String lastTwoChars = lastTwoCharsOfSymbol();
			if(lastTwoChars.equals("us")
					|| lastTwoChars.equals("uk")
					|| lastTwoChars.equals("de")
					|| lastTwoChars.equals("fr")
					|| lastTwoChars.equals("nl")
					|| lastTwoChars.equals("it")
					|| lastTwoChars.equals("ch")
					){
				m_productType = "stock";
			}
		}
	}
	

	// set currency by symbol
	private void setCurrency(){
		// todo:how?
		assert(false);
	}

	public float getVol(){
		return this.m_volume;
	}

	//input: "2012.11.07 04:04"
	//index:  0123456789
	@SuppressWarnings("deprecation")
	private static Date makeADate(String str){
		Date date = new Date(
				Integer.parseInt(str.substring(0, 3)),	// year
				Integer.parseInt(str.substring(5, 6)),	// month
				Integer.parseInt(str.substring(8, 9))	// day
				);
		date.setHours(Integer.parseInt(str.substring(11, 12)));		//hour
		date.setMinutes(Integer.parseInt(str.substring(14, 15)));	//minute

		return date;
	}

	public String getLogin(){
		return m_login;
	}

	public String getSymbol() {
		return m_symbol;
	}


	public float getVolume() {
		return this.m_volume;
	}

	/* true if closeTime - openTime < xMinutes
	 * 
	 * time string format: "2012.11.07 04:04"
	 * index:  			    0123456789
	 * careful: the time accuracy is minutes, so if you want to filter any deal sooner than 2 minutes,
	 * you should pass in 1, not 2
	 */
	public boolean openTimeCloseTimeTooClose(int xMinutes){
		assert(null != this.m_openTime && null != this.m_closeTime);
		
		int yearOpen	=	Integer.parseInt(this.m_openTime.substring(0, 3));	// year
		int yearClose	=	Integer.parseInt(this.m_closeTime.substring(0, 3));	// year
	
		int monthOpen	=	Integer.parseInt(this.m_openTime.substring(5, 6));	// month
		int monthClose	=	Integer.parseInt(this.m_closeTime.substring(5, 6));	// month
		
		int dayOpen		=	Integer.parseInt(this.m_openTime.substring(8, 9));	// day
		int dayClose	=	Integer.parseInt(this.m_closeTime.substring(8, 9));	// day
		
		int hourOpen	=	Integer.parseInt(this.m_openTime.substring(11, 12));	//hour
		int hourClose	=	Integer.parseInt(this.m_closeTime.substring(11, 12));	//hour
		
		int minOpen		=	Integer.parseInt(this.m_openTime.substring(14, 15));	//minute
		int minClose	=	Integer.parseInt(this.m_closeTime.substring(14, 15));	//minute
		
		if(	yearOpen == yearClose
				&& monthOpen == monthClose
				&& dayOpen == dayClose
				&& hourOpen == hourClose
				&& minClose - minOpen < xMinutes)
			return true;
		else
			return false;
	}

	public String getProductType() {
		// TODO Auto-generated method stub
		return this.m_productType;
	}

	public int getRoughProductType() {
		todo
		i need a map: symbol --> product type
	}

	public void setInComm(float inComm) {
		m_inComm = inComm;
	}

	public void setMarkup(float markup) {
		m_markup = markup;
	}
	
	public void setOutComm(float outComm){
		m_outComm = outComm;
	}
	
}
