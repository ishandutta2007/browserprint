package DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import beans.StatisticsBean;
import datastructures.Fingerprint;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;

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
			if(ua.getBrowser().getGroup().equals(Browser.UNKNOWN)){
				insertStatistics.setString(3, "Unknown");
			}
			else{
				insertStatistics.setString(3, ua.getBrowserVersion().getMajorVersion());
			}
			insertStatistics.setString(4, ua.getOperatingSystem().getGroup().toString());
			insertStatistics.setString(5, ua.getOperatingSystem().getName());
			
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