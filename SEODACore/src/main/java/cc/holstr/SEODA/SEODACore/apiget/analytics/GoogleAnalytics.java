package cc.holstr.SEODA.SEODACore.apiget.analytics;

import java.util.Date;
import java.util.TreeMap;

import com.google.api.services.analytics.Analytics;

import cc.holstr.SEODA.SEODACore.apiget.GoogleGet;
import cc.holstr.SEODA.SEODACore.apiget.model.QueryMap;
import cc.holstr.SEODA.SEODACore.auth.GoogleOAuth;
import cc.holstr.SEODA.SEODACore.http.HandledGoogleHttpHelper;
import cc.holstr.SEODA.SEODACore.output.model.Position;

public class GoogleAnalytics extends GoogleGet{
	
	public static GoogleOAuth analyticsAuth; 
	
	private Analytics analytics; 
	
	public GoogleAnalytics(){
		super("Analytics");
		init();
	}
	
	  public void init(){
		//initialize analytics 
			if(GoogleOAuth.isSimple()) {
				if(GoogleOAuth.isSimpleAuthorized()) {
				    analytics = new Analytics.Builder(GoogleOAuth.getHttpTransport(), GoogleOAuth.getJsonFactory(), GoogleOAuth.getSimpleAuth().getCredential()).setApplicationName(
				    GoogleOAuth.getApplicationName()).build();
				}
			} else {
				if(analyticsAuth.isCredentialInitialized()) {
				    analytics = new Analytics.Builder(GoogleOAuth.getHttpTransport(), GoogleOAuth.getJsonFactory(), analyticsAuth.getCredential()).setApplicationName(
				    GoogleOAuth.getApplicationName()).build();
				}
			}
	  }

	@Override
	public String[] get(Date d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected QueryMap getValidQueries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected HandledGoogleHttpHelper initHelper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String buildUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[][] getAll(String[][] start, TreeMap<Date, Position> map) {
		// TODO Auto-generated method stub
		return null;
	}

}
