package beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class StatisticsBean implements Serializable{
	private int numSamples;

	public StatisticsBean() {
		numSamples = -1;
	}

	public int getNumSamples() {
		return numSamples;
	}

	public void setNumSamples(int numSamples) {
		this.numSamples = numSamples;
	}
}
