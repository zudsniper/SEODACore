package cc.holstr.SEODA.SEODACore.output;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

import com.google.api.client.auth.oauth2.Credential;

import cc.holstr.SEODA.SEODACore.auth.GoogleOAuth;
import cc.holstr.SEODA.SEODACore.http.HandledGoogleHttpHelper;
import cc.holstr.SEODA.SEODACore.log.SEODALogger;
import cc.holstr.SEODA.SEODACore.output.model.OutputSheet;
import cc.holstr.SEODA.SEODACore.output.model.json.GoogleJsonGenerator;
import cc.holstr.SEODA.SEODACore.properties.Properties;
import cc.holstr.util.ZMisc;

public class GoogleSheetsWriter {
	/*
	 * GoogleSheetsWriter.java by zudsniper @ github
	 * 
	 */

	// for complex auth (WIP)
	public static GoogleOAuth sheetsAuth;

	// spreadsheet id
	private String currentSpreadsheet;
	private Credential credential;
	private HandledGoogleHttpHelper httpHelper;

	// year as string ex. "2016" or "current";
	public GoogleSheetsWriter(String year) {
		if (year.equals("current"))
			year = Calendar.getInstance().get(Calendar.YEAR) + "";
		init(year);
	}

	private void init(String year) {
		currentSpreadsheet = (String) Properties.getConfig().getProperty("sheet." + year);
		if (GoogleOAuth.isSimple()) {
			if (GoogleOAuth.isSimpleAuthorized()) {
				credential = GoogleOAuth.getSimpleAuth().getCredential();
			}
		} else {
			if (sheetsAuth.isCredentialInitialized()) {
				credential = sheetsAuth.getCredential();
			}
		}
		httpHelper = new HandledGoogleHttpHelper(credential);
	}

	public ArrayList<OutputSheet> getUpdatedSheets() {
		// get sheet info from currentSpreadsheet
		long getTime;
		ArrayList<OutputSheet> sheets = new ArrayList<OutputSheet>();
		InputStream response = httpHelper.get(
				"https://sheets.googleapis.com/v4/spreadsheets/" + currentSpreadsheet + "?&fields=sheets.properties");
		getTime = System.currentTimeMillis();
		JsonReader reader = Json.createReader(response);
		JsonObject obj = reader.readObject();
		JsonArray sheetsArray = obj.getJsonArray("sheets");
		for (JsonObject sheet : sheetsArray.getValuesAs(JsonObject.class)) {
			JsonObject prop = sheet.getJsonObject("properties");
			OutputSheet temp = new OutputSheet(prop.getJsonNumber("sheetId").longValue(), prop.getString("title"),
					prop.getInt("index"));
			JsonObject gridProp = prop.getJsonObject("gridProperties");
			temp.setDimensions(gridProp.getInt("rowCount"), gridProp.getInt("columnCount"));
			temp.setUpdateTime(getTime);
			sheets.add(temp);
		}
		SEODALogger.getLogger().info("GOOGLE SHEETS : Sheets objects received.");
		return sheets;
	}

	public OutputSheet makeSheet(String title, int rows, int cols) {
		// create a new sheet in currentSpreadsheet, and return OutputSheet
		// object with its info
		JsonObject newSheet = GoogleJsonGenerator.getNewSheetObject(title, rows, cols);
		InputStream response = httpHelper.post(
				"https://sheets.googleapis.com/v4/spreadsheets/" + currentSpreadsheet + ":batchUpdate",
				newSheet.toString());
		if (response != null) {
			SEODALogger.getLogger().info("GOOGLE SHEETS SUCCESSFUL : new sheet called " + title + " added.");
		}
		return OutputSheet.create(Json.createReader(response).readObject());
	}

	public void deleteSheet(OutputSheet sheet) {
		// delete a sheet from currentSpreadsheet, and return OutputSheet object
		// with its info
		JsonObject deleteSheet = GoogleJsonGenerator.getDeleteSheetObject(sheet.getId());
		InputStream response = httpHelper.post(
				"https://sheets.googleapis.com/v4/spreadsheets/" + currentSpreadsheet + ":batchUpdate",
				deleteSheet.toString());
		if (response != null) {
			SEODALogger.getLogger().info("GOOGLE SHEETS SUCCESSFUL : sheet called " + sheet.getTitle() + " deleted.");
		}
	}

	public void clearSheet(OutputSheet sheet) {
		String url = "https://sheets.googleapis.com/v4/spreadsheets/" + currentSpreadsheet + ":batchUpdate";
		String jsonString = GoogleJsonGenerator.getClearSheetObject(sheet.getId()).toString();
		InputStream response = httpHelper.post(url, jsonString);
		if (response != null) {
			SEODALogger.getLogger().info("GOOGLE SHEETS SUCCESSFUL : Sheet " + sheet.getTitle() + " cleared.");
		}
	}

	public void writeRange(String sheetTitle, String range, String valueInputOption, String[][] writeVals) {
		// set a specified range from currentSpreadsheet to provided string
		// matrix.
		/*
		 * example usage: writeRange("Sheet1","A1:D4","USER_ENTERED",{
		 * {"hello","world"}, {"meme","test"} });
		 */
		int terms = 0;
		String url = "https://sheets.googleapis.com/v4/spreadsheets/" + currentSpreadsheet + "/values/" + sheetTitle
				+ "!" + range + "?valueInputOption=" + valueInputOption;
		JsonArrayBuilder values = Json.createArrayBuilder();
		for (int r = 0; r < writeVals.length; r++) {
			JsonArrayBuilder temp = Json.createArrayBuilder();
			for (int c = 0; c < writeVals[r].length; c++) {
				temp.add(writeVals[r][c]);
				if (!writeVals[r][c].equals("")) {
					terms++;
				}
			}
			values.add(temp);
		}
		JsonObjectBuilder write = Json.createObjectBuilder().add("range", sheetTitle + "!" + range)
				.add("majorDimension", "ROWS").add("values", values);
		InputStream response = httpHelper.put(url, write.build().toString());
		if (response != null) {
			SEODALogger.getLogger().info(
					"GOOGLE SHEETS SUCCESSFUL : wrote " + terms + " cells to " + sheetTitle + "!" + range + ".");
		}
	}

	public String[][] readRange(String sheetTitle, String range) {
		// retrieve a specified range from currentSpreadsheet, return string
		// matrix with its data.
		// example usage: readRange("Sheet1","A1:D4");
		boolean success = true;
		List<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		String[][] vals;
		int terms = 0, nulls = 0;
		String url = "https://sheets.googleapis.com/v4/spreadsheets/" + currentSpreadsheet + "/values/" + sheetTitle
				+ "!" + range;
		InputStream response = httpHelper.get(url);
		if (response == null) {
			success = false;
		}
		// TODO: validation maybe?
		JsonReader reader = Json.createReader(response);
		JsonObject obj = reader.readObject();
		if (obj.toString().contains("\"values\":")) {
			JsonArray sheetsArray = obj.getJsonArray("values");
			for (JsonArray rows : sheetsArray.getValuesAs(JsonArray.class)) {
				ArrayList<String> temp = new ArrayList<String>();
				for (JsonValue value : rows) {
					temp.add(parse(value.toString()));
					terms++;
				}
				data.add(temp);
			}
			// get longest row
			int biggest = 0;
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).size() > biggest) {
					biggest = data.get(i).size();
				}
			}
			vals = new String[data.size()][biggest];
			for (int r = 0; r < data.size(); r++) {
				for (int c = 0; c < data.get(r).size(); c++) {
					vals[r][c] = data.get(r).get(c);
				}
			}
			// make safe for outside use
			for (int r = 0; r < vals.length; r++) {
				for (int c = 0; c < vals[r].length; c++) {
					if (vals[r][c] == null) {
						vals[r][c] = new String();
					}
				}
			}
		} else {
			vals = new String[1][1];
		}
		if (success) {
			SEODALogger.getLogger().info(
					"GOOGLE SHEETS SUCCESSFUL : read " + terms + " cells from " + sheetTitle + "!" + range + ".");
		}
		return vals;
	}

	public OutputSheet writeToSheet(OutputSheet sheet) {
		String range = getRangeFromDimensions(sheet.getRow(), sheet.getCol());
		writeRange(sheet.getTitle(), range, "USER_ENTERED", sheet.getContents());
		sheet.setContents(readRange(sheet.getTitle(), range));
		return sheet;
	}

	public String[][] readFromSheet(OutputSheet sheet) {
		String range = getRangeFromDimensions(sheet.getRow(), sheet.getCol());
		String[][] contents = readRange(sheet.getTitle(), range);
		return contents;
	}

	public String getRangeFromDimensions(int row, int col) {
		String range = "A1:";
		range = range + ZMisc.getAlphabetValue(col) + row;
		return range;
	}

	private static String parse(String value) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			if (!(value.charAt(i) == '"' || value.charAt(i) == '\\')) {
				str.append(value.charAt(i));
			} else if (value.charAt(i) == '"') {
				if (i - 1 >= 0) {
					if (value.charAt(i - 1) == '\\') {
						str.append(value.charAt(i));
					}
				}
			}
		}
		return str.toString();
	}

	public String getcurrentSpreadsheet() {
		return currentSpreadsheet;
	}
}
