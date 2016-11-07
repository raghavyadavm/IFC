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

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		FileReader fr = new FileReader(new File("D:\\generated.ifc"));
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

				}
				for (String string : ifcRelAssocaitesList) {
					// System.out.println(string);
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

				}

				buildingProxyTypeMap.put(proxyType, buildingProxyTypeList);

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
					propertySetMap.put(propertySetIndex, propertySetList);
				}

			}

			if (line.contains("IFCPROPERTYSINGLEVALUE") && line.contains("BEMS ID")) {
				String BEMSIndex = line.substring(line.indexOf("#"), line.indexOf("="));
				String BEMSValue = line.substring(line.indexOf("IFCTEXT('") + 9, line.indexOf("')"));
				//System.out.println("proxyType " + BEMSIndex);
				//System.out.println("sets " + BEMSValue);

				bemsMap.put(BEMSIndex, BEMSValue);

			}

		}

		associatesMaterialMap.put(indexOfAssociateMaterial, ifcRelAssocaitesList);
		Set<String> mySet1 = associatesMaterialMap.keySet();
		System.out.println("AssociateMaterial ");
		for (String str : mySet1) {
			System.out.println(str + ":" + associatesMaterialMap.get(str) + ", ");
		}

		Set<String> mySet2 = buildingProxyTypeMap.keySet();
		System.out.println("\nProxyTypeList");
		for (String str1 : mySet2) {
			System.out.println(str1 + ":" + buildingProxyTypeMap.get(str1) + ", ");
		}

		Set<String> mySet3 = propertySetMap.keySet();
		System.out.println("\npropertySet");
		for (String str1 : mySet3) {
			System.out.println(str1 + ":" + propertySetMap.get(str1) + ", ");
		}

		Set<String> mySet4 = bemsMap.keySet();
		System.out.println("\nBEMS Identity data");
		for (String str1 : mySet4) {
			System.out.println(str1 + ":" + bemsMap.get(str1) + ", ");
		}

		br.close();

	}

}
