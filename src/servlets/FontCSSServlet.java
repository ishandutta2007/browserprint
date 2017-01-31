package servlets;

import java.io.IOException;
import java.util.HashSet;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.Fonts;

/**
 * Servlet implementation class ScreenSizeCSSServlet
 */
public class FontCSSServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private HashSet<String> expectedFonts;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FontCSSServlet() {
        super();
        
        expectedFonts = new HashSet<String>(Fonts.fontsEfficient.length);
        for(String font: Fonts.fontsEfficient){
        	expectedFonts.add(font);
        }
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = ((HttpServletRequest)request).getSession(true);
		synchronized (session) {
			session.setMaxInactiveInterval(300);
			
			String url = ((HttpServletRequest)request).getRequestURL().toString();
			String font = url.substring(url.lastIndexOf("/") + 1).replace('_', ' ');
	
			int substr_end= font.indexOf(";jsessionid", 0);
			if(substr_end > -1){
				//Trim off jsessionid if present
				font = font.substring(0, substr_end);
			}
			if(expectedFonts.contains(font)){
				//System.out.println("Requested: " + font);
				TreeSet<String> fontsNotRequested = (TreeSet<String>)session.getAttribute("fontsNotRequested");
				if(fontsNotRequested == null){
					fontsNotRequested = new TreeSet<String>(expectedFonts);
					session.setAttribute("fontsNotRequested", fontsNotRequested);
				}
				
				fontsNotRequested.remove(font);
			}
			response.sendError(404);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
