package DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.json.JSONObject;

public class JSONDAO {
	public static final String getStringResults(String name) {
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(true);

			String query = "SELECT `" + name + "`, COUNT(*) FROM `Samples` GROUP BY `" + name + "`;";
			PreparedStatement select = conn.prepareStatement(query);

			ResultSet rs = select.executeQuery();

			JSONObject results = new JSONObject();
			while (rs.next()) {
				String key = rs.getString(1);
				if (rs.wasNull()) {
					key = "No JavaScript";
				}

				int count = rs.getInt(2);
				results.put(key, count);
			}
			rs.close();
			select.close();

			return results.toString();
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
	
	public static final String getBooleanResults(String name) {
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(true);

			String query = "SELECT `" + name + "`, COUNT(*) FROM `Samples` GROUP BY `" + name + "`;";
			PreparedStatement select = conn.prepareStatement(query);

			ResultSet rs = select.executeQuery();

			JSONObject results = new JSONObject();
			while (rs.next()) {
				boolean key = rs.getBoolean(1);
				String keyStr;
				if(rs.wasNull()){
					keyStr = "No JavaScript";
				}
				else if (key) {
					keyStr = "1";
				}
				else {
					keyStr = "0";
				}

				int count = rs.getInt(2);
				results.put(keyStr, count);
			}
			rs.close();
			select.close();

			return results.toString();
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
	
	public static final String getPercentageTorUsers() {	
		return getBooleanResults("UsingTor");
	}
	
	public static final String getCookiesEnabled() {		
		return getBooleanResults("CookiesEnabled");
	}
	
	public static final String getOSBreakdown() {
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(true);

			String query = "SELECT `OSGroup`, `OSName`, COUNT(*) FROM `SampleStatistics` GROUP BY `OSGroup`, `OSName`;";
			PreparedStatement select = conn.prepareStatement(query);

			ResultSet rs = select.executeQuery();

			HashMap<String, JSONObject> groups = new HashMap<String, JSONObject>();
			while (rs.next()) {
				String groupName = rs.getString(1);
				String name = rs.getString(2);
				int count = rs.getInt(3);

				JSONObject group = groups.get(groupName);
				if (group == null) {
					group = new JSONObject();
					groups.put(groupName, group);
				}
				group.put(name, count);
			}
			rs.close();
			select.close();

			JSONObject results = new JSONObject();
			for (String groupName : groups.keySet()) {
				results.put(groupName, groups.get(groupName));
			}

			return results.toString();
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

	public static final String getBrowserBreakdown() {
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(true);

			String query = "SELECT `BrowserGroup`, `BrowserVersion`, COUNT(*) FROM `SampleStatistics` GROUP BY `BrowserGroup`, `BrowserVersion`;";
			PreparedStatement select = conn.prepareStatement(query);

			ResultSet rs = select.executeQuery();

			HashMap<String, JSONObject> groups = new HashMap<String, JSONObject>();
			while (rs.next()) {
				String groupName = rs.getString(1);
				String name = rs.getString(2);
				int count = rs.getInt(3);

				JSONObject group = groups.get(groupName);
				if (group == null) {
					group = new JSONObject();
					groups.put(groupName, group);
				}
				group.put(name, count);
			}
			rs.close();
			select.close();

			JSONObject results = new JSONObject();
			for (String groupName : groups.keySet()) {
				results.put(groupName, groups.get(groupName));
			}

			return results.toString();
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

	public static final String getTimezones() {
		return getStringResults("TimeZone");
	}
	
	public static final String getLanguages() {	
		return getStringResults("LanguageFlash");
	}
	
	public static final String getAdsBlockedGoogle() {
		return getBooleanResults("AdsBlockedGoogle");
	}
	
	public static final String getScreenDetails() {
		return getStringResults("ScreenDetails");
	}
	
	/**
	 * Get number of unique / not unique samples for JavaScript enabled / disabled.
	 * Only counts samples >= version 19.
	 * @return
	 */
	public static final String getUniqueness() {
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(true);

			int uniqueWhereJsDisabled;
			{
				String query = "SELECT COUNT(*) FROM (SELECT COUNT(*) AS `NumOccurrences` FROM `Samples` WHERE `TimeZone` IS null AND `BrowserprintVersion` >= 19 GROUP BY `UserAgent`, `AcceptHeaders`, `ScreenDetailsCSS`, `FontsCSS`, `CookiesEnabled`, `DoNotTrack`) AS `NumOccurrencesTable` WHERE `NumOccurrences` = 1;";
				PreparedStatement select = conn.prepareStatement(query);

				ResultSet rs = select.executeQuery();

				rs.next();
				uniqueWhereJsDisabled = rs.getInt(1);
				rs.close();
				select.close();
			}
			int notUniqueWhereJsDisabled;
			{
				String query = "SELECT COUNT(*) FROM `Samples` WHERE `BrowserprintVersion` >= 19 AND `TimeZone` IS null;";
				PreparedStatement select = conn.prepareStatement(query);

				ResultSet rs = select.executeQuery();

				rs.next();
				notUniqueWhereJsDisabled = rs.getInt(1) - uniqueWhereJsDisabled;
				rs.close();
				select.close();
			}		
			int uniqueWhereJsEnabled;
			{
				String query = "SELECT COUNT(*) FROM (SELECT COUNT(*) AS `NumOccurrences` FROM `Samples` WHERE `TimeZone` IS NOT null AND `BrowserprintVersion` >= 19 GROUP BY `ContrastLevel`, `UserAgent`, `AcceptHeaders`, `Platform`, `PlatformFlash`, `PluginDetails`, `TimeZone`, `ScreenDetailsFlash`, `LanguageFlash`, `Fonts`, `FontsJS_CSS`, `FontsCSS`, `CookiesEnabled`, `SuperCookieLocalStorage`, `SuperCookieSessionStorage`, `SuperCookieUserData`, `IndexedDBEnabled`, `DoNotTrack`, `ClockDifference`, `DateTime`, `MathTan`, `UsingTor`, `TbbVersion`, `AdsBlockedGoogle`, `AdsBlockedBanner`, `AdsBlockedScript`, `LikeShareFacebook`, `LikeShareTwitter`, `LikeShareReddit`, `WebGLVendor`, `WebGLRenderer`, `TouchPoints`, `TouchEvent`, `TouchStart`) AS `NumOccurrencesTable` WHERE `NumOccurrences` = 1;";
				PreparedStatement select = conn.prepareStatement(query);

				ResultSet rs = select.executeQuery();

				rs.next();
				uniqueWhereJsEnabled = rs.getInt(1);
				rs.close();
				select.close();
			}
			int notUniqueWhereJsEnabled;
			{
				String query = "SELECT COUNT(*) FROM `Samples` WHERE `TimeZone` IS NOT null AND `BrowserprintVersion` >= 19";
				PreparedStatement select = conn.prepareStatement(query);

				ResultSet rs = select.executeQuery();

				rs.next();
				notUniqueWhereJsEnabled = rs.getInt(1) - uniqueWhereJsEnabled;
				rs.close();
				select.close();
			}
			
			JSONObject retval = new JSONObject();
			JSONObject unique = new JSONObject();
			unique.put("JavaScript disabled", uniqueWhereJsDisabled);
			unique.put("JavaScript enabled", uniqueWhereJsEnabled);
			retval.put("Unique", unique);
			JSONObject notUnique = new JSONObject();
			notUnique.put("JavaScript disabled", notUniqueWhereJsDisabled);
			notUnique.put("JavaScript enabled", notUniqueWhereJsEnabled);
			retval.put("NotUnique", notUnique);

			return retval.toString();
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
}
