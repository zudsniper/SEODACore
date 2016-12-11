package cc.holstr.SEODA.SEODACore.apiget.adwords;

import java.util.Date;
import java.util.TreeMap;

import cc.holstr.SEODA.SEODACore.apiget.GoogleGet;
import cc.holstr.SEODA.SEODACore.apiget.model.QueryMap;
import cc.holstr.SEODA.SEODACore.http.HandledGoogleHttpHelper;
import cc.holstr.SEODA.SEODACore.output.model.Position;

public class GoogleAdwords extends GoogleGet {
	
	public GoogleAdwords() {
		super("Adwords");
		// TODO Auto-generated constructor stub
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
