package servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAOs.FingerprintDAO;
import beans.CharacteristicsBean;
import beans.HistoryListBean;
import beans.UniquenessBean;
import datastructures.Fingerprint;
import util.SampleIDs;

/**
 * Servlet implementation class HistoryServlet
 */
public class HistoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HistoryServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get SampleSetID.
		String sampleSetID = SampleIDs.getSampleSetID(request, getServletContext());

		// Set SampleIDs associated with that SampleSetID.
		HistoryListBean history = FingerprintDAO.getSampleSetIDsHistory(sampleSetID, getServletContext());
		request.setAttribute("historyListBean", history);

		// Was a SampleID specified?
		String sampleUUID = request.getParameter("sampleUUID");
		if(sampleUUID != null){
			try{
				// Get the data associated with the SampleID.
				CharacteristicsBean chrsbean = new CharacteristicsBean();
				UniquenessBean uniquenessbean = new UniquenessBean();
				Fingerprint fingerprint = FingerprintDAO.getFingerprintBeans(sampleUUID, chrsbean, uniquenessbean);
				if(fingerprint != null){
					request.setAttribute("chrsBean", chrsbean);
					request.setAttribute("uniquenessBean", uniquenessbean);
				}
				else{
					throw new ServletException("No sample associated with given sampleUUID: " + sampleUUID);
				}
			}catch(SQLException ex){
				//Database error.
				throw new ServletException(ex);
			}
			
			// Forward to the history display page.
			request.getRequestDispatcher("/WEB-INF/historyDisplay.jsp").forward(request, response);
		}
		else{
			request.setAttribute("cookiesEnabled", (request.getCookies() != null));

			// Forward to the history page.
			request.getRequestDispatcher("/WEB-INF/history.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
