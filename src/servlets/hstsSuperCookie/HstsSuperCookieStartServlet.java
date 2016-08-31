package servlets.hstsSuperCookie;

import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class HSTSServlet
 */
public class HstsSuperCookieStartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final int NUM_ID_CHAINS = 8;
	public static final int ID_LENGTH = 4;
	
	private Pattern uriPattern;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HstsSuperCookieStartServlet() {
		super();
		
		this.uriPattern = Pattern.compile("/hstsSuperCookie/(start|midpoint)/(\\d+)(:?;jsessionid=.*)?$");
	}

	/**
	 * Serves requests for hsts0.browserprint.info/hstsSuperCookie/start
	 * The very first (and middle) request to start the whole supercookie process.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getServerName().startsWith("hsts0.") == false){
			System.err.println("HstsSuperCookieStartServlet: Unexpected domain. Start page must be subdomain hsts0. Subdomain = <" + request.getServerName() + ">.");
			response.sendError(404);
			return;
		}
		
		Matcher uriMatcher = uriPattern.matcher(request.getRequestURI());
		if(uriMatcher.matches() == false){
			System.err.println("HstsSuperCookieStartServlet: Invalid URI = <" + request.getRequestURI() + ">");
			response.sendError(404);
			return;
		}
		String action = uriMatcher.group(1);
		Integer hstsGroup = Integer.parseInt(uriMatcher.group(2));
		if(hstsGroup > NUM_ID_CHAINS){
			System.err.println("HstsSuperCookieStartServlet: Invalid HSTS group = <" + hstsGroup + ">");
			response.sendError(404);
			return;
		}
		Integer hstsGroupStartSubdomainNumber = ((hstsGroup - 1) * ID_LENGTH + 1);
		
		if(request.getServerPort() == 80){
			//New client. Generate them a new, unused ID, and encode it in the redirect URL.
			response.sendRedirect("https://hsts" + hstsGroupStartSubdomainNumber + "." + getServletContext().getInitParameter("websiteBaseURL")
					+ response.encodeRedirectURL("/hstsSuperCookie/newID/" + generateNewUnassignedIdChunk()));	
			return;
		}
		else if(request.getServerPort() == 443){
			//HTTPS is enabled, that means either there is an existing ID or we just finished setting an ID.
			
			if(action.equals("midpoint")){
				//The URI means we just got done setting an ID.
				HttpSession session = request.getSession();
				synchronized(session){
					Integer numIdChainsCompleted = (Integer) session.getAttribute("numIdChainsCompleted");
					System.out.println("numIdChainsCompleted = " + numIdChainsCompleted + ", hsts" + hstsGroup);
					if(numIdChainsCompleted == null){
						numIdChainsCompleted = 0;
					}
					++numIdChainsCompleted;
					
					if(numIdChainsCompleted >= NUM_ID_CHAINS){
						//ID has been saved fully. Enable HSTS so next time the client visits contacts this subdomain it will be using HTTPS.
						response.setHeader("Strict-Transport-Security", "max-age=31622400");
					}
					else{
						session.setAttribute("numIdChainsCompleted", numIdChainsCompleted);
					}
				}
			}
			/*else{ //action must be "start" since otherwise it would have been caught by the uriMatcher regex.
				//The URI means there is an existing ID.
				//No need to set HSTS. That's what upgraded us to HTTPS here.
				//Just continue.
			}*/
			
			//Redirect to extracting ID.
			//Redirect to the first in the chain for extracting IDs.
			response.sendRedirect("http://hsts" + hstsGroupStartSubdomainNumber + "." + getServletContext().getInitParameter("websiteBaseURL") + "/hstsSuperCookie/existingID/");
			return;
		}
		else{
			System.err.println("HstsSuperCookieStartServlet: Unexpected protocol. Port = <" + request.getServerPort() + ">.");
			response.sendError(404);
			return;
		}
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * Ideally this would generate an unused ID, but that's not very important in our example.
	 * @return
	 */
	private String generateNewUnassignedIdChunk() {
		String id = "";
		Random rand = new Random();
		int idBits = rand.nextInt();
		for(int i = 0; i < 4; ++i){
			id += (idBits & 1);
			idBits >>= 1;
		}
		return id;
	}
}