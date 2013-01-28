package com.gkfx;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {

	public ArrayList<Deal> readTheDealRecordFile(File file) throws IOException{
		ArrayList<Deal> deals = new ArrayList<Deal>();

		// note: i had problem when calling Jsoup.parse("path"), so use File
		Document doc = Jsoup.parse(file, "UTF-8"); // todo: UTF-8 ok for real data?
		Element body = doc.body();

		/* the html source is like:
		 * <table>
		 * 		<tr> 	title 				</tr>
		 * 		<tr> 	table header		</tr>
		 * 		<tr> 	first row of data 	</tr>
		 * 		... 	data rows
		 * 		<tr>	a dummy line		</tr>
		 * 		<tr> 	Profit sum			</tr>
		 * 		<tr> 	commission sum		</tr>
		 * 		...
		 * 		<tr>	Credit sum			</tr>
		 * </table> 
		 */
		Elements htmlTables = body.getElementsByTag("table"); // not "<table>"
		Element htmlTable = htmlTables.first(); // suppose the html file includes only one table

		Elements lines = htmlTable.getElementsByTag("tr");

		// the first 2 lines are not the data we want
		int firstTableContentLine = 2;
		// traverse other <tr> elements
		for(int i = firstTableContentLine; i < lines.size(); i++){
			Element tr = lines.get(i);
			// skip some invalid rows, we don't worry about invalid lines anymore :)
			Elements tds = tr.getElementsByTag("td");
			if(tds.size() == TableFormat.TheRawReport.columnNames.length){
				// todo: if filtered all invalid rows? ex: buy stop, sell stop, credit, balance ?
				ArrayList<String> newRow = new ArrayList<String>();
				for(int j = 0; j < tds.size(); j++){
					String htmlCellValue = tds.get(j).html();
						newRow.add(htmlCellValue);
				}
				Deal newDeal = Deal.makeADeal(newRow);
				if(!newDeal.openTimeCloseTimeTooClose(2)){
					deals.add(newDeal);
				}
			}
		}
		
		return deals;
	}

	
	
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		

	}

}
