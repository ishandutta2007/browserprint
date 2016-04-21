package beans;

public class CaptchaBean {
	public String captchaSrc;
	
	public CaptchaBean(){
		captchaSrc = null;
	}
	
	public CaptchaBean(String captchaSrc){
		this.captchaSrc = captchaSrc;
	}

	public String getCaptchaSrc() {
		return captchaSrc;
	}

	public void setCaptchaSrc(String captchaSrc) {
		this.captchaSrc = captchaSrc;
	}
}
