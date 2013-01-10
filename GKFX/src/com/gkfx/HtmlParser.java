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

	private DBWriter m_dbWriter;

	public HtmlParser() {
		m_dbWriter = new DBWriter();
	}


	public void parseDemo(File file) throws IOException{
		// note: i had problem when calling Jsoup.parse("path"), so use File
		//		File file = new File(path);
		Document doc = Jsoup.parse(file, "UTF-8"); // todo: UTF-8 ok for real data?
		Element body = doc.body();
		Elements tables = body.getElementsByTag("table"); // not "<table>"
		Element table = tables.first(); // suppose the html file includes only one table

		Elements lines = table.getElementsByTag("tr");
		// handle first <tr> element, this is header
		Element head = lines.first();
		// create table scheme in database
		//handleHeader(head);
		createTable();

		// traverse other <tr> elements
		for(int i = 1; i < lines.size(); i++){ //start from the second line
			Element tr = lines.get(i);
			// read td elements and add one more line to the table in db
			System.out.println("hit a <tr> element");
			insertOneRow(tr);
		}

	}

	/* assume the scheme is fixed
	 * todo: spead up, wrap all commands into one, and call dbWriter.execCommand() once.
	 *  */
	public void parseTheRawReport(File file) throws IOException{
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
		Elements tables = body.getElementsByTag("table"); // not "<table>"
		Element table = tables.first(); // suppose the html file includes only one table

		Elements lines = table.getElementsByTag("tr");
		m_dbWriter.open(); // do not forget close
		createTable();

		// traverse other <tr> elements
		for(int i = 2; i < lines.size(); i++){ //start from the third line
			Element tr = lines.get(i);
			/* 1) some rows include empty cell, actually these <tr> has less <td>
			 * 2) the last row of the table are invalid, its' first several columns are empty
			 * 3) the several summaries at the bottom and outside the table are <tr> too ! 
			 * ignore these invalid line, they share this trait: number of <td> < number of table columns
			 */
			if(TableFormat.TheRawReport.isValidRow(tr)){
				insertOneRow(tr);
				System.out.println("counter: " + i);
			}
		}
		m_dbWriter.close();
		System.out.println("parseTheRawReport done");

	}

	private void createTable(){
		/* careful: add enough blank space */
		String columnNamesAndTypes = " "
				+ TableFormat.TheRawReport.columnNames[0]
						+ " "
						+ TableFormat.TheRawReport.columnTypes[0]
								+ " primary key, ";

		int i = 1;
		for(; i < TableFormat.TheRawReport.columnNames.length-1; i++) {
			columnNamesAndTypes += " " 
					+ TableFormat.TheRawReport.columnNames[i]
							+ " "
							+ TableFormat.TheRawReport.columnTypes[i]
									+ " , ";
		}
		// i points to the last element of array now
		columnNamesAndTypes += " " 
				+ TableFormat.TheRawReport.columnNames[i]
						+ " "
						+ TableFormat.TheRawReport.columnTypes[i]
								+ " "; // no , for the last column

		String command = "create table " + TableFormat.TheRawReport.tableNameInDB
				+ " ( " + columnNamesAndTypes + " ) ";

		/* drop the old one if exist already */
		m_dbWriter.execCommand("drop table if exists " + TableFormat.TheRawReport.tableNameInDB);
		m_dbWriter.execCommand(command);
	}

	private void insertOneRow(Element line){ // handle non-header line
		Elements columns = line.getElementsByTag("td");
		// invalid lines have been filtered out
		assert(columns.size() == TableFormat.TheRawReport.columnNames.length);

		String values = "";
		int i = 0;
		for(; i < columns.size()-1; i++){ // leave the last item
			String thisColValue = columns.get(i).html();
			thisColValue = TableFormat.TheRawReport.fillBlankCellAndAddQuoteForText(thisColValue, i);
			values += thisColValue + " , ";
		}

		String lastColValue = columns.get(i).html();
		lastColValue = TableFormat.TheRawReport.fillBlankCellAndAddQuoteForText(lastColValue, i);
		values += lastColValue; // no , for the last value
		String command = "insert into " + TableFormat.TheRawReport.tableNameInDB
				+ " values ( "
				+ values
				+ " ) ";
		m_dbWriter.execCommand(command);
	}

	
	/*
	 * save everything as String in return value;
	 */
	public ArrayList<Deal> readTheRawReport(File file) throws IOException{
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

		// traverse other <tr> elements
		int firstTableContentLine = 1;
		for(int i = firstTableContentLine; i < lines.size(); i++){ // skip the table header
			Element tr = lines.get(i);
			// skip some invalid rows, we don't worry about invalid lines anymore :)
			Elements tds = tr.getElementsByTag("td");
			if(tds.size() == TableFormat.TheRawReport.columnNames.length){
				ArrayList<String> newRow = new ArrayList<String>();
				for(int j = 0; j < tds.size(); j++){
					String htmlCellValue = tds.get(j).html();
						newRow.add(htmlCellValue);
				}
				Deal newDeal = Deal.makeADeal(newRow);
				if(!newDeal.openTimeCloseTimeTooClose(1)){ // yes, 1 means 2 minutes diff, check the callee function's note
					deals.add(newDeal);
				}
			}
		}
		
		//new ExcelWriter().write(memTable, new File("D:\\coding\\windows\\gkfx\\testReadTableToMem.xls"));
		//System.out.println("parseTheRawReport done");
		return deals;
	}

	
	
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//	new HtmlParser().parse("D:/coding/windows/gkfx/gkfx_test.html");
		//new HtmlParser().parseDemo(new File("D:/coding/windows/gkfx/gkfx_test.html"));
		//System.out.println("parse done");
		
		new HtmlParser().readTheRawReport(new File("D:/coding/windows/gkfx/Raw Report CAH.html"));

	}


	public HashMap<String, String> readCustomerIBMap() {
		// TODO Auto-generated method stub
		return null;
	}
}
