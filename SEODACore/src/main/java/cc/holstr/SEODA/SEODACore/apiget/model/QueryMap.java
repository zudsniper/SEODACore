package cc.holstr.SEODA.SEODACore.apiget.model;

import java.util.HashMap;

import cc.holstr.SEODA.SEODACore.apiget.GoogleGet.APIQuery;

public class QueryMap <T extends APIQuery> extends HashMap<String, T>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3949529068097833796L;

	public APIQuery put(T value) {
		return super.put(value.getName(), value);
	}
	
}
