package DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import beans.StatisticsBean;
import datastructures.Fingerprint;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;

public class StatisticsDAO {
	public static final void saveStatistics(int sampleID, Fingerprint fingerprint){
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(false);

			String query = "INSERT INTO `SampleStatistics` (`SampleID`, `BrowserGroup`, `BrowserVersion`, `OSGroup`, `OSName`) VALUES(?, ?, ?, ?, ?);";
			PreparedStatement insertStatistics = conn.prepareStatement(query);
			
			insertStatistics.setInt(1, sampleID);
			
			UserAgent ua = new UserAgent(fingerprint.getUser_agent());
			insertStatistics.setString(2, ua.getBrowser().getGroup().toString());
			Browser browser = ua.getBrowser();
			if(browser == null){
				insertStatistics.setString(3, "Unknown1");
			}
			else if(browser.getGroup().equals(Browser.UNKNOWN)){
				insertStatistics.setString(3, "Unknown");
			}
			else{
				Version ver = ua.getBrowserVersion();
				if(ver == null){
					insertStatistics.setString(3, "Unknown2");
				}
				else{
					insertStatistics.setString(3, ua.getBrowserVersion().getMajorVersion());
				}
			}
			OperatingSystem os = ua.getOperatingSystem();
			if(os == null){
				insertStatistics.setString(4, "Unknown");
				insertStatistics.setString(5, "Unknown");
			}
			else{
				insertStatistics.setString(4, os.getGroup().toString());
				insertStatistics.setString(5, os.getName());
			}
			
			insertStatistics.execute();
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
		return;
	}
	
	public static final StatisticsBean getStatistics(){
		StatisticsBean statistics = new StatisticsBean();
		
		Connection conn = null;
		try {
			conn = Database.getConnection();
			
			statistics.setNumSamples(FingerprintDAO.getSampleCount(conn));
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
			
		return statistics;
	}
}