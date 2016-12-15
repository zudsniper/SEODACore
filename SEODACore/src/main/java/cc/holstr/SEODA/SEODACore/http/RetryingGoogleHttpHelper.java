package cc.holstr.SEODA.SEODACore.http;

import java.io.InputStream;

import com.google.api.client.auth.oauth2.Credential;

public class RetryingGoogleHttpHelper extends GoogleHttpHelper {

	private String retryOn;
	private long retryMS;
	
	
	public RetryingGoogleHttpHelper(Credential credential, long retryMS, String retryOn) {
		super(credential);
		setWait(retryMS);
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
							retryWait();
							return post(url, jsonString);
						}
					} else {
					System.out.println("EXCEPTION Retrying...");
					retryWait();
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
							retryWait();
							return get(url);
						}
					} else {
					System.out.println("EXCEPTION Retrying...");
					retryWait();
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
						retryWait();
						return put(url, jsonString);
					}
				} else {
				System.out.println("EXCEPTION Retrying...");
				retryWait();
				return put(url, jsonString);
				}
			}
			return response;
	}
	
	private void retryWait() {
		if(getWait()>=0) {
			try {
				Thread.sleep(getWait());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getRetryOn() {
		return retryOn;
	}

	public void setRetryOn(String retryOn) {
		this.retryOn = retryOn;
	}

	public long getWait() {
		return retryMS;
	}

	public void setWait(long retryMS) {
		this.retryMS = retryMS;
	}	
	
}
