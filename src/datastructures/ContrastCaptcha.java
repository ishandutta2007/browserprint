package datastructures;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		
		if(captchaAnswer.length() < captchaChars.length - 2){
			//Too short. Invalid.
			return null;
		}
		
		String patternStr = "^";
		for(int i = 0; i < captchaChars.length; ++i){
			if(captchaCharColours[i] == CaptchaColours.LIGHT_GREY){
				patternStr += "(?<light>" + captchaChars[i] + ")?";
			}
			else  if(captchaCharColours[i] == CaptchaColours.LIGHTEST_GREY){
				patternStr += "(?<lightest>" + captchaChars[i] + ")?";
			}
			else{
				patternStr += captchaChars[i];
			}
		}
		patternStr += "$";
		
		Matcher matcher = Pattern.compile(patternStr).matcher(captchaAnswer); 
		if(matcher.matches() == false){
			//Invalid answer.
			return null;
		}
		if(matcher.group("lightest") == null){
			if(matcher.group("light") == null){
				//Both lightest and light excluded. Valid CAPTCHA.
				return 2;
			}
			else{
				//Can't exclude lightest but not second lightest.
				return null;
			}
		}
		else if(matcher.group("light") == null){
			//Light excluded. Valid CAPTCHA.
			return 1;
		}
		//Neither excluded. Valid CAPTCHA.
		return 0;
	}
}
