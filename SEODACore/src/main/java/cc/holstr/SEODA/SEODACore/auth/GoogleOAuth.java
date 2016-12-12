package cc.holstr.SEODA.SEODACore.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.webmasters.WebmastersScopes;

public class GoogleOAuth {
	
	/*	GoogleOAuth.java
	 *  by zudsniper @ github
	 * 
	 * */
	
	private static final String APPLICATION_NAME = "SEODA";
	
	private static final java.io.File DATA_STORE_DIR =
		      Paths.get(System.getProperty("user.home"), ".store", "seoda", "auth").toFile();

	private static FileDataStoreFactory dataStoreFactory;
	  
	private static HttpTransport httpTransport;
	  
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static GoogleClientSecrets clientSecrets;
	
	private static boolean simple = true;
	private static boolean simpleAuthorized; 
	private static GoogleOAuth simpleAuth; 
	
	private Credential credential;
	private Set<String> scopes; 
	private String accountNickname; 
	
		public GoogleOAuth() {
			define("user");
		}
		
		public GoogleOAuth(String accountNickname) {
			define(accountNickname);
		}
		
		public GoogleOAuth(String accountNickname, Set<String> scopes) {
			define(accountNickname, scopes);
		}
	
		public void define(String accountNickname) {
			Set<String> scopes = Collections.singleton(AnalyticsScopes.ANALYTICS_READONLY 
		    		+ " " +WebmastersScopes.WEBMASTERS_READONLY 
		    		+ " " + SheetsScopes.SPREADSHEETS
		    		+ " " + DriveScopes.DRIVE
		    		);
			define(accountNickname, scopes);
		}
		
		public void define(String accountNickname, Set<String> scopes) {
			setAccountNickname(accountNickname);
			setScopes(scopes);
		}
		
		private Credential authorize() throws Exception {
		  
		  try {
			  dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
			  httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		  } catch(IOException e) {
			  e.printStackTrace();
		  } catch(Throwable x) {
			  x.printStackTrace();
		  }
		  
		  // load client secrets  
	    clientSecrets = GoogleClientSecrets.load(
	        JSON_FACTORY, new InputStreamReader(
	            GoogleOAuth.class.getResourceAsStream("client_secrets.json")));
	    
	    
	    // set up authorization code flow
	    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
	        httpTransport, JSON_FACTORY, clientSecrets,
	        getScopes()).setDataStoreFactory(
	        dataStoreFactory).setApprovalPrompt("force").build();
	    // authorize 
	    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(getAccountNickname());
	  }
	  public void authorizeCredential() throws Exception{
		  credential = authorize();
	  }
	  
	  public void authorizeCredential(String accountNickname,Set<String> scopes) throws Exception { 
		  setAccountNickname(accountNickname);
		  setScopes(scopes);
		  credential = authorize();
	  }
	  
	  public static void simpleInit() throws Exception {
		  simpleAuth = new GoogleOAuth();
		  simpleAuth.authorizeCredential();
	  }
	  
	public Set<String> getScopes() {
		return scopes;
	}

	public void setScopes(Set<String> scopes) {
		this.scopes = scopes;
	}

	public String getAccountNickname() {
		return accountNickname;
	}

	public void setAccountNickname(String accountNickname) {
		this.accountNickname = accountNickname;
	}

	public boolean isCredentialInitialized() {
		return getCredential()!=null; 
	}

	public Credential getCredential() {
		return credential;
	}

	
	public static HttpTransport getHttpTransport() {
		return httpTransport;
	}

	public static String getApplicationName() {
		return APPLICATION_NAME;
	}

	public static JsonFactory getJsonFactory() {
		return JSON_FACTORY;
	}
	
	public static java.io.File getDataStoreDir() {
		return DATA_STORE_DIR;
	}

	//simple mode getters & setters
	
	public static boolean isSimple() {
		return simple;
	}

	public static void setSimple(boolean simple) {
		GoogleOAuth.simple = simple;
	}

	public static boolean isSimpleAuthorized() {
		return simpleAuthorized;
	}

	public static void setSimpleAuthorized(boolean simpleAuthorized) {
		GoogleOAuth.simpleAuthorized = simpleAuthorized;
	}
	
	public static GoogleOAuth getSimpleAuth() {
		return simpleAuth;
	}
	  
}