import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

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
	
	static XSSFRow row;


	/**
	 * @param args
	 */
	public static void main(String[] args) throws  IOException{
		// TODO Auto-generated method stub1
		
		FileWriter fw = new FileWriter(new File("D:\\test.ifc"));
		BufferedWriter bw = new BufferedWriter(fw);
		
		FileInputStream fis = new FileInputStream(new File("D:\\test2.xlsx"));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		int counter = 0, cellCount = 0;
		for (int i = 1; i <workbook.getNumberOfSheets(); i++) {
			
			XSSFSheet spreadSheet = workbook.getSheetAt(i);
			Iterator<Row> rowIterator = spreadSheet.iterator();
			String zone = "#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('"+spreadSheet.getSheetName()+"',$,IFCTEXT('0'),$);";
			
			System.out.println(zone);
			bw.write(zone);
			bw.newLine();
			if (rowIterator.hasNext()) {
				
				row = (XSSFRow) rowIterator.next();
				cellCount= row.getLastCellNum();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					
					Cell cell = (Cell) cellIterator.next();
					System.out.println("#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('"+cell.toString()+"',$,IFCTEXT('0'),$);");
					String msg = "#"+counter+"= IFCPROPERTYSINGLEVALUE('"+cell.toString()+"',$,IFCTEXT('0'),$);";
					bw.write(msg);
					bw.newLine();
				}
				
			}
			
			String set = "#"+(++counter)+"= IFCPROPERTYSET('',#Value,'Analytical Data',$,(";
			for (int j = counter-cellCount-1; j < counter-1; ++j) {
				set = set+"#"+j+"," ;
			}
			set = set+"#"+(counter-1)+"));";
			System.out.println(set);
			
			bw.write(set);
			bw.newLine();
		}	
		
		bw.close();	
		workbook.close();
		fis.close();

	}

}
