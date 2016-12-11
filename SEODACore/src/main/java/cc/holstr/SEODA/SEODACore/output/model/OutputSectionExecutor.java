package cc.holstr.SEODA.SEODACore.output.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import cc.holstr.SEODA.SEODACore.apiget.GoogleGet;
import cc.holstr.SEODA.SEODACore.commandLine.Ansi;

public class OutputSectionExecutor{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4891681517984242653L;

	private OutputSheet sheet;
	private GoogleGet getter; 
	
	private TreeMap<Date,Position> dates;
	
	private DateFormat df;
	private long updateIntervalMillis;
	
	public OutputSectionExecutor(OutputSheet sheet, GoogleGet getter) {
		define(sheet,getter,5000);
	}
	
	public OutputSectionExecutor(OutputSheet sheet, GoogleGet getter, long updateIntervalMillis) {
		define(sheet,getter,updateIntervalMillis);
	}
	
	public void define(OutputSheet sheet, GoogleGet getter, long updateIntervalMillis) {
		this.sheet = sheet;
		this.getter = getter;
		this.updateIntervalMillis = updateIntervalMillis;
		df = new SimpleDateFormat("MM/dd/yyyy");
		dates = new TreeMap<Date,Position>();
	}
	
	private void loadDatesFromSheet() {
		String[][] cont = sheet.getContents();
		for(int c = 1; c<cont[0].length;c++) {
			try {
			dates.put(df.parse(cont[0][c]),new Position(0,c));
			} catch(ParseException e) {
				System.out.println(Ansi.RED+"OUTPUT : Error parsing cell " +cont[0][c]+Ansi.SANE);
			}
		}
	}
	
	public OutputSheet writeAll() {
		loadDatesFromSheet();
		sheet.setContents(getter.getAll(sheet.getContents(), dates));
		return sheet;
	}
	
	public OutputSheet getSheet() {
		return sheet;
	}

	public void setSheet(OutputSheet sheet) {
		this.sheet = sheet;
	}
	
	public long getUpdateIntervalMillis() {
		return updateIntervalMillis;
	}

	public void setUpdateIntervalMillis(long updateIntervalMillis) {
		this.updateIntervalMillis = updateIntervalMillis;
	}
}
