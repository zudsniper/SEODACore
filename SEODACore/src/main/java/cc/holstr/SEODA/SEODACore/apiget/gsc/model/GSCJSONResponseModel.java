package cc.holstr.SEODA.SEODACore.apiget.gsc.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GSCJSONResponseModel {

	@SerializedName("rows")
	@Expose
	private List<Row> rows = null;
	@SerializedName("responseAggregationType")
	@Expose
	private String responseAggregationType;

	/**
	 * 
	 * @return The rows
	 */
	public List<Row> getRows() {
		return rows;
	}

	/**
	 * 
	 * @param rows
	 *            The rows
	 */
	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	/**
	 * 
	 * @return The responseAggregationType
	 */
	public String getResponseAggregationType() {
		return responseAggregationType;
	}

	/**
	 * 
	 * @param responseAggregationType
	 *            The responseAggregationType
	 */
	public void setResponseAggregationType(String responseAggregationType) {
		this.responseAggregationType = responseAggregationType;
	}

	public class Row {

		@SerializedName("keys")
		@Expose
		private List<String> keys = null;
		@SerializedName("clicks")
		@Expose
		private Double clicks;
		@SerializedName("impressions")
		@Expose
		private Double impressions;
		@SerializedName("ctr")
		@Expose
		private Double ctr;
		@SerializedName("position")
		@Expose
		private Double position;

		/**
		 * 
		 * @return The keys
		 */
		public List<String> getKeys() {
			return keys;
		}

		/**
		 * 
		 * @param keys
		 *            The keys
		 */
		public void setKeys(List<String> keys) {
			this.keys = keys;
		}

		/**
		 * 
		 * @return The clicks
		 */
		public Double getClicks() {
			return clicks;
		}

		/**
		 * 
		 * @param clicks
		 *            The clicks
		 */
		public void setClicks(Double clicks) {
			this.clicks = clicks;
		}

		/**
		 * 
		 * @return The impressions
		 */
		public Double getImpressions() {
			return impressions;
		}

		/**
		 * 
		 * @param impressions
		 *            The impressions
		 */
		public void setImpressions(Double impressions) {
			this.impressions = impressions;
		}

		/**
		 * 
		 * @return The ctr
		 */
		public Double getCtr() {
			return ctr;
		}

		/**
		 * 
		 * @param ctr
		 *            The ctr
		 */
		public void setCtr(Double ctr) {
			this.ctr = ctr;
		}

		/**
		 * 
		 * @return The position
		 */
		public Double getPosition() {
			return position;
		}

		/**
		 * 
		 * @param position
		 *            The position
		 */
		public void setPosition(Double position) {
			this.position = position;
		}

	}

}