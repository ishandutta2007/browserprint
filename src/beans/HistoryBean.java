package beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class HistoryBean implements Serializable{
	private String sampleUUID;
	private String timestamp;

	public HistoryBean() {
		sampleUUID = null;
		timestamp = null;
	}

	public HistoryBean(String sampleUUID, String timestamp) {
		this.sampleUUID = sampleUUID;
		this.timestamp = timestamp;
	}

	public String getSampleUUID() {
		return sampleUUID;
	}

	public void setSampleUUID(String sampleUUID) {
		this.sampleUUID = sampleUUID;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
