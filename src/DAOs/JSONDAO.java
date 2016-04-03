package DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.json.JSONObject;

public class JSONDAO {
	public static final String getPercentageTorUsers() {
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(true);

			String query = "SELECT `UsingTor`, COUNT(*) FROM `Samples` GROUP BY `UsingTor`;";
			PreparedStatement select = conn.prepareStatement(query);

			ResultSet rs = select.executeQuery();

			JSONObject results = new JSONObject();
			while (rs.next()) {
				boolean usingTor = rs.getBoolean(1);
				String usingTorStr;
				if (usingTor) {
					usingTorStr = "1";
				}
				else {
					usingTorStr = "0";
				}

				int count = rs.getInt(2);
				results.put(usingTorStr, count);
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
	
	public static final String getCookiesEnabled() {
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(true);

			String query = "SELECT `CookiesEnabled`, COUNT(*) FROM `Samples` GROUP BY `CookiesEnabled`;";
			PreparedStatement select = conn.prepareStatement(query);

			ResultSet rs = select.executeQuery();

			JSONObject results = new JSONObject();
			while (rs.next()) {
				boolean cookiesEnabled = rs.getBoolean(1);
				String cookiesEnabledStr;
				if (cookiesEnabled) {
					cookiesEnabledStr = "1";
				}
				else {
					cookiesEnabledStr = "0";
				}

				int count = rs.getInt(2);
				results.put(cookiesEnabledStr, count);
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
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(true);

			String query = "SELECT `TimeZone`, COUNT(*) FROM `Samples` GROUP BY `TimeZone`;";
			PreparedStatement select = conn.prepareStatement(query);

			ResultSet rs = select.executeQuery();

			JSONObject results = new JSONObject();
			while (rs.next()) {
				String timezone = rs.getString(1);
				if (rs.wasNull()) {
					timezone = "No JavaScript";
				}

				int count = rs.getInt(2);
				results.put(timezone, count);
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
	
	public static final String getLanguages() {
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(true);

			String query = "SELECT `LanguageFlash`, COUNT(*) FROM `Samples` GROUP BY `LanguageFlash`;";
			PreparedStatement select = conn.prepareStatement(query);

			ResultSet rs = select.executeQuery();

			JSONObject results = new JSONObject();
			while (rs.next()) {
				String language = rs.getString(1);
				if (rs.wasNull()) {
					language = "No JavaScript";
				}

				int count = rs.getInt(2);
				results.put(language, count);
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
	
	public static final String getAdsBlocked() {
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(true);

			String query = "SELECT `AdsBlocked`, COUNT(*) FROM `Samples` GROUP BY `AdsBlocked`;";
			PreparedStatement select = conn.prepareStatement(query);

			ResultSet rs = select.executeQuery();

			JSONObject results = new JSONObject();
			while (rs.next()) {
				boolean adsBlocked = rs.getBoolean(1);
				String adsBlockedStr;
				if(rs.wasNull()){
					adsBlockedStr = "No JavaScript";
				}
				else if (adsBlocked) {
					adsBlockedStr = "1";
				}
				else {
					adsBlockedStr = "0";
				}

				int count = rs.getInt(2);
				results.put(adsBlockedStr, count);
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
	
	public static final String getScreenDetails() {
		Connection conn = null;
		try {
			conn = Database.getConnection();
			conn.setReadOnly(true);

			String query = "SELECT `ScreenDetails`, COUNT(*) FROM `Samples` GROUP BY `ScreenDetails`;";
			PreparedStatement select = conn.prepareStatement(query);

			ResultSet rs = select.executeQuery();

			JSONObject results = new JSONObject();
			while (rs.next()) {
				String screenDetails = rs.getString(1);
				if (rs.wasNull()) {
					screenDetails = "No JavaScript";
				}

				int count = rs.getInt(2);
				results.put(screenDetails, count);
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
}
