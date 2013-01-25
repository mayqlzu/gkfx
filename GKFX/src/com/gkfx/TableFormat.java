package com.gkfx;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TableFormat {
	
	public static class TheRawReport {
		
		public static String columnNames[] = {
			/* be careful, must be the exactly the same of the true file,
			 * i have some problem with blanks and /, so removed them from column names */
			"Deal",			"Login",		"OpenTime", 	"Type", 	"Symbol",
			"Volume", 		"OpenPrice",	"SL", 			"TP", 		"CloseTime",
			"ClosePrice",	"Agent", 		"Commission",	"Taxes", 	"Swap",
			"Profit", 		"Points", 		"Comment"
		};
		
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
