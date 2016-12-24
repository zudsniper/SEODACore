package cc.holstr.SEODA.SEODACore.output;

import java.util.HashMap;

import cc.holstr.SEODA.SEODACore.apiget.GoogleGet;
import cc.holstr.SEODA.SEODACore.apiget.adwords.GoogleAdwords;
import cc.holstr.SEODA.SEODACore.apiget.analytics.GoogleAnalytics;
import cc.holstr.SEODA.SEODACore.apiget.gmb.GoogleMyBusiness;
import cc.holstr.SEODA.SEODACore.apiget.gsc.GoogleSearchConsole;
import cc.holstr.SEODA.SEODACore.log.SEODALogger;
import cc.holstr.SEODA.SEODACore.output.model.HandledOutputSheet;
import cc.holstr.SEODA.SEODACore.output.model.OutputSheet;

public class SpreadsheetHandler {
	public String spreadsheetID;
	
	public GoogleSheetsWriter writer;
	public HashMap<String, HandledOutputSheet> sheets;
	
	public SpreadsheetHandler(String spreadsheetID) {
		this.spreadsheetID = spreadsheetID;
		writer = new GoogleSheetsWriter(spreadsheetID);
		sheets = loadSpreadsheets();
	}
	
	public HashMap<String, HandledOutputSheet> loadSpreadsheets() {
		HashMap<String, HandledOutputSheet> sheets = new HashMap<String, HandledOutputSheet>();
		sheets.clear();
		for(OutputSheet sheet : writer.getUpdatedSheets()) {
			String title = sheet.getTitle();
			sheet.setContents(writer.readFromSheet(sheet));
			sheets.put(title, new HandledOutputSheet(sheet, getSheetGetter(sheet),writer));
		}
		SEODALogger.getLogger().info("SPREADSHEET HANDLER: Sheets hashmap loaded.");
		return sheets;
	}
	
	public void update() {
		for(HandledOutputSheet hsheet : getSheets().values()) {
			hsheet.update();
		}
	}
	
	public String getSpreadsheetID() {
		return spreadsheetID;
	}

	public void setSpreadsheetID(String spreadsheetID) {
		this.spreadsheetID = spreadsheetID;
	}

	public GoogleSheetsWriter getWriter() {
		return writer;
	}

	public HashMap<String, HandledOutputSheet> getSheets() {
		return sheets;
	}

	//TODO: remove this in favor of something at least a little scalable
	public GoogleGet getSheetGetter(OutputSheet sheet) {
		String t = sheet.getTitle();
		GoogleGet getter = null;
		if(t.equals("GSC")) {
			getter = new GoogleSearchConsole();
		} else if(t.equals("GMB")) {
			getter = new GoogleMyBusiness();
		} else if(t.equals("Adwords")) {
			getter = new GoogleAdwords();
		} else if(t.equals("Analytics")) {
			getter = new GoogleAnalytics();
		}
		return getter;
	}
	
}
