package datastructures;


public class Fingerprint {
	private String sampleSetID;
	
	private Integer colourVision;
	
	private String user_agent;
	private String accept_headers;

	private String platform;
	private String platformFlash;
	private String pluginDetails;
	private String timeZone;
	private String screenDetails;
	private String screenDetailsFlash;
	private String languageFlash;
	private String fonts;
	private String fontsJS_CSS;
	private String charSizes;

	private boolean cookiesEnabled;

	private Boolean superCookieLocalStorage;
	private Boolean superCookieSessionStorage;
	private Boolean superCookieUserData;
	private Boolean indexedDBEnabled;
	private String doNotTrack;

	private Long clockDifference;

	private String dateTime;
	private String mathTan;

	private boolean usingTor;
	private String tbbVersion;
	private String ipAddress;
	
	private Boolean adsBlocked;
	
	private String canvas;
	private String webGLVendor;
	private String webGLRenderer;
	
	private Integer touchPoints;
	private Boolean touchEvent;
	private Boolean touchStart;

	public Fingerprint() {
		sampleSetID = null;
		colourVision = null;
		user_agent = null;
		accept_headers = null;
		platform = null;
		platformFlash = null;
		pluginDetails = null;
		timeZone = null;
		screenDetails = null;
		screenDetailsFlash = null;
		languageFlash = null;
		fonts = null;
		fontsJS_CSS = null;
		charSizes = null;
		cookiesEnabled = false;
		superCookieLocalStorage = null;
		superCookieSessionStorage = null;
		superCookieUserData = null;
		indexedDBEnabled = null;
		doNotTrack = null;
		clockDifference = null;
		dateTime = null;
		mathTan = null;
		usingTor = false;
		tbbVersion = null;
		ipAddress = null;
		adsBlocked = null;
		canvas = null;
		webGLVendor = null;
		webGLRenderer = null;
		touchPoints = null;
		touchEvent = null;
		touchStart = null;
	}

	public String getSampleSetID() {
		return sampleSetID;
	}

	public void setSampleSetID(String sampleSetID) {
		this.sampleSetID = sampleSetID;
	}
	
	public Integer getColourVision() {
		return colourVision;
	}

	public void setColourVision(Integer colourVision) {
		this.colourVision = colourVision;
	}
	
	public String getUser_agent() {
		return user_agent;
	}

	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}

	public String getAccept_headers() {
		return accept_headers;
	}

	public void setAccept_headers(String accept_headers) {
		this.accept_headers = accept_headers;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getPlatformFlash() {
		return platformFlash;
	}

	public void setPlatformFlash(String platformFlash) {
		this.platformFlash = platformFlash;
	}

	public String getPluginDetails() {
		return pluginDetails;
	}

	public void setPluginDetails(String pluginDetails) {
		this.pluginDetails = pluginDetails;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getScreenDetails() {
		return screenDetails;
	}

	public void setScreenDetails(String screenDetails) {
		this.screenDetails = screenDetails;
	}

	public String getScreenDetailsFlash() {
		return screenDetailsFlash;
	}

	public void setScreenDetailsFlash(String screenDetailsFlash) {
		this.screenDetailsFlash = screenDetailsFlash;
	}
	
	public String getLanguageFlash() {
		return languageFlash;
	}

	public void setLanguageFlash(String languageFlash) {
		this.languageFlash = languageFlash;
	}

	public String getFonts() {
		return fonts;
	}

	public void setFonts(String fonts) {
		this.fonts = fonts;
	}
	
	public String getFontsJS_CSS() {
		return fontsJS_CSS;
	}

	public void setFontsJS_CSS(String fontsJS_CSS) {
		this.fontsJS_CSS = fontsJS_CSS;
	}

	public String getCharSizes() {
		return charSizes;
	}

	public void setCharSizes(String charSizes) {
		this.charSizes = charSizes;
	}
	
	public boolean isCookiesEnabled() {
		return cookiesEnabled;
	}

	public void setCookiesEnabled(boolean cookiesEnabled) {
		this.cookiesEnabled = cookiesEnabled;
	}

	public Boolean getSuperCookieLocalStorage() {
		return superCookieLocalStorage;
	}

	public void setSuperCookieLocalStorage(Boolean superCookieLocalStorage) {
		this.superCookieLocalStorage = superCookieLocalStorage;
	}

	public Boolean getSuperCookieSessionStorage() {
		return superCookieSessionStorage;
	}

	public void setSuperCookieSessionStorage(Boolean superCookieSessionStorage) {
		this.superCookieSessionStorage = superCookieSessionStorage;
	}

	public Boolean getSuperCookieUserData() {
		return superCookieUserData;
	}

	public void setSuperCookieUserData(Boolean superCookieUserData) {
		this.superCookieUserData = superCookieUserData;
	}
	
	public Boolean getIndexedDBEnabled() {
		return indexedDBEnabled;
	}

	public void setIndexedDBEnabled(Boolean indexedDBEnabled) {
		this.indexedDBEnabled = indexedDBEnabled;
	}

	public String getDoNotTrack() {
		return doNotTrack;
	}

	public void setDoNotTrack(String doNotTrack) {
		this.doNotTrack = doNotTrack;
	}

	public Long getClockDifference() {
		return clockDifference;
	}

	public void setClockDifference(Long clockDifference) {
		this.clockDifference = clockDifference;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getMathTan() {
		return mathTan;
	}

	public void setMathTan(String mathTan) {
		this.mathTan = mathTan;
	}

	public boolean isUsingTor() {
		return usingTor;
	}

	public void setUsingTor(boolean usingTor) {
		this.usingTor = usingTor;
	}

	public String getTbbVersion() {
		return tbbVersion;
	}

	public void setTbbVersion(String tbbVersion) {
		this.tbbVersion = tbbVersion;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Boolean getAdsBlocked() {
		return adsBlocked;
	}

	public void setAdsBlocked(Boolean adsBlocked) {
		this.adsBlocked = adsBlocked;
	}

	public String getCanvas() {
		return canvas;
	}

	public void setCanvas(String canvas) {
		this.canvas = canvas;
	}

	public String getWebGLVendor() {
		return webGLVendor;
	}

	public void setWebGLVendor(String webGLVendor) {
		this.webGLVendor = webGLVendor;
	}

	public String getWebGLRenderer() {
		return webGLRenderer;
	}

	public void setWebGLRenderer(String webGLRenderer) {
		this.webGLRenderer = webGLRenderer;
	}

	public Integer getTouchPoints() {
		return touchPoints;
	}

	public void setTouchPoints(Integer touchPoints) {
		this.touchPoints = touchPoints;
	}

	public Boolean getTouchEvent() {
		return touchEvent;
	}

	public void setTouchEvent(Boolean touchEvent) {
		this.touchEvent = touchEvent;
	}

	public Boolean getTouchStart() {
		return touchStart;
	}

	public void setTouchStart(Boolean touchStart) {
		this.touchStart = touchStart;
	}
}