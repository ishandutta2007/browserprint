package beans;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class CharacteristicsBean implements Serializable{
	private ArrayList<CharacteristicBean> characteristics;

	public CharacteristicsBean(){
		characteristics = new ArrayList<CharacteristicBean>();
	}
	
	public ArrayList<CharacteristicBean> getCharacteristics() {
		return characteristics;
	}

	public void setCharacteristics(ArrayList<CharacteristicBean> characteristics) {
		this.characteristics = characteristics;
	}
}
