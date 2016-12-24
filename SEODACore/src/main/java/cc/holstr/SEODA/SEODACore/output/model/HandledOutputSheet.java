package cc.holstr.SEODA.SEODACore.output.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import cc.holstr.SEODA.SEODACore.apiget.GoogleGet;
import cc.holstr.SEODA.SEODACore.commandLine.Ansi;
import cc.holstr.SEODA.SEODACore.output.GoogleSheetsWriter;
import cc.holstr.util.ZMisc;

public class HandledOutputSheet extends OutputSheet {
	
	private GoogleGet getter;
	private GoogleSheetsWriter writer; 
	
	private TreeMap<Date,Position> dates;
	
	private DateFormat df;
	private long updateIntervalMillis;
	
	public HandledOutputSheet(OutputSheet sheet, GoogleGet getter, GoogleSheetsWriter writer) {
		super(sheet);
		define(getter, writer);
	}
	
	private void define(GoogleGet getter, GoogleSheetsWriter writer) {
		setGetter(getter);
		setWriter(writer);
		df = new SimpleDateFormat("MM/dd/yyyy");
		dates = loadDatesFromSheet();
	}
	
	public boolean update() {
		readContents();
		if(getContents()!=null) {
			if(getter!=null) {
			String[][] get = getter.getAll(getContents(), dates);
			if(get!=null) {
				setContents(get);
			}
			return true;
			}
			return false;
		}
		writeContents();
		return false;
	}
	
	public void readContents() {
		setContents(writer.readRange(getTitle(), getRangeString()));
	}
	
	public void writeContents() {
		writer.writeRange(getTitle(), getRangeString(), "USER_ENTERED", getContents());
	}
	
	private TreeMap<Date, Position> loadDatesFromSheet() {
		TreeMap<Date, Position> dates = new TreeMap<Date, Position>();
		String[][] cont = getContents();
		for(int c = 1; c<cont[0].length;c++) {
			try {
			dates.put(df.parse(cont[0][c]),new Position(0,c));
			} catch(ParseException e) {
				System.out.println(Ansi.RED+"OUTPUT : Error parsing cell " +cont[0][c]+Ansi.SANE);
			}
		}
		return dates;
	}
	
	//helper methods
	public String getRangeString() {
		return getRangeFromDimensions(getRow(), getCol());
	}
	
	public String getRangeFromDimensions(int row, int col) {
		String range = "A1:";
		range = range + ZMisc.getAlphabetValue(col) + row;
		return range;
	}
	
	public GoogleGet getGetter() {
		return getter;
	}

	public void setGetter(GoogleGet getter) {
		this.getter = getter;
	}

	public GoogleSheetsWriter getWriter() {
		return writer;
	}

	public void setWriter(GoogleSheetsWriter writer) {
		this.writer = writer;
	}
}
