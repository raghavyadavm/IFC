package BEMS_BIM_API;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;




public class Phase1 {
	public static String bems_file;
	public static String CMMS_file;
	public static String ifc_file;
	public static String new_ifc_file;
	public static ArrayList<String> objectIDs = new ArrayList<String>();
	public static HashMap<Integer, String> bemsHeader = new HashMap<Integer, String>();
	public static HashMap<String, String> objectID_LineNo_Map = new HashMap<String, String>();
	public static HashMap<String, String> objectID_LineNo_Identity_Map = new HashMap<String,String>();
	public static HashMap<String, ArrayList<String>> identity_Line_No_attributes_Map = new HashMap<String,ArrayList<String>>();
	public static HashMap<String, String[]> at_lineno_attribute_map = new HashMap<String, String[]>();
	public static HashSet<String> attributes_lineNos = new HashSet<String>();
	public static HashMap<String, Integer> identity_lineno_row_map = new HashMap<String, Integer>();

	/*
	 * STEP 0: Process BEMS File
	 * */
	public void process_bems_file() throws IOException
	{
		XSSFRow row;	
		
		
		FileInputStream fis = new FileInputStream(new File("D:\\BEMS.xlsx"));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet spreadSheet = workbook.getSheetAt(0);
		
		Iterator<Row> rowIterator = spreadSheet.iterator();
		
		String newValue = null;
		
		while (rowIterator.hasNext()) {
			
			row = (XSSFRow) rowIterator.next();			
			Iterator<Cell> cellIterator = row.cellIterator();			
			
			while (cellIterator.hasNext()) {
				String value = "";				
				Cell cell = (Cell) cellIterator.next();
				
				int columnIndex = cell.getColumnIndex();
				int rowIndex = cell.getRowIndex();
				
				if (columnIndex % 2 != 0) {
					//System.out.println(cell.getStringCellValue());
					newValue = cell.getStringCellValue();
					
					if (rowIndex == 0) {
						
						//System.out.println("Header "+ cell.getStringCellValue());
						bemsHeader.put(columnIndex, cell.getStringCellValue());
					}
					
					if (newValue != null)
					{
						value = newValue;
					}
					
					if (value.toLowerCase().contains("alarm"))
					{
						System.out.println("***alarm***");
						System.out.println(columnIndex);
						String sb = bemsHeader.get(columnIndex);
						sb = sb.substring(sb.indexOf(".")+1);
						System.out.println(sb);
						objectIDs.add(sb);
					} 	
				}
				
			}			
		}
	}

	private void phase_1() throws IOException
	{
		FileReader fr = new FileReader(new File("D:\\IFCOriginal.ifc"));
		BufferedReader br = new BufferedReader(fr);
		
		String line = "";
		int hits = 0;
		
		
		
		while ((line = br.readLine()) != null)
		{
			for (String objectid : objectIDs)
			{
				if (line.contains("IFCPROPERTYSINGLEVALUE") && line.contains("BEMS ID") && line.contains(objectid)) {
					
					System.out.println(line);
					String lineNo = line.substring(1, line.indexOf("="));
					objectID_LineNo_Map.put(objectid, lineNo);
					System.out.println(objectid + "\t" +  lineNo);
					hits++;
				}
			}	
			
			if (hits == objectIDs.size())
			{
				break;
			}
			
		}
		br.close();
		//MessageBox.Show("Done processing!");
		 
		 
	}

	private void phase_2() throws IOException
	{
		FileReader fr = new FileReader(new File("D:\\IFCOriginal.ifc"));
		BufferedReader br = new BufferedReader(fr);
		
		//java.io.InputStreamReader reader = new java.io.InputStreamReader(ifc_file);
		String line = "";
		int hits = 0;
		while ((line = br.readLine()) != null)
		{
			if (!line.contains("'Identity Data'"))
			{
				continue;
			}
			for (String objectid : objectIDs)
			{
				String lineNo = "#" + objectID_LineNo_Map.get(objectid);
				if (line.contains(lineNo))
				{
					//MessageBox.Show(line);
					System.out.println(line);
					String identity_LineNo = line.substring(1, line.indexOf("="));
					objectID_LineNo_Identity_Map.put(objectid, identity_LineNo);
					// MessageBox.Show(objectid + "\t" +  identity_LineNo);
					System.out.println(objectid + "\t" +  identity_LineNo);
					String attributesString = line.replace("));", "");
					attributesString = attributesString.substring(line.indexOf("(#") + 1);
					//MessageBox.Show(attributesString);
					System.out.println(attributesString);
					

					
					identity_Line_No_attributes_Map.put(identity_LineNo, new ArrayList<String>(Arrays.asList(attributesString.split(","))));
					String[] parts = attributesString.split("[,]", -1);
					for (int i = 0; i < parts.length; i++)
					{
						attributes_lineNos.add(parts[i]);
					}
					hits++;
				}
			}
			if (hits == objectIDs.size())
			{
				break;
			}
		}
		br.close();
		//MessageBox.Show("Done processing!");
	}

	private void phase_3() throws IOException{
		FileReader fr = new FileReader(new File("D:\\IFCOriginal.ifc"));
		BufferedReader br = new BufferedReader(fr);
		
		String line = "";
		while ((line = br.readLine()) != null)
		{
			for (String lineno : attributes_lineNos)
			{
				if (line.startsWith(lineno + "=") && line.contains("IFCTEXT"))
				{
					HashMap<String, String> attribute = new HashMap<String, String>();
					///#21191= IFCPROPERTYSINGLEVALUE('BEMS ID',$,IFCTEXT('TAB-012'),$);
					String attribute_Line = line.replace(",$);", "").replace(lineno + "= IFCPROPERTYSINGLEVALUE(", "");
					//'BEMS ID',$,IFCTEXT('TAB-012')
					String[] parts = attribute_Line.split("[,]", -1);
					String key = parts[0].replace("'", "").trim();
					String value = parts[2].replace("IFCTEXT('", "").replace("')", "").trim();
					String[] v = new String[] {key, value};
					at_lineno_attribute_map.put(lineno, v);
					//MessageBox.Show(key + "\t" + value);
					System.out.println(key + "\t" + value);
				}
			}
		}
		br.close();
		//MessageBox.Show("Done processing!");
	}
	
	private void phase_4() throws IOException
	{
		for (String identity_line_no : identity_Line_No_attributes_Map.keySet())
		{
			String cmmsId = "";
			String sn = "";
			for (String at_line_no : identity_Line_No_attributes_Map.get(identity_line_no))
			{
				if (!at_lineno_attribute_map.keySet().contains(at_line_no))
				{
					continue;
				}
				String[] attribute = at_lineno_attribute_map.get(at_line_no);
				if (attribute[0].equals("CMMS ID"))
				{
					if (!attribute[1].equals(""))
					{
						cmmsId = attribute[1];
					}
				}
				if (attribute[0].equals("Serial Number"))
				{
					if (!attribute[1].equals(""))
					{
						sn = attribute[1];
					}
				}
			}
			if (cmmsId.equals("") && sn.equals(""))
			{
				System.out.println("Problem occured. No CMMS ID or Serial Number found!");
				
			}
			
			XSSFRow row;	
			
			
			FileInputStream fis = new FileInputStream(new File("D:\\CMMS.xlsx"));
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet spreadSheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = spreadSheet.iterator();
			
	
			
			while (rowIterator.hasNext()) {
				
				row = (XSSFRow) rowIterator.next();			
				Iterator<Cell> cellIterator = row.cellIterator();			
				
				while (cellIterator.hasNext()) {
									
					Cell cell = (Cell) cellIterator.next();
					int type;
				    
				    type = cell.getCellType();

				    Object result;
					switch (type) {

				        case 0: // numeric value in Excel
				            result = cell.getNumericCellValue();
				            break;
				        case 1: // String Value in Excel 
				            result = cell.getStringCellValue();
				            break;
				        default:  
				            throw new RuntimeException("There is no support for this type of cell");                        
				    }
					String value = result.toString();
					if (cmmsId.equals(value) || sn.equals(value))
					{
						//MessageBox.Show(rCnt.ToString());
						identity_lineno_row_map.put(identity_line_no, cell.getRowIndex());
						System.out.println(identity_line_no+"  "+cell.getRowIndex());
						//cCnt = range.getColumns().getCount();
						//rCnt = range.getRows().getCount();
					}
				}
			}	

			System.out.println(identity_lineno_row_map);
			workbook.close();
		}
		//MessageBox.Show("Done processing!");
	}


	private void phase_5() throws IOException
	{
		HashMap<String, String> modifications_map = new HashMap<String, String>();
		
			
		
		
		FileInputStream fis = new FileInputStream(new File("D:\\CMMS.xlsx"));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet spreadSheet = workbook.getSheetAt(0);
				

		for (String identity_lineno : identity_lineno_row_map.keySet())
		{
			int rCnt = identity_lineno_row_map.get(identity_lineno);
			for (String attribute_lineno : identity_Line_No_attributes_Map.get(identity_lineno))
			{
				if (!at_lineno_attribute_map.keySet().contains(attribute_lineno))
				{
					continue;
				}
				String[] attribute = at_lineno_attribute_map.get(attribute_lineno);
				switch (attribute[0]) {
				case "serves": {
					// column 5
					Row row = spreadSheet.getRow(rCnt);
					Cell cell = row.getCell(4);
					String cell_value = cell.getStringCellValue();
					System.out.println(cell_value);
					modifications_map.put(attribute_lineno, cell_value);
					break;
				}
				case "model number": {
					// column 8
					Row row = spreadSheet.getRow(rCnt);
					Cell cell = row.getCell(7);
					String cell_value = cell.getStringCellValue();
					System.out.println(cell_value);
					modifications_map.put(attribute_lineno, cell_value);
					break;
				}
				case "Warranty date": {
					// column 11

					Row row = spreadSheet.getRow(rCnt);
					Cell cell = row.getCell(10);
					Object result=cell.getDateCellValue();
					String cell_value = result.toString();
					System.out.println(cell_value);
					//java.time.LocalDateTime dt = java.time.LocalDateTime.(cell_value);
					modifications_map.put(attribute_lineno, cell_value);
					break;
				}
				case "Previous Maintenance number": {
					// column 13

					Row row = spreadSheet.getRow(rCnt);
					Cell cell = row.getCell(12);
					String cell_value = cell.getStringCellValue();
					System.out.println(cell_value);
					modifications_map.put(attribute_lineno, cell_value);
					break;
				}
				case "Previous Maintenance description": {
					// column 14
					Row row = spreadSheet.getRow(rCnt);
					Cell cell = row.getCell(13);
					String cell_value = cell.getStringCellValue();
					System.out.println(cell_value);
					modifications_map.put(attribute_lineno, cell_value);
					break;
				}
				case "Maintenance Type": {
					// column 15
					Row row = spreadSheet.getRow(rCnt);
					Cell cell = row.getCell(14);
					String cell_value = cell.getStringCellValue();
					System.out.println(cell_value);
					modifications_map.put(attribute_lineno, cell_value);
					break;
				}
				case "Maintenance cost": {
					// column 16
					Row row = spreadSheet.getRow(rCnt);
					Cell cell = row.getCell(15);
					Object result= cell.getNumericCellValue();
					String cell_value = result.toString();
					System.out.println(cell_value);
					modifications_map.put(attribute_lineno, cell_value);
					break;
				}
				case "PM Maintenance tasks": {
					// column 17
					Row row = spreadSheet.getRow(rCnt);
					Cell cell = row.getCell(16);
					String cell_value = cell.getStringCellValue();
					System.out.println(cell_value);
					modifications_map.put(attribute_lineno, cell_value);
					break;
				}

				}
			}
		}

		FileWriter fw = new FileWriter(new File("D:\\outcome.ifc"));
		BufferedWriter bw = new BufferedWriter(fw);
		
		FileReader fr = new FileReader(new File("D:\\IFCOriginal.ifc"));
		BufferedReader br = new BufferedReader(fr);

		//java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(new_ifc_file);
		ArrayList<String> fileLines = new ArrayList<String>();
		//java.io.InputStreamReader reader = new java.io.InputStreamReader(ifc_file);
		String line = "";
		boolean isWritten = false;
		while ((line = br.readLine()) != null)
		{
			isWritten = false;
			for (String attribute_lineno : modifications_map.keySet())
			{
				if (line.startsWith(attribute_lineno + "="))
				{
					String value = modifications_map.get(attribute_lineno);
					///#17174= IFCPROPERTYSINGLEVALUE('PM maintenace Tasks',$,IFCTEXT(''),$);
					String new_line = line.substring(0, line.indexOf("IFCTEXT('"));
					new_line = new_line + "IFCTEXT('" + value + "'),$);";
					bw.write(new_line + System.lineSeparator());
					bw.flush();
					isWritten = true;
				}
			}
			if (!isWritten)
			{
				bw.write(line + System.lineSeparator());
				bw.flush();
			}
		}
		bw.close();
		br.close();
		//MessageBox.Show("Done processing!");
		 		
	}

	public static void main(String a[]) throws IOException{
		new Phase1().process_bems_file();
		new Phase1().phase_1();
		new Phase1().phase_2();
		new Phase1().phase_3();
		new Phase1().phase_4();
		new Phase1().phase_5();
	}
}
