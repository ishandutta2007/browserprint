package servlets.hstsSuperCookie;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HSTSServlet
 */
public class HstsSuperCookieNewIDServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Pattern domainRegexPattern;
	private Pattern pathRegexPattern;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HstsSuperCookieNewIDServlet() {
		super();
		this.domainRegexPattern = Pattern.compile("^hsts(\\d+).browserprint.info$");
		this.pathRegexPattern = Pattern.compile("^/hstsSuperCookie/newID/([01]{" + HstsSuperCookieStartServlet.ID_LENGTH + "})$");
	}

	/**
	 * Serves requests for hsts[1 to ID_LENGTH].browserprint.info/hstsSuperCookie/newID/*
	 * This script applies an given ID to a new client one bit at a time.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int subdomainNumber;
		{
			String subdomain = request.getServerName();
			Matcher domainRegexMatcher = domainRegexPattern.matcher(subdomain);
			if(domainRegexMatcher.matches() == false){
				System.err.println("HstsSuperCookieNewIDServlet: Invalid subdomain <" + subdomain + ">.");
				response.sendError(404);
				return;
			}
			subdomainNumber = Integer.parseInt(domainRegexMatcher.group(1));
		}
		int subdomainGroup = (subdomainNumber - 1) / HstsSuperCookieStartServlet.ID_LENGTH + 1;//1,2,3,4 = 1; 5,6,7,8 = 2; ...
		int subdomainGroupIndex = (subdomainNumber - 1) % HstsSuperCookieStartServlet.ID_LENGTH + 1;//1 = 1; 2 = 2; 3 = 3; 4 = 4; 5 = 1
		
		Matcher pathRegexMatcher = pathRegexPattern.matcher(request.getRequestURI());
		if(pathRegexMatcher.matches() == false){
			System.err.println("HstsSuperCookieNewIDServlet: Invalid path. Must contain valid ID. Path = <" + request.getRequestURI() + ">.");
			response.sendError(404);
			return;
		}
		
		String id = pathRegexMatcher.group(1);
		if(id.charAt(subdomainGroupIndex - 1) == '1'){
			//Enable HSTS so next time the client visits contacts this subdomain it will be using HTTPS.
			response.setHeader("Strict-Transport-Security", "max-age=31622400");
		}
		/*else{//Bit is 0, so don't do anything
		}*/
		
		if(subdomainGroupIndex < HstsSuperCookieStartServlet.ID_LENGTH){
			//Redirect the client to the next subdomain in the chain.
			response.sendRedirect("https://hsts" + (subdomainNumber + 1) + ".browserprint.info/hstsSuperCookie/newID/" + id);
			return;
		}
		else{//subdomainGroupIndex == HstsSuperCookieStartServlet.ID_LENGTH
			//This is the last subdomain in the ID assignment chain, redirect the client for ID extraction.
			response.sendRedirect("https://hsts0.browserprint.info/hstsSuperCookie/midpoint/" + subdomainGroup);
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}