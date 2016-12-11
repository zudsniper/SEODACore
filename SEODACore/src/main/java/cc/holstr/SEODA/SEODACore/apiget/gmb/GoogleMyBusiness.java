package cc.holstr.SEODA.SEODACore.apiget.gmb;

import java.util.Date;
import java.util.TreeMap;

import cc.holstr.SEODA.SEODACore.apiget.GoogleGet;
import cc.holstr.SEODA.SEODACore.apiget.model.QueryMap;
import cc.holstr.SEODA.SEODACore.http.HandledGoogleHttpHelper;
import cc.holstr.SEODA.SEODACore.output.model.Position;

public class GoogleMyBusiness extends GoogleGet{

	public GoogleMyBusiness() {
		super("GMB");
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