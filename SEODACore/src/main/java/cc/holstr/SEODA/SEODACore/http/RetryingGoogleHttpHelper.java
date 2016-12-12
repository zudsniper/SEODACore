package cc.holstr.SEODA.SEODACore.http;

import java.io.InputStream;

import com.google.api.client.auth.oauth2.Credential;

public class RetryingGoogleHttpHelper extends GoogleHttpHelper {

	private String retryOn;
	
	public RetryingGoogleHttpHelper(Credential credential, String retryOn) {
		super(credential);
		setRetryOn(retryOn);
	}
	
	public InputStream post(String url, String jsonString) {
		InputStream response = null;
				try {
					response = super.post(url, jsonString);
				} catch (Exception e) {
					if(retryOn!=null) {
						if(e.getMessage().contains(retryOn)) {
							System.out.println("EXCEPTION code "+retryOn+" Retrying...");
							return post(url, jsonString);
						}
					} else {
					System.out.println("EXCEPTION Retrying...");
					return post(url, jsonString);
					}
				}
		return response;
	}
	
	public InputStream get(String url) {
		InputStream response = null; 
				try {
					response = super.get(url);
				} catch (Exception e) {
					if(retryOn!=null) {
						if(e.getMessage().contains(retryOn)) {
							System.out.println("EXCEPTION code "+retryOn+" Retrying...");
							return get(url);
						}
					} else {
					System.out.println("EXCEPTION Retrying...");
					return get(url);
					}
				}
		return response;
	}
	
	public InputStream put(String url, String jsonString) {
		InputStream response = null;
			try {
				response = super.put(url, jsonString);
			} catch(Exception e) {
				if(retryOn!=null) {
					if(e.getMessage().contains(retryOn)) {
						System.out.println("EXCEPTION code "+retryOn+" Retrying...");
						return put(url, jsonString);
					}
				} else {
				System.out.println("EXCEPTION Retrying...");
				return put(url, jsonString);
				}
			}
			return response;
	}

	public String getRetryOn() {
		return retryOn;
	}

	public void setRetryOn(String retryOn) {
		this.retryOn = retryOn;
	}	
	
}
