package beans;

public class PlatesBean {
	private int plates[];

	public PlatesBean(int plates[]){
		this.plates = plates;
	}
	
	public PlatesBean(){
		plates = null;
	}
	
	public int[] getPlates() {
		return plates;
	}

	public void setPlates(int[] plates) {
		this.plates = plates;
	}
}
