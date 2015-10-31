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
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import beans.CharacteristicBean;
import beans.CharacteristicsBean;
import beans.HistoryBean;
import beans.HistoryListBean;
import beans.UniquenessBean;
import datastructures.Fingerprint;

public class FingerprintDAO {
	private static final String insertSampleStr = "INSERT INTO `Samples`(`SampleUUID`, `IP`, `TimeStamp`, `UserAgent`, `AcceptHeaders`, `Platform`, `PlatformFlash`, `PluginDetails`, `TimeZone`, `ScreenDetails`, `ScreenDetailsFlash`, `LanguageFlash`, `Fonts`, `CharSizes`, `CookiesEnabled`, `SuperCookie`, `DoNotTrack`, `ClockDifference`, `DateTime`, `MathTan`, `UsingTor`, `AdsBlocked`, `Canvas`, `WebGLVendor`, `WebGLRenderer`) VALUES(?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	private static final String getSampleCountStr = "SELECT COUNT(*) FROM `Samples`;";
	private static final String selectSampleStr = "SELECT `UserAgent`, `AcceptHeaders`, `Platform`, `PlatformFlash`, `PluginDetails`, `TimeZone`, `ScreenDetails`, `ScreenDetailsFlash`, `LanguageFlash`, `Fonts`, `CharSizes`, `CookiesEnabled`, `SuperCookie`, `DoNotTrack`, `ClockDifference`, `DateTime`, `MathTan`, `UsingTor`, `AdsBlocked`, `Canvas`, `WebGLVendor`, `WebGLRenderer` FROM `Samples` WHERE `SampleUUID` = ?;";
	private static final String selectSampleSetIDHistory = "SELECT `SampleUUID`, `Timestamp` FROM `SampleSets` INNER JOIN `Samples` USING (`SampleID`) WHERE `SampleSetID` = ?;";

	private static final String NO_JAVASCRIPT = "No JavaScript";
	private static final String NOT_SUPPORTED = "Not supported";

	public static final Integer processFingerprint(Fingerprint fingerprint, CharacteristicsBean chrsbean, UniquenessBean uniquenessbean) {
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(false);

			/*
			 * Check if we've seen this sample before.
			 */
			Integer sampleID = checkSampleChanged(conn, fingerprint);

			if (sampleID == null) {
				/*
				 * We haven't seen this sample before.
				 * Record it.
				 */
				sampleID = insertSample(conn, fingerprint);

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

			return sampleID;
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
		int sampleCount = getSampleCount(conn);

		/*
		 * Get uniqueness.
		 */
		int sampleOccurrences = getSampleOccurrences(conn, fingerprint);
		if (sampleOccurrences == 1) {
			uniquenessbean.setUnique(true);
		} else {
			uniquenessbean.setUnique(false);
		}
		uniquenessbean.setNum_samples(sampleCount);
		uniquenessbean.setInX(((double) sampleCount) / ((double) sampleOccurrences));
		uniquenessbean.setBits(Math.abs(Math.log(uniquenessbean.getInX()) / Math.log(2)));

		/*
		 * Get each characteristic.
		 */
		ArrayList<CharacteristicBean> characteristics = chrsbean.getCharacteristics();
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "UserAgent", fingerprint.getUser_agent());
			bean.setName("User Agent");
			bean.setNameHoverText("The User-Agent header sent with the HTTP request for the page.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "AcceptHeaders", fingerprint.getAccept_headers());
			bean.setName("HTTP_ACCEPT Headers");
			bean.setNameHoverText("The concatenation of three headers from the HTTP request:" + " The Accept request header, the Accept-Encoding request header, and the Accept-Language request header.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "Platform", fingerprint.getPlatform());
			bean.setName("Platform (JavaScript)");
			bean.setNameHoverText("The name of the platform the browser is running on, detected using JavaScript.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "PlatformFlash", fingerprint.getPlatformFlash());
			bean.setName("Platform (Flash)");
			bean.setNameHoverText("The name of the platform the browser is running on, detected using Flash.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "PluginDetails", fingerprint.getPluginDetails());
			bean.setName("Browser Plugin Details");
			bean.setNameHoverText("A list of the browsers installed plugins as detected using JavaScript.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "TimeZone", fingerprint.getTimeZone());
			bean.setName("Time Zone");
			bean.setNameHoverText("The time-zone configured on the client's machine.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "ScreenDetails", fingerprint.getScreenDetails());
			bean.setName("Screen Size and Color Depth");
			bean.setNameHoverText("The screen size and colour depth of the monitor displaying the client's web browser.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "ScreenDetailsFlash", fingerprint.getScreenDetailsFlash());
			bean.setName("Screen Size (Flash)");
			bean.setNameHoverText("The resolution of the client's monitor(s)." + " Different from the other screen size test in that this number can be the cumulative resolution of the monitors in multiple monitor set ups.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "LanguageFlash", fingerprint.getLanguageFlash());
			bean.setName("Language (Flash)");
			bean.setNameHoverText("The language of the client's browser, as detected using Flash.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "Fonts", fingerprint.getFonts());
			if (bean.getValue().equals("")) {
				bean.setValue("No fonts detected");
			}
			bean.setName("System Fonts");
			bean.setNameHoverText("The fonts installed on the client's machine, detected using Flash.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "CharSizes", fingerprint.getCharSizes());
			bean.setName("Character Sizes");
			bean.setNameHoverText("The height and width of a set of Unicode characters when rendered with a set of different styles (e.g. sans-serif).");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "CookiesEnabled", fingerprint.isCookiesEnabled());
			bean.setName("Are Cookies Enabled?");
			bean.setNameHoverText("Whether cookies are enabled.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "SuperCookie", fingerprint.getSuperCookie());
			bean.setName("Limited supercookie test");
			bean.setNameHoverText("Three tests of whether DOM storage is supported (and enabled) in the client's web browser." + " Tests for localStorage, sessionStorage, and Internet Explorer's userData.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "DoNotTrack", fingerprint.getDoNotTrack());
			if (bean.getValue().equals(NO_JAVASCRIPT)) {
				bean.setValue("No preference");
			}
			bean.setName("Do Not Track header");
			bean.setNameHoverText("The value of the DNT (Do Not Track) header from the HTTP request.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "ClockDifference", fingerprint.getClockDifference());
			bean.setName("Client/server time difference (minutes)");
			bean.setNameHoverText("The approximate amount of difference between the time on the client's computer and the clock on the server." + " i.e., the clock on the client's computer is 5 minutes ahead of the clock on the server.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "DateTime", fingerprint.getDateTime());
			bean.setName("Date/Time format");
			bean.setNameHoverText("When the JavaScript function toLocaleString() is called on a date it can reveal information about the language of the browser via the names of days and months." + " For instance the output 'Thursday January 01, 10:30:00 GMT+1030 1970' reveals that English is our configured language because 'Thursday' is English." + " Additionally different browsers tend to return differently formatted results." + " For instance Opera returns the above whereas Firefox returns '1/1/1970 9:30:00 am' for the same date (UNIX epoch)." + " Additionally timezone information may be revealed." + " For instance the above were taken on a computer configured for ACST (+9:30), which is why the times shown aren't midnight.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "MathTan", fingerprint.getMathTan());
			bean.setName("Math/Tan function");
			bean.setNameHoverText("The same math functions run on different platforms and browsers can produce different results." + " In particular we are interested in the output of Math.tan(-1e300), which has been observed to produce different values depending on operating system." + " For instance on a 64bit Linux machine it produces the value -1.4214488238747245 and on a Windows machine it produces the value -4.987183803371025.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "UsingTor", fingerprint.isUsingTor());
			bean.setName("Using Tor?");
			bean.setNameHoverText("Checks whether a client's request came from a Tor exit node, and hence whether they're using Tor." + " It does so by performing a TorDNSEL request for each client.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "AdsBlocked", fingerprint.getAdsBlocked());
			bean.setName("Ads blocked?");
			bean.setNameHoverText("Checks whether ad blocking software is installed." + " It does so by attempting to display an ad and checking whether it was successful.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "Canvas", fingerprint.getCanvas());
			bean.setName("Canvas");
			if (bean.getValue().equals(NO_JAVASCRIPT) == false && bean.getValue().equals(NOT_SUPPORTED) == false) {
				bean.setValue("<img width=\"400\" height=\"60\" src=\"" + bean.getValue() + "\">");
			}
			bean.setNameHoverText("Rendering of a specific picture with the HTML5 Canvas element following a fixed set of instructions." + " The picture presents some slight noticeable variations depending on the OS and the browser used.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "WebGLVendor", fingerprint.getWebGLVendor());
			bean.setName("WebGL Vendor");
			bean.setNameHoverText("Name of the WebGL Vendor. Some browsers give the full name of the underlying graphics card used by the device.");
			characteristics.add(bean);
		}
		{
			CharacteristicBean bean = getCharacteristicBean(conn, sampleCount, "WebGLRenderer", fingerprint.getWebGLRenderer());
			bean.setName("WebGL Renderer");
			bean.setNameHoverText("Name of the WebGL Renderer. Some browsers give the full name of the underlying graphics driver.");
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
	private static Integer insertSample(Connection conn, Fingerprint fingerprint) throws SQLException {
		PreparedStatement insertSample = conn.prepareStatement(insertSampleStr, Statement.RETURN_GENERATED_KEYS);
		int index = 2;
		insertSample.setString(index, fingerprint.getIpAddress());
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
		insertSample.setString(index, fingerprint.getLanguageFlash());
		++index;
		insertSample.setString(index, fingerprint.getFonts());
		++index;
		insertSample.setString(index, fingerprint.getCharSizes());
		++index;
		insertSample.setBoolean(index, fingerprint.isCookiesEnabled());
		++index;
		insertSample.setString(index, fingerprint.getSuperCookie());
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
		if (fingerprint.getAdsBlocked() != null) {
			insertSample.setBoolean(index, fingerprint.getAdsBlocked());
		} else {
			insertSample.setNull(index, java.sql.Types.BOOLEAN);
		}
		++index;
		insertSample.setString(index, fingerprint.getCanvas());
		++index;
		insertSample.setString(index, fingerprint.getWebGLVendor());
		++index;
		insertSample.setString(index, fingerprint.getWebGLRenderer());

		/*
		 * Try to insert with a different random UUID until a unique one is found.
		 */
		boolean foundUniqueUUID = false;
		while (!foundUniqueUUID) {
			String sampleUUID = UUID.randomUUID().toString();
			insertSample.setString(1, sampleUUID);
			try {
				insertSample.execute();
				foundUniqueUUID = true;
			} catch (MySQLIntegrityConstraintViolationException ex) {
				System.err.println("Duplicate UUID: " + sampleUUID);
			}
		}

		ResultSet rs = insertSample.getGeneratedKeys();
		Integer sampleID = null;
		if (rs.next()) {
			sampleID = rs.getInt(1);
		}
		rs.close();
		insertSample.close();
		return sampleID;
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
		if (fingerprint.getSampleSetID() == null) {
			/*
			 * Insert whole new SampleSetID.
			 */
			String query = "INSERT INTO `SampleSets`(`SampleID`) VALUES(?);";
			PreparedStatement insertSampleSet = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			insertSampleSet.setInt(1, sampleID);
			insertSampleSet.execute();

			ResultSet rs = insertSampleSet.getGeneratedKeys();
			if (rs.next()) {
				fingerprint.setSampleSetID(rs.getInt(1));
			}
			rs.close();
			insertSampleSet.close();
		} else {
			/*
			 * Insert new SampleID for existing SampleSetID.
			 */
			String query = "INSERT INTO `SampleSets`(`SampleSetID`,`SampleID`) VALUES(?, ?);";
			PreparedStatement insertSampleSet = conn.prepareStatement(query);

			insertSampleSet.setInt(1, fingerprint.getSampleSetID());
			insertSampleSet.setInt(2, sampleID);
			insertSampleSet.execute();

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
	private static Integer checkSampleChanged(Connection conn, Fingerprint fingerprint) throws SQLException {
		if (fingerprint.getSampleSetID() == null) {
			/*
			 * We know we haven't seen this sample before because there's no SampleSetID.
			 */
			return null;
		}

		/*
		 * We have seen this user before. Check if their fingerprint has changed.
		 */
		String query = "SELECT `Samples`.`SampleID` FROM `SampleSets` INNER JOIN `Samples` ON `SampleSets`.`SampleID` = `Samples`.`SampleID` WHERE `SampleSetID` = ?" + " AND `UserAgent`" + (fingerprint.getUser_agent() == null ? " IS NULL" : " = ?") + " AND `AcceptHeaders`" + (fingerprint.getAccept_headers() == null ? " IS NULL" : " = ?") + " AND `Platform`" + (fingerprint.getPlatform() == null ? " IS NULL" : " = ?") + " AND `PlatformFlash`" + (fingerprint.getPlatformFlash() == null ? " IS NULL" : " = ?") + " AND `PluginDetails`" + (fingerprint.getPluginDetails() == null ? " IS NULL" : " = ?") + " AND `TimeZone`" + (fingerprint.getTimeZone() == null ? " IS NULL" : " = ?") + " AND `ScreenDetails`" + (fingerprint.getScreenDetails() == null ? " IS NULL" : " = ?") + " AND `ScreenDetailsFlash`" + (fingerprint.getScreenDetailsFlash() == null ? " IS NULL" : " = ?") + " AND `LanguageFlash`" + (fingerprint.getLanguageFlash() == null ? " IS NULL" : " = ?") + " AND `Fonts`" + (fingerprint.getFonts() == null ? " IS NULL" : " = ?") + " AND `CharSizes`" + (fingerprint.getCharSizes() == null ? " IS NULL" : " = ?") + " AND `CookiesEnabled` = ?" + " AND `SuperCookie`" + (fingerprint.getSuperCookie() == null ? " IS NULL" : " = ?") + " AND `DoNotTrack`" + (fingerprint.getDoNotTrack() == null ? " IS NULL" : " = ?") + " AND `ClockDifference`" + (fingerprint.getClockDifference() == null ? " IS NULL" : " = ?") + " AND `DateTime`" + (fingerprint.getDateTime() == null ? " IS NULL" : " = ?") + " AND `MathTan`" + (fingerprint.getMathTan() == null ? " IS NULL" : " = ?") + " AND `UsingTor` = ?" + " AND `AdsBlocked`" + (fingerprint.getAdsBlocked() == null ? " IS NULL" : " = ?") + " AND `Canvas`" + (fingerprint.getCanvas() == null ? " IS NULL" : " = ?") + " AND `WebGLVendor`" + (fingerprint.getWebGLVendor() == null ? " IS NULL" : " = ?") + " AND `WebGLRenderer`" + (fingerprint.getWebGLRenderer() == null ? " IS NULL" : " = ?") + ";";
		PreparedStatement checkExists = conn.prepareStatement(query);

		int index = 1;
		checkExists.setInt(index, fingerprint.getSampleSetID());
		++index;

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
		if (fingerprint.getLanguageFlash() != null) {
			checkExists.setString(index, fingerprint.getLanguageFlash());
			++index;
		}
		if (fingerprint.getFonts() != null) {
			checkExists.setString(index, fingerprint.getFonts());
			++index;
		}
		if (fingerprint.getCharSizes() != null) {
			checkExists.setString(index, fingerprint.getCharSizes());
			++index;
		}
		checkExists.setBoolean(index, fingerprint.isCookiesEnabled());
		++index;
		if (fingerprint.getSuperCookie() != null) {
			checkExists.setString(index, fingerprint.getSuperCookie());
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
		if (fingerprint.getAdsBlocked() != null) {
			checkExists.setBoolean(index, fingerprint.getAdsBlocked());
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

		ResultSet rs = checkExists.executeQuery();

		Integer sampleID = null;
		if (rs.next()) {
			/*
			 * We've seen this sample before and the fingerprint hasn't changed,
			 * don't log it.
			 */
			sampleID = rs.getInt(1);
		}
		rs.close();
		checkExists.close();
		return sampleID;
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
		String query = "SELECT COUNT(*) FROM `Samples` WHERE" + " `UserAgent`" + (fingerprint.getUser_agent() == null ? " IS NULL" : " = ?") + " AND `AcceptHeaders`" + (fingerprint.getAccept_headers() == null ? " IS NULL" : " = ?") + " AND `Platform`" + (fingerprint.getPlatform() == null ? " IS NULL" : " = ?") + " AND `PlatformFlash`" + (fingerprint.getPlatformFlash() == null ? " IS NULL" : " = ?") + " AND `PluginDetails`" + (fingerprint.getPluginDetails() == null ? " IS NULL" : " = ?") + " AND `TimeZone`" + (fingerprint.getTimeZone() == null ? " IS NULL" : " = ?") + " AND `ScreenDetails`" + (fingerprint.getScreenDetails() == null ? " IS NULL" : " = ?") + " AND `ScreenDetailsFlash`" + (fingerprint.getScreenDetailsFlash() == null ? " IS NULL" : " = ?") + " AND `LanguageFlash`" + (fingerprint.getLanguageFlash() == null ? " IS NULL" : " = ?") + " AND `Fonts`" + (fingerprint.getFonts() == null ? " IS NULL" : " = ?") + " AND `CharSizes`" + (fingerprint.getCharSizes() == null ? " IS NULL" : " = ?") + " AND `CookiesEnabled` = ?" + " AND `SuperCookie`" + (fingerprint.getSuperCookie() == null ? " IS NULL" : " = ?") + " AND `DoNotTrack`" + (fingerprint.getDoNotTrack() == null ? " IS NULL" : " = ?") + " AND `ClockDifference`" + (fingerprint.getClockDifference() == null ? " IS NULL" : " = ?") + " AND `DateTime`" + (fingerprint.getDateTime() == null ? " IS NULL" : " = ?") + " AND `MathTan`" + (fingerprint.getMathTan() == null ? " IS NULL" : " = ?") + "" + " AND `UsingTor` = ?" + " AND `AdsBlocked`" + (fingerprint.getAdsBlocked() == null ? " IS NULL" : " = ?") + " AND `Canvas`" + (fingerprint.getCanvas() == null ? " IS NULL" : " = ?") + " AND `WebGLVendor`" + (fingerprint.getWebGLVendor() == null ? " IS NULL" : " = ?") + " AND `WebGLRenderer`" + (fingerprint.getWebGLRenderer() == null ? " IS NULL" : " = ?") + ";";
		PreparedStatement checkExists = conn.prepareStatement(query);

		int index = 1;
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
		if (fingerprint.getLanguageFlash() != null) {
			checkExists.setString(index, fingerprint.getLanguageFlash());
			++index;
		}
		if (fingerprint.getFonts() != null) {
			checkExists.setString(index, fingerprint.getFonts());
			++index;
		}
		if (fingerprint.getCharSizes() != null) {
			checkExists.setString(index, fingerprint.getCharSizes());
			++index;
		}
		checkExists.setBoolean(index, fingerprint.isCookiesEnabled());
		++index;
		if (fingerprint.getSuperCookie() != null) {
			checkExists.setString(index, fingerprint.getSuperCookie());
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
		if (fingerprint.getAdsBlocked() != null) {
			checkExists.setBoolean(index, fingerprint.getAdsBlocked());
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
		chrbean.setInX(((double) num_samples) / ((double) count));
		chrbean.setBits(Math.abs(Math.log(chrbean.getInX()) / Math.log(2)));

		return chrbean;
	}

	public static HistoryListBean getSampleSetIDsHistory(Integer sampleSetID, ServletContext context) throws ServletException {
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
			getHistory.setInt(1, sampleSetID);

			ResultSet rs = getHistory.executeQuery();
			while (rs.next()) {
				Timestamp timestamp = rs.getTimestamp(2);
				SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss z");
				dateformat.setTimeZone(TimeZone.getTimeZone("UTC"));
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
		// LanguageFlash
		fingerprint.setLanguageFlash(rs.getString(index));
		++index;
		// Fonts
		fingerprint.setFonts(rs.getString(index));
		++index;
		// CharSizes
		fingerprint.setCharSizes(rs.getString(index));
		++index;
		// CookiesEnabled
		fingerprint.setCookiesEnabled(rs.getBoolean(index));
		++index;
		// SuperCookie
		fingerprint.setSuperCookie(rs.getString(index));
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
		// AdsBlocked
		fingerprint.setAdsBlocked(rs.getBoolean(index));
		if (rs.wasNull()) {
			fingerprint.setAdsBlocked(null);
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

		rs.close();
		getFingerprint.close();

		return fingerprint;
	}
}