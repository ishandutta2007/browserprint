package beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PredictionBean implements Serializable{
	private String browserPrediction;
	private String useragentSpecifiedBrowser;
	private String osPrediction;
	private String useragentSpecifiedOs;
	
	public PredictionBean(){
		browserPrediction = "ERROR";
		useragentSpecifiedBrowser = "ERROR";
	}
	
	public String getBrowserPrediction() {
		return browserPrediction;
	}
	public void setBrowserPrediction(String browserPrediction) {
		this.browserPrediction = browserPrediction;
	}
	public String getUseragentSpecifiedBrowser() {
		return useragentSpecifiedBrowser;
	}
	public void setUseragentSpecifiedBrowser(String useragentSpecifiedBrowser) {
		this.useragentSpecifiedBrowser = useragentSpecifiedBrowser;
	}

	public String getOsPrediction() {
		return osPrediction;
	}

	public void setOsPrediction(String osPrediction) {
		this.osPrediction = osPrediction;
	}

	public String getUseragentSpecifiedOs() {
		return useragentSpecifiedOs;
	}

	public void setUseragentSpecifiedOs(String useragentSpecifiedOs) {
		this.useragentSpecifiedOs = useragentSpecifiedOs;
	}
}