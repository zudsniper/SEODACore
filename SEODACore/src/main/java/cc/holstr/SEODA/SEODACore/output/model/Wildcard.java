package cc.holstr.SEODA.SEODACore.output.model;

public class Wildcard extends Position{
	private String title; 
	private String description; 
	
	private String precedingConstant; 
	private String followingConstant;
	
	public Wildcard(String title, String description) {
		super(0,0);
		setTitle(title);
		setDescription(description);
	}
	
	public Wildcard(String title, String description, int row, int column) {
		super(row, column);
		setTitle(title);
		setDescription(description);
	}
	
	public Wildcard(String title, String precedingConstant, String followingConstant, int row, int column) {
		super(row,column);
		setTitle(title);
		setPrecedingConstant(precedingConstant);
		setFollowingConstant(followingConstant);
	}
	
	public Wildcard(String title, int row, int column) {
		super(row,column);
		setTitle(title);
	}
	
	public Wildcard(int row, int column) {
		super(row,column);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrecedingConstant() {
		return precedingConstant;
	}

	public void setPrecedingConstant(String precedingConstant) {
		this.precedingConstant = precedingConstant;
	}

	public String getFollowingConstant() {
		return followingConstant;
	}

	public void setFollowingConstant(String followingConstant) {
		this.followingConstant = followingConstant;
	}
}
