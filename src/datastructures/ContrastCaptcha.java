package datastructures;

/**
 * Is case insensitive.
 */
public class ContrastCaptcha {
	public char captchaChars[];
	public CaptchaColours captchaCharColours[];
	
	public enum CaptchaColours{UNIMPORTANT, LIGHT_GREY, LIGHTEST_GREY};
	
	public ContrastCaptcha(char[] captchaChars, CaptchaColours[] captchaCharColours) {
		setCaptchaChars(captchaChars);
		this.captchaCharColours = captchaCharColours;
	}
	
	public char[] getCaptchaChars() {
		return captchaChars;
	}
	public void setCaptchaChars(char[] captchaChars) {
		this.captchaChars = captchaChars.clone();
		for(int i = 0; i < captchaChars.length; ++i){
			captchaChars[i] = Character.toUpperCase(captchaChars[i]); 
		}
	}
	public CaptchaColours[] getCaptchaCharColours() {
		return captchaCharColours;
	}
	public void setCaptchaCharColours(CaptchaColours[] captchaCharColours) {
		this.captchaCharColours = captchaCharColours;
	}
	
	/**
	 * This code is pretty ugly but it seems to do the job.
	 * @param captchaAnswer
	 * @return
	 */
	public Integer isValid(String captchaAnswer){
		captchaAnswer = captchaAnswer.toUpperCase();
		int contrastLevel = 0;
		
		if(captchaAnswer.length() < captchaChars.length - 2){
			//Too short. Invalid.
			return null;
		}
		
		boolean bothMissing = false;
		int i, j;
		for(i = j = 0; i < captchaChars.length && j < captchaAnswer.length(); ++i, ++j){
			if(captchaChars[i] != captchaAnswer.charAt(j)){
				if(captchaCharColours[i] == CaptchaColours.UNIMPORTANT){
					//Invalid.
					return null;
				}
				else{
					if(captchaCharColours[i] == CaptchaColours.LIGHTEST_GREY){//1
						if(contrastLevel == 2){
							bothMissing = true;
						}
						
						contrastLevel = 1;
						++i;
					}
					else if(captchaCharColours[i] == CaptchaColours.LIGHT_GREY){//2
						if(captchaCharColours.length - 2 != captchaAnswer.length()){
							//Wrong length. Invalid.
							return null;
						}
						if(contrastLevel == 1){
							bothMissing = true;
						}
						if(contrastLevel < 2){
							contrastLevel = 2;
						}
						++i;
					}
					else{
						//Unknown colour. Invalid.
						return null;
					}
				}
			}
		}
		
		if(contrastLevel == 0 && captchaAnswer.length() != captchaCharColours.length){
			//Wrong length. Invalid.
			return null;
		}
		else if(contrastLevel == 1 && captchaAnswer.length() != captchaCharColours.length - 1){
			//Wrong length. Invalid.
			return null;
		}
		else if(contrastLevel == 2 && !bothMissing){
			//Lightest grey character wasn't input but the light grey one was.
			return null;
		}
		
		return contrastLevel;
	}
}
