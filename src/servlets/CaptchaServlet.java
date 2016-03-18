package servlets;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.PlatesBean;
import util.Encryption;

/**
 * Servlet implementation class CaptchaServlet
 */
@WebServlet("/CaptchaServlet")
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
		Random rand = new SecureRandom();
		
		/*
		 * Calculate which random plates we'll show.
		 */
		int plates[] = new int[3];
		plates[0] = rand.nextInt(6) + 2;
		plates[1] = rand.nextInt(6) + 8;
		//plates[2] = rand.nextInt(2) + 14;
		plates[2] = rand.nextInt(2) + 16;
		
		/*
		 * Encrypt which plates we'll show so it can't be changed by the client.
		 */
		String password = this.getServletContext().getInitParameter("CaptchaEncryptionPassword");
		String encStr = Encryption.encryptIntegers(plates, password);
		
		request.setAttribute("platesBean", new PlatesBean(plates, encStr));
		
		request.getRequestDispatcher("/WEB-INF/captcha.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
