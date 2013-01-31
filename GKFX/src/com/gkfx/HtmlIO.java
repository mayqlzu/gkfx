package com.gkfx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlIO {
	private static String TITLE_PLACEHOLDER = "TITLE_PLACE_HODER";
	private static String ROW_PLACEHOLDER = "<tr><td>ROW_PLACEHOLDER</td></tr>"; // without tag, it will show in wrong place

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

	

	private void writeTableToHtml(ArrayList<Object> table, File file){
		// this template is copied from "Raw Report CAH.htm", it makes life easy:)
		String template = "" 
			+ "<html><head><title>Raw Report</title>"
			+ "<style type=\"text/css\" media=\"screen\">"
			+ "td     { font: 8pt Tahoma,Arial; }"
			+ "</style>"
			+ "<style type=\"text/css\" media=\"print\">"
			+ "td     { font: 6pt Tahoma,Arial; }"
			+ "</style>"
			+ "</head>"
			+ "<body topmargin=1 marginheight=1>"
			+ "<style>"
			+ ".money { mso-number-format:\\#\\,\\#\\#0\\.00; }"
			+ ".lots  { mso-number-format:0\\.00; }"
			+ ".dt    { mso-number-format:\"yyyy\\.mm\\.dd hh\\:mm\"; }"
			+ ".pt0   {mso-number-format:0;}"
			+ ".pt1   {mso-number-format:0\\.0;}"
			+ ".pt2   {mso-number-format:0\\.00;}"
			+ "</style>"
			+ "<div align=center>"
			+ "<table cellspacing=1 cellpadding=2 border=0 width=99%>"
			+ "<tr><td colspan=17><font size=2>" + TITLE_PLACEHOLDER + "</font></td></tr>"
			+ "<tr bgcolor=#c0c0c0 align=right><td>Deal</td><td>Login</td><td>Open Time</td><td>Type</td></tr>"
			+ ROW_PLACEHOLDER
			+ "</table>"
			+ "</div>"
			+ "</body></html>";
		
		String rows = "";
		int count = 0;
		for(Object rowObj: table){
			count++;
			if(count%2 == 0)
				rows += "<tr bgcolor=#e0e0e0 align=right>"; //with bgcolor
			else
				rows += "<tr  align=right>";			// no bgcolor
			Object[] objArr = (Object[])rowObj;
			for(Object cellObj: objArr){
				rows += "<td>";
				if(cellObj instanceof String) 
					rows += (String)cellObj;
				else if(cellObj instanceof Float)
					rows += ((Float)cellObj).toString();
				rows += "</td>";
			}
			rows += "</tr>";
		}
		
		String htmlStr = template.replaceAll(ROW_PLACEHOLDER, rows);
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(htmlStr);
			out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
    	ArrayList<Object> table = new ArrayList<Object>();
    	table.add(new Object[] {"IB", "rebate", "markup", "commission"});
    	table.add(new Object[] {"IBA", 1.0f, 2.0f, 3.0f});
    	table.add(new Object[] {"IBA", 1.0f, 2.0f, 3.0f});
    	table.add(new Object[] {"IBA", 1.0f, 2.0f, 3.0f});
    	table.add(new Object[] {"IBA", 1.0f, 2.0f, 3.0f});
    	
    	new HtmlIO().writeTableToHtml(table, new File("test.htm"));
    	System.out.println("done");

	}

}
