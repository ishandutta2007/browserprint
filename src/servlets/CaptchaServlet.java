package servlets;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.PlatesBean;

/**
 * Servlet implementation class CaptchaServlet
 */
public class CaptchaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CaptchaServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		Random rand = new SecureRandom();
		
		/*
		 * Calculate which random plates we'll show.
		 */
		int plates[] = new int[3];
		plates[0] = rand.nextInt(6) + 2;
		plates[1] = rand.nextInt(6) + 8;
		//plates[2] = rand.nextInt(2) + 14;
		plates[2] = rand.nextInt(2) + 16;
			
		request.setAttribute("platesBean", new PlatesBean(plates));
		
		session.setMaxInactiveInterval(60);//Session invalidates after 60 seconds.
		session.setAttribute("captcha", plates);
		
		request.getRequestDispatcher("/WEB-INF/captcha.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
