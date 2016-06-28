package DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import beans.CharacteristicBean;
import beans.CharacteristicsBean;
import beans.HistoryBean;
import beans.HistoryListBean;
import beans.UniquenessBean;
import datastructures.Fingerprint;

public class FingerprintDAO {
	private static final String insertSampleStr = "INSERT INTO `Samples`(`SampleUUID`, `IP`, `TimeStamp`, `AllHeaders`, `ContrastLevel`, `UserAgent`, `AcceptHeaders`, `Platform`, `PlatformFlash`, `PluginDetails`, `TimeZone`, `ScreenDetails`, `ScreenDetailsFlash`, `ScreenDetailsCSS`, `LanguageFlash`, `Fonts`, `FontsJS_CSS`, `CharSizes`, `CookiesEnabled`, `SuperCookieLocalStorage`, `SuperCookieSessionStorage`, `SuperCookieUserData`, `IndexedDBEnabled`, `DoNotTrack`, `ClockDifference`, `DateTime`, `MathTan`, `UsingTor`, `TbbVersion`, `AdsBlockedGoogle`, `AdsBlockedBanner`, `AdsBlockedScript`, `LikeShareFacebook`, `LikeShareTwitter`, `LikeShareReddit`, `Canvas`, `WebGLVendor`, `WebGLRenderer`, `TouchPoints`, `TouchEvent`, `TouchStart`, `AudioFingerprintPXI`, `AudioFingerprintPXIFullBuffer`, `AudioFingerprintNtVc`, `AudioFingerprintCC`, `AudioFingerprintHybrid`) VALUES(?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	private static final String getSampleCountStr = "SELECT COUNT(*) FROM `Samples`;";
	private static final String getSampleCountVersionAwareStr = "SELECT `BrowserprintVersion` AS `Version`, (SELECT COUNT(*) FROM `Samples` WHERE `BrowserprintVersion` >= `Version`) FROM `Samples` GROUP BY `BrowserprintVersion` UNION SELECT 1, COUNT(*) FROM `Samples`;";
	private static final String selectSampleStr = "SELECT `ContrastLevel`, `UserAgent`, `AcceptHeaders`, `Platform`, `PlatformFlash`, `PluginDetails`, `TimeZone`, `ScreenDetails`, `ScreenDetailsFlash`, `ScreenDetailsCSS`, `LanguageFlash`, `Fonts`, `FontsJS_CSS`, `CharSizes`, `CookiesEnabled`, `SuperCookieLocalStorage`, `SuperCookieSessionStorage`, `SuperCookieUserData`, `IndexedDBEnabled`, `DoNotTrack`, `ClockDifference`, `DateTime`, `MathTan`, `UsingTor`, `TbbVersion`, `AdsBlockedGoogle`, `AdsBlockedBanner`, `AdsBlockedScript`, `LikeShareFacebook`, `LikeShareTwitter`, `LikeShareReddit`, `Canvas`, `WebGLVendor`, `WebGLRenderer`, `TouchPoints`, `TouchEvent`, `TouchStart`, `AudioFingerprintPXI`, `AudioFingerprintPXIFullBuffer`, `AudioFingerprintNtVc`, `AudioFingerprintCC`, `AudioFingerprintHybrid` FROM `Samples` WHERE `SampleUUID` = ?;";
	private static final String selectSampleSetIDHistory = "SELECT `SampleUUID`, `Timestamp` FROM `SampleSets` INNER JOIN `Samples` USING (`SampleID`) WHERE `SampleSetID` = ? ORDER BY `Timestamp` DESC;";

	private static final String NO_JAVASCRIPT = "No JavaScript";
	private static final String NOT_SUPPORTED = "Not supported";

	public static final String processFingerprint(Fingerprint fingerprint, CharacteristicsBean chrsbean, UniquenessBean uniquenessbean) {
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(false);

			/*
			 * Check if we've seen this sample before.
			 */
			Integer sampleID;
			String sampleUUID;
			{
				ImmutablePair<Integer, String> ret = checkSampleChanged(conn, fingerprint);
				sampleID = ret.left;
				sampleUUID = ret.right;
			}

			if (sampleID == null) {
				/*
				 * We haven't seen this sample before.
				 * Record it.
				 */
				{
					ImmutablePair<Integer, String> ret = insertSample(conn, fingerprint);
					sampleID = ret.left;
					sampleUUID = ret.right;
				}

				/*
				 * Insert SampleID into SampleSets table.
				 */
				insertSampleSet(conn, fingerprint, sampleID);

				/*
				 * Save statistics of the fingerprint.
				 */
				StatisticsDAO.saveStatistics(sampleID, fingerprint);
			}
			getFingerprintBeans(conn, fingerprint, chrsbean, uniquenessbean);

			return sampleUUID;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close the connection
			// Finally triggers even if we return
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// Ignore
				}
			}
		}
		return null;
	}

	/**
	 * Takes a sampleID, gets the fingerprint associated with it, and then fills a CharacteristicsBean and UniquenessBean with the fingerprint's data and statistics.
	 * 
	 * @param sampleID
	 * @param chrsbean
	 * @param uniquenessbean
	 * @return The fingerprint, or null if no fingerprint with the specified sampleID was found.
	 * @throws SQLException
	 */
	public static Fingerprint getFingerprintBeans(String sampleUUID, CharacteristicsBean chrsbean, UniquenessBean uniquenessbean) throws SQLException {
		Connection conn = Database.getConnection();
		conn.setReadOnly(true);

		Fingerprint fingerprint = getFingerprintFromSampleID(conn, sampleUUID);
		if (fingerprint == null) {
			conn.close();
			return null;
		}
		getFingerprintBeans(conn, fingerprint, chrsbean, uniquenessbean);
		conn.close();
		return fingerprint;
	}

	/**
	 * Takes a fingerprint and then fills a CharacteristicsBean and UniquenessBean with the fingerprint's data and statistics.
	 * 
	 * @param conn
	 * @param fingerprint
	 * @param chrsbean
	 * @param uniquenessbean
	 * @throws SQLException
	 */
	private static void getFingerprintBeans(Connection conn, Fingerprint fingerprint, CharacteristicsBean chrsbean, UniquenessBean uniquenessbean) throws SQLException {
		/*
		 * Get number of samples.
		 */
		TreeSet<VersionCount> sampleCounts = getSampleCountVersionAware(conn);
		int totalSamples = sampleCounts.first().getCount();

		/*
		 * Get uniqueness.
		 */
		int sampleOccurrences = getSampleOccurrences(conn, fingerprint);
		if (sampleOccurrences == 1) {
			uniquenessbean.setUnique(true);
		} else {
			uniquenessbean.setUnique(false);
		}
		uniquenessbean.setNum_samples(totalSamples);
		uniquenessbean.setInX(((double) totalSamples) / ((double) sampleOccurrences));
		uniquenessbean.setBits(Math.abs(Math.log(uniquenessbean.getInX()) / Math.log(2)));
		uniquenessbean.setNum_occurrences(sampleOccurrences);

		/*
		 * Get each characteristic.
		 */
		ArrayList<CharacteristicBean> characteristics = chrsbean.getCharacteristics();
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCounts.lower(new VersionCount(9 + 1)).getCount(), "ContrastLevel", fingerprint.getContrastLevel());
			bean.setName("Monitor Contrast Level");
			bean.setNameHoverText("A rough measure of the level of contrast of the monitor the browser is being displayed on.");
			
			String contrastLevelStr = "";
			if(fingerprint.getContrastLevel() == 0){
				contrastLevelStr = "Normal or low contrast.";
			}
			if(fingerprint.getContrastLevel() == 1){
				contrastLevelStr = "High contrast.";
			}
			if(fingerprint.getContrastLevel() == 2){
				contrastLevelStr = "Extreme contrast.";
			}
			bean.setValue(contrastLevelStr);
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "UserAgent", fingerprint.getUser_agent());
			bean.setName("User Agent");
			bean.setNameHoverText("The User-Agent header sent with the HTTP request for the page.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "AcceptHeaders", fingerprint.getAccept_headers());
			bean.setName("HTTP_ACCEPT Headers");
			bean.setNameHoverText("The concatenation of three headers from the HTTP request:"
					+ " The Accept request header, the Accept-Encoding request header, and the Accept-Language request header.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "Platform", fingerprint.getPlatform());
			bean.setName("Platform (JavaScript)");
			bean.setNameHoverText("The name of the platform the browser is running on, detected using JavaScript.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "PlatformFlash", fingerprint.getPlatformFlash());
			bean.setName("Platform (Flash)");
			bean.setNameHoverText("The name of the platform the browser is running on, detected using Flash.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "PluginDetails", fingerprint.getPluginDetails());
			bean.setName("Browser Plugin Details");
			bean.setNameHoverText("A list of the browsers installed plugins as detected using JavaScript.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "TimeZone", fingerprint.getTimeZone());
			bean.setName("Time Zone");
			bean.setNameHoverText("The time-zone configured on the client's machine.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "ScreenDetails", fingerprint.getScreenDetails());
			bean.setColour(CharacteristicBean.DEPRECATED_COLOUR);
			bean.setName("Screen Size and Colour Depth [DEPRECATED]");
			bean.setNameHoverText("The screen size and colour depth of the monitor displaying the client's web browser."
					+ " Deprecated because in the current implementation zooming changes the result in newer browsers.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "ScreenDetailsFlash", fingerprint.getScreenDetailsFlash());
			bean.setName("Screen Size (Flash)");
			bean.setNameHoverText("The resolution of the client's monitor(s)."
					+ " Different from the other screen size test in that this number can be the cumulative resolution of the monitors in multiple monitor set ups.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCounts.lower(new VersionCount(13 + 1)).getCount(), "ScreenDetailsCSS", fingerprint.getScreenDetailsCSS());
			bean.setColour(CharacteristicBean.DEPRECATED_COLOUR);
			bean.setName("Screen Size (CSS) [DEPRECATED]");
			bean.setNameHoverText("The screen size and colour depth of the monitor displaying the client's web browser, detected using CSS."
					+ " Deprecated because in the current implementation zooming changes the result in newer browsers.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "LanguageFlash", fingerprint.getLanguageFlash());
			bean.setName("Language (Flash)");
			bean.setNameHoverText("The language of the client's browser, as detected using Flash.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "Fonts", fingerprint.getFonts());
			if (bean.getValue().equals("")) {
				bean.setValue("No fonts detected");
			}
			bean.setName("System Fonts (Flash)");
			bean.setNameHoverText("The fonts installed on the client's machine, detected using Flash.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCounts.lower(new VersionCount(6 + 1)).getCount(), "FontsJS_CSS", fingerprint.getFontsJS_CSS());
			if (bean.getValue().equals("")) {
				bean.setValue("No fonts detected");
			}
			bean.setName("System Fonts (JS/CSS)");
			bean.setNameHoverText("The fonts installed on the client's machine, detected using JavaScript. Fonts list may be incomplete.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCounts.lower(new VersionCount(2 + 1)).getCount(), "CharSizes", fingerprint.getCharSizes());
			bean.setColour(CharacteristicBean.DEPRECATED_COLOUR);
			bean.setName("Character Sizes [DEPRECATED]");
			bean.setNameHoverText("The height and width of a set of Unicode characters when rendered with a set of different styles (e.g. sans-serif)."
					+ " Deprecated because in the current implementation zooming changes the result.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "CookiesEnabled", fingerprint.isCookiesEnabled());
			bean.setName("Are Cookies Enabled?");
			bean.setNameHoverText("Whether cookies are enabled.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getSuperCookieCharacteristicBean(conn, sampleCounts.lower(new VersionCount(5 + 1)).getCount(), fingerprint);
			bean.setName("Limited supercookie test");
			bean.setNameHoverText("Three tests of whether DOM storage is supported (and enabled) in the client's web browser."
					+ " Tests for localStorage, sessionStorage, and Internet Explorer's userData.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCounts.lower(new VersionCount(8 + 1)).getCount(), "IndexedDBEnabled", fingerprint.getIndexedDBEnabled());
			bean.setName("Does the browser support IndexedDB?");
			bean.setNameHoverText("Detects whether the browser supports IndexedDB, a database embedded within the browser.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "DoNotTrack", fingerprint.getDoNotTrack());
			if (bean.getValue().equals(NO_JAVASCRIPT)) {
				bean.setValue("No preference");
			}
			bean.setName("Do Not Track header");
			bean.setNameHoverText("The value of the DNT (Do Not Track) header from the HTTP request.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "ClockDifference", fingerprint.getClockDifference());
			bean.setName("Client/server time difference (minutes)");
			bean.setNameHoverText("The approximate amount of difference between the time on the client's computer and the clock on the server."
					+ " i.e., the clock on the client's computer is 5 minutes ahead of the clock on the server.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "DateTime", fingerprint.getDateTime());
			bean.setName("Date/Time format");
			bean.setNameHoverText("When the JavaScript function toLocaleString() is called on a date it can reveal information about the language of the browser via the names of days and months." + " For instance the output 'Thursday January 01, 10:30:00 GMT+1030 1970' reveals that English is our configured language because 'Thursday' is English." + " Additionally different browsers tend to return differently formatted results." + " For instance Opera returns the above whereas Firefox returns '1/1/1970 9:30:00 am' for the same date (UNIX epoch)." + " Additionally timezone information may be revealed." + " For instance the above were taken on a computer configured for ACST (+9:30), which is why the times shown aren't midnight.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "MathTan", fingerprint.getMathTan());
			bean.setName("Math/Tan function");
			bean.setNameHoverText("The same math functions run on different platforms and browsers can produce different results."
					+ " In particular we are interested in the output of Math.tan(-1e300), which has been observed to produce different values depending on operating system."
					+ " For instance on a 64bit Linux machine it produces the value -1.4214488238747245 and on a Windows machine it produces the value -4.987183803371025.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "UsingTor", fingerprint.isUsingTor());
			bean.setName("Using Tor?");
			bean.setNameHoverText("Checks whether a client's request came from a Tor exit node, and hence whether they're using Tor."
					+ " It does so by performing a TorDNSEL request for each client.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCounts.lower(new VersionCount(8 + 1)).getCount(), "TbbVersion", fingerprint.getTbbVersion());
			bean.setName("TBB version");
			if (bean.getValue().equals("")) {
				bean.setValue("No Tor Browser Bundle version detected");
			}
			bean.setNameHoverText("The version of the Tor Browser Bundle (TBB) you are using (if you're using the TBB).");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getAdsBlockedCharacteristicBean(conn, sampleCounts.lower(new VersionCount(12 + 1)).getCount(), fingerprint);
			bean.setName("Blocking ads?");
			bean.setNameHoverText("Checks whether ad blocking software is installed."
					+ " It does so by attempting to display 2 ads and trying to call a function from a script named like an ad serving script."
					+ " The Google ad may also be affected by tracker blocking software.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getLikeShareCharacteristicBean(conn, sampleCounts.lower(new VersionCount(16 + 1)).getCount(), fingerprint);
			bean.setName("Blocking like/share buttons?");
			bean.setNameHoverText("Checks whether software is installed that blocks or modifies like or share buttons."
					+ " It does so by attempting to display 3 share buttons and checking if they're displayed properly.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "Canvas", fingerprint.getCanvas());
			bean.setName("Canvas [DEPRECATED]");
			bean.setColour(CharacteristicBean.DEPRECATED_COLOUR);
			if (bean.getValue().equals(NO_JAVASCRIPT) == false && bean.getValue().equals(NOT_SUPPORTED) == false) {
				bean.setValue("<img width=\"400\" height=\"60\" alt=\"A HTML5 canvas test\" src=\"" + bean.getValue() + "\">");
			}
			bean.setNameHoverText("Rendering of a specific picture with the HTML5 Canvas element following a fixed set of instructions."
					+ " The picture presents some slight noticeable variations depending on the OS and the browser used."
					+ " Deprecated because under some circumstances a browser can produce different canvases just by refreshing the page.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "WebGLVendor", fingerprint.getWebGLVendor());
			bean.setName("WebGL Vendor");
			bean.setNameHoverText("Name of the WebGL Vendor. Some browsers give the full name of the underlying graphics card used by the device.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, totalSamples, "WebGLRenderer", fingerprint.getWebGLRenderer());
			bean.setName("WebGL Renderer");
			bean.setNameHoverText("Name of the WebGL Renderer. Some browsers give the full name of the underlying graphics driver.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getTouchCharacteristicBean(conn, sampleCounts.lower(new VersionCount(8 + 1)).getCount(), fingerprint);
			bean.setName("Touch Support");
			bean.setNameHoverText("Primative touch screen detection.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getAudioTestsCharacteristicBean(conn, sampleCounts.lower(new VersionCount(17 + 1)).getCount(), fingerprint);
			bean.setName("Audio Fingerprints [EXPERIMENTAL]");
			bean.setColour(CharacteristicBean.EXPERIMENTAL_COLOUR);
			bean.setNameHoverText("A set of fingerprinting tests that work using the AudioContext API. Based on fingerprinting code from the wild.");
			characteristics.add(bean);
		}
	}

	/**
	 * Insert a sample into the Samples database.
	 * 
	 * @param conn
	 * @param fingerprint
	 * @return The sample ID of the inserted sample.
	 * @throws SQLException
	 */
	private static ImmutablePair<Integer, String> insertSample(Connection conn, Fingerprint fingerprint) throws SQLException {
		PreparedStatement insertSample = conn.prepareStatement(insertSampleStr, Statement.RETURN_GENERATED_KEYS);
		int index = 2;
		insertSample.setString(index, fingerprint.getIpAddress());
		++index;
		insertSample.setString(index, fingerprint.getAllHeaders());
		++index;
		insertSample.setInt(index, fingerprint.getContrastLevel());
		++index;
		insertSample.setString(index, fingerprint.getUser_agent());
		++index;
		insertSample.setString(index, fingerprint.getAccept_headers());
		++index;
		insertSample.setString(index, fingerprint.getPlatform());
		++index;
		insertSample.setString(index, fingerprint.getPlatformFlash());
		++index;
		insertSample.setString(index, fingerprint.getPluginDetails());
		++index;
		if (fingerprint.getTimeZone() != null) {
			insertSample.setString(index, fingerprint.getTimeZone());
		} else {
			insertSample.setNull(index, java.sql.Types.INTEGER);
		}
		++index;
		insertSample.setString(index, fingerprint.getScreenDetails());
		++index;
		insertSample.setString(index, fingerprint.getScreenDetailsFlash());
		++index;
		insertSample.setString(index, fingerprint.getScreenDetailsCSS());
		++index;
		insertSample.setString(index, fingerprint.getLanguageFlash());
		++index;
		insertSample.setString(index, fingerprint.getFonts());
		++index;
		insertSample.setString(index, fingerprint.getFontsJS_CSS());
		++index;
		insertSample.setString(index, fingerprint.getCharSizes());
		++index;
		insertSample.setBoolean(index, fingerprint.isCookiesEnabled());
		++index;
		if(fingerprint.getSuperCookieLocalStorage() != null){
			insertSample.setBoolean(index, fingerprint.getSuperCookieLocalStorage());
		} else {
			insertSample.setNull(index, java.sql.Types.BOOLEAN);
		}
		++index;
		if(fingerprint.getSuperCookieSessionStorage() != null){
			insertSample.setBoolean(index, fingerprint.getSuperCookieSessionStorage());
		} else {
			insertSample.setNull(index, java.sql.Types.BOOLEAN);
		}
		++index;
		if(fingerprint.getSuperCookieUserData() != null){
			insertSample.setBoolean(index, fingerprint.getSuperCookieUserData());
		} else {
			insertSample.setNull(index, java.sql.Types.BOOLEAN);
		}
		++index;
		if(fingerprint.getIndexedDBEnabled() != null){
			insertSample.setBoolean(index, fingerprint.getIndexedDBEnabled());
		} else {
			insertSample.setNull(index, java.sql.Types.BOOLEAN);
		}
		++index;
		insertSample.setString(index, fingerprint.getDoNotTrack());
		++index;
		if (fingerprint.getClockDifference() != null) {
			insertSample.setLong(index, fingerprint.getClockDifference());
		} else {
			insertSample.setNull(index, java.sql.Types.BIGINT);
		}
		++index;
		insertSample.setString(index, fingerprint.getDateTime());
		++index;
		insertSample.setString(index, fingerprint.getMathTan());
		++index;
		insertSample.setBoolean(index, fingerprint.isUsingTor());
		++index;
		insertSample.setString(index, fingerprint.getTbbVersion());
		++index;
		if (fingerprint.getAdsBlockedGoogle() != null) {
			insertSample.setBoolean(index, fingerprint.getAdsBlockedGoogle());
		} else {
			insertSample.setNull(index, java.sql.Types.BOOLEAN);
		}
		++index;
		if (fingerprint.getAdsBlockedBanner() != null) {
			insertSample.setBoolean(index, fingerprint.getAdsBlockedBanner());
		} else {
			insertSample.setNull(index, java.sql.Types.BOOLEAN);
		}
		++index;
		if (fingerprint.getAdsBlockedScript() != null) {
			insertSample.setBoolean(index, fingerprint.getAdsBlockedScript());
		} else {
			insertSample.setNull(index, java.sql.Types.BOOLEAN);
		}
		++index;
		if (fingerprint.getLikeShareFacebook() != null) {
			insertSample.setInt(index, fingerprint.getLikeShareFacebook());
		} else {
			insertSample.setNull(index, java.sql.Types.INTEGER);
		}
		++index;
		if (fingerprint.getLikeShareTwitter() != null) {
			insertSample.setInt(index, fingerprint.getLikeShareTwitter());
		} else {
			insertSample.setNull(index, java.sql.Types.INTEGER);
		}
		++index;
		if (fingerprint.getLikeShareReddit() != null) {
			insertSample.setInt(index, fingerprint.getLikeShareReddit());
		} else {
			insertSample.setNull(index, java.sql.Types.INTEGER);
		}
		++index;
		insertSample.setString(index, fingerprint.getCanvas());
		++index;
		insertSample.setString(index, fingerprint.getWebGLVendor());
		++index;
		insertSample.setString(index, fingerprint.getWebGLRenderer());
		++index;
		if (fingerprint.getTouchPoints() != null) {
			insertSample.setLong(index, fingerprint.getTouchPoints());
		} else {
			insertSample.setNull(index, java.sql.Types.BIGINT);
		}
		++index;
		if (fingerprint.getTouchEvent() != null) {
			insertSample.setBoolean(index, fingerprint.getTouchEvent());
		} else {
			insertSample.setNull(index, java.sql.Types.BOOLEAN);
		}
		++index;
		if (fingerprint.getTouchStart() != null) {
			insertSample.setBoolean(index, fingerprint.getTouchStart());
		} else {
			insertSample.setNull(index, java.sql.Types.BOOLEAN);
		}
		++index;
		insertSample.setString(index, fingerprint.getAudioFingerprintPXI());
		++index;
		insertSample.setString(index, fingerprint.getAudioFingerprintPXIFullBuffer());
		++index;
		insertSample.setString(index, fingerprint.getAudioFingerprintNtVc());
		++index;
		insertSample.setString(index, fingerprint.getAudioFingerprintCC());
		++index;
		insertSample.setString(index, fingerprint.getAudioFingerprintHybrid());

		/*
		 * Try to insert with a different random UUID until a unique one is found.
		 */
		String sampleUUID = null;
		boolean foundUniqueUUID = false;
		while (!foundUniqueUUID) {
			sampleUUID = UUID.randomUUID().toString();
			insertSample.setString(1, sampleUUID);
			try {
				insertSample.execute();
				foundUniqueUUID = true;
			} catch (MySQLIntegrityConstraintViolationException ex) {
				System.err.println("Duplicate SampleUUID: " + sampleUUID);
			}
		}

		ResultSet rs = insertSample.getGeneratedKeys();
		Integer sampleID = null;
		if (rs.next()) {
			sampleID = rs.getInt(1);
		}
		rs.close();
		insertSample.close();
		return new ImmutablePair<Integer, String>(sampleID, sampleUUID);
	}

	/**
	 * 
	 * @param conn
	 * @param fingerprint
	 * @param sampleID
	 * @return
	 * @throws SQLException
	 */
	private static void insertSampleSet(Connection conn, Fingerprint fingerprint, Integer sampleID) throws SQLException {
		String insertQuery = "INSERT INTO `SampleSets`(`SampleSetID`,`SampleID`) VALUES(?, ?);";
		PreparedStatement insertSampleSet = conn.prepareStatement(insertQuery);
		insertSampleSet.setInt(2, sampleID);
		
		if(fingerprint.getSampleSetID() != null){
			/*
			 * Check if the SampleSetID exists.
			 * If it doesn't set fingerprint.sampleSetID to null so that it creates a new sampleSetID and inserts that.
			 */
			String selectQuery = "SELECT 1 FROM `SampleSets` WHERE `SampleSetID` = ? GROUP BY `SampleSetID`";
			PreparedStatement selectSampleSet = conn.prepareStatement(selectQuery);
			selectSampleSet.setString(1, fingerprint.getSampleSetID());
			ResultSet rs = selectSampleSet.executeQuery();
			if(!rs.next()){
				fingerprint.setSampleSetID(null);
			}
			else{
				/*
				 * SampleSetID exists.
				 * Insert new SampleID for existing SampleSetID.
				 */
				insertSampleSet.setString(1, fingerprint.getSampleSetID());
				insertSampleSet.execute();
				insertSampleSet.close();
			}
		}
		
		if(fingerprint.getSampleSetID() == null) {
			/*
			 * Insert whole new SampleSetID.
			 * Try to insert with a different random SampleSetUUID until a unique one is found.
			 */
			boolean foundUniqueUUID = false;
			while (!foundUniqueUUID) {
				String sampleSetID = UUID.randomUUID().toString();
				insertSampleSet.setString(1, sampleSetID);
				try {
					insertSampleSet.execute();
					foundUniqueUUID = true;
					fingerprint.setSampleSetID(sampleSetID);
				} catch (MySQLIntegrityConstraintViolationException ex) {
					System.err.println("Duplicate SampleSetUUID: " + sampleSetID);
				}
			}
			insertSampleSet.close();
		}
	}

	/**
	 * Returns the sampleID of the matching sample if we've seen this sample (with SampleSetID) before.
	 * Otherwise returns null.
	 * 
	 * @param conn
	 * @param fingerprint
	 * @return
	 * @throws SQLException
	 */
	private static ImmutablePair<Integer, String> checkSampleChanged(Connection conn, Fingerprint fingerprint) throws SQLException {
		if (fingerprint.getSampleSetID() == null) {
			/*
			 * We know we haven't seen this sample before because there's no SampleSetID.
			 */
			return new ImmutablePair<Integer, String>(null, null);
		}

		/*
		 * We have seen this user before. Check if their fingerprint has changed.
		 */
		String query = "SELECT `Samples`.`SampleID`, `Samples`.`SampleUUID` FROM `SampleSets` INNER JOIN `Samples` ON `SampleSets`.`SampleID` = `Samples`.`SampleID` WHERE `SampleSetID` = ?"
		 + " AND `ContrastLevel` = ?"
		 + " AND `UserAgent`" + (fingerprint.getUser_agent() == null ? " IS NULL" : " = ?")
		 + " AND `AcceptHeaders`" + (fingerprint.getAccept_headers() == null ? " IS NULL" : " = ?")
		 + " AND `Platform`" + (fingerprint.getPlatform() == null ? " IS NULL" : " = ?")
		 + " AND `PlatformFlash`" + (fingerprint.getPlatformFlash() == null ? " IS NULL" : " = ?")
		 + " AND `PluginDetails`" + (fingerprint.getPluginDetails() == null ? " IS NULL" : " = ?")
		 + " AND `TimeZone`" + (fingerprint.getTimeZone() == null ? " IS NULL" : " = ?")
		 + " AND `ScreenDetails`" + (fingerprint.getScreenDetails() == null ? " IS NULL" : " = ?")
		 + " AND `ScreenDetailsFlash`" + (fingerprint.getScreenDetailsFlash() == null ? " IS NULL" : " = ?")
		 + " AND `ScreenDetailsCSS`" + (fingerprint.getScreenDetailsCSS() == null ? " IS NULL" : " = ?")
		 + " AND `LanguageFlash`" + (fingerprint.getLanguageFlash() == null ? " IS NULL" : " = ?")
		 + " AND `Fonts`" + (fingerprint.getFonts() == null ? " IS NULL" : " = ?")
		 + " AND `FontsJS_CSS`" + (fingerprint.getFontsJS_CSS() == null ? " IS NULL" : " = ?")
		 + " AND `CharSizes`" + (fingerprint.getCharSizes() == null ? " IS NULL" : " = ?")
		 + " AND `CookiesEnabled` = ?"
		 + " AND `SuperCookieLocalStorage`" + (fingerprint.getSuperCookieLocalStorage() == null ? " IS NULL" : " = ?")
		 + " AND `SuperCookieSessionStorage`" + (fingerprint.getSuperCookieSessionStorage() == null ? " IS NULL" : " = ?")
		 + " AND `SuperCookieUserData`" + (fingerprint.getSuperCookieUserData() == null ? " IS NULL" : " = ?")
		 + " AND `IndexedDBEnabled`" + (fingerprint.getIndexedDBEnabled() == null ? " IS NULL" : " = ?")
		 + " AND `DoNotTrack`" + (fingerprint.getDoNotTrack() == null ? " IS NULL" : " = ?")
		 + " AND `ClockDifference`" + (fingerprint.getClockDifference() == null ? " IS NULL" : " = ?")
		 + " AND `DateTime`" + (fingerprint.getDateTime() == null ? " IS NULL" : " = ?")
		 + " AND `MathTan`" + (fingerprint.getMathTan() == null ? " IS NULL" : " = ?")
		 + " AND `UsingTor` = ?"
		 + " AND `TbbVersion`" + (fingerprint.getTbbVersion() == null ? " IS NULL" : " = ?")
		 + " AND `AdsBlockedGoogle`" + (fingerprint.getAdsBlockedGoogle() == null ? " IS NULL" : " = ?")
		 + " AND `AdsBlockedBanner`" + (fingerprint.getAdsBlockedBanner() == null ? " IS NULL" : " = ?")
		 + " AND `AdsBlockedScript`" + (fingerprint.getAdsBlockedScript() == null ? " IS NULL" : " = ?")
		 + " AND `LikeShareFacebook`" + (fingerprint.getLikeShareFacebook() == null ? " IS NULL" : " = ?")
		 + " AND `LikeShareTwitter`" + (fingerprint.getLikeShareTwitter() == null ? " IS NULL" : " = ?")
		 + " AND `LikeShareReddit`" + (fingerprint.getLikeShareReddit() == null ? " IS NULL" : " = ?")
		 + " AND `Canvas`" + (fingerprint.getCanvas() == null ? " IS NULL" : " = ?")
		 + " AND `WebGLVendor`" + (fingerprint.getWebGLVendor() == null ? " IS NULL" : " = ?")
		 + " AND `WebGLRenderer`" + (fingerprint.getWebGLRenderer() == null ? " IS NULL" : " = ?")
		 + " AND `TouchPoints`" + (fingerprint.getTouchPoints() == null ? " IS NULL" : " = ?")
		 + " AND `TouchEvent`" + (fingerprint.getTouchEvent() == null ? " IS NULL" : " = ?")
		 + " AND `TouchStart`" + (fingerprint.getTouchStart() == null ? " IS NULL" : " = ?")
		 + " AND `AudioFingerprintPXI`" + (fingerprint.getAudioFingerprintPXI() == null ? " IS NULL" : " = ?")
		 + " AND `AudioFingerprintPXIFullBuffer`" + (fingerprint.getAudioFingerprintPXIFullBuffer() == null ? " IS NULL" : " = ?")
		 + " AND `AudioFingerprintNtVc`" + (fingerprint.getAudioFingerprintNtVc() == null ? " IS NULL" : " = ?")
		 + " AND `AudioFingerprintCC`" + (fingerprint.getAudioFingerprintCC() == null ? " IS NULL" : " = ?")
		 + " AND `AudioFingerprintHybrid`" + (fingerprint.getAudioFingerprintHybrid() == null ? " IS NULL" : " = ?") + ";";
		PreparedStatement checkExists = conn.prepareStatement(query);

		int index = 1;
		checkExists.setString(index, fingerprint.getSampleSetID());
		++index;

		if (fingerprint.getContrastLevel() != null) {
			checkExists.setInt(index, fingerprint.getContrastLevel());
			++index;
		}
		if (fingerprint.getUser_agent() != null) {
			checkExists.setString(index, fingerprint.getUser_agent());
			++index;
		}
		if (fingerprint.getAccept_headers() != null) {
			checkExists.setString(index, fingerprint.getAccept_headers());
			++index;
		}
		if (fingerprint.getPlatform() != null) {
			checkExists.setString(index, fingerprint.getPlatform());
			++index;
		}
		if (fingerprint.getPlatformFlash() != null) {
			checkExists.setString(index, fingerprint.getPlatformFlash());
			++index;
		}
		if (fingerprint.getPluginDetails() != null) {
			checkExists.setString(index, fingerprint.getPluginDetails());
			++index;
		}
		if (fingerprint.getTimeZone() != null) {
			checkExists.setString(index, fingerprint.getTimeZone());
			++index;
		}
		if (fingerprint.getScreenDetails() != null) {
			checkExists.setString(index, fingerprint.getScreenDetails());
			++index;
		}
		if (fingerprint.getScreenDetailsFlash() != null) {
			checkExists.setString(index, fingerprint.getScreenDetailsFlash());
			++index;
		}
		if (fingerprint.getScreenDetailsCSS() != null) {
			checkExists.setString(index, fingerprint.getScreenDetailsCSS());
			++index;
		}
		if (fingerprint.getLanguageFlash() != null) {
			checkExists.setString(index, fingerprint.getLanguageFlash());
			++index;
		}
		if (fingerprint.getFonts() != null) {
			checkExists.setString(index, fingerprint.getFonts());
			++index;
		}
		if (fingerprint.getFontsJS_CSS() != null) {
			checkExists.setString(index, fingerprint.getFontsJS_CSS());
			++index;
		}
		if (fingerprint.getCharSizes() != null) {
			checkExists.setString(index, fingerprint.getCharSizes());
			++index;
		}
		checkExists.setBoolean(index, fingerprint.isCookiesEnabled());
		++index;
		if (fingerprint.getSuperCookieLocalStorage() != null) {
			checkExists.setBoolean(index, fingerprint.getSuperCookieLocalStorage());
			++index;
		}
		if (fingerprint.getSuperCookieSessionStorage() != null) {
			checkExists.setBoolean(index, fingerprint.getSuperCookieSessionStorage());
			++index;
		}
		if (fingerprint.getSuperCookieUserData() != null) {
			checkExists.setBoolean(index, fingerprint.getSuperCookieUserData());
			++index;
		}
		if (fingerprint.getIndexedDBEnabled() != null) {
			checkExists.setBoolean(index, fingerprint.getIndexedDBEnabled());
			++index;
		}
		if (fingerprint.getDoNotTrack() != null) {
			checkExists.setString(index, fingerprint.getDoNotTrack());
			++index;
		}
		if (fingerprint.getClockDifference() != null) {
			checkExists.setLong(index, fingerprint.getClockDifference());
			++index;
		}
		if (fingerprint.getDateTime() != null) {
			checkExists.setString(index, fingerprint.getDateTime());
			++index;
		}
		if (fingerprint.getMathTan() != null) {
			checkExists.setString(index, fingerprint.getMathTan());
			++index;
		}
		checkExists.setBoolean(index, fingerprint.isUsingTor());
		++index;
		if (fingerprint.getTbbVersion() != null) {
			checkExists.setString(index, fingerprint.getTbbVersion());
			++index;
		}
		if (fingerprint.getAdsBlockedGoogle() != null) {
			checkExists.setBoolean(index, fingerprint.getAdsBlockedGoogle());
			++index;
		}
		if (fingerprint.getAdsBlockedBanner() != null) {
			checkExists.setBoolean(index, fingerprint.getAdsBlockedBanner());
			++index;
		}
		if (fingerprint.getAdsBlockedScript() != null) {
			checkExists.setBoolean(index, fingerprint.getAdsBlockedScript());
			++index;
		}
		if (fingerprint.getLikeShareFacebook() != null) {
			checkExists.setInt(index, fingerprint.getLikeShareFacebook());
			++index;
		}
		if (fingerprint.getLikeShareTwitter() != null) {
			checkExists.setInt(index, fingerprint.getLikeShareTwitter());
			++index;
		}
		if (fingerprint.getLikeShareReddit() != null) {
			checkExists.setInt(index, fingerprint.getLikeShareReddit());
			++index;
		}
		if (fingerprint.getCanvas() != null) {
			checkExists.setString(index, fingerprint.getCanvas());
			++index;
		}
		if (fingerprint.getWebGLVendor() != null) {
			checkExists.setString(index, fingerprint.getWebGLVendor());
			++index;
		}
		if (fingerprint.getWebGLRenderer() != null) {
			checkExists.setString(index, fingerprint.getWebGLRenderer());
			++index;
		}
		if (fingerprint.getTouchPoints() != null) {
			checkExists.setInt(index, fingerprint.getTouchPoints());
			++index;
		}
		if (fingerprint.getTouchEvent() != null) {
			checkExists.setBoolean(index, fingerprint.getTouchEvent());
			++index;
		}
		if (fingerprint.getTouchStart() != null) {
			checkExists.setBoolean(index, fingerprint.getTouchStart());
			++index;
		}
		if (fingerprint.getAudioFingerprintPXI() != null) {
			checkExists.setString(index, fingerprint.getAudioFingerprintPXI());
			++index;
		}
		if (fingerprint.getAudioFingerprintPXIFullBuffer() != null) {
			checkExists.setString(index, fingerprint.getAudioFingerprintPXIFullBuffer());
			++index;
		}
		if (fingerprint.getAudioFingerprintNtVc() != null) {
			checkExists.setString(index, fingerprint.getAudioFingerprintNtVc());
			++index;
		}
		if (fingerprint.getAudioFingerprintCC() != null) {
			checkExists.setString(index, fingerprint.getAudioFingerprintCC());
			++index;
		}
		if (fingerprint.getAudioFingerprintHybrid() != null) {
			checkExists.setString(index, fingerprint.getAudioFingerprintHybrid());
			++index;
		}

		ResultSet rs = checkExists.executeQuery();

		Integer sampleID = null;
		String sampleUUID = null;
		if (rs.next()) {
			/*
			 * We've seen this sample before and the fingerprint hasn't changed,
			 * don't log it.
			 */
			sampleID = rs.getInt(1);
			sampleUUID = rs.getString(2);
		}
		rs.close();
		checkExists.close();
		return new ImmutablePair<Integer, String>(sampleID, sampleUUID);
	}

	public static int getSampleCount(Connection conn) throws SQLException {
		PreparedStatement getSampleCount = conn.prepareStatement(getSampleCountStr);
		ResultSet rs = getSampleCount.executeQuery();
		rs.next();
		int sampleCount = rs.getInt(1);
		rs.close();
		return sampleCount;
	}
	
	/**
	 * Get number of samples for each version.
	 * Counts include all samples with version number higher than or equal to the version number in question.
	 * E.g. For version 1 we have all samples, since all samples are version 1 or higher.
	 * For version 2 we have all samples of version 2, version 3, version 4, ..., up until the latest version.
	 * We only need to use this version aware version of sample count because we're adding features to the live site, so there will be fingerprints from older versions.
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static TreeSet<VersionCount> getSampleCountVersionAware(Connection conn) throws SQLException {
		PreparedStatement getSampleCount = conn.prepareStatement(getSampleCountVersionAwareStr);
		
		TreeSet<VersionCount> counts = new TreeSet<VersionCount>();
		ResultSet rs = getSampleCount.executeQuery();
		while(rs.next()){
			counts.add(new VersionCount(rs.getInt(1), rs.getInt(2)));
		}
		rs.close();
		return counts;
	}

	/**
	 * Check whether a fingerprint with all the given details, including matching SampleID,
	 * is already inside the database.
	 * 
	 * @param conn
	 * @param fingerprint
	 * @return
	 * @throws SQLException
	 */
	private static int getSampleOccurrences(Connection conn, Fingerprint fingerprint) throws SQLException {
		/*
		 * We have seen this user before. Check if their fingerprint has changed.
		 */
		String query = "SELECT COUNT(*) FROM `Samples` WHERE"
		+ " `ContrastLevel` = ?"
		+ " AND `UserAgent`" + (fingerprint.getUser_agent() == null ? " IS NULL" : " = ?")
		+ " AND `AcceptHeaders`" + (fingerprint.getAccept_headers() == null ? " IS NULL" : " = ?")
		+ " AND `Platform`" + (fingerprint.getPlatform() == null ? " IS NULL" : " = ?")
		+ " AND `PlatformFlash`" + (fingerprint.getPlatformFlash() == null ? " IS NULL" : " = ?")
		+ " AND `PluginDetails`" + (fingerprint.getPluginDetails() == null ? " IS NULL" : " = ?")
		+ " AND `TimeZone`" + (fingerprint.getTimeZone() == null ? " IS NULL" : " = ?")
		/*+ " AND `ScreenDetails`" + (fingerprint.getScreenDetails() == null ? " IS NULL" : " = ?")*/
		+ " AND `ScreenDetailsFlash`" + (fingerprint.getScreenDetailsFlash() == null ? " IS NULL" : " = ?")
		/*+ " AND `ScreenDetailsCSS`" + (fingerprint.getScreenDetailsCSS() == null ? " IS NULL" : " = ?")*/
		+ " AND `LanguageFlash`" + (fingerprint.getLanguageFlash() == null ? " IS NULL" : " = ?")
		+ " AND `Fonts`" + (fingerprint.getFonts() == null ? " IS NULL" : " = ?")
		+ " AND `FontsJS_CSS`" + (fingerprint.getFontsJS_CSS() == null ? " IS NULL" : " = ?")
		/*+ " AND `CharSizes`" + (fingerprint.getCharSizes() == null ? " IS NULL" : " = ?")*/
		+ " AND `CookiesEnabled` = ?"
		+ " AND `SuperCookieLocalStorage`" + (fingerprint.getSuperCookieLocalStorage() == null ? " IS NULL" : " = ?")
		+ " AND `SuperCookieSessionStorage`" + (fingerprint.getSuperCookieSessionStorage() == null ? " IS NULL" : " = ?")
		+ " AND `SuperCookieUserData`" + (fingerprint.getSuperCookieUserData() == null ? " IS NULL" : " = ?")
		+ " AND `IndexedDBEnabled`" + (fingerprint.getIndexedDBEnabled() == null ? " IS NULL" : " = ?")
		+ " AND `DoNotTrack`" + (fingerprint.getDoNotTrack() == null ? " IS NULL" : " = ?")
		+ " AND `ClockDifference`" + (fingerprint.getClockDifference() == null ? " IS NULL" : " = ?")
		+ " AND `DateTime`" + (fingerprint.getDateTime() == null ? " IS NULL" : " = ?")
		+ " AND `MathTan`" + (fingerprint.getMathTan() == null ? " IS NULL" : " = ?")
		+ " AND `UsingTor` = ?"
		+ " AND `TbbVersion`" + (fingerprint.getTbbVersion() == null ? " IS NULL" : " = ?")
		+ " AND `AdsBlockedGoogle`" + (fingerprint.getAdsBlockedGoogle() == null ? " IS NULL" : " = ?")
		+ " AND `AdsBlockedBanner`" + (fingerprint.getAdsBlockedBanner() == null ? " IS NULL" : " = ?")
		+ " AND `AdsBlockedScript`" + (fingerprint.getAdsBlockedScript() == null ? " IS NULL" : " = ?")
		+ " AND `LikeShareFacebook`" + (fingerprint.getLikeShareFacebook() == null ? " IS NULL" : " = ?")
		+ " AND `LikeShareTwitter`" + (fingerprint.getLikeShareTwitter() == null ? " IS NULL" : " = ?")
		+ " AND `LikeShareReddit`" + (fingerprint.getLikeShareReddit() == null ? " IS NULL" : " = ?")
		/*+ " AND `Canvas`" + (fingerprint.getCanvas() == null ? " IS NULL" : " = ?")*/
		+ " AND `WebGLVendor`" + (fingerprint.getWebGLVendor() == null ? " IS NULL" : " = ?")
		+ " AND `WebGLRenderer`" + (fingerprint.getWebGLRenderer() == null ? " IS NULL" : " = ?")
		+ " AND `TouchPoints`" + (fingerprint.getTouchPoints() == null ? " IS NULL" : " = ?")
		+ " AND `TouchEvent`" + (fingerprint.getTouchEvent() == null ? " IS NULL" : " = ?")
		+ " AND `TouchStart`" + (fingerprint.getTouchStart() == null ? " IS NULL" : " = ?")
		/*+ " AND `AudioFingerprintPXI`" + (fingerprint.getAudioFingerprintPXI() == null ? " IS NULL" : " = ?")
		+ " AND `AudioFingerprintPXIFullBuffer`" + (fingerprint.getAudioFingerprintPXIFullBuffer() == null ? " IS NULL" : " = ?")
		+ " AND `AudioFingerprintNtVc`" + (fingerprint.getAudioFingerprintNtVc() == null ? " IS NULL" : " = ?")
		+ " AND `AudioFingerprintCC`" + (fingerprint.getAudioFingerprintCC() == null ? " IS NULL" : " = ?")
		+ " AND `AudioFingerprintHybrid`" + (fingerprint.getAudioFingerprintHybrid() == null ? " IS NULL" : " = ?")*/
		+ ";";
		PreparedStatement checkExists = conn.prepareStatement(query);

		int index = 1;
		if (fingerprint.getContrastLevel() != null) {
			checkExists.setInt(index, fingerprint.getContrastLevel());
			++index;
		}
		if (fingerprint.getUser_agent() != null) {
			checkExists.setString(index, fingerprint.getUser_agent());
			++index;
		}
		if (fingerprint.getAccept_headers() != null) {
			checkExists.setString(index, fingerprint.getAccept_headers());
			++index;
		}
		if (fingerprint.getPlatform() != null) {
			checkExists.setString(index, fingerprint.getPlatform());
			++index;
		}
		if (fingerprint.getPlatformFlash() != null) {
			checkExists.setString(index, fingerprint.getPlatformFlash());
			++index;
		}
		if (fingerprint.getPluginDetails() != null) {
			checkExists.setString(index, fingerprint.getPluginDetails());
			++index;
		}
		if (fingerprint.getTimeZone() != null) {
			checkExists.setString(index, fingerprint.getTimeZone());
			++index;
		}
		/*if (fingerprint.getScreenDetails() != null) {
			checkExists.setString(index, fingerprint.getScreenDetails());
			++index;
		}*/
		if (fingerprint.getScreenDetailsFlash() != null) {
			checkExists.setString(index, fingerprint.getScreenDetailsFlash());
			++index;
		}
		/*if (fingerprint.getScreenDetailsCSS() != null) {
			checkExists.setString(index, fingerprint.getScreenDetailsCSS());
			++index;
		}*/
		if (fingerprint.getLanguageFlash() != null) {
			checkExists.setString(index, fingerprint.getLanguageFlash());
			++index;
		}
		if (fingerprint.getFonts() != null) {
			checkExists.setString(index, fingerprint.getFonts());
			++index;
		}
		if (fingerprint.getFontsJS_CSS() != null) {
			checkExists.setString(index, fingerprint.getFontsJS_CSS());
			++index;
		}
		/*if (fingerprint.getCharSizes() != null) {
			checkExists.setString(index, fingerprint.getCharSizes());
			++index;
		}*/
		checkExists.setBoolean(index, fingerprint.isCookiesEnabled());
		++index;
		if (fingerprint.getSuperCookieLocalStorage() != null) {
			checkExists.setBoolean(index, fingerprint.getSuperCookieLocalStorage());
			++index;
		}
		if (fingerprint.getSuperCookieSessionStorage() != null) {
			checkExists.setBoolean(index, fingerprint.getSuperCookieSessionStorage());
			++index;
		}
		if (fingerprint.getSuperCookieUserData() != null) {
			checkExists.setBoolean(index, fingerprint.getSuperCookieUserData());
			++index;
		}
		if (fingerprint.getIndexedDBEnabled() != null) {
			checkExists.setBoolean(index, fingerprint.getIndexedDBEnabled());
			++index;
		}
		if (fingerprint.getDoNotTrack() != null) {
			checkExists.setString(index, fingerprint.getDoNotTrack());
			++index;
		}
		if (fingerprint.getClockDifference() != null) {
			checkExists.setLong(index, fingerprint.getClockDifference());
			++index;
		}
		if (fingerprint.getDateTime() != null) {
			checkExists.setString(index, fingerprint.getDateTime());
			++index;
		}
		if (fingerprint.getMathTan() != null) {
			checkExists.setString(index, fingerprint.getMathTan());
			++index;
		}
		checkExists.setBoolean(index, fingerprint.isUsingTor());
		++index;
		if (fingerprint.getTbbVersion() != null) {
			checkExists.setString(index, fingerprint.getTbbVersion());
			++index;
		}
		if (fingerprint.getAdsBlockedGoogle() != null) {
			checkExists.setBoolean(index, fingerprint.getAdsBlockedGoogle());
			++index;
		}
		if (fingerprint.getAdsBlockedBanner() != null) {
			checkExists.setBoolean(index, fingerprint.getAdsBlockedBanner());
			++index;
		}
		if (fingerprint.getAdsBlockedScript() != null) {
			checkExists.setBoolean(index, fingerprint.getAdsBlockedScript());
			++index;
		}
		if (fingerprint.getLikeShareFacebook() != null) {
			checkExists.setInt(index, fingerprint.getLikeShareFacebook());
			++index;
		}
		if (fingerprint.getLikeShareTwitter() != null) {
			checkExists.setInt(index, fingerprint.getLikeShareTwitter());
			++index;
		}
		if (fingerprint.getLikeShareReddit() != null) {
			checkExists.setInt(index, fingerprint.getLikeShareReddit());
			++index;
		}
		/*if (fingerprint.getCanvas() != null) {
			checkExists.setString(index, fingerprint.getCanvas());
			++index;
		}*/
		if (fingerprint.getWebGLVendor() != null) {
			checkExists.setString(index, fingerprint.getWebGLVendor());
			++index;
		}
		if (fingerprint.getWebGLRenderer() != null) {
			checkExists.setString(index, fingerprint.getWebGLRenderer());
			++index;
		}
		if (fingerprint.getTouchPoints() != null) {
			checkExists.setInt(index, fingerprint.getTouchPoints());
			++index;
		}
		if (fingerprint.getTouchEvent() != null) {
			checkExists.setBoolean(index, fingerprint.getTouchEvent());
			++index;
		}
		if (fingerprint.getTouchStart() != null) {
			checkExists.setBoolean(index, fingerprint.getTouchStart());
			++index;
		}
		/*if (fingerprint.getAudioFingerprintPXI() != null) {
			checkExists.setString(index, fingerprint.getAudioFingerprintPXI());
			++index;
		}
		if (fingerprint.getAudioFingerprintPXIFullBuffer() != null) {
			checkExists.setString(index, fingerprint.getAudioFingerprintPXIFullBuffer());
			++index;
		}
		if (fingerprint.getAudioFingerprintNtVc() != null) {
			checkExists.setString(index, fingerprint.getAudioFingerprintNtVc());
			++index;
		}
		if (fingerprint.getAudioFingerprintCC() != null) {
			checkExists.setString(index, fingerprint.getAudioFingerprintCC());
			++index;
		}
		if (fingerprint.getAudioFingerprintHybrid() != null) {
			checkExists.setString(index, fingerprint.getAudioFingerprintHybrid());
			++index;
		}*/

		ResultSet rs = checkExists.executeQuery();

		rs.next();
		int count = rs.getInt(1);
		rs.close();
		checkExists.close();
		return count;
	}

	/**
	 * Create the cookies enabled CharacteristicBean.
	 * 
	 * @param conn
	 *            A connection to the database.
	 * @param num_samples
	 *            The number of samples in the database.
	 * @param value
	 *            The value of this sample.
	 * @return
	 * @throws SQLException
	 */
	private static CharacteristicBean getCharacteristicBean(Connection conn, int num_samples, String dbname, Boolean value) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		PreparedStatement getCount;
		String querystr = "SELECT COUNT(*) FROM `Samples` WHERE `" + dbname + "`";
		if (value != null) {
			if (value) {
				chrbean.setValue("Yes");
			} else {
				chrbean.setValue("No");
			}
			querystr += " = ?;";

			getCount = conn.prepareStatement(querystr);
			getCount.setBoolean(1, value);
		} else {
			chrbean.setValue(NO_JAVASCRIPT);

			querystr += " IS NULL;";
			getCount = conn.prepareStatement(querystr);
		}

		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setNumOccurrences(count);
		chrbean.setInX(((double) num_samples) / ((double) count));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}

	/**
	 * Create a CharacteristicBean.
	 * 
	 * @param conn
	 *            A connection to the database.
	 * @param num_samples
	 *            The number of samples in the database.
	 * @param value
	 *            The value of this sample.
	 * @return
	 * @throws SQLException
	 */
	private static CharacteristicBean getCharacteristicBean(Connection conn, int num_samples, String dbname, String value) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		PreparedStatement getCount;
		String querystr = "SELECT COUNT(*) FROM `Samples` WHERE `" + dbname + "`";
		if (value != null) {
			chrbean.setValue(StringEscapeUtils.escapeHtml4(value));
			querystr += " = ?;";

			getCount = conn.prepareStatement(querystr);
			getCount.setString(1, value);
		} else {
			chrbean.setValue(NO_JAVASCRIPT);

			querystr += " IS NULL;";
			getCount = conn.prepareStatement(querystr);
		}

		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setNumOccurrences(count);
		chrbean.setInX(((double) num_samples) / ((double) count));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}

	/**
	 * Create a CharacteristicBean.
	 * 
	 * @param conn
	 *            A connection to the database.
	 * @param num_samples
	 *            The number of samples in the database.
	 * @param value
	 *            The value of this sample.
	 * @return
	 * @throws SQLException
	 */
	private static CharacteristicBean getCharacteristicBean(Connection conn, int num_samples, String dbname, Integer value) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		PreparedStatement getCount;
		String querystr = "SELECT COUNT(*) FROM `Samples` WHERE `" + dbname + "`";
		if (value != null) {
			chrbean.setValue(value.toString());
			querystr += " = ?;";

			getCount = conn.prepareStatement(querystr);
			getCount.setInt(1, value);
		} else {
			chrbean.setValue(NO_JAVASCRIPT);

			querystr += " IS NULL;";
			getCount = conn.prepareStatement(querystr);
		}

		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setNumOccurrences(count);
		chrbean.setInX(((double) num_samples) / ((double) count));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}

	/**
	 * Create a CharacteristicBean.
	 * 
	 * @param conn
	 *            A connection to the database.
	 * @param num_samples
	 *            The number of samples in the database.
	 * @param value
	 *            The value of this sample.
	 * @return
	 * @throws SQLException
	 */
	private static CharacteristicBean getCharacteristicBean(Connection conn, int num_samples, String dbname, Long value) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		PreparedStatement getCount;
		String querystr = "SELECT COUNT(*) FROM `Samples` WHERE `" + dbname + "`";
		if (value != null) {
			chrbean.setValue(value.toString());
			querystr += " = ?;";

			getCount = conn.prepareStatement(querystr);
			getCount.setLong(1, value);
		} else {
			chrbean.setValue(NO_JAVASCRIPT);

			querystr += " IS NULL;";
			getCount = conn.prepareStatement(querystr);
		}

		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setNumOccurrences(count);
		chrbean.setInX(((double) num_samples) / ((double) count));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}
	
	/**
	 * Create the super cookie CharacteristicBean.
	 * 
	 * @param conn
	 *            A connection to the database.
	 * @param num_samples
	 *            The number of samples in the database.
	 * @param value
	 *            The value of this sample.
	 * @return
	 * @throws SQLException
	 */
	private static CharacteristicBean getSuperCookieCharacteristicBean(Connection conn, int num_samples, Fingerprint fingerprint) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		PreparedStatement getCount;
		String querystr = "SELECT COUNT(*) FROM `Samples` WHERE"
		+ " `SuperCookieLocalStorage`" + (fingerprint.getSuperCookieLocalStorage() == null ? " IS NULL": " = ?")
		+ " AND `SuperCookieSessionStorage`" + (fingerprint.getSuperCookieSessionStorage() == null ? " IS NULL": " = ?")
		+ " AND `SuperCookieUserData`" + (fingerprint.getSuperCookieUserData() == null ? " IS NULL": " = ?");
		getCount = conn.prepareStatement(querystr);
		
		int index = 1;
		
		String superCookieStr = "DOM localStorage: ";
		if(fingerprint.getSuperCookieLocalStorage() == null && fingerprint.getSuperCookieSessionStorage() == null && fingerprint.getSuperCookieUserData() == null){
			superCookieStr = NO_JAVASCRIPT;
		}
		else{
			if (fingerprint.getSuperCookieLocalStorage() != null) {
				if (fingerprint.getSuperCookieLocalStorage()) {
					superCookieStr += "Yes";
				} else {
					superCookieStr += "No";
				}
				getCount.setBoolean(index, fingerprint.getSuperCookieLocalStorage());
				++index;
			}
			else{
				superCookieStr += "NoJS";
			}
			superCookieStr += ", ";
			
			superCookieStr += "DOM sessionStorage: ";
			if (fingerprint.getSuperCookieSessionStorage() != null) {
				if (fingerprint.getSuperCookieSessionStorage()) {
					superCookieStr += "Yes";
				} else {
					superCookieStr += "No";
				}
				getCount.setBoolean(index, fingerprint.getSuperCookieSessionStorage());
				++index;
			}
			else{
				superCookieStr += "NoJS";
			}
			superCookieStr += ", ";
			
			superCookieStr += "IE userData: ";
			if (fingerprint.getSuperCookieUserData() != null) {
				if (fingerprint.getSuperCookieUserData()) {
					superCookieStr += "Yes";
				} else {
					superCookieStr += "No";
				}
				getCount.setBoolean(index, fingerprint.getSuperCookieUserData());
				++index;
			}
			else{
				superCookieStr += "NoJS";
			}
		}
		chrbean.setValue(superCookieStr);

		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setNumOccurrences(count);
		chrbean.setInX(((double) num_samples) / ((double) count));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}
	
	/**
	 * Create the ads blocked CharacteristicBean.
	 * 
	 * @param conn
	 *            A connection to the database.
	 * @param num_samples
	 *            The number of samples in the database.
	 * @param value
	 *            The value of this sample.
	 * @return
	 * @throws SQLException
	 */
	private static CharacteristicBean getAdsBlockedCharacteristicBean(Connection conn, int num_samples, Fingerprint fingerprint) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		PreparedStatement getCount;
		String querystr = "SELECT COUNT(*) FROM `Samples` WHERE"
		+ " `AdsBlockedGoogle`" + (fingerprint.getAdsBlockedGoogle() == null ? " IS NULL": " = ?")
		+ " AND `AdsBlockedBanner`" + (fingerprint.getAdsBlockedBanner() == null ? " IS NULL": " = ?")
		+ " AND `AdsBlockedScript`" + (fingerprint.getAdsBlockedScript() == null ? " IS NULL": " = ?");
		getCount = conn.prepareStatement(querystr);
		
		int index = 1;
		
		String adsBlockedStr = "Google ad: ";
		if(fingerprint.getAdsBlockedGoogle() == null && fingerprint.getAdsBlockedBanner() == null && fingerprint.getAdsBlockedScript() == null){
			adsBlockedStr = NO_JAVASCRIPT;
		}
		else{
			if (fingerprint.getAdsBlockedGoogle() != null) {
				if (fingerprint.getAdsBlockedGoogle()) {
					adsBlockedStr += "Blocked";
				} else {
					adsBlockedStr += "Not blocked";
				}
				getCount.setBoolean(index, fingerprint.getAdsBlockedGoogle());
				++index;
			}
			else{
				adsBlockedStr += "NoJS";
			}
			adsBlockedStr += ", ";
			
			adsBlockedStr += "Banner ad: ";
			if (fingerprint.getAdsBlockedBanner() != null) {
				if (fingerprint.getAdsBlockedBanner()) {
					adsBlockedStr += "Blocked";
				} else {
					adsBlockedStr += "Not blocked";
				}
				getCount.setBoolean(index, fingerprint.getAdsBlockedBanner());
				++index;
			}
			else{
				adsBlockedStr += "NoJS";
			}
			adsBlockedStr += ", ";
			
			adsBlockedStr += "Ad script: ";
			if (fingerprint.getAdsBlockedScript() != null) {
				if (fingerprint.getAdsBlockedScript()) {
					adsBlockedStr += "Blocked";
				} else {
					adsBlockedStr += "Not blocked";
				}
				getCount.setBoolean(index, fingerprint.getAdsBlockedScript());
				++index;
			}
			else{
				adsBlockedStr += "NoJS";
			}
		}
		chrbean.setValue(adsBlockedStr);

		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setNumOccurrences(count);
		chrbean.setInX(((double) num_samples) / ((double) count));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}
	
	/**
	 * Create the ads blocked CharacteristicBean.
	 * 
	 * @param conn
	 *            A connection to the database.
	 * @param num_samples
	 *            The number of samples in the database.
	 * @param value
	 *            The value of this sample.
	 * @return
	 * @throws SQLException
	 */
	private static CharacteristicBean getLikeShareCharacteristicBean(Connection conn, int num_samples, Fingerprint fingerprint) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		PreparedStatement getCount;
		String querystr = "SELECT COUNT(*) FROM `Samples` WHERE"
		+ " `LikeShareFacebook`" + (fingerprint.getLikeShareFacebook() == null ? " IS NULL": " = ?")
		+ " AND `LikeShareTwitter`" + (fingerprint.getLikeShareTwitter() == null ? " IS NULL": " = ?")
		+ " AND `LikeShareReddit`" + (fingerprint.getLikeShareReddit() == null ? " IS NULL": " = ?");
		getCount = conn.prepareStatement(querystr);
		
		int index = 1;
		
		String likeShareBlockedStr = "Facebook button: ";
		if(fingerprint.getLikeShareFacebook() == null && fingerprint.getLikeShareTwitter() == null && fingerprint.getLikeShareReddit() == null){
			likeShareBlockedStr = NO_JAVASCRIPT;
		}
		else{
			if (fingerprint.getLikeShareFacebook() != null) {
				if (fingerprint.getLikeShareFacebook() == 1) {
					likeShareBlockedStr += "Replaced by Privacy Badger or similar";
				} else if(fingerprint.getLikeShareFacebook() == 2) {
					likeShareBlockedStr += "Blocked by script blocker";
				}
				else if(fingerprint.getLikeShareFacebook() == 3){
					likeShareBlockedStr += "Blocked by Adblock Plus Anti-Social list or similar";
				}
				else if(fingerprint.getLikeShareFacebook() == 0){
					likeShareBlockedStr += "Not blocked";
				}
				else{
					likeShareBlockedStr += "Invalid";
				}
				getCount.setInt(index, fingerprint.getLikeShareFacebook());
				++index;
			}
			else{
				likeShareBlockedStr += "NoJS";
			}
			likeShareBlockedStr += ",<br/>\n";
			
			likeShareBlockedStr += "Twitter share button: ";
			if (fingerprint.getLikeShareTwitter() != null) {
				if (fingerprint.getLikeShareTwitter() == 1) {
					likeShareBlockedStr += "Replaced by Privacy Badger or similar";
				}
				else if (fingerprint.getLikeShareTwitter() == 2) {
					likeShareBlockedStr += "Blocked by script blocker or Adblock Plus Anti-Social list or similar";
				}
				else if (fingerprint.getLikeShareTwitter() == 0) {
					likeShareBlockedStr += "Not blocked";
				}
				else {
					likeShareBlockedStr += "Invalid";
				}
				getCount.setInt(index, fingerprint.getLikeShareTwitter());
				++index;
			}
			else{
				likeShareBlockedStr += "NoJS";
			}
			likeShareBlockedStr += ",<br/>\n";
			
			likeShareBlockedStr += "Reddit button: ";
			if (fingerprint.getLikeShareReddit() != null) {
				if (fingerprint.getLikeShareReddit() == 2) {
					likeShareBlockedStr += "Blocked by script blocker or Adblock Plus Anti-Social list or similar";
				} 
				else if (fingerprint.getLikeShareReddit() == 4) {
					likeShareBlockedStr += "Blocked by unknown";
				}
				else if (fingerprint.getLikeShareReddit() == 0) {
					likeShareBlockedStr += "Not blocked";
				}
				else {
					likeShareBlockedStr += "Invalid";
				}
				getCount.setInt(index, fingerprint.getLikeShareReddit());
				++index;
			}
			else{
				likeShareBlockedStr += "NoJS";
			}
		}
		chrbean.setValue(likeShareBlockedStr);

		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setNumOccurrences(count);
		chrbean.setInX(((double) num_samples) / ((double) count));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}
	
	/**
	 * Create the touch CharacteristicBean.
	 * 
	 * @param conn
	 *            A connection to the database.
	 * @param num_samples
	 *            The number of samples in the database.
	 * @param value
	 *            The value of this sample.
	 * @return
	 * @throws SQLException
	 */
	private static CharacteristicBean getTouchCharacteristicBean(Connection conn, int num_samples, Fingerprint fingerprint) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		PreparedStatement getCount;
		String querystr = "SELECT COUNT(*) FROM `Samples` WHERE"
		+ " `TouchPoints`" + (fingerprint.getTouchPoints() == null ? " IS NULL": " = ?")
		+ " AND `TouchEvent`" + (fingerprint.getTouchEvent() == null ? " IS NULL": " = ?")
		+ " AND `TouchStart`" + (fingerprint.getTouchStart() == null ? " IS NULL": " = ?");
		getCount = conn.prepareStatement(querystr);
		
		int index = 1;
		
		String touchStr;
		if(fingerprint.getTouchPoints() == null && fingerprint.getTouchEvent() == null && fingerprint.getTouchStart() == null){
			touchStr = NO_JAVASCRIPT;
		}
		else{
			touchStr = "Max touchpoints: ";
			if(fingerprint.getTouchPoints() != null){
				touchStr += fingerprint.getTouchPoints();
				getCount.setInt(index, fingerprint.getTouchPoints());
				++index;
			}
			else{
				touchStr += "NoJS";
			}
			
			touchStr += "; TouchEvent supported: ";
			if(fingerprint.getTouchEvent() != null){
				touchStr += fingerprint.getTouchEvent();
				getCount.setBoolean(index, fingerprint.getTouchEvent());
				++index;
			}
			else{
				touchStr += "NoJS";
			}
			
			touchStr += "; onTouchStart supported: ";
			if(fingerprint.getTouchStart() != null){
				touchStr += fingerprint.getTouchStart();
				getCount.setBoolean(index, fingerprint.getTouchStart());
				++index;
			}
			else{
				touchStr += "NoJS";
			}
		}
		chrbean.setValue(touchStr);

		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setNumOccurrences(count);
		chrbean.setInX(((double) num_samples) / ((double) count));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}

	/**
	 * Create the audio fingerprint CharacteristicBean.
	 * 
	 * @param conn
	 *            A connection to the database.
	 * @param num_samples
	 *            The number of samples in the database.
	 * @param value
	 *            The value of this sample.
	 * @return
	 * @throws SQLException
	 */
	private static CharacteristicBean getAudioTestsCharacteristicBean(Connection conn, int num_samples, Fingerprint fingerprint) throws SQLException {
		CharacteristicBean chrbean = new CharacteristicBean();

		PreparedStatement getCount;
		String querystr = "SELECT COUNT(*) FROM `Samples` WHERE"
		+ " `AudioFingerprintPXI`" + (fingerprint.getAudioFingerprintPXI() == null ? " IS NULL": " = ?")
		+ " AND `AudioFingerprintPXIFullBuffer`" + (fingerprint.getAudioFingerprintPXIFullBuffer() == null ? " IS NULL": " = ?")
		+ " AND `AudioFingerprintNtVc`" + (fingerprint.getAudioFingerprintNtVc() == null ? " IS NULL": " = ?")
		+ " AND `AudioFingerprintCC`" + (fingerprint.getAudioFingerprintCC() == null ? " IS NULL": " = ?")
		+ " AND `AudioFingerprintHybrid`" + (fingerprint.getAudioFingerprintHybrid() == null ? " IS NULL": " = ?");
		getCount = conn.prepareStatement(querystr);
		
		int index = 1;
		
		String touchStr;
		if(fingerprint.getAudioFingerprintPXI() == null && fingerprint.getAudioFingerprintPXIFullBuffer() == null && fingerprint.getAudioFingerprintNtVc() == null
				&& fingerprint.getAudioFingerprintCC() == null && fingerprint.getAudioFingerprintHybrid() == null){
			touchStr = NO_JAVASCRIPT;
		}
		else{
			touchStr = "<b>Fingerprint using DynamicsCompressor (sum of buffer values):</b><br> ";
			if(fingerprint.getAudioFingerprintPXI() != null){
				touchStr += fingerprint.getAudioFingerprintPXI();
				getCount.setString(index, fingerprint.getAudioFingerprintPXI());
				++index;
			}
			else{
				touchStr += "NoJS";
			}
			
			touchStr += "<br><b>Fingerprint using DynamicsCompressor (hash of full buffer):</b><br> ";
			if(fingerprint.getAudioFingerprintPXIFullBuffer() != null){
				touchStr += fingerprint.getAudioFingerprintPXIFullBuffer();
				getCount.setString(index, fingerprint.getAudioFingerprintPXIFullBuffer());
				++index;
			}
			else if(fingerprint.getAudioFingerprintPXI().equals("Not supported") || fingerprint.getAudioFingerprintPXI().equals("Error")){
				touchStr += "Error or not supported";
			}
			else{
				touchStr += "NoJS";
			}
			
			touchStr += "<br><b>AudioContext properties:</b><br> ";
			if(fingerprint.getAudioFingerprintNtVc() != null){
				touchStr += fingerprint.getAudioFingerprintNtVc();
				getCount.setString(index, fingerprint.getAudioFingerprintNtVc());
				++index;
			}
			else{
				touchStr += "NoJS";
			}
			
			touchStr += "<br><b>Fingerprint using OscillatorNode:</b><br> ";
			if(fingerprint.getAudioFingerprintCC() != null){
				touchStr += fingerprint.getAudioFingerprintCC();
				getCount.setString(index, fingerprint.getAudioFingerprintCC());
				++index;
			}
			else{
				touchStr += "NoJS";
			}
			
			touchStr += "<br><b>Fingerprint using hybrid of OscillatorNode/DynamicsCompressor method:</b><br> ";
			if(fingerprint.getAudioFingerprintHybrid() != null){
				touchStr += fingerprint.getAudioFingerprintHybrid();
				getCount.setString(index, fingerprint.getAudioFingerprintHybrid());
				++index;
			}
			else{
				touchStr += "NoJS";
			}
		}
		chrbean.setValue(touchStr);

		ResultSet rs = getCount.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		chrbean.setNumOccurrences(count);
		chrbean.setInX(((double) num_samples) / ((double) count));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}
	
	public static HistoryListBean getSampleSetIDsHistory(String sampleSetID, ServletContext context) throws ServletException {
		HistoryListBean history = new HistoryListBean();
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(true);

			if (sampleSetID == null) {
				/*
				 * No sampleSetID means no history.
				 */
				return history;
			}

			PreparedStatement getHistory = conn.prepareStatement(selectSampleSetIDHistory);
			getHistory.setString(1, sampleSetID);

			ResultSet rs = getHistory.executeQuery();
			SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss z");
			dateformat.setTimeZone(TimeZone.getTimeZone("UTC"));
			while (rs.next()) {
				Timestamp timestamp = rs.getTimestamp(2);
				history.addHistoryBean(new HistoryBean(rs.getString(1), dateformat.format(timestamp)));
			}
			rs.close();
			getHistory.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close the connection
			// Finally triggers even if we return
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// Ignore
				}
			}
		}
		return history;
	}

	private static Fingerprint getFingerprintFromSampleID(Connection conn, String sampleUUID) throws SQLException {
		PreparedStatement getFingerprint = conn.prepareStatement(selectSampleStr);
		getFingerprint.setString(1, sampleUUID);

		ResultSet rs = getFingerprint.executeQuery();
		if (!rs.next()) {
			// No sample found with that sampleID.
			return null;
		}

		Fingerprint fingerprint = new Fingerprint();

		int index = 1;
		// ColourVision
		fingerprint.setContrastLevel(rs.getInt(index));
		++index;
		// UserAgent
		fingerprint.setUser_agent(rs.getString(index));
		++index;
		// AcceptHeaders
		fingerprint.setAccept_headers(rs.getString(index));
		++index;
		// Platform
		fingerprint.setPlatform(rs.getString(index));
		++index;
		// PlatformFlash
		fingerprint.setPlatformFlash(rs.getString(index));
		++index;
		// PluginDetails
		fingerprint.setPluginDetails(rs.getString(index));
		++index;
		// TimeZone
		fingerprint.setTimeZone(rs.getString(index));
		++index;
		// ScreenDetails
		fingerprint.setScreenDetails(rs.getString(index));
		++index;
		// ScreenDetailsFlash
		fingerprint.setScreenDetailsFlash(rs.getString(index));
		++index;
		// ScreenDetailsCSS
		fingerprint.setScreenDetailsCSS(rs.getString(index));
		++index;
		// LanguageFlash
		fingerprint.setLanguageFlash(rs.getString(index));
		++index;
		// Fonts
		fingerprint.setFonts(rs.getString(index));
		++index;
		// FontsJS_CSS
		fingerprint.setFontsJS_CSS(rs.getString(index));
		++index;
		// CharSizes
		fingerprint.setCharSizes(rs.getString(index));
		++index;
		// CookiesEnabled
		fingerprint.setCookiesEnabled(rs.getBoolean(index));
		++index;
		// SuperCookieLocalStorage
		fingerprint.setSuperCookieLocalStorage(rs.getBoolean(index));
		if (rs.wasNull()) {
			fingerprint.setSuperCookieLocalStorage(null);
		}
		++index;
		// SuperCookieSessionStorage
		fingerprint.setSuperCookieSessionStorage(rs.getBoolean(index));
		if (rs.wasNull()) {
			fingerprint.setSuperCookieSessionStorage(null);
		}
		++index;
		// SuperCookieUserData
		fingerprint.setSuperCookieUserData(rs.getBoolean(index));
		if (rs.wasNull()) {
			fingerprint.setSuperCookieUserData(null);
		}
		++index;
		// IndexedDBEnabled
		fingerprint.setIndexedDBEnabled(rs.getBoolean(index));
		if (rs.wasNull()) {
			fingerprint.setIndexedDBEnabled(null);
		}
		++index;
		// DoNotTrack
		fingerprint.setDoNotTrack(rs.getString(index));
		++index;
		// ClockDifference
		fingerprint.setClockDifference(rs.getLong(index));
		if (rs.wasNull()) {
			fingerprint.setClockDifference(null);
		}
		++index;
		// DateTime
		fingerprint.setDateTime(rs.getString(index));
		++index;
		// MathTan
		fingerprint.setMathTan(rs.getString(index));
		++index;
		// UsingTor
		fingerprint.setUsingTor(rs.getBoolean(index));
		++index;
		// TbbVersion
		fingerprint.setTbbVersion(rs.getString(index));
		++index;
		// AdsBlockedGoogle
		fingerprint.setAdsBlockedGoogle(rs.getBoolean(index));
		if (rs.wasNull()) {
			fingerprint.setAdsBlockedGoogle(null);
		}
		++index;
		// AdsBlockedBanner
		fingerprint.setAdsBlockedBanner(rs.getBoolean(index));
		if (rs.wasNull()) {
			fingerprint.setAdsBlockedBanner(null);
		}
		++index;
		// AdsBlockedScript
		fingerprint.setAdsBlockedScript(rs.getBoolean(index));
		if (rs.wasNull()) {
			fingerprint.setAdsBlockedScript(null);
		}
		++index;
		// LikeShareFacebook
		fingerprint.setLikeShareFacebook(rs.getInt(index));
		if (rs.wasNull()) {
			fingerprint.setLikeShareFacebook(null);
		}
		++index;
		// LikeShareTwitter
		fingerprint.setLikeShareTwitter(rs.getInt(index));
		if (rs.wasNull()) {
			fingerprint.setLikeShareTwitter(null);
		}
		++index;
		// LikeShareReddit
		fingerprint.setLikeShareReddit(rs.getInt(index));
		if (rs.wasNull()) {
			fingerprint.setLikeShareReddit(null);
		}
		++index;
		// Canvas
		fingerprint.setCanvas(rs.getString(index));
		++index;
		// WebGLVendor
		fingerprint.setWebGLVendor(rs.getString(index));
		++index;
		// WebGLRenderer
		fingerprint.setWebGLRenderer(rs.getString(index));
		++index;
		// TouchPoints
		fingerprint.setTouchPoints(rs.getInt(index));
		if (rs.wasNull()) {
			fingerprint.setTouchPoints(null);
		}
		++index;
		// TouchEvent
		fingerprint.setTouchEvent(rs.getBoolean(index));
		if (rs.wasNull()) {
			fingerprint.setTouchEvent(null);
		}
		++index;
		// TouchStart
		fingerprint.setTouchStart(rs.getBoolean(index));
		if (rs.wasNull()) {
			fingerprint.setTouchStart(null);
		}
		++index;
		// AudioFingerprintPXI
		fingerprint.setAudioFingerprintPXI(rs.getString(index));
		++index;
		// AudioFingerprintPXIFullBuffer
		fingerprint.setAudioFingerprintPXIFullBuffer(rs.getString(index));
		++index;
		// AudioFingerprintNtVc
		fingerprint.setAudioFingerprintNtVc(rs.getString(index));
		++index;
		// AudioFingerprintCC
		fingerprint.setAudioFingerprintCC(rs.getString(index));
		++index;
		// AudioFingerprintHybrid
		fingerprint.setAudioFingerprintHybrid(rs.getString(index));
		++index;

		rs.close();
		getFingerprint.close();

		return fingerprint;
	}
}

class VersionCount implements Comparable<VersionCount>{
	private int version;
	private int count;
	
	public VersionCount(int version){
		this.version = version;
		this.count = 0;
	}
	
	public VersionCount(int version, int count){
		this.version = version;
		this.count = count;
	}

	@Override
	public int compareTo(VersionCount o) {
		return this.version - o.version;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}