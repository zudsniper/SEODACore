package cc.holstr.SEODA.SEODACore.apiget.gsc;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.google.api.services.webmasters.Webmasters;

import cc.holstr.SEODA.SEODACore.apiget.GoogleGet;
import cc.holstr.SEODA.SEODACore.apiget.gsc.model.GSCJSONModel;
import cc.holstr.SEODA.SEODACore.apiget.gsc.model.GSCJSONResponseModel;
import cc.holstr.SEODA.SEODACore.apiget.model.QueryMap;
import cc.holstr.SEODA.SEODACore.auth.GoogleOAuth;
import cc.holstr.SEODA.SEODACore.http.HandledGoogleHttpHelper;
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
	  	protected HandledGoogleHttpHelper initHelper() {
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
		  return new HandledGoogleHttpHelper(gscAuth.getCredential());
	  }
	  
	  @Override
		protected QueryMap<GSCQuery> getValidQueries() {
			QueryMap<GSCQuery> temp = new QueryMap<GSCQuery>();
			temp.put(new AllQueries());
			temp.put(new DesktopQuery());
			//TODO: more
			return temp;
		}

	@Override
	public String[][] getAll(String[][] start, TreeMap<Date, Position> map) {
		int usageLimitCount = 0; 
		int position = 0; 
		Date now = new Date();
		List<Date> dts = new ArrayList<Date>(map.keySet());
		while(position < dts.size()) {
			Date d = dts.get(position);
			long diff = TimeUnit.MILLISECONDS.toSeconds((now.getTime()-d.getTime()));
			//if less than 90 days ago
			System.out.println("diff is " + diff);
			if(diff < 7776000 && d.before(now)) {
				//if fired 5 times in succession
				if(usageLimitCount>=5) {
					try {
						System.out.println("Sleeping... (too many requests)");
						Thread.sleep(1000);
						usageLimitCount=0;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					String[] temp = get(d);
					if(temp!=null) {
					start = ZMisc.mergeColumn(start, temp, 1, map.get(d).getColumn());
					}
					position++; 
					usageLimitCount++;
				}	
			} else {
				position++;
			}
		}
		return start;
	}
	
	@Override
	public String[] get(Date d) {
		out = new ArrayList<String>();
		String name = "All Queries";
		for(String item : template) {
			GSCQuery query = (GSCQuery)validQueries.get(item);
			if(query!=null) {
				System.out.println("last QUERY NAME : "+name);
				System.out.println("new QUERY NAME : "+query.getName());
				if(!name.equals(query.getName())) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					name = query.getName();
				}
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
	
	private class AllQueries extends GSCQuery {
		public AllQueries() {
			super("All Queries");
		}
		@Override
		public void get(Date d) {
			out.add("");
			
			model.setStartDate(queryFormat.format(getWeekDayBefore(d)));
			model.setEndDate(queryFormat.format(d));
			
			System.out.println(gson.toJson(model));
			
			InputStream in = httpHelper.post(url, gson.toJson(model));
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
	private class DesktopQuery extends GSCQuery {
		public DesktopQuery() {
			super("Desktop Device");
		}
		@Override
		public void get(Date d) {
			out.add("");
			
			model = GSCJSONModel.build(
						queryFormat.format(getWeekDayBefore(d)),
						queryFormat.format(d), 
						"and", 
						"device", 
						"DESKTOP"
					);
			
			System.out.println(gson.toJson(model));
			
			InputStream in = httpHelper.post(url, gson.toJson(model));
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
}