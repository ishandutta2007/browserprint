package datastructures;

public class PlatesCaptcha {
	public static final int RED_GREEN = 1;
	public static final int PROTANOPIA = 2;
	public static final int DEUTERANOMALIA = 4;
	public static final int UNKNOWN_COLOURBLINDNESS = 8;
	
	private int eyesight;
	private boolean valid;
	
	public PlatesCaptcha(int plates[], int captchaAnswers[]){
		eyesight = 0;
		valid = true;
		
		int index1 = 0;
		int index2 = 0;
		switch(plates[index1]){
		case 2:
			if(captchaAnswers[index2] == 8){
				//Do nothing.
			}
			else if(captchaAnswers[index2] == 3){
				eyesight |= RED_GREEN;				
			}
			else{
				//Incorrect captcha.
				valid = false;
				return;
			}
			break;
		case 3:
			if(captchaAnswers[index2] == 29){
				//Do nothing.
			}
			else if(captchaAnswers[index2] == 70){
				eyesight |= RED_GREEN;				
			}
			else{
				//Incorrect captcha.
				valid = false;
				return;
			}
			break;
		case 4:
			if(captchaAnswers[index2] == 5){
				//Do nothing.
			}
			else if(captchaAnswers[index2] == 2){
				eyesight |= RED_GREEN;				
			}
			else{
				//Incorrect captcha.
				valid = false;
				return;
			}
			break;
		case 5:
			if(captchaAnswers[index2] == 3){
				//Do nothing.
			}
			else if(captchaAnswers[index2] == 5){
				eyesight |= RED_GREEN;				
			}
			else{
				//Incorrect captcha.
				valid = false;
				return;
			}
			break;
		case 6:
			if(captchaAnswers[index2] == 15){
				//Do nothing.
			}
			else if(captchaAnswers[index2] == 17){
				eyesight |= RED_GREEN;				
			}
			else{
				//Incorrect captcha.
				valid = false;
				return;
			}
			break;
		case 7:
			if(captchaAnswers[index2] == 74){
				//Do nothing.
			}
			else if(captchaAnswers[index2] == 21){
				eyesight |= RED_GREEN;				
			}
			else{
				//Incorrect captcha.
				valid = false;
				return;
			}
			break;
		default:
			//Unknown  or unexpected plate.
			valid = false;
			return;
		}
		
		++index1;
		if(captchaAnswers.length == 3){//All three numbers are provided, the middle wasn't missed
			++index2;
			switch(plates[index1]){
			case 8:
				if(captchaAnswers[index2] == 6){
					//Do nothing.
				}
				else{
					valid = false;
					return;
				}
				break;
			case 9:
				if(captchaAnswers[index2] == 45){
					//Do nothing.
				}
				else{
					valid = false;
					return;
				}
				break;
			case 10:
				if(captchaAnswers[index2] == 5){
					//Do nothing.
				}
				else{
					valid = false;
					return;
				}
				break;
			case 11:
				if(captchaAnswers[index2] == 7){
					//Do nothing.
				}
				else{
					valid = false;
					return;
				}
				break;
			case 12:
				if(captchaAnswers[index2] == 16){
					//Do nothing.
				}
				else{
					valid = false;
					return;
				}
				break;
			case 13:
				if(captchaAnswers[index2] == 73){
					//Do nothing.
				}
				else{
					valid = false;
					return;
				}
				break;
			default:
				//Unknown  or unexpected plate.
				valid = false;
				return;
			}
		}
		else{
			//Middle was skipped, probably because they couldn't see any number.
			eyesight |= UNKNOWN_COLOURBLINDNESS;
		}
		
		++index1;
		++index2;
		switch(plates[index1]){
		case 16:
			if(captchaAnswers[index2] == 26){
				//Do nothing.
			}
			else if(captchaAnswers[index2] == 6){
				eyesight |= PROTANOPIA;
			}
			else if(captchaAnswers[index2] == 2){
				eyesight |= DEUTERANOMALIA;
			}
			else{
				valid = false;
				return;
			}
			break;
		case 17:
			if(captchaAnswers[index2] == 42){
				//Do nothing.
			}
			else if(captchaAnswers[index2] == 2){
				eyesight |= PROTANOPIA;
			}
			else if(captchaAnswers[index2] == 4){
				eyesight |= DEUTERANOMALIA;
			}
			else{
				valid = false;
				return;
			}
			break;
		default:
			//Unknown  or unexpected plate.
			valid = false;
			return;
		}
	}

	public int getEyesight() {
		return eyesight;
	}

	public void setEyesight(int eyesight) {
		this.eyesight = eyesight;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
