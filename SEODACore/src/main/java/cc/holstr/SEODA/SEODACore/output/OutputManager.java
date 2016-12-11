package cc.holstr.SEODA.SEODACore.output;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cc.holstr.SEODA.SEODACore.apiget.GoogleGet;
import cc.holstr.SEODA.SEODACore.apiget.adwords.GoogleAdwords;
import cc.holstr.SEODA.SEODACore.apiget.analytics.GoogleAnalytics;
import cc.holstr.SEODA.SEODACore.apiget.gmb.GoogleMyBusiness;
import cc.holstr.SEODA.SEODACore.apiget.gsc.GoogleSearchConsole;
import cc.holstr.SEODA.SEODACore.commandLine.Ansi;
import cc.holstr.SEODA.SEODACore.output.model.OutputSectionExecutor;
import cc.holstr.SEODA.SEODACore.output.model.OutputSheet;
import cc.holstr.SEODA.SEODACore.output.model.TemplateSheet;
import cc.holstr.SEODA.SEODACore.properties.ApplicationProperties;
import cc.holstr.util.ZMisc;

public class OutputManager {
	
	//date format 
	public final static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	//required output types
	private final String[] requiredSheets = {"GSC","GMB","Adwords","Analytics","Sheet1"};
	private HashMap<String, TemplateSheet> requiredSheetLayouts;
	
	//subclass manager objects
	private GoogleSearchConsoleManager gsc; 
	private GoogleAnalyticsManager analytics;
	private GoogleAdwordsManager adwords;
	private GoogleMyBusinessManager gmb;
	//subclass manager map
	public HashMap<String, SubManager> submanagers;
	
	//internal manager classes
	private ComplianceManager comply; 
	private DatesManager dates; 
	
	private GoogleSheetsWriter writer;
	
	//loaded sheets hashmap
	public HashMap<String, OutputSheet> sheets;
	
	//last update
	private long lastUpdateMillis; 
	
	private int year;
	
	public OutputManager(int year) {
		//build
		define(year);
	}
	
	public void define(int year) {
		this.year = year;
		writer = new GoogleSheetsWriter(year+"");
		sheets = new HashMap<String, OutputSheet>();
		requiredSheetLayouts = new HashMap<String, TemplateSheet>();
		
		comply = new ComplianceManager();
		dates = new DatesManager();
		submanagers = new HashMap<String, SubManager>();
		lastUpdateMillis = -1; 
	}
	
	public void start() {
		load();
	}
	
	public void load() {
		fillSheetHashMap();
		boolean b = createRequiredSheets();
		loadTemplates();
		//DEPRECATED: loadSheetContent();
		if(setToDefaultTemplatesIfNecessary()) {
			loadSheetContent();
		}
		//TODO: fix loadDatesIfNecessary(), then use
		loadDates();
		initSubManagers();
		lastUpdateMillis = System.currentTimeMillis();
		System.out.println(Ansi.BACKGROUND_BLUE+Ansi.WHITE+"OUTPUT MANAGER : Load finished."+Ansi.SANE);
	
	}
	
	public void fillSheetHashMap() {
		//reload sheets hashmap from spreadsheet.
		sheets.clear();
		for(OutputSheet sheet : writer.getUpdatedSheets()) {
			String title = sheet.getTitle();
			sheet.setContents(writer.loadContentFromSheet(sheet));
			sheets.put(title, sheet);
		}
		System.out.println("OUTPUT MANAGER : Sheets cache hashmap loaded.");
	}
	
	public void loadSheetContent() {
		//retrieve the content from each loaded sheet in hashmap, and apply to said sheet. 
		for(OutputSheet sheet : sheets.values()) {
			sheet.setContents(writer.loadContentFromSheet(sheet));
		}
	}
	
	public void loadTemplates() {
		boolean success = true; 
		for(String sheet : requiredSheets) {
			TemplateSheet temp; 
			temp = new TemplateSheet(sheet);
			if(ApplicationProperties.verbose) {
				System.out.println(temp.getTitle()+" template loaded.");
			}
			requiredSheetLayouts.put(sheet, temp);
			}
		if(success) 
			System.out.println("OUTPUT MANAGER : Loaded all sheet layout templates from store directory.");
		}
	
	public void loadDates() {
		lloadDates();
	}	
	
	public void loadDatesIfNecessary() {
		//WIP
		lloadDates();
	}
	
	private void lloadDates() {
		for(OutputSheet sheet : sheets.values()) {
			if(!sheet.getTitle().equals("Sheet1")) {
				if(!comply.isSheetDateLoaded(sheet)) {
					sheet.appendToContents(dates.getDates(), "ROWS", 1, 0);
					if(ApplicationProperties.verbose) {
						System.out.println("# OF ROWS: " + sheet.getContents().length);
						System.out.println("ROW 0 LENGTH: " + sheet.getContents()[0].length);
						System.out.println("rows: " + sheet.getRow());
						System.out.println("cols: " + sheet.getCol());
						}
					sheet.normalizeContents();
					if(ApplicationProperties.verbose) {
					ZMisc.printMatrix(sheet.getContents());
					}
					sheet = writer.writeToSheet(sheet);
				}
			}
		}
	}
	
	public boolean createRequiredSheets() {
		int created = 0;
		boolean changed = true;
		//create all non-existent & required sheets.
		for(String key : requiredSheets) {
			if(!sheets.containsKey(key)) {
				OutputSheet newSheet = writer.makeSheet(key,100,55);
				created++;
				sheets.put(key, newSheet);
			}
		}
		if(created==0) {
			System.out.println("OUTPUT MANAGER : All required sheets present.");
			changed = true;
		} else {
			System.out.println("OUTPUT MANAGER : " +created + " Required sheets created.");
		}
		return changed;
		
	}
	
	public void setToDefaultTemplates() {
		for(OutputSheet sheet : sheets.values()) {
			toDefault(sheet);
		}
	}
	
	public boolean setToDefaultTemplatesIfNecessary() {
		boolean changes = false; 
		for(OutputSheet sheet : sheets.values()) {
			if(!comply.isSheetTemplateCompliant(sheet)) {
				changes = true;
				toDefault(sheet);
			} 
		}
		return changes;
	}
	
	private void toDefault(OutputSheet sheet) {
		if(!ZMisc.isEmpty(sheet.getContents())) {
			writer.clearSheet(sheet);
		} 
		String[][] wildTemplate = requiredSheetLayouts.get(sheet.getTitle()).getLayout();
		sheet.setContents(wildTemplate);
		sheet = writer.writeToSheet(sheet);
	}
	
	public void initSubManagers() {
		if(gsc==null) {
			gsc = new GoogleSearchConsoleManager(sheets.get("GSC"));
		}
		if(gmb==null) {
			gmb = new GoogleMyBusinessManager(sheets.get("GMB"));
		}
		if(adwords==null) {
			adwords = new GoogleAdwordsManager(sheets.get("Adwords"));
		}
		if(analytics==null) {
			analytics = new GoogleAnalyticsManager(sheets.get("Analytics"));
		}
		submanagers.put("GSC", gsc);
		submanagers.put("GMB", gmb);
		submanagers.put("Adwords", adwords);
		submanagers.put("Analytics", analytics);
	}
	
	public boolean reset() {
		boolean success = false;
		for(OutputSheet sheet : sheets.values()) {
			if(!sheet.getTitle().equals("Sheet1"))
			writer.deleteSheet(sheet);
		}
		load();
		return success; 
	}
	
	public void debug() {
		//getGSCManager().loadAll();
		GoogleSearchConsole debugger = new GoogleSearchConsole();
		try {
			debugger.get(dateFormat.parse("10/10/2016"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public HashMap<String, TemplateSheet> getRequiredSheetLayouts() {
		return requiredSheetLayouts;
	}

	public long getLastUpdateTime() {
		return lastUpdateMillis;
	}

	public GoogleSearchConsoleManager getGSCManager() {
		return gsc; 
	}
	
	public GoogleAnalyticsManager getAnalyticsManager() {
		return analytics;
	}
	
	public GoogleAdwordsManager getAdwordsManager() {
		return adwords;
	}
	
	public GoogleMyBusinessManager getGMBManager() {
		return gmb;
	}
	
	public SubManager getManagerByString(String name) {
		return submanagers.get(name);
	}
	
	public class SubManager {
		private OutputSectionExecutor exec; 
		private OutputSheet sheet;
		
		public SubManager(OutputSheet sheet, GoogleGet getter) {
			this.sheet = sheet;
			exec = new OutputSectionExecutor(sheet,getter);
		}

		public void loadAll() {
			sheet = exec.writeAll();
			if(sheet.getContents()!=null) {
			writer.writeToSheet(sheet);
			}
		}
		
		public OutputSectionExecutor getExecutor() {
			return exec;
		}
	}
	
	private class GoogleSearchConsoleManager extends SubManager{

		public GoogleSearchConsoleManager(OutputSheet sheet) {
			super(sheet, new GoogleSearchConsole());
		}
		
	}

	private class GoogleAnalyticsManager extends SubManager{

		public GoogleAnalyticsManager(OutputSheet sheet) {
			super(sheet, new GoogleAnalytics());
		}
		
	}
	
	private class GoogleAdwordsManager extends SubManager{

		public GoogleAdwordsManager(OutputSheet sheet) {
			super(sheet, new GoogleAdwords());
		}
		
	}
	
	private class GoogleMyBusinessManager extends SubManager{

		public GoogleMyBusinessManager(OutputSheet sheet) {
			super(sheet, new GoogleMyBusiness());
		}
		
	}
	
	private class ComplianceManager {
	
		public boolean isSheetTemplateCompliant(OutputSheet sheet) {
			boolean compliant = true;
			String[][] sheetData = sheet.getContents();
			String[][] template = requiredSheetLayouts.get(sheet.getTitle()).getLayout();
			if(sheetData==null) {
				return false;
			}
			if(ApplicationProperties.verbose) {
				System.out.println("\n    SHEET " + sheet.getTitle());
				System.out.println(" --Actual Load: ");
				ZMisc.printMatrix(sheetData);
				System.out.println("\n --Template: ");
				ZMisc.printMatrix(template);
				System.out.println();
			}
			
			if(ZMisc.isEmpty(sheetData)) {
				return false;
			}
			int loadNulls = 0, templateNulls = 0;
			for(int i =0; i<sheetData.length;i++) {
				if(sheetData[i][0]!=null) {
					if(sheetData[i][0].equals("")) {
						loadNulls++;
					}
				} else {
					loadNulls++;
				}
			}
			for(int i =0; i<template.length;i++) {
				if(template[i][0]!=null) {
					if(template[i][0].equals("")) {
						templateNulls++;
					}
				} else {
					templateNulls++;
				}
			}
			if((sheetData.length-loadNulls)!=(template.length-templateNulls)) {
				compliant = false;
			}
			for(int i = 0;i<sheetData.length;i++) {
					if(!(sheetData[i][0]==null)) {
						if(i<template.length) {
							if(!(sheetData[i][0].equals(template[i][0]))) {
								compliant = false;
							}
						}
					} else {
						for(int r = i; r<sheetData.length;r++) {
							if(sheetData[r][0]!=null) {
								compliant = false;
							}
						}
					}
			}
			
			if(compliant) {
				System.out.println("OUTPUT MANAGER : " + sheet.getTitle() + " is compliant to layout template.");
			} else {
				System.out.println("OUTPUT MANAGER : " + sheet.getTitle() + " is NOT compliant to layout template.");
			}
			return compliant;
			
		}
		
		public boolean isSheetDateLoaded(OutputSheet sheet) {
			boolean compliant = true; 
			int pos = 0; 
			String[][] sheetData = sheet.getContents();
			if(sheetData==null) {
				return false;
			}
			if(ApplicationProperties.verbose) {
				System.out.println("\n    SHEET " + sheet.getTitle());
				System.out.println(" --Actual Load: ");
				ZMisc.printMatrix(sheetData);
			}
			if(ZMisc.isEmpty(sheetData)) {
				return false;
			}
			
			if(sheetData[0].length-1<dates.getDates().length) {
				System.out.println("LENGTHS NOT COMPATIBLE");
				return false;
			}
			for(int i = 1; i<sheetData[0].length;i++) {
				if(sheetData[0][i]!=dates.getDates()[pos]) {
					System.out.println("DATES NOT CORRECT");
					compliant = false;
				}
				pos++;
			}
			if(compliant) {
				System.out.println("OUTPUT MANAGER : " + sheet.getTitle() + " is compliant to correct dates.");
			} else {
				System.out.println("OUTPUT MANAGER : " + sheet.getTitle() + " is NOT compliant to correct dates.");
			}
			return compliant; 
		}
	}
	
	private class DatesManager {
		//calendar
		private Calendar calendar;
		private String[] dates; 
		
		public DatesManager() {
			calendar = Calendar.getInstance();
			dates = generateDates();
		}

		private String[] generateDates() {
			//loads 52 sundays
					DateFormat dfprint = new SimpleDateFormat("EEE dd/MM/yyyy");
					DateFormat df = dateFormat;
					boolean isSunday = false;
					List<Date> sunDates = new ArrayList<Date>();
					calendar.set(year, 0, 0);
					while(!isSunday) {
						if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
							sunDates.add(calendar.getTime());
							isSunday = true;
						} else {
							calendar.add(Calendar.DATE,1);
						}
					}
					do {
						calendar.add(Calendar.DAY_OF_YEAR, 7);
						sunDates.add(calendar.getTime());
					} while(calendar.get(Calendar.YEAR)==year);
					String[] dates = new String[sunDates.size()];
					for(int i =0; i<dates.length;i++) {
						dates[i] = df.format(sunDates.get(i));
					}
					return dates;
			}
		
		public String[] getDates() {
			return dates;
		}
	}
	
}
