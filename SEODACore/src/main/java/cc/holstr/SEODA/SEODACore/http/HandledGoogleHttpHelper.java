package cc.holstr.SEODA.SEODACore.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.google.api.client.auth.oauth2.Credential;

public class HandledGoogleHttpHelper extends GoogleHttpHelper{

	public HandledGoogleHttpHelper(Credential credential) {
		super(credential);
	}
	
	public InputStream post(String url, String jsonString) {
		InputStream response = null;
				try {
					response = super.post(url, jsonString);
				} catch (Exception e) {
					System.out.println("GOOGLE HTTP HELPER : POST exception " + url + "\n with json: " + jsonString);
					System.out.println("response: " + response);
					e.printStackTrace();
				}
		return response;
	}
	
	public InputStream get(String url) {
		InputStream response = null; 
				try {
					response = super.get(url);
				} catch (Exception e) {
					System.out.println("GOOGLE HTTP HELPER : GET exception " + url);
					e.printStackTrace();
				}
		return response;
	}
	
	public InputStream put(String url, String jsonString) {
		InputStream response = null;
			try {
				response = super.put(url, jsonString);
			} catch(Exception e) {
				System.out.println("GOOGLE HTTP HELPER : PUT exception " + url + "\n with json: " + jsonString);
				System.out.println("response: " + response);
				e.printStackTrace();
			}
			return response;
	}
	
	public String getAsString(String url) {
		//do get, return as a string
		String output = "no_response";
		try {
			output = IOUtils.toString(get(url), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}
	
	public String postWithStringResponse(String url,String jsonString) {
		//do post, return with a string
		String output = "no_response";
		try {
			output = IOUtils.toString(post(url,jsonString), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}
}
