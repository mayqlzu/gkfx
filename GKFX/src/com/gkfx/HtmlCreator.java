package com.gkfx;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


/* 
 * sqlite query result set(table) -> dom tree -> file
 */
public class HtmlCreator {
	Document doc;
	Element table;
	
	HtmlCreator(){
    		String basicHtml = ""; /* Jsoup will create closed <html> and <body> for you */
    		doc = Jsoup.parse(basicHtml); 
    		table = doc.select("body").first().appendElement("table");
	}

	/* todo: add args */
	public void addTableHeader(){
    		// add header
    		Element tr = table.appendElement("tr");
    		tr.appendElement("td").text("name");
    		tr.appendElement("td").text("age");

	}

	/* todo: add args */
	public void addOneLineOfData(){
    		// add one line
    		Element tr = table.appendElement("tr");
    		tr.appendElement("td").text("mayq");
    		tr.appendElement("td").text("29");
	}


	public void saveHtml(){
    		// get all source code of dom tree
    		String resultHtml = doc.html();
    		
    		// save string to file
        	BufferedWriter out;
			try {
				out = new BufferedWriter(new FileWriter("output.html"));
				out.write(resultHtml);
				out.close();
				System.out.println("check the output file: output.html");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       	     
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/* 
		 * notice the \\ and /, maybe i will use it later
		String dirName = "D:\\coding\\android\\workspace-java\\GKFX";
		String fileName="output.html";
		File file = new File(dirName + "/" + fileName);
		 */
		
		HtmlCreator c = new HtmlCreator();
		c.addTableHeader();
		for(int i=0; i<10; i++){
			c.addOneLineOfData();
		}

		c.saveHtml();
    }
}
	
	
