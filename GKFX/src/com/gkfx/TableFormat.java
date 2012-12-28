package com.gkfx;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TableFormat {
	
	public static class TheRawReport {
		
		public static String tableNameInDB = "theRawReport";
		
		public static String columnNames[] = {
			/* be careful, must be the exactly the same of the true file,
			 * i have some problem with blanks and /, so removed them from column names */
			"Deal",			"Login",		"OpenTime", 	"Type", 	"Symbol",
			"Volume", 		"OpenPrice",	"SL", 			"TP", 		"CloseTime",
			"ClosePrice",	"Agent", 		"Commission",	"Taxes", 	"Swap",
			"Profit", 		"Points", 		"Comment"
		};
		
		public static String columnTypes[] = {
			"INTEGER",	//Deal
			"INTEGER",	//Login	 	
			"TEXT", 	//Open Time	
			"TEXT", 	//Type	
			"TEXT",		//Symbol
			
			/* this comlumn is strange, may be string or real, how can i assign a SQLite datatype
			 * for this column ?
			 * solution: if can convert to int, store as int, otherwiser, store a 0 */
			"REAL", 	//Volume	
			"REAL",		//Open Price	
			"REAL",		//S/L
			"REAL",		//T/P
			"TEXT",		//Close Time
			
			"REAL",		//Close Price
			"REAL",		//Agent	
			"REAL",		//Commison	
			"REAL",		//Taxes	
			"REAL",		//Swap
			
			"REAL",		//Profit	
			"INTEGER",	//Points	
			"TEXT"		//Comment
		};
		
		/* some times, the cell value may be empty, so we will insert a default value into DB */
		public static String defaultValues[] = {
				"0",				//Deal
				"0",				//Login	 	
				"1970.01.01 00:00", //Open Time	
				"blank", 			//Type	
				"blank",			//Symbol
				
				"0.0", 				//Volume	
				"0.0",				//Open Price	
				"0.0",				//S/L
				"0.0",				//T/P
				"1970.01.01 00:00",	//Close Time
				
				"0.0",				//Close Price
				"0.0",				//Agent	
				"0.0",				//Commission
				"0.0",				//Taxes	
				"0.0",				//Swap
				
				"0.0",				//Profit	
				"0",				//Points	
				"blank"				//Comment
		};
		
		/* if the init value is empty, set it as default value */
		public static String fillBlankCellAndAddQuoteForText(String v, int columnIndex) {
			if(v.isEmpty())
				v = defaultValues[columnIndex];
			/* take any non-number value as text, including the Time 2012.11.02 13:49
			 * and SQL asks for 'text' format
			 */
			if(!Tools.isNumber(v))
				v = "'" + v + "'";
			return v;
		}
		
		public static boolean isValidRow(Element tr) {
			/* the invalid column has less <td> */
			if(tr.getElementsByTag("td").size() == columnNames.length)
				return true;
			else
				return false;
		}
	}

	public static class addMoreFilesHere {
		
	}
}
