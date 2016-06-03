package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ScreenSizeCSSServlet
 */
public class ScreenSizeCSSServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ScreenSizeCSSServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = ((HttpServletRequest)request).getSession(true);
		session.setMaxInactiveInterval(300);
		
		String url = ((HttpServletRequest)request).getRequestURL().toString();
		String filename = url.substring(url.lastIndexOf("/") + 1);
		int substr_end= filename.indexOf("px", 0);
		if(substr_end > -1){
			filename = filename.substring(0, substr_end);
		}
		if(filename.length() < 30){
			String filename_parts[] = filename.split("\\.");
			session.setAttribute(filename_parts[0], filename_parts[1]);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
