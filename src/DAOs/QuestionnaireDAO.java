package DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

public class QuestionnaireDAO {
	/**
	 * 
	 * @param conn
	 * @param fingerprint
	 * @param sampleID
	 * @return
	 * @throws SQLException
	 */
	public static void insertQuestionnaireAnswers(Connection conn, HttpSession session, Integer sampleID) throws SQLException {
		String usingProxy = (String) session.getAttribute("usingProxy");
		String isSpoofing = (String) session.getAttribute("isSpoofing");
		String whatBrowser = (String) session.getAttribute("whatBrowser");
		String whatOS = (String) session.getAttribute("whatOS");
		if(usingProxy == null && isSpoofing == null && whatBrowser == null && whatOS == null){
			//No point putting in empty database entries.
			return;
		}
		
		String insertQuery = "INSERT INTO `SampleQuestionnaire`(`SampleID`,`usingProxy`, `isSpoofing`, `whatBrowser`, `whatOS`) VALUES(?, ?, ?, ?, ?);";
		PreparedStatement insertSampleSet = conn.prepareStatement(insertQuery);
		int index = 1;
		insertSampleSet.setInt(index, sampleID);
		++index;	
		insertSampleSet.setString(index, usingProxy);
		++index;
		insertSampleSet.setString(index, isSpoofing);
		++index;
		insertSampleSet.setString(index, whatBrowser);
		++index;
		insertSampleSet.setString(index, whatOS);

		insertSampleSet.execute();
		insertSampleSet.close();
	}
}