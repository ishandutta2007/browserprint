package beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class HistoryBean implements Serializable{
	private Integer sampleID;
	private String encryptedSampleID;
	private String timestamp;

	public HistoryBean() {
		sampleID = null;
		encryptedSampleID = null;
		timestamp = null;
	}

	public HistoryBean(Integer sampleID, String encryptedSampleID, String timestamp) {
		this.sampleID = sampleID;
		this.encryptedSampleID = encryptedSampleID;
		this.timestamp = timestamp;
	}

	public Integer getSampleID() {
		return sampleID;
	}

	public void setSampleID(Integer sampleID) {
		this.sampleID = sampleID;
	}

	public String getEncryptedSampleID() {
		return encryptedSampleID;
	}

	public void setEncryptedSampleID(String encryptedSampleID) {
		this.encryptedSampleID = encryptedSampleID;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
