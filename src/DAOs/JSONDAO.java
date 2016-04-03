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
	
	public static final String getAdsBlocked() {
		return getBooleanResults("AdsBlocked");
	}
	
	public static final String getScreenDetails() {
		return getStringResults("ScreenDetails");
	}
}
