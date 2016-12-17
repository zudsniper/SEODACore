package cc.holstr.SEODA.SEODACore.output.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import cc.holstr.SEODA.SEODACore.apiget.GoogleGet;
import cc.holstr.SEODA.SEODACore.commandLine.Ansi;

public class HandledOutputSheet extends OutputSheet {
	
	private GoogleGet getter;
	
	private TreeMap<Date,Position> dates;
	
	private DateFormat df;
	private long updateIntervalMillis;
	
	public HandledOutputSheet(OutputSheet sheet, GoogleGet getter) {
		super(sheet);
		define(getter);
	}
	
	public HandledOutputSheet(long id, String title, int index, GoogleGet getter) {
		super(id,title,index);
		define(getter);
	}	
	
	private void define(GoogleGet getter) {
		setGetter(getter);
		df = new SimpleDateFormat("MM/dd/yyyy");
		dates = new TreeMap<Date,Position>();
	}
	
	private void loadDatesFromSheet() {
		String[][] cont = getContents();
		for(int c = 1; c<cont[0].length;c++) {
			try {
			dates.put(df.parse(cont[0][c]),new Position(0,c));
			} catch(ParseException e) {
				System.out.println(Ansi.RED+"OUTPUT : Error parsing cell " +cont[0][c]+Ansi.SANE);
			}
		}
	}
	
	public boolean update() {
		loadDatesFromSheet();
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
		return false;
	}
	
	public GoogleGet getGetter() {
		return getter;
	}

	public void setGetter(GoogleGet getter) {
		this.getter = getter;
	}
}
