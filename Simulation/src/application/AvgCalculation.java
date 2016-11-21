package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AvgCalculation {
	
	public void printExcel() throws IOException{
		XSSFRow row;
		
		
		FileInputStream fis = new FileInputStream(new File(SampleController.results));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		
		HashMap analyticalMap = new HashMap();
		int cellCount = 0;
		int counter = 100;
		
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
		
		
		
		
		for (int i = 2; i <3; i++) {
			
			XSSFSheet spreadSheet = workbook.getSheetAt(i);
			Iterator<Row> rowIterator = spreadSheet.iterator();
			String zone = "#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Zone',$,IFCTEXT('"+spreadSheet.getSheetName()+"'),$);";
			
			System.out.println(zone);
			//workbook.getSheetAt(i).getRow(1).getCell(cell.getColumnIndex())).getRawValue();
			
			while (rowIterator.hasNext()) {
				
				row = (XSSFRow) rowIterator.next();
				//cellCount= 10; // No of single values
				Iterator<Cell> cellIterator = row.cellIterator();
				
				while (cellIterator.hasNext()) {
					
					Cell cell = (Cell) cellIterator.next();
					int columnIndex = cell.getColumnIndex();
					int rowIndex = cell.getRowIndex();
					
					if(columnIndex ==2 && rowIndex >1 ){
						
						if(countAverage1++<24){
							//System.out.println("value"+cell.getNumericCellValue());
							average1 =average1+cell.getNumericCellValue();
							
							//System.out.println("count1 "+countAverage1);
							//countAverage1++;
							if(countAverage1 == 24){
								computed1 = average1/24;
								//computed.put(computed1,"zone a");
								System.out.println("#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Zone Heating A',$,IFCTEXT('"+average1/24+"'),$);");
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
								System.out.println("#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Zone Heating B',$,IFCTEXT('"+average2/24+"'),$);");
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
								cellCount = cellCount + 5;
								//computed.put(computed3,"analytical");
								System.out.println("#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Actual heating vavle',$,IFCTEXT('"+average3/24+"'),$);");
								System.out.println("#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Difference A',$,IFCTEXT('"+(computed1 - computed3)+"'),$);");
								System.out.println("#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Difference B',$,IFCTEXT('"+(computed2 - computed3)+"'),$);");
								//System.out.println("computed1 "+computed1+" computed2 "+computed2+" computed3 "+computed3);
								average3=0;
								countAverage3=0;
							}
						}
						//System.out.println("value3 "+row.getCell(columnIndex).getRawValue());
						
						//System.out.println("Average3 "+average3);
						
					}
					
					
				}
				/*String msg = "#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('A Difference',$,IFCTEXT('0'),$);";
				System.out.println(msg);
				
				
				String msg1 = "#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('B Difference',$,IFCTEXT('0'),$);";
				System.out.println(msg1);*/
				
				
			}
			//linkMap = Mapping.linkGeneration();
			
			//int setCounter = ++counter;
			
			System.out.println("cellCount "+ cellCount);
			
			String set = "#"+(++counter)+"= IFCPROPERTYSET('',#Value,'Analytical Data',$,(";
			analyticalMap.put( "#"+counter,spreadSheet.getSheetName());
			for (int j = counter-cellCount-1; j < counter-1; ++j) {
				set = set+"#"+j+"," ;
			}
			set = set+"#"+(counter-1)+"));";
			System.out.println(set);
			
			
		}	
		Set<Double> analyticalMapSet = computed.keySet();
		//System.out.println("\ncomputed Map");
		for (Double str1 : analyticalMapSet) {
			//System.out.println(str1 + ":" + computed.get(str1) + ", ");
		}
		
			
		workbook.close();
		fis.close();
		

	}
	
	

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		new AvgCalculation().printExcel();

	}

}
