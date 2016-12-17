package cc.holstr.SEODA.SEODACore.apiget.gsc;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.webmasters.Webmasters;

import cc.holstr.SEODA.SEODACore.apiget.GoogleGet;
import cc.holstr.SEODA.SEODACore.apiget.gsc.model.RankModel;
import cc.holstr.SEODA.SEODACore.apiget.gsc.model.json.GSCJSONModel;
import cc.holstr.SEODA.SEODACore.apiget.gsc.model.json.GSCJSONResponseModel;
import cc.holstr.SEODA.SEODACore.apiget.model.QueryMap;
import cc.holstr.SEODA.SEODACore.auth.GoogleOAuth;
import cc.holstr.SEODA.SEODACore.http.RetryingGoogleHttpHelper;
import cc.holstr.SEODA.SEODACore.output.model.Position;
import cc.holstr.SEODA.SEODACore.properties.Properties;
import cc.holstr.util.ZMisc;
import cc.holstr.util.ZUrl;

public class GoogleSearchConsole extends GoogleGet {
	protected final static String urlStubFront = "https://www.googleapis.com/webmasters/v3/sites/";
	protected final static String urlStubBack = "/searchAnalytics/query";
	
	private List<String> out; 
	
	public static GoogleOAuth gscAuth; 
	
	private Webmasters webmasters; 
	
	public GoogleSearchConsole() {
		super("GSC");
		init();
	}
	
	  private void init() {
	   //initialize webmasters 
	  } 

	  @Override
	  	protected RetryingGoogleHttpHelper initHelper() {
		  if(GoogleOAuth.isSimple()) {
				if(GoogleOAuth.isSimpleAuthorized()) {
					gscAuth = GoogleOAuth.getSimpleAuth();
//				    webmasters = new Webmasters.Builder(GoogleOAuth.getHttpTransport(), GoogleOAuth.getJsonFactory(), GoogleOAuth.getSimpleAuth().getCredential()).setApplicationName(
//				    GoogleOAuth.getApplicationName()).build();
				}
			} else {
				if(gscAuth.isCredentialInitialized()) {
//				    webmasters = new Webmasters.Builder(GoogleOAuth.getHttpTransport(), GoogleOAuth.getJsonFactory(), gscAuth.getCredential()).setApplicationName(
//				    GoogleOAuth.getApplicationName()).build();
				}
			}
		  return new RetryingGoogleHttpHelper(gscAuth.getCredential(),200,"403");
	  }
	  
	  @Override
		protected QueryMap<GSCQuery> getValidQueries() {
			QueryMap<GSCQuery> temp = new QueryMap<GSCQuery>();
			temp.put(new AllQueries());
			temp.put(new DesktopQuery());
			temp.put(new ContainsQuery(Properties.getConfig().getString("GSC.containsTerm")));
			temp.put(new Blank("Term List",2));
			temp.put(new GSCTermRankQuery("term1",Properties.getConfig().getString("GSC.term1")));
			temp.put(new GSCTermRankQuery("term2",Properties.getConfig().getString("GSC.term2")));
			temp.put(new GSCTermRankQuery("term3",Properties.getConfig().getString("GSC.term3")));
			temp.put(new GSCRankList("TopR (Queries)","query"));
			temp.put(new GSCRankList("TopR (Pages)","page"));
			return temp;
		}

	@Override
	public String[][] getAll(String[][] start, TreeMap<Date, Position> map) {
		int position = 0; 
		Date now = new Date();
		List<Date> dts = new ArrayList<Date>(map.keySet());
		while(position < dts.size()) {
			Date d = dts.get(position);
			long diff = TimeUnit.MILLISECONDS.toSeconds((now.getTime()-d.getTime()));
			//if less than 90 days ago
			if(diff < 7776000 && d.before(now)) {
				String[] temp = get(d);
				if(temp!=null) {
				start = ZMisc.mergeColumn(start, temp, 1, map.get(d).getColumn());
				}
				position++; 
			} else {
				position++;
			}
		}
		return start;
	}
	
	@Override
	public String[] get(Date d) {
		out = new ArrayList<String>();
		for(String item : template) {
			GSCQuery query = (GSCQuery)validQueries.get(item);
			if(query!=null) {
				query.get(d);
			}
		}
		return out.toArray(new String[1]);
	}
	
	@Override
	protected String buildUrl() {
		String site = Properties.getConfig().getString("site");
		site = ZUrl.encodeURIComponent(site);
		return urlStubFront + site + urlStubBack;
	}
	
	private abstract class GSCQuery extends APIQuery {
		
		protected GSCJSONModel model; 
		
		public GSCQuery(String name) {
			super(name);
			model = new GSCJSONModel();
		}
		
		public abstract void get(Date d);
		
	}
	
	private class GSCRankList extends GSCQuery {

		private String dimensionType;
		
		public GSCRankList(String name,String dimensionType) {
			super(name);
			this.dimensionType = dimensionType;
		}

		@Override
		public void get(Date d) {
			out.add("");
			
			model = new GSCJSONModel();
			
			model.setStartDate(queryFormat.format(getWeekDayBefore(d)));
			model.setEndDate(queryFormat.format(d));
			List<String> dimensions = new ArrayList<String>();
			dimensions.add(dimensionType);
			model.setDimensions(dimensions);
			model.setRowLimit(25);
		
		InputStream in = ((RetryingGoogleHttpHelper)httpHelper).post(url, gson.toJson(model));
		Reader reader;
		try {
			reader = new InputStreamReader(in, "UTF-8");
			GSCJSONResponseModel result = gson.fromJson(reader, GSCJSONResponseModel.class);
			
			List<RankModel> ranks = new ArrayList<RankModel>();
			
			for(GSCJSONResponseModel.Row row : result.getRows()) {
				if(row!=null) {
					String key = row.getKeys().get(0);
					double clicks = row.getClicks();
					
					ranks.add(new RankModel(key,clicks));
				} 
			}
			Collections.sort(ranks);
			for(RankModel rank : ranks) {
				out.add(rank.getKey()+", "+rank.getClicks());
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		}
		
	}
	
	private class Blank extends GSCQuery {

		private int spaces;
		
		public Blank(String name,int spaces) {
			super(name);
			this.spaces = spaces;
		}
		
		@Override
		public void get(Date d) {
			for(int i = 0; i<spaces; i++) {
				out.add("");
			}
		}
		
	}
	
	private class GSCTermRankQuery extends GSCQuery {

		private String term;
		
		public GSCTermRankQuery(String name, String term) {
			super("%w"+name+"%");
			this.term = term;
		}
		
		@Override
		public void get(Date d) {
			out.add("");
			
			model = GSCJSONModel.build(
						queryFormat.format(getWeekDayBefore(d)),
						queryFormat.format(d), 
						"and", 
						"query",
						"contains",
						term
					);
			
			InputStream in = ((RetryingGoogleHttpHelper)httpHelper).post(url, gson.toJson(model));
			Reader reader;
			try {
				reader = new InputStreamReader(in, "UTF-8");
				GSCJSONResponseModel result = gson.fromJson(reader, GSCJSONResponseModel.class);
				
				if(result.getRows()!=null) {
				GSCJSONResponseModel.Row row = result.getRows().get(0);
				
				String ctrPercentage = String.format("%f%%", row.getCtr()*100);
				
				String positionRounded = String.format("%f", row.getPosition());
				
				out.add(row.getClicks().toString());
				out.add(ctrPercentage);
				out.add(positionRounded);
				} else {
					//this should never be executed, valid date checks happen in getAll()
					out.add("NDF");
					out.add("NDF");
					out.add("NDF");
				}
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private abstract class GSCPlainQuery extends GSCQuery {

		public String dimension,operator,expression;
		
		public GSCPlainQuery(String name,String dimension, String operator,
				String expression) {
			super(name);
			
			this.dimension = dimension;
			this.operator = operator;
			this.expression = expression;
		}
		
		@Override
		public void get(Date d) {
			out.add("");
			
			model = GSCJSONModel.build(
						queryFormat.format(getWeekDayBefore(d)),
						queryFormat.format(d), 
						"and", 
						dimension,
						operator,
						expression
					);
			
			InputStream in = ((RetryingGoogleHttpHelper)httpHelper).post(url, gson.toJson(model));
			Reader reader;
			try {
				reader = new InputStreamReader(in, "UTF-8");
				GSCJSONResponseModel result = gson.fromJson(reader, GSCJSONResponseModel.class);
				
				if(result.getRows()!=null) {
				GSCJSONResponseModel.Row row = result.getRows().get(0);
				
				String ctrPercentage = String.format("%f%%", row.getCtr()*100);
				
				out.add(row.getClicks().toString());
				out.add(row.getImpressions().toString());
				out.add(ctrPercentage);
				} else {
					//this should never be executed, valid date checks happen in getAll()
					out.add("NDF");
					out.add("NDF");
					out.add("NDF");
				}
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
	}
 	
	private class AllQueries extends GSCQuery {
		public AllQueries() {
			super("All Queries");
		}
		@Override
		public void get(Date d) {
			out.add("");
			
			model.setStartDate(queryFormat.format(getWeekDayBefore(d)));
			model.setEndDate(queryFormat.format(d));
			
			InputStream in = ((RetryingGoogleHttpHelper)httpHelper).post(url, gson.toJson(model));
			Reader reader;
			try {
				reader = new InputStreamReader(in, "UTF-8");
				GSCJSONResponseModel result = gson.fromJson(reader, GSCJSONResponseModel.class);
				
				if(result.getRows()!=null) {
				GSCJSONResponseModel.Row row = result.getRows().get(0);
				
				String ctrPercentage = String.format("%f%%", row.getCtr()*100);
				
				out.add(row.getClicks().toString());
				out.add(row.getImpressions().toString());
				out.add(ctrPercentage);
				} else {
					//this should never be executed, valid date checks happen in getAll()
					out.add("NDF");
					out.add("NDF");
					out.add("NDF");
				}
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
	}
	private class DesktopQuery extends GSCPlainQuery {
		public DesktopQuery() {
			super("Desktop Device","device","equals","DESKTOP");
		}
	}
	
	private class ContainsQuery extends GSCPlainQuery {
		public ContainsQuery(String term) {
			super("containing \"%wcontainsTerm%\"","query","contains",term);
		}
	}
}
