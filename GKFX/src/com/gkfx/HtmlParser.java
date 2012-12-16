package com.gkfx;
import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {

	public void parse(String path) throws IOException{
		// note: i had problem when calling Jsoup.parse("path"), so use File
		File file = new File(path);
		Document doc = Jsoup.parse(file, "UTF-8"); // todo: UTF-8 ok for real data?
		Element body = doc.body();
		Elements tables = body.getElementsByTag("table"); // not "<table>"
		Element table = tables.first(); // suppose the html file includes only one table
		
		Elements lines = table.getElementsByTag("tr");
		// handle first <tr> element, this is header
		Element head = lines.first();
		// create table scheme in database
		handleHeader(head);
		
		// traverse other <tr> elements
		for(int i = 1; i < lines.size(); i++){ //start from the second line
			Element tr = lines.get(i);
			// read td elements and add one more line to the table in db
			System.out.println("hit a <tr> element");
			handleOneLine(tr);
		}
		
	}
	
	private void handleHeader(Element head){
		Elements columns = head.getElementsByTag("th"); // i had problems when call getAllElements
		for(Element column: columns){
			String data = column.html(); //do not call .data()
			System.out.println(data);
		}
	}
	
	private void handleOneLine(Element line){ // handle non-header line
		Elements columns = line.getElementsByTag("td");
		for(Element column: columns){
			String data = column.html();
			int i = Integer.parseInt(data);
			System.out.println(i);
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		new HtmlParser().parse("D:/coding/gkfx/gkfx_test.html");
		System.out.println("parse done");

	}

}
