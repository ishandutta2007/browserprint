package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAOs.FingerprintDAO;
import beans.PredictionBean;
import beans.CharacteristicsBean;
import beans.HistoryListBean;
import beans.UniquenessBean;
import datastructures.Fingerprint;
import util.SampleIDs;
import util.browserPrediction.Predictor;

/**
 * Servlet implementation class HistoryServlet
 */
public class ViewSampleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ViewSampleServlet() {
		super();
		// Auto-generated constructor stub
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

		String action = request.getParameter("action");

		/*
		 * Get SampleUUID1 and the source of the UUID.
		 */
		String sampleUUID1 = null;
		{
			String inputType = request.getParameter("source1");
			if(inputType == null){
				/*
				 * TODO: Handle JavaScript submit.
				 * TODO: set input type (history / UUID).
				 */
				//throw new ServletException("Not implemented yet.");
			}
			else if(inputType.equals("history")){
				sampleUUID1 = request.getParameter("UUID1history");
				request.setAttribute("source1", "history");
			}
			else if(inputType.equals("UUID")){
				sampleUUID1 = request.getParameter("UUID1UUID");
				request.setAttribute("source1", "UUID");
			}
			else{
				throw new ServletException("Unknown inputType1.");
			}
			if(sampleUUID1 != null){
				sampleUUID1 = sampleUUID1.trim();
			}
			request.setAttribute("sampleUUID1", sampleUUID1);
		}
		
		if (action == null || action.equals("View")) {
			if (sampleUUID1 != null && !sampleUUID1.equals("")) {
				// Get the data associated with the SampleID.
				CharacteristicsBean chrsbean = new CharacteristicsBean();
				UniquenessBean uniquenessbean = new UniquenessBean();
				PredictionBean predictionBean;
				Fingerprint fingerprint;
				try {
					fingerprint = FingerprintDAO.getFingerprintBeans(sampleUUID1, chrsbean, uniquenessbean);
					predictionBean = Predictor.getPredictionBean(fingerprint);
				} catch (Exception e) {
					throw new ServletException(e);
				}
				if (fingerprint != null) {
					request.setAttribute("chrsBean1", chrsbean);
					request.setAttribute("uniquenessBean1", uniquenessbean);
					request.setAttribute("predictionBean1", predictionBean);
				} else {
					throw new ServletException("No sample associated with given sampleUUID1: " + sampleUUID1);
				}

				// Forward to the history display page.
				request.getRequestDispatcher("/WEB-INF/viewSampleDisplay.jsp").forward(request, response);
				return;
			} else {
				request.setAttribute("cookiesEnabled", (request.getCookies() != null));

				// Forward to the history page.
				request.getRequestDispatcher("/WEB-INF/viewSample.jsp").forward(request, response);
				return;
			}
		}
		else if(action.equals("Compare")){
			String sampleUUID2 = null;
			{
				String inputType = request.getParameter("source2");
				if(inputType == null){
					/*
					 * TODO: Handle JavaScript submit.
					 * TODO: set input type (history / UUID).
					 */
					request.setAttribute("errorMessage", "Not implemented yet.");
					throw new ServletException("Not implemented yet.");
				}
				else if(inputType.equals("history")){
					sampleUUID2 = request.getParameter("UUID2history");
					request.setAttribute("source2", "history");
				}
				else if(inputType.equals("UUID")){
					sampleUUID2 = request.getParameter("UUID2UUID");
					request.setAttribute("source2", "UUID");
				}
				else{
					throw new ServletException("Unknown inputType2.");
				}
				if(sampleUUID2 != null){
					sampleUUID2 = sampleUUID2.trim();
				}
				request.setAttribute("sampleUUID2", sampleUUID2);
			}
			
			// Were the SampleUUIDs specified?
			if (sampleUUID1 != null && !sampleUUID1.equals("")) {
				// Get the data associated with the SampleUUID1.
				CharacteristicsBean chrsbean1 = new CharacteristicsBean();
				UniquenessBean uniquenessbean1 = new UniquenessBean();
				PredictionBean predictionBean1;
				Fingerprint fingerprint1;
				try {
					fingerprint1 = FingerprintDAO.getFingerprintBeans(sampleUUID1, chrsbean1, uniquenessbean1);
					predictionBean1 = Predictor.getPredictionBean(fingerprint1);
				} catch (Exception e) {
					throw new ServletException(e);
				}
				if (fingerprint1 != null) {
					request.setAttribute("chrsBean1", chrsbean1);
					request.setAttribute("uniquenessBean1", uniquenessbean1);
					request.setAttribute("predictionBean1", predictionBean1);
				} else {
					throw new ServletException("No sample associated with given sampleUUID1: " + sampleUUID1);
				}
				
				if(sampleUUID2 != null && !sampleUUID2.equals("")){
					// Get the data associated with the SampleUUID2.
					CharacteristicsBean chrsbean2 = new CharacteristicsBean();
					UniquenessBean uniquenessbean2 = new UniquenessBean();
					PredictionBean predictionBean2;
					Fingerprint fingerprint2;
					try {
						fingerprint2 = FingerprintDAO.getFingerprintBeans(sampleUUID2, chrsbean2, uniquenessbean2);
						predictionBean2 = Predictor.getPredictionBean(fingerprint2);
					} catch (Exception e) {
						throw new ServletException(e);
					}
					if (fingerprint2 != null) {
						request.setAttribute("chrsBean2", chrsbean2);
						request.setAttribute("uniquenessBean2", uniquenessbean2);
						request.setAttribute("predictionBean2", predictionBean2);
					} else {
						throw new ServletException("No sample associated with given sampleUUID2: " + sampleUUID2);
					}
					request.setAttribute("compare", true);
				}
				request.getRequestDispatcher("/WEB-INF/viewSampleDisplay.jsp").forward(request, response);
				return;
			} else {
				request.setAttribute("errorMessage", "Both sampleUUID1 and sampleUUID2 must be specified during a compare.");
				throw new ServletException("sampleUUID1 or sampleUUID2 not specified in a compare.");
			}
		}
		else{
			throw new ServletException("Unknown action.");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
