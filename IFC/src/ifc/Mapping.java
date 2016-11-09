package ifc;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Mapping {
	
	
	@SuppressWarnings("rawtypes")
	public static HashMap linkGeneration() throws IOException{
		
		FileReader fr = new FileReader(new File("D:\\test.ifc"));
		BufferedReader br = new BufferedReader(fr);

		HashMap associatesMaterialMap = new HashMap();
		HashMap buildingProxyTypeMap = new HashMap();
		HashMap propertySetMap = new HashMap();
		HashMap bemsMap = new HashMap();
		String indexOfAssociateMaterial = null;
		String line;
		LinkedList<String> ifcRelAssocaitesList = null, buildingProxyTypeList = null, propertySetList = null;
		while ((line = br.readLine()) != null) {
			// System.out.println(line);

			if (line.contains("IFCRELASSOCIATESMATERIAL") && line.contains("#195620")) {
				indexOfAssociateMaterial = line.substring(line.indexOf("#"), line.indexOf("="));
				String buildingProxyType = line.substring(line.indexOf("$,(") + 3, line.indexOf(")"));
				// System.out.println("indexOfAssociateMaterial " +
				// indexOfAssociateMaterial);
				// System.out.println("sets " + buildingProxyType);
				ifcRelAssocaitesList = new LinkedList<String>();
				for (String s : buildingProxyType.split(",")) {

					ifcRelAssocaitesList.add("#"+s.substring(1));
					associatesMaterialMap.put("#"+s.substring(1), indexOfAssociateMaterial);

				}
				for (String string : ifcRelAssocaitesList) {
					
				}
				
				
			}

			if (line.contains("IFCBUILDINGELEMENTPROXYTYPE") && line.contains("Size 2 - 12 inch Inlet")) {
				String proxyType = line.substring(line.indexOf("#"), line.indexOf("="));
				String sets = line.substring(line.indexOf("$,(") + 3, line.indexOf(")"));
				// System.out.println("proxyType " + proxyType);
				// System.out.println("sets " + sets);
				buildingProxyTypeList = new LinkedList<String>();
				for (String s : sets.split(",")) {

					buildingProxyTypeList.add("#"+s.substring(1));
					buildingProxyTypeMap.put("#"+s.substring(1),proxyType);

				}
				
				

				

			}

			if (line.contains("IFCPROPERTYSET") && line.contains("Identity Data")) {
				String propertySetIndex = line.substring(line.indexOf("#"), line.indexOf("="));
				String sets = line.substring(line.indexOf("$,(") + 3, line.indexOf(")"));
				// System.out.println("proxyType " + propertySetIndex);
				// System.out.println("sets " + sets);
				propertySetList = new LinkedList<String>();
				for (String s : sets.split(",")) {

					propertySetList.add("#"+s.substring(1));

				}

				if (propertySetList.size() > 10) {
					for (String string : propertySetList) {
						//System.out.println(string);
						propertySetMap.put(string,propertySetIndex);
					}
					
				}

			}

			if (line.contains("IFCPROPERTYSINGLEVALUE") && line.contains("BEMS ID")) {
				String BEMSIndex = line.substring(line.indexOf("#"), line.indexOf("="));
				String BEMSValue = line.substring(line.indexOf("IFCTEXT('") + 9, line.indexOf("')"));
				//System.out.println("proxyType " + BEMSIndex);
				//System.out.println("sets " + BEMSValue);

				bemsMap.put(BEMSValue, BEMSIndex);

			}

		}

		
		Set<String> mySet1 = associatesMaterialMap.keySet();
		System.out.println("IFCRELAssociatesMaterial Map");
		for (String str : mySet1) {
			System.out.println(str + ":" + associatesMaterialMap.get(str) + ", ");
		}

		Set<String> mySet2 = buildingProxyTypeMap.keySet();
		System.out.println("\nIFCBUILDINGELEMENTProxyType Map");
		for (String str1 : mySet2) {
			System.out.println(str1 + ":" + buildingProxyTypeMap.get(str1) + ", ");
		}

		Set<String> mySet3 = propertySetMap.keySet();
		System.out.println("\nIFCPropertSet(Dimensions) Map");
		for (String str1 : mySet3) {
			System.out.println(str1 + ":" + propertySetMap.get(str1) + ", ");
		}

		Set<String> mySet4 = bemsMap.keySet();
		System.out.println("\nBEMS Identity Data Map");
		for (String str1 : mySet4) {
			System.out.println(str1 + ":" + bemsMap.get(str1) + ", ");
		}
		
		
		
		Set<String> bemsMapRetrieve = bemsMap.keySet();
		
		
		HashMap linkMap =new  HashMap();
		//System.out.println("\nBEMS Identity data");
		for (String level1 : bemsMapRetrieve) {
			
				String gen=	(String) buildingProxyTypeMap.get(propertySetMap.get(bemsMap.get(level1)));
				//System.out.println(gen);
			linkMap.put(level1, gen);
		}
		
		//System.out.println(linkMap);
		
		Set<String> linkMapSet = linkMap.keySet();
		System.out.println("\nlink Map");
		for (String str1 : linkMapSet) {
			System.out.println(str1 + ":" + linkMap.get(str1) + ", ");
		}

		br.close();
		
		
		
		
		return linkMap;
		
	}

	
	
}
