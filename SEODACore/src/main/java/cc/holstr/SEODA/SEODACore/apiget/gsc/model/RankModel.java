package cc.holstr.SEODA.SEODACore.apiget.gsc.model;

public class RankModel implements Comparable{
	private String key;
	private Double clicks;
	
	public RankModel(String key, Double clicks) {
		super();
		this.key = key;
		this.clicks = clicks;
	}
	
	@Override
	public int compareTo(Object o) {
		RankModel r = (RankModel)o;
		if(this.getClicks()>r.getClicks()) {
			return -1;
		} else if(this.getClicks()==r.getClicks()) {
			return 0;
		} else {
		return 1;
		}
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Double getClicks() {
		return clicks;
	}
	public void setClicks(Double clicks) {
		this.clicks = clicks;
	}
}
