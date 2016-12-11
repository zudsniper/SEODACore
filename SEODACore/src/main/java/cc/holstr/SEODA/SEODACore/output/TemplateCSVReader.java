package cc.holstr.SEODA.SEODACore.output;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

import cc.holstr.SEODA.SEODACore.properties.Unpacker;

public class TemplateCSVReader {
	
	public static ArrayList<String[][]> getLayouts(String[] requiredSheets) throws IOException {
		ArrayList<String[][]> data = new ArrayList<String[][]>();
		String[][] dataArr;
		List<String[]> list;
		CSVReader csvReader;
		File f; 
		for(int i =0; i<requiredSheets.length;i++) {
		f = Paths.get(Unpacker.getStorePath().toString(),"layouts",requiredSheets[i]+".csv").toFile();
		csvReader = new CSVReader(new FileReader(f));
		list = csvReader.readAll();
		// Convert to 2D array
		dataArr = new String[list.size()][];
		dataArr = list.toArray(dataArr);
		data.add(dataArr);
		}
		return data;
	}
	
	public static String[][] getLayout(String requiredSheet) throws IOException {
		String[][] dataArr;
		List<String[]> list;
		CSVReader csvReader;
		File f; 
		f = Paths.get(Unpacker.getStorePath().toString(),"layouts",requiredSheet+".csv").toFile();
		csvReader = new CSVReader(new FileReader(f));
		list = csvReader.readAll();
		// Convert to 2D array
		dataArr = new String[list.size()][];
		dataArr = list.toArray(dataArr);
		csvReader.close();
		return dataArr;
	}
}
