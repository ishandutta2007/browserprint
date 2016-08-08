import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Properties;

import datastructures.Fingerprint;

/**
 * Program to go through the database and set the base64 fingerprint hash for each sample that doesn't have it set. 
 */
public class Main {
	private static final String selectSampleStr = "SELECT `SampleID`, `ContrastLevel`, `UserAgent`, `AcceptHeaders`, `Platform`, `PlatformFlash`, `PluginDetails`, `TimeZone`, `ScreenDetails`,"
			+ " `ScreenDetailsFlash`, `ScreenDetailsCSS`, `LanguageFlash`, `Fonts`, `FontsJS_CSS`, `FontsCSS`, `CharSizes`, `CookiesEnabled`, `SuperCookieLocalStorage`,"
			+ " `SuperCookieSessionStorage`, `SuperCookieUserData`, `IndexedDBEnabled`, `DoNotTrack`, `ClockDifference`, `DateTime`, `MathTan`, `UsingTor`, `TbbVersion`,"
			+ " `AdsBlockedGoogle`, `AdsBlockedBanner`, `AdsBlockedScript`, `LikeShareFacebook`, `LikeShareTwitter`, `LikeShareReddit`, `Canvas`, `WebGLVendor`, `WebGLRenderer`,"
			+ " `TouchPoints`, `TouchEvent`, `TouchStart`, `AudioFingerprintPXI`, `AudioFingerprintPXIFullBuffer`, `AudioFingerprintNtVc`, `AudioFingerprintCC`, `AudioFingerprintHybrid` FROM `Samples`"
			+ " WHERE `FingerprintHash` IS null;";
	private static final String setHashStr = "UPDATE `Samples` SET `FingerprintHash` = ? WHERE `SampleID` = ?;";
	
	public static void main(String args[]) throws Exception {
		/*
		 * Get DB connection.
		 */
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", "root");

		conn = DriverManager.getConnection("jdbc:mysql://localhost/browserprint", connectionProps);

		setHashOfHashlessSamples(conn);
	}
	
	public static void setHashOfHashlessSamples(Connection conn) throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
		PreparedStatement getFingerprint = conn.prepareStatement(selectSampleStr);

		ResultSet fingerprintsRs = getFingerprint.executeQuery();
		while (fingerprintsRs.next()) {
			/*
			 * Build the fingerprint object.
			 */
			Fingerprint fingerprint = new Fingerprint();

			int index = 1;
			// SampleID
			int sampleID = fingerprintsRs.getInt(index);
			++index;
			// ColourVision
			fingerprint.setContrastLevel(fingerprintsRs.getInt(index));
			++index;
			// UserAgent
			fingerprint.setUser_agent(fingerprintsRs.getString(index));
			++index;
			// AcceptHeaders
			fingerprint.setAccept_headers(fingerprintsRs.getString(index));
			++index;
			// Platform
			fingerprint.setPlatform(fingerprintsRs.getString(index));
			++index;
			// PlatformFlash
			fingerprint.setPlatformFlash(fingerprintsRs.getString(index));
			++index;
			// PluginDetails
			fingerprint.setPluginDetails(fingerprintsRs.getString(index));
			++index;
			// TimeZone
			fingerprint.setTimeZone(fingerprintsRs.getString(index));
			++index;
			// ScreenDetails
			fingerprint.setScreenDetails(fingerprintsRs.getString(index));
			++index;
			// ScreenDetailsFlash
			fingerprint.setScreenDetailsFlash(fingerprintsRs.getString(index));
			++index;
			// ScreenDetailsCSS
			fingerprint.setScreenDetailsCSS(fingerprintsRs.getString(index));
			++index;
			// LanguageFlash
			fingerprint.setLanguageFlash(fingerprintsRs.getString(index));
			++index;
			// Fonts
			fingerprint.setFonts(fingerprintsRs.getString(index));
			++index;
			// FontsJS_CSS
			fingerprint.setFontsJS_CSS(fingerprintsRs.getString(index));
			++index;
			// FontsCSS
			fingerprint.setFontsCSS(fingerprintsRs.getString(index));
			++index;
			// CharSizes
			fingerprint.setCharSizes(fingerprintsRs.getString(index));
			++index;
			// CookiesEnabled
			fingerprint.setCookiesEnabled(fingerprintsRs.getBoolean(index));
			++index;
			// SuperCookieLocalStorage
			fingerprint.setSuperCookieLocalStorage(fingerprintsRs.getBoolean(index));
			if (fingerprintsRs.wasNull()) {
				fingerprint.setSuperCookieLocalStorage(null);
			}
			++index;
			// SuperCookieSessionStorage
			fingerprint.setSuperCookieSessionStorage(fingerprintsRs.getBoolean(index));
			if (fingerprintsRs.wasNull()) {
				fingerprint.setSuperCookieSessionStorage(null);
			}
			++index;
			// SuperCookieUserData
			fingerprint.setSuperCookieUserData(fingerprintsRs.getBoolean(index));
			if (fingerprintsRs.wasNull()) {
				fingerprint.setSuperCookieUserData(null);
			}
			++index;
			// IndexedDBEnabled
			fingerprint.setIndexedDBEnabled(fingerprintsRs.getBoolean(index));
			if (fingerprintsRs.wasNull()) {
				fingerprint.setIndexedDBEnabled(null);
			}
			++index;
			// DoNotTrack
			fingerprint.setDoNotTrack(fingerprintsRs.getString(index));
			++index;
			// ClockDifference
			fingerprint.setClockDifference(fingerprintsRs.getLong(index));
			if (fingerprintsRs.wasNull()) {
				fingerprint.setClockDifference(null);
			}
			++index;
			// DateTime
			fingerprint.setDateTime(fingerprintsRs.getString(index));
			++index;
			// MathTan
			fingerprint.setMathTan(fingerprintsRs.getString(index));
			++index;
			// UsingTor
			fingerprint.setUsingTor(fingerprintsRs.getBoolean(index));
			++index;
			// TbbVersion
			fingerprint.setTbbVersion(fingerprintsRs.getString(index));
			++index;
			// AdsBlockedGoogle
			fingerprint.setAdsBlockedGoogle(fingerprintsRs.getBoolean(index));
			if (fingerprintsRs.wasNull()) {
				fingerprint.setAdsBlockedGoogle(null);
			}
			++index;
			// AdsBlockedBanner
			fingerprint.setAdsBlockedBanner(fingerprintsRs.getBoolean(index));
			if (fingerprintsRs.wasNull()) {
				fingerprint.setAdsBlockedBanner(null);
			}
			++index;
			// AdsBlockedScript
			fingerprint.setAdsBlockedScript(fingerprintsRs.getBoolean(index));
			if (fingerprintsRs.wasNull()) {
				fingerprint.setAdsBlockedScript(null);
			}
			++index;
			// LikeShareFacebook
			fingerprint.setLikeShareFacebook(fingerprintsRs.getInt(index));
			if (fingerprintsRs.wasNull()) {
				fingerprint.setLikeShareFacebook(null);
			}
			++index;
			// LikeShareTwitter
			fingerprint.setLikeShareTwitter(fingerprintsRs.getInt(index));
			if (fingerprintsRs.wasNull()) {
				fingerprint.setLikeShareTwitter(null);
			}
			++index;
			// LikeShareReddit
			fingerprint.setLikeShareReddit(fingerprintsRs.getInt(index));
			if (fingerprintsRs.wasNull()) {
				fingerprint.setLikeShareReddit(null);
			}
			++index;
			// Canvas
			fingerprint.setCanvas(fingerprintsRs.getString(index));
			++index;
			// WebGLVendor
			fingerprint.setWebGLVendor(fingerprintsRs.getString(index));
			++index;
			// WebGLRenderer
			fingerprint.setWebGLRenderer(fingerprintsRs.getString(index));
			++index;
			// TouchPoints
			fingerprint.setTouchPoints(fingerprintsRs.getInt(index));
			if (fingerprintsRs.wasNull()) {
				fingerprint.setTouchPoints(null);
			}
			++index;
			// TouchEvent
			fingerprint.setTouchEvent(fingerprintsRs.getBoolean(index));
			if (fingerprintsRs.wasNull()) {
				fingerprint.setTouchEvent(null);
			}
			++index;
			// TouchStart
			fingerprint.setTouchStart(fingerprintsRs.getBoolean(index));
			if (fingerprintsRs.wasNull()) {
				fingerprint.setTouchStart(null);
			}
			++index;
			// AudioFingerprintPXI
			fingerprint.setAudioFingerprintPXI(fingerprintsRs.getString(index));
			++index;
			// AudioFingerprintPXIFullBuffer
			fingerprint.setAudioFingerprintPXIFullBuffer(fingerprintsRs.getString(index));
			++index;
			// AudioFingerprintNtVc
			fingerprint.setAudioFingerprintNtVc(fingerprintsRs.getString(index));
			++index;
			// AudioFingerprintCC
			fingerprint.setAudioFingerprintCC(fingerprintsRs.getString(index));
			++index;
			// AudioFingerprintHybrid
			fingerprint.setAudioFingerprintHybrid(fingerprintsRs.getString(index));
			++index;
			
			/*
			 * Hash the fingerprint.
			 */
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.reset();
			if(fingerprint.getContrastLevel() != null){
				digest.update(fingerprint.getContrastLevel().byteValue());
			}
			if(fingerprint.getUser_agent() != null){
				digest.update(fingerprint.getUser_agent().getBytes());
			}
			if(fingerprint.getAccept_headers() != null){
				digest.update(fingerprint.getAccept_headers().getBytes());
			}
			if(fingerprint.getPlatform() != null){
				digest.update(fingerprint.getPlatform().getBytes());
			}
			if(fingerprint.getPlatformFlash() != null){
				digest.update(fingerprint.getPlatformFlash().getBytes());
			}
			if(fingerprint.getPluginDetails() != null){
				digest.update(fingerprint.getPluginDetails().getBytes());
			}
			if(fingerprint.getTimeZone() != null){
				digest.update(fingerprint.getTimeZone().getBytes());
			}
			/*if(fingerprint.getScreenDetails() != null){
				digest.update(fingerprint.getScreenDetails().getBytes());
			}*/
			if(fingerprint.getScreenDetailsFlash() != null){
				digest.update(fingerprint.getScreenDetailsFlash().getBytes());
			}
			/*if(fingerprint.getScreenDetailsCSS() != null){
				digest.update(fingerprint.getScreenDetailsCSS().getBytes());
			}*/
			if(fingerprint.getLanguageFlash() != null){
				digest.update(fingerprint.getLanguageFlash().getBytes());
			}
			if(fingerprint.getFonts() != null){
				digest.update(fingerprint.getFonts().getBytes());
			}
			if(fingerprint.getFontsJS_CSS() != null){
				digest.update(fingerprint.getFontsJS_CSS().getBytes());
			}
			if(fingerprint.getFontsCSS() != null){
				digest.update(fingerprint.getFontsCSS().getBytes());
			}
			/*if(fingerprint.getCharSizes() != null){
				digest.update(fingerprint.getCharSizes().getBytes());
			}*/
			digest.update(Boolean.toString(fingerprint.isCookiesEnabled()).getBytes());
			if(fingerprint.getSuperCookieLocalStorage() != null){
				digest.update(Boolean.toString(fingerprint.getSuperCookieLocalStorage()).getBytes());
			}
			if(fingerprint.getSuperCookieSessionStorage() != null){
				digest.update(Boolean.toString(fingerprint.getSuperCookieSessionStorage()).getBytes());
			}
			if(fingerprint.getSuperCookieUserData() != null){
				digest.update(Boolean.toString(fingerprint.getSuperCookieUserData()).getBytes());
			}
			if(fingerprint.getIndexedDBEnabled() != null){
				digest.update(Boolean.toString(fingerprint.getIndexedDBEnabled()).getBytes());
			}
			if(fingerprint.getDoNotTrack() != null){
				digest.update(fingerprint.getDoNotTrack().getBytes());
			}
			if(fingerprint.getClockDifference() != null){
				digest.update(Long.toString(fingerprint.getClockDifference()).getBytes());
			}
			if(fingerprint.getDateTime() != null){
				digest.update(fingerprint.getDateTime().getBytes());
			}
			if(fingerprint.getMathTan() != null){
				digest.update(fingerprint.getMathTan().getBytes());
			}
			digest.update(Boolean.toString(fingerprint.isUsingTor()).getBytes());
			if(fingerprint.getTbbVersion() != null){
				digest.update(fingerprint.getTbbVersion().getBytes());
			}
			if(fingerprint.getAdsBlockedGoogle() != null){
				digest.update(Boolean.toString(fingerprint.getAdsBlockedGoogle()).getBytes());
			}
			if(fingerprint.getAdsBlockedBanner() != null){
				digest.update(Boolean.toString(fingerprint.getAdsBlockedBanner()).getBytes());
			}
			if(fingerprint.getAdsBlockedScript() != null){
				digest.update(Boolean.toString(fingerprint.getAdsBlockedScript()).getBytes());
			}
			if(fingerprint.getLikeShareFacebook() != null){
				digest.update(Integer.toString(fingerprint.getLikeShareFacebook()).getBytes());
			}
			if(fingerprint.getLikeShareTwitter() != null){
				digest.update(Integer.toString(fingerprint.getLikeShareTwitter()).getBytes());
			}
			if(fingerprint.getLikeShareReddit() != null){
				digest.update(Integer.toString(fingerprint.getLikeShareReddit()).getBytes());
			}
			/*if(fingerprint.getCanvas() != null){
				digest.update(fingerprint.getCanvas().getBytes());
			}*/
			if(fingerprint.getWebGLVendor() != null){
				digest.update(fingerprint.getWebGLVendor().getBytes());
			}
			if(fingerprint.getWebGLRenderer() != null){
				digest.update(fingerprint.getWebGLRenderer().getBytes());
			}
			if(fingerprint.getTouchPoints() != null){
				digest.update(Integer.toString(fingerprint.getTouchPoints()).getBytes());
			}
			if(fingerprint.getTouchEvent() != null){
				digest.update(Boolean.toString(fingerprint.getTouchEvent()).getBytes());
			}
			if(fingerprint.getTouchStart() != null){
				digest.update(Boolean.toString(fingerprint.getTouchStart()).getBytes());
			}
			/*if(fingerprint.getAudioFingerprintPXI() != null){
				digest.update(fingerprint.getAudioFingerprintPXI().getBytes());
			}*/
			/*if(fingerprint.getAudioFingerprintPXIFullBuffer() != null){
				digest.update(fingerprint.getAudioFingerprintPXIFullBuffer().getBytes());
			}*/
			/*if(fingerprint.getAudioFingerprintNtVc() != null){
				digest.update(fingerprint.getAudioFingerprintNtVc().getBytes());
			}*/
			/*if(fingerprint.getAudioFingerprintCC() != null){
				digest.update(fingerprint.getAudioFingerprintCC().getBytes());
			}*/
			/*if(fingerprint.getAudioFingerprintHybrid() != null){
				digest.update(fingerprint.getAudioFingerprintHybrid().getBytes());
			}*/
			String hash = Base64.getEncoder().encodeToString(digest.digest());
			
			/*
			 * Save hash.
			 */
			PreparedStatement setHash = conn.prepareStatement(setHashStr);
			setHash.setString(1, hash);
			setHash.setInt(2, sampleID);
			setHash.executeUpdate();
			
			System.out.println("SampleID = " + sampleID + ", hash = " + hash);
		}
		fingerprintsRs.close();
		getFingerprint.close();
	}
}
