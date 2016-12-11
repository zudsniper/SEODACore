package cc.holstr.SEODA.SEODACore.output.model;

import java.io.IOException;
import java.util.HashMap;

import cc.holstr.SEODA.SEODACore.output.TemplateCSVReader;
import cc.holstr.SEODA.SEODACore.properties.Properties;
import cc.holstr.util.RegExp;

public class TemplateSheet {
	private String title; 
	
	private String[][] layout; 
	private HashMap<Position, Wildcard> wildcards;
	
	public TemplateSheet(String title) {
	setTitle(title);
	load();
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String[][] getLayout() {
		return layout;
	}
	
	public int wildcardSize() {
		return wildcards.size();
	}
	
	public String[] getRequiredWildcards() {
		return (String[])wildcards.values().toArray();
	}
	
	public HashMap<Position, Wildcard> getWildcards() {
		return wildcards;
	}

	public boolean doesWildcardExist(String key) {
		return wildcards.containsKey(key);
	}
	
	public void load() {
		 layout = getLayoutFromCSV();
		 wildcards = loadWildcards();
		 loadWildsFromCFG();
	}
	
	public void loadWildsFromCFG() {
		//TODO finish
		for(Wildcard c : wildcards.values()) {
				String propName = getTitle() + "." + c.getTitle();
				if(Properties.getConfig().containsKey(propName)) {
					setWildcard(c,Properties.getConfig().getString(propName));
				}
		}
	}
	
	private void setWildcard(Wildcard c, String val) {
		layout[c.getRow()][c.getColumn()] = c.getPrecedingConstant() + val + c.getFollowingConstant(); 
	}
	
	private String[][] getLayoutFromCSV() {
		String[][] layout = null;
		//main load, without wilds
		try {
			layout = TemplateCSVReader.getLayout(getTitle());
		} catch (IOException e) {
			System.out.println("TEMPLATE LOAD : Error reading layout csv for " + getTitle());
		}
		return layout; 
		
	}
	
	private HashMap<Position, Wildcard> loadWildcards() {
		HashMap<Position, Wildcard> cards = new HashMap<Position, Wildcard>();
		String[][] layout = getLayout();
		for(int r = 0; r<layout.length;r++) {
			for(int c = 0; c<layout[r].length;c++) {
				String wildcard = RegExp.find(layout[r][c], "((.+)|)((%w).+(%))((.+)|)");
				if(wildcard!=null) {
					String precedingConstant = wildcard.substring(0,wildcard.indexOf("%w"));
					String newTitle = wildcard.substring(wildcard.indexOf("%w")+2, wildcard.lastIndexOf("%"));
					String followingConstant = wildcard.substring(wildcard.lastIndexOf("%")+1,wildcard.length());
					cards.put(new Position(r,c),new Wildcard(newTitle,precedingConstant,followingConstant,r,c));
				}
			}
		}
		return cards;
	}
}
