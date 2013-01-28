package com.gkfx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelIO {

	private void readSample() {
		try {

			System.out.println("start to read excel file");
			FileInputStream file = new FileInputStream(new File("D:/coding/windows/gkfx/tables-from-jeffery/Input_New/Group-Client.xlsx"));

			//Get the workbook instance for XLSX file 
			Workbook workbook = WorkbookFactory.create(file);

			//Get first sheet from the workbook
			Sheet sheet = workbook.getSheetAt(0);

			//Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();

				//For each row, iterate through each columns
				Iterator<Cell> cellIterator = row.cellIterator();
				while(cellIterator.hasNext()) {

					Cell cell = cellIterator.next();

					switch(cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						System.out.print(cell.getBooleanCellValue() + "\t\t");
						break;
					case Cell.CELL_TYPE_NUMERIC:
						System.out.print(cell.getNumericCellValue() + "\t\t");
						break;
					case Cell.CELL_TYPE_STRING:
						System.out.print(cell.getStringCellValue() + "\t\t");
						break;
					}
				}
				System.out.println("");
			}
			file.close();
			System.out.println("read excel file done");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// discard this one, read auto-generated file Accounts-Group.xlsx
	public HashMap<String, String> readGroupClientFileHandMade(File f){
		HashMap<String, String> map = new HashMap<String, String>();
		ArrayList<String>	ibCodes = new ArrayList<String>();
		ArrayList<String>  clientIDs = new ArrayList<String>();
		
		try {

			FileInputStream file = new FileInputStream(f);

			//Get the workbook instance for XLSX file 
			Workbook workbook = WorkbookFactory.create(file);

			//Get first sheet from the workbook
			Sheet sheet = workbook.getSheetAt(0);

			//Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next(); // skip first line, it's table header
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();

				Iterator<Cell> cellIterator = row.cellIterator();
				// get first 2 column for every row
				Cell cellA = cellIterator.next();
				Cell cellB = cellIterator.next();
				// columen B may include blank cells, skip these rows
				if(Cell.CELL_TYPE_BLANK == cellB.getCellType()){
					continue;
				}
				
				String cellBdata = null;
				// user may write double or string as client id, just in case
				switch(cellB.getCellType()) {
				case Cell.CELL_TYPE_NUMERIC:
					cellBdata = new Double(cellB.getNumericCellValue()).toString();
					break;
				case Cell.CELL_TYPE_STRING:
					cellBdata = cellB.getStringCellValue();
					break;
				}
				
				ibCodes.add(cellA.getStringCellValue());
				clientIDs.add(cellBdata);
			}
			file.close();
			System.out.println("read excel file done");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// create map: clientID -> IB Code
		// you will get exception if you write: toArray();
		String[] c = (String[]) clientIDs.toArray(new String[0]);
		String[] ibs = (String[]) ibCodes.toArray(new String[0]);
		for(int i = 0; i<c.length; i++){
			/* find the corresponding IB code, 
			 * assume excel format:
			 * IBCodeXXXX  clientIDXXX
			 * 			   clientIDXXX
			 * 			   clientIDXXX
			 * 			   you can add blank cell here
			 * IBCodeXXX must on top-left and align with his' first client
			 */
			
			int j = i;
			for(; j > 0; j--){ // from current row to upper
				if(!ibs[j].isEmpty()) // yes, we can judge by isEmpty()
					break;
			}
			if(map.containsKey(c[i]))
				System.out.println("readGroupClientFile(): client id duplicated !" + c[i]);
			map.put(c[i], ibs[j]);
		}
		
		// output to check the result
		Set<String> keys = map.keySet();
		Iterator<String> keyItor = keys.iterator();
		while(keyItor.hasNext()){
			String key = keyItor.next();
			System.out.println(map.get(key) + " " + key);
		}
		
		return map;
		
	}

	private void writeSample() {
		System.out.println("start to write excel file");
		/*
		 * Workbook workbook = new Workbook();  for office2003
		 * witch from office2003 to 2007
		 * 		READING:
		 * 			hssf.* => ss.*
		 * 			new Workbook() => WorkbookFactory.create()
		 * 			import more jars
		 * 		WRITING:
		 * 			hssf.* => ss.*
		 * 			new HSSFWorkbook() => new XSSFWorkbook()
		 */
		XSSFWorkbook workbook = new XSSFWorkbook(); // for office2007
		Sheet sheet = workbook.createSheet("Sample sheet");

		Map<String, Object[]> data = new HashMap<String, Object[]>();
		data.put("1", new Object[] {"Emp No.", "Name", "Salary"});
		data.put("2", new Object[] {1d, "John", 1500000d});
		data.put("3", new Object[] {2d, "Sam", 800000d});
		data.put("4", new Object[] {3d, "Dean", 700000d});

		Set<String> keyset = data.keySet();
		int rownum = 0;
		for (String key : keyset) {
			Row row = sheet.createRow(rownum++);
			Object [] objArr = data.get(key);
			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
				if(obj instanceof Date) 
					cell.setCellValue((Date)obj);
				else if(obj instanceof Boolean)
					cell.setCellValue((Boolean)obj);
				else if(obj instanceof String)
					cell.setCellValue((String)obj);
				else if(obj instanceof Double)
					cell.setCellValue((Double)obj);
			}
		}

		try {
			FileOutputStream out = 
					new FileOutputStream(new File("D:\\coding\\windows\\gkfx\\output.xlsx"));
			workbook.write(out);
			out.close();
			System.out.println("write excel file done");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void writeTable(ArrayList<Line> table, File file) {
		System.out.println("start to write excel file");
		
		// Workbook workbook = new Workbook();  for office2003
		XSSFWorkbook workbook = new XSSFWorkbook(); // for office2007
		Sheet sheet = workbook.createSheet("Sheet1");

		int rownum = 0;
		for (Line line : table) {
			Row row = sheet.createRow(rownum++);
			Object [] objArr = line.getArray();
			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
				/* attention: add enough case to output all types of Object
				 * or some cell will be blank
				 */
				if(obj instanceof Date) 
					cell.setCellValue((Date)obj);
				else if(obj instanceof Boolean)
					cell.setCellValue((Boolean)obj);
				else if(obj instanceof String)
					cell.setCellValue((String)obj);
				else if(obj instanceof Double)
					cell.setCellValue((Double)obj);
				else if(obj instanceof Float)
					cell.setCellValue((Float)obj); // setCellValue CAN handle Float
				else if(obj instanceof Integer)
					cell.setCellValue((Integer)obj); // setCellValue CAN handle Integer
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
	
	
	public HashMap<String, String> readIBGroupFile(File f) {
		//todo
		return new HashMap<String, String>();
		
	}

	/*
	 * excel format contract: 
	 * 1) first row is header
	 * 2) data start from second row
	 * 3) every group occupy 3 rows
	 * 4) no 'gap' between groups
	 * 5) when program found cell[firstRowInCurrentGroup, col-B] is blank, ignore current group
	 * 6) no 'gap' between columns
	 * 
	 * algorithm:
	 * for(row+3)
	 * 		get group id
	 * 		for(col++)
	 * 			get 3 val in current col (span 3 rows)
	 */
	public HashMap<String, Condition> readGroupConditonFile(File f) {
		HashMap<String, Condition> map = new HashMap<String, Condition>();
		
		try {
			FileInputStream file = new FileInputStream(f);

			//Get the workbook instance for XLSX file 
			Workbook workbook = WorkbookFactory.create(file);

			//Get first sheet from the workbook
			Sheet sheet = workbook.getSheetAt(0);

			/*
			 * attention:
			 * POI count row from 0;
			 * but you see row index is labeled from 1 in excel file
			 */
			int lastRowIndex = sheet.getLastRowNum();
			// whey never hit? you should compile with '-ea' to enable assert check
			// assert(false); // test
			assert(0 == (lastRowIndex+1-1)%3);
			if(0 != (lastRowIndex+1-1)%3){
				System.out.println("ERROR: Group-Condition table is invalid, rowNum != 3n+1");
				return null; // tell caller failed
			}
			
			for(int rowIndex = 1; rowIndex < lastRowIndex; rowIndex = rowIndex+3) { // skip first head line
				Row row1 = sheet.getRow(rowIndex);
				Row row2 = sheet.getRow(rowIndex+1);
				Row row3 = sheet.getRow(rowIndex+2);
				
				/*
				 * skip same 3-row area, in which all cells are blank
				 * check cell[row1, col-C] as flag
				 * 
				 * when handle blank cells, getCell() will return null, so we should check cell pointer before call cell.getCellType()
				 */
				Cell cell_C = row1.getCell(2);
				if(null == cell_C || Cell.CELL_TYPE_BLANK == cell_C.getCellType())
					continue;

				ArrayList<Object> oneGroupData = new ArrayList<Object>();
				String groupID = row1.getCell(0).getStringCellValue();
				groupID = this.removeAllBlankSpace(groupID); //user may input typo
				oneGroupData.add(groupID);
				// skip column "Note"
				for(int col=2; col < Condition.LAST_COL_INDEX; col++){
					/*
					 *   			col
					 *   			 |
					 *   			 \/
					 * row1 ->		cell 1
					 * row2 ->		cell 2
					 * row3 ->		cell 3
					 */
					Cell cell1 	= row1.getCell(col);
					Cell cell2 	= row2.getCell(col);
					Cell cell3 	= row3.getCell(col);
					float data1 = 0;
					if(null != cell1 && Cell.CELL_TYPE_BLANK != cell1.getCellType())
						data1 = this.readFloatFromNumOrStringCell(cell1);
					
					float data2 = 0;
					if(null != cell2 && Cell.CELL_TYPE_BLANK != cell2.getCellType())
						data2 = this.readFloatFromNumOrStringCell(cell2);
					
					float data3 = 0;
					if(null != cell3 && Cell.CELL_TYPE_BLANK != cell3.getCellType())
						data3 = this.readFloatFromNumOrStringCell(cell3);
					
					float[] threeValOfCurrentCol = new float[]{data1, data2, data3};
					
					// check the three cell on bottom-left corner
					if(rowIndex == 577 && col == 1)
						System.out.println(data1 + " " + data2 + " " +data3);
					// check the three cell on bottom-right corner
					if(rowIndex == 577 && col == Condition.LAST_COL_INDEX-1)
						System.out.println(data1 + " " + data2 + " " +data3);
						
					oneGroupData.add(threeValOfCurrentCol);
				}
				
				Condition conditionForCurrentGroup = new Condition(oneGroupData.toArray(new Object[0]));
				map.put(groupID, conditionForCurrentGroup);
			}
			file.close();
			System.out.println("read excel file done");
		} catch (Exception e) {
			e.printStackTrace();
			return null; // indicate failed for caller
		}
		
		//System.out.println(map.size());
		return map;
	}
	
	// the cell may contain number or string
	private float readFloatFromNumOrStringCell(Cell cell){
		float val = 0;
		switch(cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			val = (float) cell.getNumericCellValue();
			break;
		case Cell.CELL_TYPE_STRING:
			val = Float.parseFloat(cell.getStringCellValue());
			break;
		}
		return val;
	}

	// the cell may contain number or string
	private String readStringFromNumOrStringCell(Cell cell){
		String str = null;
		switch(cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			str = ((Double)cell.getNumericCellValue()).toString();
			break;
		case Cell.CELL_TYPE_STRING:
			str = cell.getStringCellValue();
			break;
		}
		// when you see 200 in excel, you may get 200.0 here, so remove the last two chars if so
		String lastTwoChars = str.substring(str.length()-2, str.length());
		if(lastTwoChars.equals(".0"))
			str = cutLastTwoChars(str);
		return str;
	}
	
	private String cutLastTwoChars(String str){
			return str.substring(0, str.length()-2); // attention: start inclusive; end: exclusive!
	}
	
	/* always use aaabbb in program
	 * convert to aaabbb if user write AAABBB in file
	 * 
	 * todo: add more code to check invalid format, ex: are first columns are all String ?
	 * are second column are all float ?
	 */
	public ExRateTable readExRateFile(File f) {
		HashMap<String, Float> map = new HashMap<String, Float>();

		try {

			FileInputStream file = new FileInputStream(f);

			//Get the workbook instance for XLSX file 
			Workbook workbook = WorkbookFactory.create(file);

			//Get first sheet from the workbook
			Sheet sheet = workbook.getSheetAt(0);

			//Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();
			// assumes: no table head line
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();

				Iterator<Cell> cellIterator = row.cellIterator();
				// get first 2 column for every row
				Cell cellA, cellB;
				if(cellIterator.hasNext()) // check first and get next, or exception
					cellA = cellIterator.next();
				else
					continue;
				
				if(cellIterator.hasNext())
					cellB = cellIterator.next();
				else
					continue;
				
				String symbol = cellA.getStringCellValue();
				float val = (float) cellB.getNumericCellValue();
				System.out.println(symbol + " " + val);
				// discard invalid ex rates
				if(val > 0){
					map.put(symbol.toLowerCase(), val); // store all fx symbols in lowercase in program
				}
			}
			file.close();
			System.out.println("read excel file done");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return new ExRateTable(map);
		
	}
	
	public void checkDuplicatedClientIDs(File f) {
		HashSet<String> set = new HashSet<String>();
		int conflictCount = 0;

		try {

			FileInputStream file = new FileInputStream(f);

			//Get the workbook instance for XLSX file 
			Workbook workbook = WorkbookFactory.create(file);

			//Get first sheet from the workbook
			Sheet sheet = workbook.getSheetAt(0);

			//Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();
			// skip table head line
			rowIterator.next();
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();

				Cell cell_C = row.getCell(2);
				if(null != cell_C && Cell.CELL_TYPE_BLANK != cell_C.getCellType()){
					String id = this.readStringFromNumOrStringCell(cell_C);
					id = id.substring(0, 7); // 19636(FX1) => 19636
					if(set.contains(id)){
						System.out.println("duplicated client id: " + id);
						conflictCount++;
					}
					else
						set.add(id);
				}
			}
			file.close();
			System.out.println("read excel file done");
			System.out.println("conflict count: " + conflictCount);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String removeAllBlankSpace(String str){
		return str.replaceAll(" ", "");
	}

	public HashMap<String, String> readClientGroupFile(File f) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {

			FileInputStream file = new FileInputStream(f);

			//Get the workbook instance for XLSX file 
			Workbook workbook = WorkbookFactory.create(file);

			//Get first sheet from the workbook
			Sheet sheet = workbook.getSheetAt(0);

			//Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();
			// skip table head line
			rowIterator.next();
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();

				Cell cell_A = row.getCell(0);
				Cell cell_C = row.getCell(2);
				String client = this.readStringFromNumOrStringCell(cell_A);
				String group = cell_C.getStringCellValue();
				//System.out.println(client + " " + group);
				map.put(client, group);
			}
			file.close();
			System.out.println("read excel file done");
		} catch (Exception e) {
			return null;
		}
		return map;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ExcelIO io = new ExcelIO();
		//io.readSample();
		//io.generateclientIBMapFromXLS(null);
		//io.writeSample();
		
		//io.readExRateFile(new File("D:/coding/windows/gkfx/ExRate.xlsx"));
		//io.readGroupConditonFile(new File("D:/coding/windows/gkfx/tables-from-jeffery/Input_New/Group-Condition.xlsx"));
		//io.readGroupClientFile(new File("D:/coding/windows/gkfx/tables-from-jeffery/Input_New/Group-Clients.xlsx"));
		
		//io.checkDuplicatedClientIDs(new File("D:/coding/windows/gkfx/tables-from-jeffery/Input_New/Group-Clients_nomainib.xlsx"));
		io.readClientGroupFile(new File("D:/coding/windows/gkfx/tables-from-jeffery/Input_New/Accounts-Group.xlsx"));
		
	}

}
