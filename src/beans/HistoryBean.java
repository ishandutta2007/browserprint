package beans;

public class HistoryBean {
	private String sampleID;
	private String timestamp;

	public HistoryBean() {
		sampleID = null;
		timestamp = null;
	}

	public HistoryBean(String sampleID, String timestamp) {
		this.sampleID = sampleID;
		this.timestamp = timestamp;
	}

	public String getSampleID() {
		return sampleID;
	}

	public void setSampleID(String sampleID) {
		this.sampleID = sampleID;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
