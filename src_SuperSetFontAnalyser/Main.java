import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

public class Main {
	/**
	 * Use with CrossBrowserLinker.5
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", "root");

		conn = DriverManager.getConnection("jdbc:mysql://localhost/browserprint", connectionProps);
		
		PreparedStatement getSampleTimes = conn.prepareStatement(""
				+ "SELECT `Join1`.`BrowserGroup`, `Join1`.`BrowserVersion`, `Join1`.`Fonts`, `Join2`.`BrowserGroup`, `Join2`.`BrowserVersion`, `Join2`.`Fonts` FROM"
				+ " (SELECT `SampleSuperSetID`, `BrowserGroup`, `BrowserVersion`, `Fonts` FROM `SuperSampleSets`"
				+ "  INNER JOIN (SELECT `Samples`.`SampleID`, `SampleStatistics`.`BrowserGroup`, `SampleStatistics`.`BrowserVersion`, `Samples`.`Fonts`"
				+ "   FROM `Samples` INNER JOIN `SampleStatistics` USING(`SampleID`) WHERE `Fonts` != 'Flash disabled' AND `Fonts` != 'No Flash') AS `InnerJoin1` USING(`SampleID`)) AS `Join1`"
				+ " INNER JOIN"
				+ " (SELECT `SampleSuperSetID`, `BrowserGroup`, `BrowserVersion`, `Fonts` FROM `SuperSampleSets`"
				+ "  INNER JOIN (SELECT `Samples`.`SampleID`, `SampleStatistics`.`BrowserGroup`, `SampleStatistics`.`BrowserVersion`, `Samples`.`Fonts`"
				+ "   FROM `Samples` INNER JOIN `SampleStatistics` USING(`SampleID`) WHERE `Fonts` != 'Flash disabled' AND `Fonts` != 'No Flash') AS `InnerJoin1` USING(`SampleID`)) AS `Join2`"
				+ " ON `Join1`.`SampleSuperSetID` = `Join2`.`SampleSuperSetID` AND (`Join1`.`BrowserGroup` != `Join2`.`BrowserGroup` OR `Join1`.`BrowserVersion` != `Join2`.`BrowserVersion`)");
		ResultSet rs = getSampleTimes.executeQuery();
		
		HashMap<BrowserGroupVersion, HashMap<BrowserGroupVersion, Fonts>>crossBrowserFonts = new HashMap<BrowserGroupVersion, HashMap<BrowserGroupVersion, Fonts>>();
		while(rs.next()){
			int index = 1;
			
			/*
			 * Extract data from query.
			 */
			BrowserGroupVersion bgv1 = new BrowserGroupVersion(rs.getString(index), rs.getString(index + 1));
			index += 2;
			String fonts1 = rs.getString(index);
			++index;
			BrowserGroupVersion bgv2 = new BrowserGroupVersion(rs.getString(index), rs.getString(index + 1));
			index += 2;
			String fonts2 = rs.getString(index);
			++index;
			
			/*
			 * Save fonts that are in fonts2 but not fonts1.
			 */
			HashSet<String>fontsSet = new HashSet<String>();
			//Add all of fonts2 to the fonts set.
			String fontsSplit2[] = fonts2.split(", ");
			for(int i = 0; i < fontsSplit2.length; ++i){
				fontsSet.add(fontsSplit2[i]);
			}
			//Remove all the fonts that fonts2 has in common with fonts1 from the fonts set.
			String fontsSplit1[] = fonts1.split(", ");
			for(int i = 0; i < fontsSplit1.length; ++i){
				fontsSet.remove(fontsSplit1[i]);
			}
			HashMap<BrowserGroupVersion, Fonts> bgv1BrowserSet = crossBrowserFonts.get(bgv1);
			if(bgv1BrowserSet == null){
				bgv1BrowserSet = new HashMap<BrowserGroupVersion, Fonts>();
				crossBrowserFonts.put(bgv1, bgv1BrowserSet);
			}
			Fonts fonts = bgv1BrowserSet.get(bgv2);
			if(fonts == null){
				fonts = new Fonts();
				bgv1BrowserSet.put(bgv2, fonts);
			}
			fonts.totalCount++;
			for(String font: fontsSet){
				Integer count = fonts.fontsNotFoundCount.get(font);
				if(count == null){
					count = 0;
				}
				++count;
				fonts.fontsNotFoundCount.put(font, count);
			}
		}
		
		/*
		 * Output the results.
		 */
		for(BrowserGroupVersion bgv1: crossBrowserFonts.keySet()){
			HashMap<BrowserGroupVersion, Fonts> bgv1BrowserSet = crossBrowserFonts.get(bgv1);
			for(BrowserGroupVersion bgv2: bgv1BrowserSet.keySet()){
				Fonts fonts = bgv1BrowserSet.get(bgv2);
				System.out.println(bgv1.browserGroup + ", " + bgv1.browserVersion + " : " + bgv2.browserGroup + ", " + bgv2.browserVersion + " : " + fonts.totalCount);
				for(String font: fonts.fontsNotFoundCount.keySet()){
					System.out.println("\t" + font + ": " + fonts.fontsNotFoundCount.get(font));
				}
			}
		}
	}
}

class Fonts{
	public Integer totalCount;
	public HashMap<String, Integer> fontsNotFoundCount;
	
	public Fonts(){
		totalCount = 0;
		fontsNotFoundCount = new HashMap<String, Integer>();
	}
}

class BrowserGroupVersion implements Comparable<BrowserGroupVersion>{
	public String browserGroup;
	public String browserVersion;
	
	public BrowserGroupVersion(String browserGroup, String browserVersion) {
		this.browserGroup = browserGroup;
		this.browserVersion = browserVersion;
	}

	public int compareTo(BrowserGroupVersion o) {
		int retval = this.browserGroup.compareTo(o.browserGroup);
		if(retval != 0){
			return retval;
		}
		else{
			return this.browserVersion.compareTo(o.browserVersion);
		}
	}

	public int hashCode() {
		return browserGroup.hashCode() ^ browserVersion.hashCode();
	}

	public boolean equals(Object o) {
		if(o instanceof BrowserGroupVersion){
			return this.equals((BrowserGroupVersion)o);
		}
		return false;
	}
	
	public boolean equals(BrowserGroupVersion o) {
		return this.compareTo(o) == 0;
	}
}