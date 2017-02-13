package beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BrowserPredictionBean implements Serializable{
	private String browserPrediction;
	private String useragentSpecifiedBrowser;
	
	public BrowserPredictionBean(){
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
}