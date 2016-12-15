package cc.holstr.SEODA.SEODACore.apiget.gsc.model.json;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GSCJSONModel {

	/*
	 * generated with javaschema2pojo.com
	 * 
	 */
	
	@SerializedName("startDate")
	@Expose
	private String startDate;
	@SerializedName("endDate")
	@Expose
	private String endDate;
	@SerializedName("dimensions")
	@Expose
	private List<String> dimensions = null;
	@SerializedName("searchType")
	@Expose
	private String searchType;
	@SerializedName("dimensionFilterGroups")
	@Expose
	private List<DimensionFilterGroup> dimensionFilterGroups = null;
	@SerializedName("aggregationType")
	@Expose
	private String aggregationType;
	@SerializedName("rowLimit")
	@Expose
	private Integer rowLimit;
	@SerializedName("startRow")
	@Expose
	private Integer startRow;

	public static GSCJSONModel build(String startDate, String endDate, String groupType, String dimension, String operator, String expression) {
		GSCJSONModel model = new GSCJSONModel();
		model.setStartDate(startDate);
		model.setEndDate(endDate);
		model.buildDFGroup(groupType, dimension, operator, expression);
		return model;
	}
	
	public void buildDFGroup(String groupType, String dimension, String operator, String expression) {
		List<DimensionFilterGroup> fgroups = new ArrayList<DimensionFilterGroup>();
		DimensionFilterGroup fgroup = new DimensionFilterGroup();
		List<Filter> filters = new ArrayList<Filter>();
		Filter filter = new Filter();
		filter.setDimension(dimension);
		filter.setOperator(operator);
		filter.setExpression(expression);
		filters.add(filter);
		fgroup.setGroupType(groupType);
		fgroup.setFilters(filters);
		fgroups.add(fgroup);
		this.setDimensionFilterGroups(fgroups);
	}
	
	/**
	 * 
	 * @return The startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * 
	 * @param startDate
	 *            The startDate
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * 
	 * @return The endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * 
	 * @param endDate
	 *            The endDate
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * 
	 * @return The dimensions
	 */
	public List<String> getDimensions() {
		return dimensions;
	}

	/**
	 * 
	 * @param dimensions
	 *            The dimensions
	 */
	public void setDimensions(List<String> dimensions) {
		this.dimensions = dimensions;
	}

	/**
	 * 
	 * @return The searchType
	 */
	public String getSearchType() {
		return searchType;
	}

	/**
	 * 
	 * @param searchType
	 *            The searchType
	 */
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	/**
	 * 
	 * @return The dimensionFilterGroups
	 */
	public List<DimensionFilterGroup> getDimensionFilterGroups() {
		return dimensionFilterGroups;
	}

	/**
	 * 
	 * @param dimensionFilterGroups
	 *            The dimensionFilterGroups
	 */
	public void setDimensionFilterGroups(List<DimensionFilterGroup> dimensionFilterGroups) {
		this.dimensionFilterGroups = dimensionFilterGroups;
	}

	/**
	 * 
	 * @return The aggregationType
	 */
	public String getAggregationType() {
		return aggregationType;
	}

	/**
	 * 
	 * @param aggregationType
	 *            The aggregationType
	 */
	public void setAggregationType(String aggregationType) {
		this.aggregationType = aggregationType;
	}

	/**
	 * 
	 * @return The rowLimit
	 */
	public Integer getRowLimit() {
		return rowLimit;
	}

	/**
	 * 
	 * @param rowLimit
	 *            The rowLimit
	 */
	public void setRowLimit(Integer rowLimit) {
		this.rowLimit = rowLimit;
	}

	/**
	 * 
	 * @return The startRow
	 */
	public Integer getStartRow() {
		return startRow;
	}

	/**
	 * 
	 * @param startRow
	 *            The startRow
	 */
	public void setStartRow(Integer startRow) {
		this.startRow = startRow;
	}

	public class Filter {

		@SerializedName("dimension")
		@Expose
		private String dimension;
		@SerializedName("operator")
		@Expose
		private String operator;
		@SerializedName("expression")
		@Expose
		private String expression;

		/**
		 * 
		 * @return The dimension
		 */
		public String getDimension() {
			return dimension;
		}

		/**
		 * 
		 * @param dimension
		 *            The dimension
		 */
		public void setDimension(String dimension) {
			this.dimension = dimension;
		}

		/**
		 * 
		 * @return The operator
		 */
		public String getOperator() {
			return operator;
		}

		/**
		 * 
		 * @param operator
		 *            The operator
		 */
		public void setOperator(String operator) {
			this.operator = operator;
		}

		/**
		 * 
		 * @return The expression
		 */
		public String getExpression() {
			return expression;
		}

		/**
		 * 
		 * @param expression
		 *            The expression
		 */
		public void setExpression(String expression) {
			this.expression = expression;
		}

	}

	public class DimensionFilterGroup {

		@SerializedName("groupType")
		@Expose
		private String groupType;
		@SerializedName("filters")
		@Expose
		private List<Filter> filters = null;

		/**
		 * 
		 * @return The groupType
		 */
		public String getGroupType() {
			return groupType;
		}

		/**
		 * 
		 * @param groupType
		 *            The groupType
		 */
		public void setGroupType(String groupType) {
			this.groupType = groupType;
		}

		/**
		 * 
		 * @return The filters
		 */
		public List<Filter> getFilters() {
			return filters;
		}

		/**
		 * 
		 * @param filters
		 *            The filters
		 */
		public void setFilters(List<Filter> filters) {
			this.filters = filters;
		}

	}

}