package application;
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
		
		printExcel();
		
		//Mapping.linkGeneration();
		
		
	
	}
	
	public void printExcel() throws IOException{
		XSSFRow row;
		
		
		FileInputStream fis = new FileInputStream(new File(SampleController.results));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		
		HashMap analyticalMap = new HashMap();
		int cellCount = 0;
		int counter = getCount();
		
		double average1 = 0;
		int countAverage1 = 0;
		
		double average2 = 0;
		int countAverage2 = 0;
		
		double average3 = 0;
		int countAverage3 = 0;
		
		double computed1 = 0;
		double computed2 = 0;
		double computed3 = 0;
		
		HashMap computed = new HashMap();
		
		
		
		
		for (int i = 1; i <workbook.getNumberOfSheets(); i++) {
			
			XSSFSheet spreadSheet = workbook.getSheetAt(i);
			Iterator<Row> rowIterator = spreadSheet.iterator();
			String zone = "#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Zone',$,IFCTEXT('"+spreadSheet.getSheetName()+"'),$);";
			cellCount = cellCount+1;
			System.out.println(zone);
			bwg.write(zone);
			bwg.newLine();
			//workbook.getSheetAt(i).getRow(1).getCell(cell.getColumnIndex())).getRawValue();
			
			while (rowIterator.hasNext()) {
				
				row = (XSSFRow) rowIterator.next();
				//cellCount= 10; // No of single values
				Iterator<Cell> cellIterator = row.cellIterator();
				
				while (cellIterator.hasNext()) {
					
					Cell cell = (Cell) cellIterator.next();
					int columnIndex = cell.getColumnIndex();
					int rowIndex = cell.getRowIndex();
					
					
					if(columnIndex ==0){
						if((rowIndex-2) % 24 ==0){
							String msg = "#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Date',$,IFCTEXT('"+cell.getDateCellValue()+"'),$);";
							System.out.println(msg);
							bwg.write(msg);
							bwg.newLine();
						}
						
					}
					
					if(columnIndex ==2 && rowIndex >1 ){
						
						if(countAverage1++<24){
							//System.out.println("value"+cell.getNumericCellValue());
							average1 =average1+cell.getNumericCellValue();
							
							//System.out.println("count1 "+countAverage1);
							//countAverage1++;
							if(countAverage1 == 24){
								computed1 = average1/24;
								//computed.put(computed1,"zone a");
								String msg = "#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Zone Heating A',$,IFCTEXT('"+average1/24+"'),$);";
								System.out.println(msg);
								bwg.write(msg);
								bwg.newLine();
								average1=0;
								countAverage1=0;
							}
						}
						//System.out.println("value1 "+row.getCell(columnIndex).getRawValue());
						
						//System.out.println("Average2 "+average1);
						
					}
					
					if(columnIndex ==14 && rowIndex >1 ){
						
						if(countAverage2++<24){
							//System.out.println("value"+cell.getNumericCellValue());
							average2 =average2+cell.getNumericCellValue();
							
							//System.out.println("count1 "+countAverage2);
							//countAverage1++;
							if(countAverage2 == 24){								
								computed2 = average2/24;
								//computed.put(computed2,"zone b");
								String msg = "#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Zone Heating B',$,IFCTEXT('"+average2/24+"'),$);";
								//System.out.println("#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Zone Heating B',$,IFCTEXT('"+average2/24+"'),$);");
								System.out.println(msg);
								bwg.write(msg);
								bwg.newLine();
								average2=0;
								countAverage2=0;
							}
						}
						//System.out.println("value2 "+row.getCell(columnIndex).getRawValue());
						
						//System.out.println("Average2 "+average2);
						
					}
					
					if(columnIndex ==24 && rowIndex >1 ){
						
						if(countAverage3++<24){
							//System.out.println("value"+cell.getNumericCellValue());
							average3 =average3+cell.getNumericCellValue();
							
							//System.out.println("count1 "+countAverage1);
							//countAverage1++;
							if(countAverage3 == 24){								
								computed3 = average3/24;
								cellCount = cellCount + 6; // cell count of each 24 range generated single values
								//computed.put(computed3,"analytical");
								String msg = "#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Actual heating vavle',$,IFCTEXT('"+average3/24+"'),$);";
								System.out.println(msg);
								bwg.write(msg);
								bwg.newLine();
								
								msg= "#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Difference A',$,IFCTEXT('"+(computed1 - computed3)+"'),$);";
								//System.out.println("#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Actual heating vavle',$,IFCTEXT('"+average3/24+"'),$);");
								System.out.println(msg);
								bwg.write(msg);
								bwg.newLine();
								
								msg= "#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Difference B',$,IFCTEXT('"+(computed2 - computed3)+"'),$);";
								//System.out.println("#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Actual heating vavle',$,IFCTEXT('"+average3/24+"'),$);");
								System.out.println(msg);
								bwg.write(msg);
								bwg.newLine();
								
								//****************************Setting the Flags**************************************
								if((computed1 - computed3)<0){
									cellCount = cellCount + 1;
									msg= "#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Flag 1',$,IFCTEXT('true'),$);";
									System.out.println(msg);
									bwg.write(msg);
									bwg.newLine();
								}
								if((computed2 - computed3)<0){
									cellCount = cellCount + 1;
									msg= "#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Flag 2',$,IFCTEXT('true'),$);";
									System.out.println(msg);
									bwg.write(msg);
									bwg.newLine();
								}
								
								//System.out.println("#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Difference A',$,IFCTEXT('"+(computed1 - computed3)+"'),$);");
								//System.out.println("#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Difference B',$,IFCTEXT('"+(computed2 - computed3)+"'),$);");
								//System.out.println("computed1 "+computed1+" computed2 "+computed2+" computed3 "+computed3);
								average3=0;
								countAverage3=0;
							}
						}
						//System.out.println("value3 "+row.getCell(columnIndex).getRawValue());
						
						//System.out.println("Average3 "+average3);
						
					}
					
					
				}
				
				
				
			}
			linkMap = Mapping.linkGeneration();
			
			
			
			System.out.println("cellCount "+ cellCount);
			
			//******************************************IFCPropertySET***************
			
			String set = "#"+(++counter)+"= IFCPROPERTYSET('',#Value,'Analytical Data',$,(";
			analyticalMap.put( "#"+counter,spreadSheet.getSheetName());
			for (int j = counter-cellCount; j < counter-1; ++j) {
				set = set+"#"+j+"," ;
			}
			set = set+"#"+(counter-1)+"));";
			System.out.println(set);
			cellCount=0;
			
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
		FileReader fr = new FileReader(new File("D:\\outcome.ifc"));
		BufferedReader br = new BufferedReader(fr);
		
	//	FileWriter fw = new FileWriter(new File("D:\\result1.ifc"));
	//	BufferedWriter bw = new BufferedWriter(fw);
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
				//bw.write(String.valueOf(num));
				//bw.newLine();
			}	
			
		}
		
		
		br.close();
		//bw.close();
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
		
		new Module1();
		new IFCGeneration();
		//System.out.println("Mapping"+Mapping.linkGeneration());
		
	}

}
