import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
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
	
	public IFCGeneration() throws IOException {
		// TODO Auto-generated constructor stub
	//int cc = getCount();
	//System.out.println(cc);
		
		printExcel();
	
	}
	
	public void printExcel() throws IOException{
		XSSFRow row;
		FileWriter fw = new FileWriter(new File("D:\\test3.ifc"));
		BufferedWriter bw = new BufferedWriter(fw);
		
		FileInputStream fis = new FileInputStream(new File("D:\\test2.xlsx"));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		int cellCount = 0;
		int counter = getCount();
		
		for (int i = 1; i <5; i++) {
			
			XSSFSheet spreadSheet = workbook.getSheetAt(i);
			Iterator<Row> rowIterator = spreadSheet.iterator();
			String zone = "#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('Zone',$,IFCTEXT('"+spreadSheet.getSheetName()+"'),$);";
			
			System.out.println(zone);
			bw.write(zone);
			bw.newLine();
			
			if (rowIterator.hasNext()) {
				
				row = (XSSFRow) rowIterator.next();
				cellCount= row.getLastCellNum();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					
					Cell cell = (Cell) cellIterator.next();
					//System.out.println(workbook.getSheetAt(i).getRow(1).getCell(cell.getColumnIndex()));
					System.out.println("#"+(++counter)+"= IFCPROPERTYSINGLEVALUE('"+cell.toString()+"',$,IFCTEXT('"+(workbook.getSheetAt(i).getRow(1).getCell(cell.getColumnIndex())).getRawValue()+"'),$);");
					String msg = "#"+counter+"= IFCPROPERTYSINGLEVALUE('"+cell.toString()+"',$,IFCTEXT('"+(workbook.getSheetAt(i).getRow(1).getCell(cell.getColumnIndex())).getRawValue()+"'),$);";
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
	
	
	public  int getCount() throws IOException{
		FileReader fr = new FileReader(new File("D:\\test.ifc"));
		BufferedReader br = new BufferedReader(fr);
		
		FileWriter fw = new FileWriter(new File("D:\\result1.ifc"));
		BufferedWriter bw = new BufferedWriter(fw);
		TreeSet<Integer> ts1 = new TreeSet<Integer>();
		
		
		String line;
		while((line = br.readLine()) != null)
		{
			
			if (line.contains("#")) {
				//System.out.println("true");
			
				String requiredString = line.substring(line.indexOf("#") + 1, line.indexOf("="));
				//System.out.println(requiredString);
				int num = Integer.parseInt(requiredString);
				ts1.add(num);
				bw.write(String.valueOf(num));
				bw.newLine();
			}	
			
		}
		
		//System.out.println(ts1);
		System.out.println(ts1.last());
		br.close();
		bw.close();
		return ts1.last();
		
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) throws  IOException{
		// TODO Auto-generated method stub1
		
		new IFCGeneration();
		
	}

}
