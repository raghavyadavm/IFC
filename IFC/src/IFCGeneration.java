import java.io.File;
import java.io.FileInputStream;
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
		// TODO Auto-generated method stub
		
		FileInputStream fis = new FileInputStream(new File("D:\\test.xlsx"));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet spreadSheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = spreadSheet.iterator();
		while (rowIterator.hasNext()) {
			row = (XSSFRow) rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = (Cell) cellIterator.next();
				System.out.println(cell.toString());								
			}
			
		}
		
		workbook.close();
		fis.close();

	}

}
