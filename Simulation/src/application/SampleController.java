package application;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;

public class SampleController {

	@FXML
	TextField bemsTF;
	@FXML
	TextField cmmsTF;
	@FXML
	TextField resultsTF;
	@FXML
	TextField ifcTF;	
	@FXML
	Button processButton;
	@FXML
	Button BEMSButton;
	@FXML
	Button CMMSButton;
	@FXML
	Button ResultsButton;
	@FXML
	Button IFCButton;
	public static String bems, cmms, results, ifc;
	@SuppressWarnings("rawtypes")
	@FXML ListView alarmsList;
	@FXML ProgressBar progressBar;
	@FXML ListView flagList;

	public void bemsButtonAction(ActionEvent event) {

		FileChooser fc = new FileChooser();
		File selectedFile = fc.showOpenDialog(null);

		if (selectedFile != null) {
			bems = selectedFile.getAbsolutePath();
			bemsTF.setText(bems);

			System.out.println(bems + " is selected");
		} else {
			System.out.println("file is not valid");
		}

	}

	public void cmmsButtonAction(ActionEvent event) {

		FileChooser fc = new FileChooser();
		File selectedFile = fc.showOpenDialog(null);

		if (selectedFile != null) {
			cmms = selectedFile.getAbsolutePath();
			cmmsTF.setText(cmms);
			// bemsTF.setText(s);
			System.out.println(cmms + " is selected");
		} else {
			System.out.println("file is not valid");
		}

	}

	public void resultsButtonAction(ActionEvent event) {

		FileChooser fc = new FileChooser();
		File selectedFile = fc.showOpenDialog(null);

		if (selectedFile != null) {
			results = selectedFile.getAbsolutePath();
			resultsTF.setText(results);
			// bemsTF.setText(s);
			System.out.println(results + " is selected");
		} else {
			System.out.println("file is not valid");
		}

	}

	public void ifcButtonAction(ActionEvent event) {

		FileChooser fc = new FileChooser();
		File selectedFile = fc.showOpenDialog(null);

		if (selectedFile != null) {
			ifc = selectedFile.getAbsolutePath();
			ifcTF.setText(ifc);
			// bemsTF.setText(s);
			System.out.println(ifc + " is selected");
		} else {
			System.out.println("file is not valid");
		}

	}

	@SuppressWarnings("unchecked")
	public void processButtonAction(ActionEvent event) throws IOException {
		new Module1();
		for (String s : Module1.objectIDs) {
			alarmsList.getItems().add(s);
		}
		progressBar.setProgress(0.25F);
		new IFCGeneration();
		Set<String> flagSet = IFCGeneration.flagsMap.keySet();
		System.out.println("\nanalyticalMap");
		for (String str1 : flagSet) {
			System.out.println(str1 + ":" + IFCGeneration.flagsMap.get(str1) + ", ");
			int test = (int) IFCGeneration.flagsMap.get(str1);
			if (test > 0) {
				flagList.getItems().add(str1 + "   :   " + IFCGeneration.flagsMap.get(str1));
			}			
		}
			
		progressBar.setProgress(1F);
		
		
		
	}

	public SampleController() throws IOException {
		// new Module1();
		// new IFCGeneration();
	}
}
