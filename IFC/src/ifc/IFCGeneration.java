package ifc;
import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ifc.Mapping;



/**
 * IFC generation Project
 */

/**
 * @author raghavyadavm(raghavyadav258@gmail.com)
 *
 */
public class IFCGeneration {
	
	@SuppressWarnings("rawtypes")
	public static HashMap linkMap;
	public static HashMap analyticalLinkMap;
	FileWriter fwg = new FileWriter(new File("D:\\interm.ifc"));
	BufferedWriter bwg = new BufferedWriter(fwg);
	
	public IFCGeneration() throws IOException {
		// TODO Auto-generated constructor stub
	//int cc = getCount();
	//System.out.println(cc);
		
		//printExcel();
		
		Mapping.linkGeneration();
		
		
	
	}
	
	public void printExcel() throws IOException{
		XSSFRow row;
		
		
		FileInputStream fis = new FileInputStream(new File("D:\\result.xlsx"));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		
		HashMap analyticalMap = new HashMap();
		int cellCount = 0;
		int counter = getCount();
		
		for (int i = 1; i <workbook.getNumberOfSheets(); i++) {
			
			XSSFSheet spreadSheet = workbook.getSheetAt(i);
			Iterator<Row> rowIterator = spreadSheet.iterator();
			String zone = "#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Zone',$,IFCTEXT('"+spreadSheet.getSheetName()+"'),$);";
			
			//System.out.println(zone);
			bwg.write(zone);
			bwg.newLine();
			
			if (rowIterator.hasNext()) {
				
				row = (XSSFRow) rowIterator.next();
				cellCount= row.getLastCellNum();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					
					Cell cell = (Cell) cellIterator.next();
					//System.out.println(workbook.getSheetAt(i).getRow(1).getCell(cell.getColumnIndex()));
					//System.out.println("#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('"+cell.toString()+"',$,IFCTEXT('"+(workbook.getSheetAt(i).getRow(1).getCell(cell.getColumnIndex())).getRawValue()+"'),$);");
					String msg = "#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('"+cell.toString()+"',$,IFCTEXT('0'),$);";
					bwg.write(msg);
					bwg.newLine();
				}
				
			}
			linkMap = Mapping.linkGeneration();
			
			//int setCounter = ++counter;
			
			String set = "#"+(++counter)+"= IFCPROPERTYSET('',#Value,'Analytical Data',$,(";
			analyticalMap.put( "#"+counter,spreadSheet.getSheetName());
			for (int j = counter-cellCount-1; j < counter-1; ++j) {
				set = set+"#"+j+"," ;
			}
			set = set+"#"+(counter-1)+"));";
			//System.out.println(set);
			
			bwg.write(set);
			bwg.newLine();
		}	
		
		Set<String> analyticalMapSet = analyticalMap.keySet();
		System.out.println("\nanalyticalMap");
		for (String str1 : analyticalMapSet) {
			System.out.println(str1 + ":" + analyticalMap.get(str1) + ", ");
		}
		
		Set<String> analyticalMapRetrieve = analyticalMap.keySet();
		analyticalLinkMap =new  HashMap();
		
		for (String level1 : analyticalMapRetrieve) {
			
				String gen=	(String) linkMap.get(analyticalMap.get(level1));
				//System.out.println(gen);
				analyticalLinkMap.put(level1, gen);
		}
		
		Set<String> analyticalLinkMapSet = analyticalLinkMap.keySet();
		System.out.println("\nAnalytical link Map");
		for (String str1 : analyticalLinkMapSet) {
			System.out.println(str1 + ":" + analyticalLinkMap.get(str1) + ", ");
		}

		
		//linking();
		bwg.write("ENDSEC;");
		bwg.newLine();
		bwg.write("END-ISO-10303-21;");
		bwg.newLine();
		bwg.close();	
		workbook.close();
		fis.close();
		linking();

	}
	
	
	public  int getCount() throws IOException{
		FileReader fr = new FileReader(new File("D:\\test.ifc"));
		BufferedReader br = new BufferedReader(fr);
		
		FileWriter fw = new FileWriter(new File("D:\\result1.ifc"));
		BufferedWriter bw = new BufferedWriter(fw);
		TreeSet<Integer> ts1 = new TreeSet<Integer>();
		
		
		String line;
		while((line = br.readLine()) != null)
		{
			if ( (!line.contains("ENDSEC;")) && (!line.contains("END-ISO-10303-21;")) ){
				bwg.write(line);
				bwg.newLine();
			}
			if (line.contains("#")) {
				
			
				String requiredString = line.substring(line.indexOf("#") + 1, line.indexOf("="));
				
				int num = Integer.parseInt(requiredString);
				ts1.add(num);
				bw.write(String.valueOf(num));
				bw.newLine();
			}	
			
		}
		
		
		br.close();
		bw.close();
		return ts1.last();
		
	}


	public void linking() throws IOException{
		FileReader frl = new FileReader(new File("D:\\interm.ifc"));
		BufferedReader brl = new BufferedReader(frl);
		
		FileWriter fwg = new FileWriter(new File("D:\\generated.ifc"));
		BufferedWriter bwg = new BufferedWriter(fwg);
		
		
		String line;
		while ((line = brl.readLine()) != null) {
			
			
			Set<String> analyticalLinkMapSet = analyticalLinkMap.keySet();
			//System.out.println("\nlink Map");
			for (String str1 : analyticalLinkMapSet) {
				//System.out.println(str1 + ":" + linkMap.get(str1) + ", ");
				if(line.startsWith(analyticalLinkMap.get(str1)+"=")){
					//System.out.println("****line***\n"+line);
					//line = line.substring(0,(line.indexOf("$,("))+3)+"#18865"+","+line.substring(line.indexOf("$,(")+,line.length());
					StringBuilder sb = new StringBuilder(line);
					sb.insert((line.indexOf("$,("))+3, str1+",");
					
					//System.out.println("****line***\n"+sb);
					//bwg.write(sb.toString());
					//bwg.newLine();
					
					line = sb.toString();
					//System.out.println("****replace line***\n"+line);
					
				}
			}
			//System.out.println("line"+line);
			bwg.write(line);
			bwg.newLine();
			
			
		}
		
		bwg.close();		
		
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws  IOException{
		// TODO Auto-generated method stub1
		
		new IFCGeneration();
		//System.out.println("Mapping"+Mapping.linkGeneration());
		
	}

}
