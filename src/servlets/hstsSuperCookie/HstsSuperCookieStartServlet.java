package servlets.hstsSuperCookie;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HSTSServlet
 */
public class HstsSuperCookieStartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final int ID_LENGTH = 32;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HstsSuperCookieStartServlet() {
		super();
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * Serves requests for hsts0.browserprint.info/hstsSuperCookie/start
	 * The very first (and middle) request to start the whole supercookie process.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getServerName().equals("hsts0.browserprint.info") == false){
			System.err.println("HstsSuperCookieStartServlet: Unexpected domain. Start page must be subdomain hsts0. Subdomain = <" + request.getServerName() + ">.");
			response.sendError(404);
			return;
		}
		
		if(request.getServerPort() == 80){
			//New client. Generate them a new, unused ID, and encode it in the redirect URL.
			String redirectUrl = "https://hsts1.browserprint.info/hstsSuperCookie/newID/";
			
			redirectUrl = redirectUrl + generateNewUnassignedID();
			System.out.println(redirectUrl);
			
			response.sendRedirect(redirectUrl);
			return;
		}
		else if(request.getServerPort() == 443){
			//Enable HSTS so that next time the client contacts this subdomain it will be using HTTPS.
			//(HSTS may already enabled, or it may not be and we were just finished assigning an ID)
			response.setHeader("Strict-Transport-Security", "max-age=31622400");
			
			//Redirect to the first in the chain for extracting IDs.
			response.sendRedirect("http://hsts1.browserprint.info/hstsSuperCookie/existingID/");
			return;
		}
		else{
			System.err.println("HstsSuperCookieStartServlet: Unexpected protocol.");
			response.sendError(404);
		}
	}
	
	/**
	 * Ideally this would generate an unused ID, but that's not very important in our example.
	 * @return
	 */
	private String generateNewUnassignedID() {
		String id = "";
		Random rand = new Random();
		int idBits = rand.nextInt();
		for(int i = 0; i < 32; ++i){
			id += (idBits & 1);
			idBits >>= 1;
		}
		return id;
	}
}