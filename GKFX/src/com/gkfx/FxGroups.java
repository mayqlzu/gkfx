package com.gkfx;

import java.util.HashMap;

public class FxGroups {
	HashMap<String, PRODUCT_TYPE> m_map;

	public FxGroups(){
		m_map = new HashMap<String, PRODUCT_TYPE>();
		
		String[] A = new String[]{"EURUSD"};
		String[] B = new String[]{"GBPUSD"};
		
		String[] C = new String[]{"AUDUSD", "EURGBP", "USDCAD", "USDCHF", "USDJPY", "EURCAD", "EURCHF",
				"EURJPY", "NZDUSD"};
		
		String[] D = new String[]{"AUDCAD", "AUDCHF", "AUDJPY", "CADJPY", "CHFJPY", "EURTRY", "GBPCAD", "GBPCHF", "GBPJPY",
				"NOKSEK", "NZDCAD", "NZDJPY", "USDDKK", "USDTRY", "CHFCAD", "EURDKK", "GBPEUR", "HKDSGD", "SGDJPY", "USDHKD",
				"USDSGD", "AUDNZD", "AUDSGD", "AUDTHB", "CADSGD", "CHFNZD", "CHFSGD", "CHFTRY", "EURAUD", "EURNZD", "GBPAUD",
				"NOKDKK", "NZDSGD", "SEKDKK", "TRYJPY"};
		
		String[] E = new String[]{"USDPLN", "AUDHKD", "AUDTRY", "CADDKK", "CADTRY", "EURHDK", "EURHUF", "EURNOK", "EURSAR", "EURSGD",
				"EURTHB", "EURZAR", "GBPHKG", "GBPNZD", "GBPSGD", "HKDMXN", "NZDHKD", "SGDTRY", "USDHUF", "USDNOK", "USDSAR",
				"USDDKK", "AUDMXN", "AUDNOK", "AUDSEK", "AUDZAR", "CADHKD", "CADMXN", "CADNOK", "CADSEK", "CADZAR", "CHFNOK",
				"CHFSEK", "CHFZAR", "EURCZK", "EURMXN", "EURPLN", "EURSEK", "GBPCZK", "GBPDKK", "GBPHUF", "GBPMXN", "GBPNOK",
				"GBPPLN", "GBPSAR", "BGPTRY", "GBPZAR", "HKDJPY", "MXNJPY", "SARJPY", "SGDTHB", "SGDZAR", "THBJPY", "USDCZK", "USDMXN",
				"USDSEK", "USDZAR", "ZARJPY", "GBPTRY", "GBPZAR"};
		
		String[] F = new String[]{}; // todo: what are in group F ?
		
		
		for(String s: A){
			m_map.put(s, PRODUCT_TYPE.A);
		}
		
		for(String s: B){
			m_map.put(s, PRODUCT_TYPE.B);
		}
		
		for(String s: C){
			m_map.put(s, PRODUCT_TYPE.C);
		}
		
		for(String s: D){
			m_map.put(s, PRODUCT_TYPE.D);
		}
		
		for(String s: E){
			m_map.put(s, PRODUCT_TYPE.E);
		}
		
		for(String s: F){
			m_map.put(s, PRODUCT_TYPE.F);
		}
	}
	
	public PRODUCT_TYPE getABCDEF(String symbolInLowerCase){
		String symbolInUpperCase = symbolInLowerCase.toUpperCase();
		PRODUCT_TYPE e = m_map.get(symbolInUpperCase);
		if(null == e){
			System.out.println(
					"FxGroups:getFxGroupEnumBySymbol():invalid symbol:"	+ symbolInUpperCase);
		}
		
		return e;
	}
	
}
