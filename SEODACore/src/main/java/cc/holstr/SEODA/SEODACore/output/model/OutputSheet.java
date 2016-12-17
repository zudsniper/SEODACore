package cc.holstr.SEODA.SEODACore.output.model;

import javax.json.JsonArray;
import javax.json.JsonObject;

import cc.holstr.util.ZCheckable;
import cc.holstr.util.ZMisc;

public class OutputSheet implements ZCheckable<OutputSheet>{
	private long id; 
	private long updateTime; 
	private String title; 
	private int index; 
	private int row,col;
	private String[][] contents; 
	
	public OutputSheet() {
		define(0,null,0,0,0);
	}
	
	public OutputSheet(long id, String title, int index, int row, int col) {
		define(id,title,index,row,col);
	}
	
	public OutputSheet(long id, String title, int index) {
		define(id,title,index,0,0);
	}
	
	public OutputSheet(OutputSheet s) {
		define(s.getId(),s.getTitle(),s.getIndex(),s.getRow(),s.getCol());
		setUpdateTime(s.getUpdateTime());
		setContents(s.getContents());
	}
	
	public void define(long id, String title, int index, int row, int col){
		setId(id);
		setTitle(title);
		setIndex(index);
		setRow(row);
		setCol(col);
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	
	public void setDimensions(int row, int col) {
		setRow(row);
		setCol(col);
	}
	
	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public String[][] getContents() {
		return contents;
	}

	public boolean setContents(String[][] contents) {
		//set the contents of outputsheet object
		this.contents = contents;
		return true; 
	}
	
	public boolean setContents(String[][] contents, int row, int col) {
		//convenience method 
		boolean success = false;
		setRow(row);
		setCol(col);
		success = setContents(contents);
		return success;
	}
	
	public boolean appendToContents(String[] array,String appType, int offset, int colOrRow) {
		boolean success = false;
		if(appType.equals("row")) {
			setContents(ZMisc.mergeRow(getContents(), array, offset, colOrRow));
			setDimensions(getContents().length,ZMisc.getLongestRow(getContents()));
			success = true;
		} else if(appType.equals("col")) {
			setContents(ZMisc.mergeColumn(getContents(), array, offset, colOrRow));
			setDimensions(getContents().length,ZMisc.getLongestRow(getContents()));
			success = true;
		} else {
			return false;
		}
		return success;
	}
	
	public boolean normalizeContents() {
		boolean success = true;
		String[][] cont= getContents();
		for(int r = 0; r<cont.length; r++) {
			for(int c = 0; c<cont[r].length;c++) {
				if(cont[r][c]==null) {
					cont[r][c]="";
				}
			}
		}
		return success;
	}

	public String toString() {
		return "title: " + getTitle()
		+ "\nid: " + getId()
		+ "\nindex: " + getIndex()
		+ "\nrows: " + getRow() + " cols: " + getCol() 
		+ "\n";
	}
	
	public static OutputSheet create(JsonObject obj) {
		//create object from a jsonObject retrieved from creating a new sheet.
		OutputSheet sheet = new OutputSheet();
		JsonArray replies = obj.getJsonArray("replies");
		for (JsonObject reply : replies.getValuesAs(JsonObject.class)) {
			JsonObject properties = reply.getJsonObject("addSheet").getJsonObject("properties");
			sheet.setTitle(properties.getString("title"));
			sheet.setId(properties.getJsonNumber("sheetId").longValue());
			sheet.setIndex(properties.getInt("index"));
			JsonObject gridProperties = properties.getJsonObject("gridProperties");
			sheet.setRow(gridProperties.getInt("rowCount"));
			sheet.setCol(gridProperties.getInt("columnCount"));
		    }
		return sheet;
	}
	
	@Override
	public boolean test(OutputSheet t) {
		return (getId()==t.getId() && getTitle().equals(t.getTitle()) && getIndex()==t.getIndex());
	}
	
}
