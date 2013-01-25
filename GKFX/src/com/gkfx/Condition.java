package com.gkfx;

// every Group has such a Condition object
public class Condition {
	public static final int USEFUL_COL_NUM = 35;
	
	private	String	groupID;		// 'IB Code' in file
	private float[] fxCommVol; 		// fx Commodity volume sum
	private WAY		fxRebateType;	// 1=pip; 2=fix
	
	private	float[] fxARebate;		// A, B as two groups when counting rebate
	private float[]	fxBRebate;
	private float[]	fxCRebate;
	private float[]	fxDRebate;
	private float[]	fxERebate;
	private float[]	fxFRebate;
	
	//private float[]	commVolMultiplier;	// 0.1 all the time, unnecessary, so deleted from file
	private	float[] commGoldRebatefixUSD;
	private	WAY		commRebateType;		// 1=fix; 2=%, do a converions when readig file, int 1/2 => enum
	private	float[]	commSilverRebateFixUSDorPercent;	// maybe USD:{6,7,8} or %:{0.0035, 0.004, 0.0045}, depends on the previous column
	private	float[]	commOilRebateFixUSDorPercent;	// maybe USD:{6,7,8} or %:{0.0035, 0.004, 0.0045}, depends on the previous previous column
	
	private	float[]	indexRebateVolMillionUSD;	// 250 in file means 250 million USD
	private	float[]	indexRebatePercent;	// 0.0035 in file means: 0.0035%
	private	float[]	stockRebatePercent;	// 50 in file means: 0.1%*50%
	
	private float[] fxABCommUSD;		// AB as same group when counting commission
	private float[] fxCCommUSD;
	private float[] fxDCommUSD;
	private float[] fxECommUSD;
	private float[] fxFCommUSD;
	
	private float[]	fxABMarkupPIP;		// AB as same group when counting markup
	private float[]	fxCMarkupPIP;
	private float[]	fxDMarkupPIP;
	private float[]	fxEMarkupPIP;
	private float[]	fxFMarkupPIP;
	
	private float[]	commGoldCommUSD;	// two 'comm': commodity Gold commission USD
	private float[] commSilverCommUSD;
	private float[] commOilCommUSD;
	private float[] indexCommUSD;
	private float[] stockCommUSD;

	private float[] commGoldMarkupPIP;
	private float[] commSilverMarkupPIP;
	private float[] commOilMarkupPIP;
	private float[] indexMarkupPIP;
	
	//private String[] note;  do NOT read this column for now;
	
	public Condition(Object[] arr){ // two many params, pack as an Array
		assert(USEFUL_COL_NUM == arr.length); 	// check column numbers
		
		groupID							= (String)	arr[0];
		fxCommVol						= (float[]) arr[1];
		fxRebateType					= (1 == ((float[])arr[2])[0]) ? WAY.POINT : WAY.QUOTA; // 1=pip; 2=fix
		
		fxARebate						= (float[]) arr[3]; 	// A, B as two groups when counting rebate
		fxBRebate						= (float[]) arr[4];
		fxCRebate						= (float[]) arr[5];
		fxDRebate						= (float[]) arr[6];
		fxERebate						= (float[]) arr[7];
		fxFRebate						= (float[]) arr[8];
		
		//commVolMultiplier deleted from file
		commGoldRebatefixUSD			= (float[]) arr[9];
		commRebateType					= (1 == ((float[])arr[10])[0]) ? WAY.QUOTA : WAY.PERCENT;		// 1=fix; 2=%
		commSilverRebateFixUSDorPercent = (float[]) arr[11];	// maybe USD:{6,7,8} or %:{0.0035, 0.004, 0.0045}, depends on the previous column
		commOilRebateFixUSDorPercent	= (float[]) arr[12];	 // maybe USD:{6,7,8} or %:{0.0035, 0.004, 0.0045}, depends on the previous previous column
		
		indexRebateVolMillionUSD		= (float[]) arr[13];	// 250 in file means 250 million USD
		indexRebatePercent				= (float[]) arr[14];	// 0.0035 in file means: 0.0035%
		stockRebatePercent				= (float[]) arr[15];	// 50 in file means: 0.1%*50%
		
		fxABCommUSD						= (float[]) arr[16];	// AB as same group when counting commission
		fxCCommUSD						= (float[]) arr[17];
		fxDCommUSD						= (float[]) arr[18];
		fxECommUSD						= (float[]) arr[19];
		fxFCommUSD						= (float[]) arr[20];
		
		fxABMarkupPIP					= (float[]) arr[21];	// AB as same group when counting markup
		fxCMarkupPIP					= (float[]) arr[22];
		fxDMarkupPIP					= (float[]) arr[23];
		fxEMarkupPIP					= (float[]) arr[24];
		fxFMarkupPIP					= (float[]) arr[25];
		
		commGoldCommUSD					= (float[]) arr[26];	// two 'comm': commodity Gold commission USD
		commSilverCommUSD				= (float[]) arr[27];
		commOilCommUSD					= (float[]) arr[28];
		indexCommUSD					= (float[]) arr[29];
		stockCommUSD					= (float[]) arr[30];
	
		commGoldMarkupPIP				= (float[]) arr[31];
		commSilverMarkupPIP				= (float[]) arr[32];
		commOilMarkupPIP				= (float[]) arr[33];
		indexMarkupPIP					= (float[]) arr[34];	// num = 35, right
	}

	public String getGroupID() {
		return groupID;
	}

	public float[] getFxCommVol() {
		return fxCommVol;
	}

	public WAY getFxRebateType() {
		return fxRebateType;
	}

	public float[] getFxARebate() {
		return fxARebate;
	}

	public float[] getFxBRebate() {
		return fxBRebate;
	}

	public float[] getFxCRebate() {
		return fxCRebate;
	}

	public float[] getFxDRebate() {
		return fxDRebate;
	}

	public float[] getFxERebate() {
		return fxERebate;
	}

	public float[] getFxFRebate() {
		return fxFRebate;
	}

	public float[] getCommGoldRebatefixUSD() {
		return commGoldRebatefixUSD;
	}

	public WAY getCommRebateType() {
		return commRebateType;
	}

	public float[] getCommSilverRebateFixUSDorPercent() {
		return commSilverRebateFixUSDorPercent;
	}

	public float[] getCommOilRebateFixUSDorPercent() {
		return commOilRebateFixUSDorPercent;
	}

	public float[] getIndexRebateVolMillionUSD() {
		return indexRebateVolMillionUSD;
	}

	public float[] getIndexRebatePercent() {
		return indexRebatePercent;
	}

	public float[] getStockRebatePercent() {
		return stockRebatePercent;
	}

	public float[] getFxABCommUSD() {
		return fxABCommUSD;
	}

	public float[] getFxCCommUSD() {
		return fxCCommUSD;
	}

	public float[] getFxDCommUSD() {
		return fxDCommUSD;
	}

	public float[] getFxECommUSD() {
		return fxECommUSD;
	}

	public float[] getFxFCommUSD() {
		return fxFCommUSD;
	}

	public float[] getFxABMarkupPIP() {
		return fxABMarkupPIP;
	}

	public float[] getFxCMarkupPIP() {
		return fxCMarkupPIP;
	}

	public float[] getFxDMarkupPIP() {
		return fxDMarkupPIP;
	}

	public float[] getFxEMarkupPIP() {
		return fxEMarkupPIP;
	}

	public float[] getFxFMarkupPIP() {
		return fxFMarkupPIP;
	}

	public float[] getCommGoldCommUSD() {
		return commGoldCommUSD;
	}

	public float[] getCommSilverCommUSD() {
		return commSilverCommUSD;
	}

	public float[] getCommOilCommUSD() {
		return commOilCommUSD;
	}

	public float[] getIndexCommUSD() {
		return indexCommUSD;
	}

	public float[] getStockCommUSD() {
		return stockCommUSD;
	}

	public float[] getCommGoldMarkupPIP() {
		return commGoldMarkupPIP;
	}

	public float[] getCommSilverMarkupPIP() {
		return commSilverMarkupPIP;
	}

	public float[] getCommOilMarkupPIP() {
		return commOilMarkupPIP;
	}

	public float[] getIndexMarkupPIP() {
		return indexMarkupPIP;
	}
	
		
}
