package cc.holstr.SEODA.SEODACore.apiget;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cc.holstr.SEODA.SEODACore.apiget.model.QueryMap;
import cc.holstr.SEODA.SEODACore.http.GoogleHttpHelper;
import cc.holstr.SEODA.SEODACore.output.TemplateCSVReader;
import cc.holstr.SEODA.SEODACore.output.model.Position;

public abstract class GoogleGet {
	protected final static DateFormat queryFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	protected Date today = new Date();
	
	protected Gson gson;
	protected String url;
	
	protected String templateName;
	protected String[] template;
	protected QueryMap<? extends APIQuery> validQueries;
	
	protected GoogleHttpHelper httpHelper;
	
	public GoogleGet(String templateName) {
		this.templateName = templateName;
		url = buildUrl();
		httpHelper = initHelper();
		template = getTemplateFromName();
		validQueries = getValidQueries();
		GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
	}
	
	private String[] getTemplateFromName() {
		String[] out = null; 
		try {
			String[][] temp = TemplateCSVReader.getLayout(templateName);
			out = new String[temp.length-1];
			for(int i =1; i<temp.length;i++) {
				out[i-1]=temp[i][0];
			}
		} catch (IOException e) {
			System.out.println("GOOGLE GET : Template \""+templateName+"\" couldn't be loaded.");
			e.printStackTrace();
		}
		return out;
	}
	
	protected Date getWeekDayBefore(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.DAY_OF_WEEK, -7);
		return c.getTime();
		
	}
	
	public abstract String[][] getAll(String[][] start, TreeMap<Date,Position> map);
	
	protected abstract QueryMap<? extends APIQuery> getValidQueries();
	
	protected abstract GoogleHttpHelper initHelper();
	
	protected abstract String buildUrl();
	
	public abstract String[] get(Date d);
	
	public abstract class APIQuery{
		private String name; 
		
		public APIQuery(String name) {
			setName(name);
		}
		
		public abstract void get(Date date);
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
	}
	
}
