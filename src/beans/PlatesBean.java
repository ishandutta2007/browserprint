package beans;

public class PlatesBean {
	private int plates[];
	private String platesEncrypted;

	public PlatesBean(int plates[], String platesEncrypted){
		this.plates = plates;
		this.platesEncrypted = platesEncrypted;
	}
	
	public PlatesBean(){
		plates = null;
		platesEncrypted = null;
	}
	
	public int[] getPlates() {
		return plates;
	}

	public void setPlates(int[] plates) {
		this.plates = plates;
	}

	public String getPlatesEncrypted() {
		return platesEncrypted;
	}

	public void setPlatesEncrypted(String platesEncrypted) {
		this.platesEncrypted = platesEncrypted;
	}
}
