package BEMS_BIM_API;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Form1 extends Form
{
	public static String bems_file;
	public static String CMMS_file;
	public static String ifc_file;
	public static String new_ifc_file;
	public static ArrayList<String> objectIDs = new ArrayList<String>();
	public static HashMap<String, String> objectID_LineNo_Map = new HashMap<String, String>();
	public static HashMap<String, String> objectID_LineNo_Identity_Map = new HashMap<String,String>();
	public static HashMap<String, ArrayList<String>> identity_Line_No_attributes_Map = new HashMap<String,ArrayList<String>>();
	public static HashMap<String, String[]> at_lineno_attribute_map = new HashMap<String, String[]>();
	public static HashSet<String> attributes_lineNos = new HashSet<String>();
	public static HashMap<String, Integer> identity_lineno_row_map = new HashMap<String, Integer>();

	public Form1()
	{
		InitializeComponent();
	}

	private void button1_Click(Object sender, tangible.EventArgs e)
	{
		openFileDialog1.ShowDialog();
	}

	private void openFileDialog1_FileOk(Object sender, CancelEventArgs e)
	{
		bems_file = openFileDialog1.FileName;
		textBox1.Text = bems_file;
	}

	private void button2_Click(Object sender, tangible.EventArgs e)
	{
		openFileDialog2.ShowDialog();
	}

	private void openFileDialog2_FileOk(Object sender, CancelEventArgs e)
	{
		ifc_file = openFileDialog2.FileName;
		textBox2.Text = ifc_file;
	}

	private void button6_Click(Object sender, tangible.EventArgs e)
	{
		saveFileDialog1.InitialDirectory = openFileDialog2.InitialDirectory;
		saveFileDialog1.ShowDialog();
	}

	private void saveFileDialog1_FileOk(Object sender, CancelEventArgs e)
	{
		new_ifc_file = saveFileDialog1.FileName;
		textBox3.Text = new_ifc_file;
	}

	private void CMMSBr_Click(Object sender, tangible.EventArgs e)
	{
		openFileDialog3.ShowDialog();
	}


	private void openFileDialog3_FileOk(Object sender, CancelEventArgs e)
	{
		CMMS_file = openFileDialog3.FileName;
		textBox4.Text = CMMS_file;
	}

	private void button3_Click(Object sender, tangible.EventArgs e)
	{
		process_bems_file();
	}

	private void button4_Click_1(Object sender, tangible.EventArgs e)
	{
		phase_1();
	}

	private void button5_Click(Object sender, tangible.EventArgs e)
	{
		phase_2();
	}

	private void button7_Click(Object sender, tangible.EventArgs e)
	{
		phase_3();
	}

	private void button8_Click(Object sender, tangible.EventArgs e)
	{
		phase_4();
	}

	private void button9_Click(Object sender, tangible.EventArgs e)
	{
		phase_5();
	}

	/*
	 * STEP 0: Prcess BEMS File
	 * */
	private void process_bems_file()
	{
		if (bems_file == null || bems_file.length() == 0)
		{
			JOptionPane.showConfirmDialog(null, "No BEMS file to read!", "Error!", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		}
		Microsoft.Office.Interop.Excel.Application xlApp = new Microsoft.Office.Interop.Excel.Application();
		Microsoft.Office.Interop.Excel.Workbook xlWorkBook = xlApp.getWorkbooks().Open(bems_file, 0, true, 5, "", "", true, Microsoft.Office.Interop.Excel.XlPlatform.xlWindows, "\t", false, false, 0, true, 1, 0);
		Microsoft.Office.Interop.Excel.Worksheet xlWorkSheet = (Microsoft.Office.Interop.Excel.Worksheet)xlWorkBook.getWorksheets().get_Item(1);
		Microsoft.Office.Interop.Excel.Range range = xlWorkSheet.getUsedRange();
		for (int cCnt = 2; cCnt <= range.getColumns().getCount(); cCnt += 2)
		{
			String objectID = (String)((Microsoft.Office.Interop.Excel.Range)((range.getCells().getCharacters(1, cCnt) instanceof Microsoft.Office.Interop.Excel.Range) ? range.getCells().getCharacters(1, cCnt) : null)).getValue2();
			String value = "";
			for (int rCnt = 1; rCnt <= range.getRows().getCount(); rCnt++)
			{
				String newValue = (String)((Microsoft.Office.Interop.Excel.Range)((range.getCells().getCharacters(rCnt, cCnt) instanceof Microsoft.Office.Interop.Excel.Range) ? range.getCells().getCharacters(rCnt, cCnt) : null)).getValue2();
				if (newValue != null)
				{
					value = newValue;
				}
			}
			if (value.toLowerCase().contains("alarm"))
			{
				objectID = objectID.split("[.]", -1)[1];
				listBox1.Items.Add(objectID);
				objectIDs.add(objectID);
				//MessageBox.Show("Object ID:" + objectID);
			}
		}
		xlWorkBook.Close(true, null, null);
		xlApp.Quit();
		if (objectIDs.isEmpty())
		{
			JOptionPane.showConfirmDialog(null, "No Alarming objects found in BEMS Excel file!", "Error!", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	private void phase_1()
	{
		
		java.io.InputStreamReader reader = new java.io.InputStreamReader(ifc_file);
		String line = "";
		String template = "#(\\d)+= IFCPROPERTYSINGLEVALUE\\('BEMS ID',\\$,IFCTEXT\\('@@@'\\),\\$\\)";
		int hits = 0;
		while ((line = reader.ReadLine()) != null)
		{
			for (String objectid : objectIDs)
			{
				Regex regex = new Regex(template.replace("@@@", objectid));
				Match match = regex.Match(line);
				if (match.Success)
				{
					//MessageBox.Show(line);
					String lineNo = line.substring(1, line.indexOf("="));
					objectID_LineNo_Map.put(objectid, lineNo);
					// MessageBox.Show(objectid + "\t" +  lineNo);
					hits++;
				}
			}
			if (hits == objectIDs.size())
			{
				break;
			}
		}
		reader.close();
		//MessageBox.Show("Done processing!");
	}

	private void phase_2()
	{
		FileReader fr = new FileReader(new File("D:\\IFCOriginal.ifc"));
		BufferedReader reader = new BufferedReader(fr);
		
		//java.io.InputStreamReader reader = new java.io.InputStreamReader("D:\\IFCOriginal.ifc");
		String line = "";
		int hits = 0;
		while ((line = reader.readLine()) != null)
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
					String identity_LineNo = line.substring(1, line.indexOf("="));
					objectID_LineNo_Identity_Map.put(objectid, identity_LineNo);
					// MessageBox.Show(objectid + "\t" +  identity_LineNo);
					String attributesString = line.replace("));", "");
					attributesString = attributesString.substring(line.indexOf("(#") + 1);
					//MessageBox.Show(attributesString);
					identity_Line_No_attributes_Map.put(identity_LineNo, attributesString.split("[,]", -1).ToList());
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
		reader.close();
		//MessageBox.Show("Done processing!");
	}

	private void phase_3()
	{
		java.io.InputStreamReader reader = new java.io.InputStreamReader(ifc_file);
		String line = "";
		while ((line = reader.ReadLine()) != null)
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
				}
			}
		}
		reader.close();
		//MessageBox.Show("Done processing!");
	}

	private void phase_4()
	{
		for (String identity_line_no : identity_Line_No_attributes_Map.keySet())
		{
			String cmmsId = "";
			String sn = "";
			for (String at_line_no : identity_Line_No_attributes_Map.get(identity_line_no))
			{
				if (!at_lineno_attribute_map.keySet().Contains(at_line_no))
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
				JOptionPane.showMessageDialog(null, "Problem occured. No CMMS ID or Serial Number found!");
				return;
			}

			Microsoft.Office.Interop.Excel.Application xlApp = new Microsoft.Office.Interop.Excel.Application();
			Microsoft.Office.Interop.Excel.Workbook xlWorkBook = xlApp.getWorkbooks().Open(CMMS_file, 0, true, 5, "", "", true, Microsoft.Office.Interop.Excel.XlPlatform.xlWindows, "\t", false, false, 0, true, 1, 0);
			Microsoft.Office.Interop.Excel.Worksheet xlWorkSheet = (Microsoft.Office.Interop.Excel.Worksheet)xlWorkBook.getWorksheets().get_Item(1);
			Microsoft.Office.Interop.Excel.Range range = xlWorkSheet.getUsedRange();
			for (int cCnt = 1; cCnt <= range.getColumns().getCount(); cCnt++)
			{
				for (int rCnt = 1; rCnt <= range.getRows().getCount(); rCnt++)
				{
					String cell_value = (String)((Microsoft.Office.Interop.Excel.Range)((range.getCells().getCharacters(rCnt, cCnt) instanceof Microsoft.Office.Interop.Excel.Range) ? range.getCells().getCharacters(rCnt, cCnt) : null)).getValue2();
					if (cmmsId.equals(cell_value) || sn.equals(cell_value))
					{
						//MessageBox.Show(rCnt.ToString());
						identity_lineno_row_map.put(identity_line_no, rCnt);
						cCnt = range.getColumns().getCount();
						rCnt = range.getRows().getCount();
					}
				}
			}
			xlWorkBook.Close(true, null, null);
			xlApp.Quit();
		}
		//MessageBox.Show("Done processing!");
	}

	private void phase_5()
	{
		HashMap<String, String> modifications_map = new HashMap<String, String>();

		Microsoft.Office.Interop.Excel.Application xlApp = new Microsoft.Office.Interop.Excel.Application();
		Microsoft.Office.Interop.Excel.Workbook xlWorkBook = xlApp.getWorkbooks().Open(CMMS_file, 0, true, 5, "", "", true, Microsoft.Office.Interop.Excel.XlPlatform.xlWindows, "\t", false, false, 0, true, 1, 0);
		Microsoft.Office.Interop.Excel.Worksheet xlWorkSheet = (Microsoft.Office.Interop.Excel.Worksheet)xlWorkBook.getWorksheets().get_Item(1);
		Microsoft.Office.Interop.Excel.Range range = xlWorkSheet.getUsedRange();

		for (String identity_lineno : identity_lineno_row_map.keySet())
		{
			int rCnt = identity_lineno_row_map.get(identity_lineno);
			for (String attribute_lineno : identity_Line_No_attributes_Map.get(identity_lineno))
			{
				if (!at_lineno_attribute_map.keySet().Contains(attribute_lineno))
				{
					continue;
				}
				String[] attribute = at_lineno_attribute_map.get(attribute_lineno);
				switch (attribute[0])
				{
					case "serves":
					{
							//column 5
							String cell_value = String.valueOf(((Microsoft.Office.Interop.Excel.Range)((range.getCells().getCharacters(rCnt, 5) instanceof Microsoft.Office.Interop.Excel.Range) ? range.getCells().getCharacters(rCnt, 5) : null)).getValue2());
							modifications_map.put(attribute_lineno, cell_value);
							break;
					}
					case "model number":
					{
							//column 8
							String cell_value = String.valueOf(((Microsoft.Office.Interop.Excel.Range)((range.getCells().getCharacters(rCnt, 8) instanceof Microsoft.Office.Interop.Excel.Range) ? range.getCells().getCharacters(rCnt, 8) : null)).getValue2());
							modifications_map.put(attribute_lineno, cell_value);
							break;
					}
					case "Warranty date":
					{
							//column 11

							double cell_value = (double)(((Microsoft.Office.Interop.Excel.Range)((range.getCells().getCharacters(rCnt, 11) instanceof Microsoft.Office.Interop.Excel.Range) ? range.getCells().getCharacters(rCnt, 11) : null)).getValue2());
							java.time.LocalDateTime dt = java.time.LocalDateTime.FromOADate(cell_value);
							modifications_map.put(attribute_lineno, dt.ToShortDateString());
							break;
					}
					case "Previous Maintenance number":
					{
							//column 13
							String cell_value = String.valueOf(((Microsoft.Office.Interop.Excel.Range)((range.getCells().getCharacters(rCnt, 13) instanceof Microsoft.Office.Interop.Excel.Range) ? range.getCells().getCharacters(rCnt, 13) : null)).getValue2());
							modifications_map.put(attribute_lineno, cell_value);
							break;
					}
					case "Previous Maintenance description":
					{
							//column 14
							String cell_value = String.valueOf(((Microsoft.Office.Interop.Excel.Range)((range.getCells().getCharacters(rCnt, 14) instanceof Microsoft.Office.Interop.Excel.Range) ? range.getCells().getCharacters(rCnt, 14) : null)).getValue2());
							modifications_map.put(attribute_lineno, cell_value);
							break;
					}
					case "Maintenance Type":
					{
							//column 15
							String cell_value = String.valueOf(((Microsoft.Office.Interop.Excel.Range)((range.getCells().getCharacters(rCnt, 15) instanceof Microsoft.Office.Interop.Excel.Range) ? range.getCells().getCharacters(rCnt, 15) : null)).getValue2());
							modifications_map.put(attribute_lineno, cell_value);
							break;
					}
					case "Maintenance cost":
					{
							//column 16
							String cell_value = String.valueOf(((Microsoft.Office.Interop.Excel.Range)((range.getCells().getCharacters(rCnt, 16) instanceof Microsoft.Office.Interop.Excel.Range) ? range.getCells().getCharacters(rCnt, 16) : null)).getValue2());
							modifications_map.put(attribute_lineno, cell_value);
							break;
					}
					case "PM Maintenance tasks":
					{
							//column 17
							String cell_value = String.valueOf(((Microsoft.Office.Interop.Excel.Range)((range.getCells().getCharacters(rCnt, 17) instanceof Microsoft.Office.Interop.Excel.Range) ? range.getCells().getCharacters(rCnt, 17) : null)).getValue2());
							modifications_map.put(attribute_lineno, cell_value);
							break;
					}
				}
			}
		}


		java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(new_ifc_file);
		ArrayList<String> fileLines = new ArrayList<String>();
		java.io.InputStreamReader reader = new java.io.InputStreamReader(ifc_file);
		String line = "";
		boolean isWritten = false;
		while ((line = reader.ReadLine()) != null)
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
					writer.write(new_line + System.lineSeparator());
					writer.flush();
					isWritten = true;
				}
			}
			if (!isWritten)
			{
				writer.write(line + System.lineSeparator());
				writer.flush();
			}
		}
		reader.close();
		writer.close();
		//MessageBox.Show("Done processing!");
	}

	private void button10_Click(Object sender, tangible.EventArgs e)
	{
		process_bems_file();
		toolStripProgressBar1.Increment(10);
		phase_1();
		toolStripProgressBar1.Increment(10);
		phase_2();
		toolStripProgressBar1.Increment(20);
		phase_3();
		toolStripProgressBar1.Increment(20);
		phase_4();
		toolStripProgressBar1.Increment(20);
		phase_5();
		toolStripProgressBar1.Increment(20);
		JOptionPane.showMessageDialog(null, "Done processing!");
	}


	/** 
	 Required designer variable.
	*/
	private System.ComponentModel.IContainer components = null;

	/** 
	 Clean up any resources being used.
	 
	 @param disposing true if managed resources should be disposed; otherwise, false.
	*/
	@Override
	protected void Dispose(boolean disposing)
	{
		if (disposing && (components != null))
		{
			components.Dispose();
		}
		super.Dispose(disposing);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Windows Form Designer generated code

	/** 
	 Required method for Designer support - do not modify
	 the contents of this method with the code editor.
	*/
	private void InitializeComponent()
	{
		this.label1 = new System.Windows.Forms.Label();
		this.label2 = new System.Windows.Forms.Label();
		this.label3 = new System.Windows.Forms.Label();
		this.textBox1 = new System.Windows.Forms.TextBox();
		this.textBox2 = new System.Windows.Forms.TextBox();
		this.textBox3 = new System.Windows.Forms.TextBox();
		this.button1 = new System.Windows.Forms.Button();
		this.button2 = new System.Windows.Forms.Button();
		this.groupBox1 = new System.Windows.Forms.GroupBox();
		this.button9 = new System.Windows.Forms.Button();
		this.button8 = new System.Windows.Forms.Button();
		this.button7 = new System.Windows.Forms.Button();
		this.button5 = new System.Windows.Forms.Button();
		this.button4 = new System.Windows.Forms.Button();
		this.CMMSBr = new System.Windows.Forms.Button();
		this.textBox4 = new System.Windows.Forms.TextBox();
		this.label5 = new System.Windows.Forms.Label();
		this.button6 = new System.Windows.Forms.Button();
		this.button3 = new System.Windows.Forms.Button();
		this.openFileDialog1 = new System.Windows.Forms.OpenFileDialog();
		this.openFileDialog2 = new System.Windows.Forms.OpenFileDialog();
		this.listBox1 = new System.Windows.Forms.ListBox();
		this.groupBox2 = new System.Windows.Forms.GroupBox();
		this.saveFileDialog1 = new System.Windows.Forms.SaveFileDialog();
		this.openFileDialog3 = new System.Windows.Forms.OpenFileDialog();
		this.button10 = new System.Windows.Forms.Button();
		this.statusStrip1 = new System.Windows.Forms.StatusStrip();
		this.toolStripProgressBar1 = new System.Windows.Forms.ToolStripProgressBar();
		this.groupBox1.SuspendLayout();
		this.groupBox2.SuspendLayout();
		this.statusStrip1.SuspendLayout();
		this.SuspendLayout();
		// 
		// label1
		// 
		this.label1.AutoSize = true;
		this.label1.Location = new System.Drawing.Point(52, 26);
		this.label1.Name = "label1";
		this.label1.Size = new System.Drawing.Size(59, 13);
		this.label1.TabIndex = 0;
		this.label1.Text = "BEMS File:";
		// 
		// label2
		// 
		this.label2.AutoSize = true;
		this.label2.Location = new System.Drawing.Point(66, 79);
		this.label2.Name = "label2";
		this.label2.Size = new System.Drawing.Size(45, 13);
		this.label2.TabIndex = 1;
		this.label2.Text = "IFC File:";
		// 
		// label3
		// 
		this.label3.AutoSize = true;
		this.label3.Location = new System.Drawing.Point(6, 102);
		this.label3.Name = "label3";
		this.label3.Size = new System.Drawing.Size(101, 13);
		this.label3.TabIndex = 2;
		this.label3.Text = "New IFC File Name:";
		// 
		// textBox1
		// 
		this.textBox1.Location = new System.Drawing.Point(113, 23);
		this.textBox1.Name = "textBox1";
		this.textBox1.Size = new System.Drawing.Size(226, 20);
		this.textBox1.TabIndex = 3;
		// 
		// textBox2
		// 
		this.textBox2.Location = new System.Drawing.Point(113, 76);
		this.textBox2.Name = "textBox2";
		this.textBox2.Size = new System.Drawing.Size(227, 20);
		this.textBox2.TabIndex = 4;
		// 
		// textBox3
		// 
		this.textBox3.Location = new System.Drawing.Point(113, 102);
		this.textBox3.Name = "textBox3";
		this.textBox3.Size = new System.Drawing.Size(227, 20);
		this.textBox3.TabIndex = 5;
		// 
		// button1
		// 
		this.button1.Location = new System.Drawing.Point(345, 21);
		this.button1.Name = "button1";
		this.button1.Size = new System.Drawing.Size(75, 23);
		this.button1.TabIndex = 6;
		this.button1.Text = "Browse";
		this.button1.UseVisualStyleBackColor = true;
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C#-style event wireups:
		this.button1.Click += new EventHandler(this.button1_Click);
		// 
		// button2
		// 
		this.button2.Location = new System.Drawing.Point(345, 74);
		this.button2.Name = "button2";
		this.button2.Size = new System.Drawing.Size(75, 23);
		this.button2.TabIndex = 7;
		this.button2.Text = "Browse";
		this.button2.UseVisualStyleBackColor = true;
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C#-style event wireups:
		this.button2.Click += new EventHandler(this.button2_Click);
		// 
		// groupBox1
		// 
		this.groupBox1.Controls.Add(this.button10);
		this.groupBox1.Controls.Add(this.button9);
		this.groupBox1.Controls.Add(this.button8);
		this.groupBox1.Controls.Add(this.button7);
		this.groupBox1.Controls.Add(this.button5);
		this.groupBox1.Controls.Add(this.button4);
		this.groupBox1.Controls.Add(this.CMMSBr);
		this.groupBox1.Controls.Add(this.textBox4);
		this.groupBox1.Controls.Add(this.label5);
		this.groupBox1.Controls.Add(this.button6);
		this.groupBox1.Controls.Add(this.button3);
		this.groupBox1.Controls.Add(this.button2);
		this.groupBox1.Controls.Add(this.button1);
		this.groupBox1.Controls.Add(this.textBox3);
		this.groupBox1.Controls.Add(this.textBox2);
		this.groupBox1.Controls.Add(this.textBox1);
		this.groupBox1.Controls.Add(this.label3);
		this.groupBox1.Controls.Add(this.label2);
		this.groupBox1.Controls.Add(this.label1);
		this.groupBox1.Location = new System.Drawing.Point(12, 12);
		this.groupBox1.Name = "groupBox1";
		this.groupBox1.Size = new System.Drawing.Size(501, 178);
		this.groupBox1.TabIndex = 8;
		this.groupBox1.TabStop = false;
		// 
		// button9
		// 
		this.button9.Location = new System.Drawing.Point(404, 132);
		this.button9.Name = "button9";
		this.button9.Size = new System.Drawing.Size(75, 34);
		this.button9.TabIndex = 19;
		this.button9.Text = "Process IFC Phase 5";
		this.button9.UseVisualStyleBackColor = true;
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C#-style event wireups:
		this.button9.Click += new EventHandler(this.button9_Click);
		// 
		// button8
		// 
		this.button8.Location = new System.Drawing.Point(323, 132);
		this.button8.Name = "button8";
		this.button8.Size = new System.Drawing.Size(75, 34);
		this.button8.TabIndex = 18;
		this.button8.Text = "Process IFC Phase 4";
		this.button8.UseVisualStyleBackColor = true;
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C#-style event wireups:
		this.button8.Click += new EventHandler(this.button8_Click);
		// 
		// button7
		// 
		this.button7.Location = new System.Drawing.Point(241, 132);
		this.button7.Name = "button7";
		this.button7.Size = new System.Drawing.Size(75, 34);
		this.button7.TabIndex = 17;
		this.button7.Text = "Process IFC Phase 3";
		this.button7.UseVisualStyleBackColor = true;
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C#-style event wireups:
		this.button7.Click += new EventHandler(this.button7_Click);
		// 
		// button5
		// 
		this.button5.Location = new System.Drawing.Point(159, 132);
		this.button5.Name = "button5";
		this.button5.Size = new System.Drawing.Size(75, 34);
		this.button5.TabIndex = 16;
		this.button5.Text = "Process IFC Phase 2";
		this.button5.UseVisualStyleBackColor = true;
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C#-style event wireups:
		this.button5.Click += new EventHandler(this.button5_Click);
		// 
		// button4
		// 
		this.button4.Location = new System.Drawing.Point(79, 132);
		this.button4.Name = "button4";
		this.button4.Size = new System.Drawing.Size(74, 34);
		this.button4.TabIndex = 15;
		this.button4.Text = "Process IFC Phase 1";
		this.button4.UseVisualStyleBackColor = true;
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C#-style event wireups:
		this.button4.Click += new EventHandler(this.button4_Click_1);
		// 
		// CMMSBr
		// 
		this.CMMSBr.Location = new System.Drawing.Point(346, 49);
		this.CMMSBr.Name = "CMMSBr";
		this.CMMSBr.Size = new System.Drawing.Size(74, 20);
		this.CMMSBr.TabIndex = 14;
		this.CMMSBr.Text = "Browse";
		this.CMMSBr.UseVisualStyleBackColor = true;
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C#-style event wireups:
		this.CMMSBr.Click += new EventHandler(this.CMMSBr_Click);
		// 
		// textBox4
		// 
		this.textBox4.Location = new System.Drawing.Point(113, 50);
		this.textBox4.Name = "textBox4";
		this.textBox4.Size = new System.Drawing.Size(226, 20);
		this.textBox4.TabIndex = 13;
		// 
		// label5
		// 
		this.label5.AutoSize = true;
		this.label5.Location = new System.Drawing.Point(50, 54);
		this.label5.Name = "label5";
		this.label5.Size = new System.Drawing.Size(61, 13);
		this.label5.TabIndex = 12;
		this.label5.Text = "CMMS File:";
		// 
		// button6
		// 
		this.button6.Location = new System.Drawing.Point(345, 100);
		this.button6.Name = "button6";
		this.button6.Size = new System.Drawing.Size(75, 23);
		this.button6.TabIndex = 11;
		this.button6.Text = "Browse";
		this.button6.UseVisualStyleBackColor = true;
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C#-style event wireups:
		this.button6.Click += new EventHandler(this.button6_Click);
		// 
		// button3
		// 
		this.button3.Location = new System.Drawing.Point(11, 132);
		this.button3.Name = "button3";
		this.button3.Size = new System.Drawing.Size(62, 34);
		this.button3.TabIndex = 8;
		this.button3.Text = "Process BEMS";
		this.button3.UseVisualStyleBackColor = true;
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C#-style event wireups:
		this.button3.Click += new EventHandler(this.button3_Click);
		// 
		// openFileDialog1
		// 
		this.openFileDialog1.Filter = "Excel files|*.xlsx";
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C#-style event wireups:
		this.openFileDialog1.FileOk += new System.ComponentModel.CancelEventHandler(this.openFileDialog1_FileOk);
		// 
		// openFileDialog2
		// 
		this.openFileDialog2.Filter = "IFC files|*.ifc";
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C#-style event wireups:
		this.openFileDialog2.FileOk += new System.ComponentModel.CancelEventHandler(this.openFileDialog2_FileOk);
		// 
		// listBox1
		// 
		this.listBox1.FormattingEnabled = true;
		this.listBox1.Location = new System.Drawing.Point(6, 19);
		this.listBox1.Name = "listBox1";
		this.listBox1.Size = new System.Drawing.Size(200, 147);
		this.listBox1.TabIndex = 9;
		// 
		// groupBox2
		// 
		this.groupBox2.Controls.Add(this.listBox1);
		this.groupBox2.Location = new System.Drawing.Point(519, 12);
		this.groupBox2.Name = "groupBox2";
		this.groupBox2.Size = new System.Drawing.Size(213, 178);
		this.groupBox2.TabIndex = 10;
		this.groupBox2.TabStop = false;
		this.groupBox2.Text = "Elements";
		// 
		// saveFileDialog1
		// 
		this.saveFileDialog1.DefaultExt = "ifc";
		this.saveFileDialog1.Filter = "IFC files|*.ifc";
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C#-style event wireups:
		this.saveFileDialog1.FileOk += new System.ComponentModel.CancelEventHandler(this.saveFileDialog1_FileOk);
		// 
		// openFileDialog3
		// 
		this.openFileDialog3.FileName = "openFileDialog3";
		this.openFileDialog3.Filter = "Excel files|*.xlsx";
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C#-style event wireups:
		this.openFileDialog3.FileOk += new System.ComponentModel.CancelEventHandler(this.openFileDialog3_FileOk);
		// 
		// button10
		// 
		this.button10.Location = new System.Drawing.Point(426, 23);
		this.button10.Name = "button10";
		this.button10.Size = new System.Drawing.Size(69, 99);
		this.button10.TabIndex = 20;
		this.button10.Text = "Process";
		this.button10.UseVisualStyleBackColor = true;
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C#-style event wireups:
		this.button10.Click += new EventHandler(this.button10_Click);
		// 
		// statusStrip1
		// 
		this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {this.toolStripProgressBar1});
		this.statusStrip1.Location = new System.Drawing.Point(0, 200);
		this.statusStrip1.Name = "statusStrip1";
		this.statusStrip1.Size = new System.Drawing.Size(763, 22);
		this.statusStrip1.TabIndex = 12;
		this.statusStrip1.Text = "statusStrip1";
		// 
		// toolStripProgressBar1
		// 
		this.toolStripProgressBar1.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
		this.toolStripProgressBar1.Name = "toolStripProgressBar1";
		this.toolStripProgressBar1.Size = new System.Drawing.Size(100, 16);
		this.toolStripProgressBar1.Style = System.Windows.Forms.ProgressBarStyle.Continuous;
		// 
		// Form1
		// 
		this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
		this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
		this.ClientSize = new System.Drawing.Size(763, 222);
		this.Controls.Add(this.statusStrip1);
		this.Controls.Add(this.groupBox2);
		this.Controls.Add(this.groupBox1);
		this.Name = "Form1";
		this.Text = "BIM Facility Management";
		this.groupBox1.ResumeLayout(false);
		this.groupBox1.PerformLayout();
		this.groupBox2.ResumeLayout(false);
		this.statusStrip1.ResumeLayout(false);
		this.statusStrip1.PerformLayout();
		this.ResumeLayout(false);
		this.PerformLayout();

	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	private System.Windows.Forms.Label label1;
	private System.Windows.Forms.Label label2;
	private System.Windows.Forms.Label label3;
	private System.Windows.Forms.TextBox textBox1;
	private System.Windows.Forms.TextBox textBox2;
	private System.Windows.Forms.TextBox textBox3;
	private System.Windows.Forms.Button button1;
	private System.Windows.Forms.Button button2;
	private System.Windows.Forms.GroupBox groupBox1;
	private System.Windows.Forms.OpenFileDialog openFileDialog1;
	private System.Windows.Forms.OpenFileDialog openFileDialog2;
	private System.Windows.Forms.Button button3;
	private System.Windows.Forms.ListBox listBox1;
	private System.Windows.Forms.GroupBox groupBox2;
	private System.Windows.Forms.Button button6;
	private System.Windows.Forms.SaveFileDialog saveFileDialog1;
	private System.Windows.Forms.Label label5;
	private System.Windows.Forms.TextBox textBox4;
	private System.Windows.Forms.Button CMMSBr;
	private System.Windows.Forms.OpenFileDialog openFileDialog3;
	private System.Windows.Forms.Button button4;
	private System.Windows.Forms.Button button5;
	private System.Windows.Forms.Button button7;
	private System.Windows.Forms.Button button8;
	private System.Windows.Forms.Button button9;
	private System.Windows.Forms.Button button10;
	private System.Windows.Forms.StatusStrip statusStrip1;
	private System.Windows.Forms.ToolStripProgressBar toolStripProgressBar1;
}
