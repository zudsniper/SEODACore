package cc.holstr.SEODA.SEODACore.output.model.json;

import javax.json.Json;
import javax.json.JsonObject;

public class GoogleJsonGenerator {
	
	public static JsonObject getNewSheetObject(String title, int row, int col) {
		//create jsonObject to send to sheets api
		JsonObject newSheet = Json.createObjectBuilder()
				.add("requests",Json.createArrayBuilder()
						.add(Json.createObjectBuilder()
								.add("addSheet", Json.createObjectBuilder()
										.add("properties", Json.createObjectBuilder()
												.add("title", ""+title)
												.add("gridProperties", Json.createObjectBuilder()
														.add("rowCount", row) //100 is a guess, TBD
														.add("columnCount",col) //number of weeks in a year + 1 label column
														)
												)
										)
								)
						).build();
		return newSheet;
	}
	
	public static JsonObject getDeleteSheetObject(long sheetID) {
		JsonObject deleteSheet = Json.createObjectBuilder()
				.add("requests",Json.createArrayBuilder()
						.add(Json.createObjectBuilder()
								.add("deleteSheet", Json.createObjectBuilder()
										.add("sheetId", sheetID)
										)
								)
						).build();
		return deleteSheet;
	}

	
	public static JsonObject getClearSheetObject(long sheetID) {
		JsonObject clearSheet = Json.createObjectBuilder()
				.add("requests", Json.createArrayBuilder()
						.add(Json.createObjectBuilder()
								.add("updateCells", Json.createObjectBuilder()
										.add("range", Json.createObjectBuilder()
												.add("sheetId", sheetID)
												)
										.add("fields", "userEnteredValue")
										)
								)
						).build();
				
		return clearSheet; 
	}
	
}
