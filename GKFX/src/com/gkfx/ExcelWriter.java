package com.gkfx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelWriter {

	public void write(ArrayList<Object[]> table, File file) {
		System.out.println("start to write excel file");
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("sheet1");

		int rownum = 0;
		for (Object[] fromRow : table) {
			Row toRow = sheet.createRow(rownum++);
			int cellnum = 0;
			for (Object obj : fromRow) {
				Cell cell = toRow.createCell(cellnum++);
				if(obj instanceof Integer)
					cell.setCellValue((Integer)obj);
				else if(obj instanceof Float)
					cell.setCellValue((Float)obj);
				else if(obj instanceof String)
					cell.setCellValue((String)obj);
				else if(obj instanceof Double)
					cell.setCellValue((Double)obj);
				else if(obj instanceof Date) 
					cell.setCellValue((Date)obj);
			}
		}

		try {
			FileOutputStream out = 
					new FileOutputStream(file);
			workbook.write(out);
			out.close();
			System.out.println("write excel file done");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ExcelWriter writer = new ExcelWriter();
		
		ArrayList<Object[]> table = new ArrayList<Object[]>();
		table.add( new Object[] {"id", "name", "volume"} );
		table.add( new Object[] {1, "mike", 2.3} );
		table.add( new Object[] {2, "jeff", 10.0} );
		table.add( new Object[] {3, "tom", 0.0} );
		
		File file = new File("D:\\coding\\windows\\gkfx\\excel_writer_output.xls");
		writer.write(table, file);
		
	}

	/*
	 * output table format:
	 * 
	 * 	IBCode	IBName	product	volume	commission	plus 	income
	 * 	123333	xiaojie	gold	102.3	50.2		20.3
	 * 					silver	30.3	10.0		32.2
	 * 					ex		2.3		49.0		12.9
	 * 														123.0
	 * 
	 * 	124444	mike	silver	30.3	33.0		23.0
	 * 					ex		29.0	34.4		12.3
	 * 														87.4
	 * 	...
	 * 
	 */
	public void printFinalReport(HashMap<String, IB> ibs) {
		// TODO Auto-generated method stub
		ArrayList<IB> ibList = new ArrayList<IB>(ibs.values());
		ArrayList<Object[]> printCache = new ArrayList<Object[]>();
		
		Object[] tableHeader = new Object[] {
			"IBCode", "Name", "product", "volume", "commission", "plus", "income"
		};
		
		printCache.add(tableHeader);
		
		for(IB ib: ibList){
			/*
			Object[] newRow1 = new Object[]{
				ib.getID(), ib.getName(), ib.getGoldVol(),
				ib.getGoldCommssion(), ib.getGoldPlus(), ""};
				
			Object[] newRow2 = new Object[]{
				"", "", ib.getSilverVol(),						// use "" as place holder
				ib.getSilverCommssion(), ib.getSilverPlus(), ""};
				
			Object[] newRow3 = new Object[]{
				"", "", ib.getSilverVol(),
				ib.getSilverCommssion(), ib.getSilverPlus(), ""};
				
			Object[] newRow4 = new Object[]{
				"", "", ib.getSilverVol(),
				ib.getSilverCommssion(), ib.getSilverPlus(), ""};
			
			Object[] newRow5 = new Object[]{
				"", "", "", "", "", ib.getIncome()};
			
			printCache.add(newRow1);
			printCache.add(newRow2);
			printCache.add(newRow3);
			printCache.add(newRow4);
			*/
		}
		
		this.write(printCache, new File("D:/IBIncome.xsl"));
	}

}
