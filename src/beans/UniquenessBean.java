package beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UniquenessBean implements Serializable{
	private boolean isUnique;
	private double bits;
	private double inX;
	private int num_samples;

	public UniquenessBean(){
		isUnique = false;
		bits = 0;
		inX = 0;
		num_samples = 0;
	}

	public boolean getIsUnique() {
		return isUnique;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}

	public double getBits() {
		return bits;
	}

	public void setBits(double bits) {
		this.bits = bits;
	}

	public double getInX() {
		return inX;
	}

	public void setInX(double inX) {
		this.inX = inX;
	}

	public int getNum_samples() {
		return num_samples;
	}

	public void setNum_samples(int num_samples) {
		this.num_samples = num_samples;
	}
}
